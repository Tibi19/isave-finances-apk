package com.tam.isave;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

// Tracks a list of categories.
// Keeps track of all payments made in these categories in a history.
// Handles any overflow that happens between them
// By making modify requests to categories that can handle the overflow.
public class CategoryTracker {

    private ArrayList<Category> categories;
    private History history;

    // Whether positive overflow handling should be done only by categories that follow a solicitor.
    private boolean orderedHandling;

    public CategoryTracker(ArrayList<Category> categories, History history, boolean orderedHandling) {
        this.categories = categories;
        this.history = history;
        this.orderedHandling = orderedHandling;
    }

    // Default constructor, does not have ordered handling.
    public CategoryTracker(ArrayList<Category> categories, History history) {
        this(categories, history, false);
    }

    // Constructor overload for arrays.
    public CategoryTracker(Category[] categories, History history, boolean orderedHandling) {
        this(new ArrayList<Category>(Arrays.asList(categories)), history, orderedHandling);
    }

    public void addCategory(Category category) {
        if( (category == null) || (categories.contains(category)) ) { return; }
        categories.add(category);
    }

    public void addCategories(ArrayList<Category> categories) {
        if( (categories == null) || (categories.isEmpty()) ) { return; }
        this.categories.addAll(categories);
    }

    // Remove category from list of categories;
    // Dispose of it as it's no longer needed.
    public void removeCategory(Category category) {
        if( (category == null) || !categories.contains(category) ) { return; }
        categories.remove(category);
        // category.dispose();
    }

    // Change name/goal of target category
    // By calling its modify method.
    public void modifyCategory(Category category, String name, double goal) {
        if(category == null) { return; }
        category.modify(name, goal);
    }

    // Make payment in target category and add to history.
    // Assign category as payment's parent category if @assign is true.
    // Handles overflow if it's the case.
    public void makePayment(Category category, Payment payment, boolean assign) {
        if( (category == null) || (payment == null) ) { return; }
        // Protect from duplicate payments.
        if(history.hasTransaction(payment)) { return; }

        if(assign) {
            assignCategory(category, payment);
        }

        // Make payment in category,
        // Spent changes, handle overflow if it's the case.
        if(category.makePayment(payment)) {
            handleOverflow(category);
        }
        history.addTransaction(payment);
    }

    // Make payment for target category but don't assign it.
    public void makePayment(Category category, Payment payment) {
        makePayment(category, payment, false);
    }

    // Make payment in payment's parent category.
    public void makePayment(Payment payment) {
        if(payment == null) { return; }
        makePayment(payment.getParentCategory(), payment, false);
    }

    // Removes payment from target category and history.
    // Handles overflow if it's the case.
    public void removePayment(Category category, Payment payment) {
        if( (category == null) || (payment == null) ) { return; }
        if(!history.hasTransaction(payment)) { return; }

        // Remove payment from category,
        // Spent changes, handle overflow if it's the case.
        if(category.removePayment(payment)) {
            handleOverflow(category);
        }
        history.removeTransaction(payment);
    }

    // Remove payment from its parent category.
    public void removePayment(Payment payment) {
        if(payment == null) { return; }
        removePayment(payment.getParentCategory(), payment);
        releaseCategory(payment);
    }

    // Modify payment in its parent category.
    public void modifyPayment(Payment payment, double valueDiff) {
        if(payment == null) { return; }
        modifyPayment(payment.getParentCategory(), payment, valueDiff);
    }

    // Modify payment in target category if it exists there.
    public void modifyPayment(Category category, Payment payment, double valueDiff) {
        if( (category == null) || (payment == null) ) { return; }
        if(!category.getHistory().hasTransaction(payment)) { return; }

        history.modifyTransaction(payment);
        if(category.modifyPayment(payment, valueDiff)) {
            handleOverflow(category);
        }
    }

    // Moves @payment from @origCategory to @newCategory.
    // Handles overflow if it's the case.
    public void movePayment(Category origCategory, Category newCategory, Payment payment) {
        if( (origCategory == null) || (newCategory == null) ) { return; }
        if(payment == null) { return; }

        // Move payment by removing it from original category,
        // And making the payment in the new category.
        removePayment(origCategory, payment);
        makePayment(newCategory, payment);
     }

    // Handles overflow of a solicitor.
    // @solicitor: category that produced the overflow and solicits handling.
    // Makes modify requests to categories that can handle the overflow.
    private void handleOverflow(Category solicitor) {
        double overflow = solicitor.getOverflow();
        if(Utils.isZeroDouble(overflow)) {
            return;
        }

        ArrayList<Category> handlers = new ArrayList<Category>(); // Categories that can handle a part of the overflow.
        double goalsTotal = 0.0; // The goals total of all handlers.
        boolean isPositiveOverflow = overflow > Utils.ZERO_DOUBLE;
        boolean orderedHandling = this.orderedHandling && isPositiveOverflow; // Ordered handling can be done only if overflow is positive.

        // If it's an ordered handling, iteration will start immediately after solicitor.
        int i = ( orderedHandling ) ? ( categories.indexOf(solicitor) + 1 ) : 0;
        for( ; i < categories.size(); i++) {
            Category category = categories.get(i);
            if(category.equals(solicitor)) { continue; } // Solicitor can't handle its own request.
            if(!category.canHandleOverflow(isPositiveOverflow)) { continue; } // If category can't handle this type of overflow, continue.

            handlers.add(category);
            goalsTotal += category.getEndGoal(); // add category's goal to the total of goals.
        }

        // Handle overflow by making personalized request for each handler.
        // Request is based on handler's share in the goals total of handlers.
        for(Category handler : handlers) {
            double modifyRequest = getModifyRequest(overflow, handler.getEndGoal(), goalsTotal);
            handler.modifyGoal(modifyRequest);
        }

        // If there are new overflows as a result of the handling, start recursion.
        for(Category category : categories) {
            if(category.hasOverflow()) {
                handleOverflow(category);
            }
        }
    }

    // The modify request to be solicited from a handler based on a rapport:
    // Between the overflow and the handler's share in a sum of goals of all handlers.
    private double getModifyRequest(double overflow, double handlerGoal, double goalsTotal) {
        return overflow * (handlerGoal / goalsTotal);
    }

    // Assigns @category to @payment as its parent category.
    public void assignCategory(Category category, Payment payment) {
        if(payment == null) { return; }
        payment.setParentCategory(category);
    }

    // Sets payment's category to null.
    public void releaseCategory(Payment payment) {
        if(payment == null) { return; }
        payment.setParentCategory(null);
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }
}
