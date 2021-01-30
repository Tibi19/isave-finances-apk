package com.tam.isave.model.CategoryTools;

import com.tam.isave.model.TransactionTools.History;
import com.tam.isave.model.TransactionTools.Payment;

import java.util.ArrayList;
import java.util.Arrays;

// Tracks a list of categories.
// Keeps track of all payments made in these categories in a history.
// Calls goal adapter to handle any overflow that happens between them.
public class CategoryTracker {

    private ArrayList<Category> categories;
    private History history;
    private GoalAdapter adapter;

    public CategoryTracker() {

    }

    public void setup(ArrayList<Category> categories, History history, boolean orderedHandling) {
        this.categories = categories;
        this.history = history;
        this.adapter = new GoalAdapter(categories, orderedHandling);
    }

    public void setup(ArrayList<Category> categories, History history) {
        setup(categories, history, false);
    }

    public CategoryTracker(ArrayList<Category> categories, History history, boolean orderedHandling) {
        setup(categories, history, orderedHandling);
    }

    // Default constructor, does not have ordered handling.
    public CategoryTracker(ArrayList<Category> categories, History history) {
        setup(categories, history);
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

    public Category getCategory(int index) {
        if(categories.isEmpty()) { return null; }
        if(index < 0 || index >= categories.size()) { return null; }
        return categories.get(index);
    }

    // Remove category from list of categories;
    // Dispose of it as it's no longer needed.
    public void removeCategory(Category category) {
        if( (category == null) || !categories.contains(category) ) { return; }
        // First reset category to handle overflow if it's the case.
        category.reset(adapter);
        categories.remove(category);
        category.dispose();
    }

    // Change name, spent, goal of target category
    // By calling its modify method.
    public void modifyCategory(Category category, String name, double spent, double goal, boolean hasFlexibleGoal) {
        category.modify(name, spent, goal, hasFlexibleGoal, adapter);
    }

    // Make payment in target category and add to history.
    // Assign category as payment's parent category if @assign is true.
    // Handles overflow if it's the case.
    public void makePayment(Category category, Payment payment, boolean assign) {
        if( (category == null) || (payment == null) ) { return; }
        if(history.hasTransaction(payment)) { return; }

        if(assign) {
            assignCategory(category, payment);
        }
        history.addTransaction(payment);
        // Spent changes, handle overflow if it's the case.
        category.makePayment(payment, adapter);
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

    /**
     * Removes payment only from target category.
     * @param category The category where to remove the payment from.
     * @param payment The payment to be removed.
     */
    public void removePayment(Category category, Payment payment) {
        if( (category == null) || (payment == null) ) { return; }
        if(!history.hasTransaction(payment)) { return; }

        category.removePayment(payment, adapter);
        history.removeTransaction(payment);
    }

    /**
     * Remove payment from its category and have history dispose of it.
     * @param payment The payment to be removed.
     */
    public void removePaymentGlobally(Payment payment) {
        if(payment == null) { return; }

        Category category = payment.getParentCategory();
        category.removePayment(payment, adapter);
        history.removeTransaction(payment, true);
    }

    /**
     * Modify payment in its parent category.
     * @param payment The payment to be modified.
     * @param valueDiff The value difference of the payment after modification.
     */
    public void modifyPaymentInParent(Payment payment, double valueDiff) {
        if(payment == null) { return; }
        Category origCategory = getPaymentCategoryByHistory(payment);
        if(movePayment(origCategory, payment.getParentCategory(), payment)) { return; }
        modifyPayment(payment.getParentCategory(), payment, valueDiff);
    }

    // Return the category where payment parameter is found.
    // Checks for payment in category history.
    private Category getPaymentCategoryByHistory(Payment payment) {
        for(Category category : categories) {
            if(category.getHistory().hasTransaction(payment)) { return category; }
        }
        return null;
    }

    /**
     * Modifies payment in an interval.
     * @param interval The interval where payment should be modified.
     * @param payment The payment to be modified.
     * @param valueDiff The value difference of the payment after modification.
     */
    public void modifyPaymentInInterval(Interval interval, Payment payment, double valueDiff) {
        if( (interval == null) || (payment == null) ) { return; }
        if(!interval.getHistory().hasTransaction(payment)) { return; }

        modifyPayment(interval, payment, valueDiff);
    }

    // Modify payment in target category and update history.
    private void modifyPayment(Category category, Payment payment, double valueDiff) {
        history.modifyTransaction(payment);
        category.modifyPayment(payment, valueDiff, adapter);
    }

    // Moves @payment from @origCategory to @newCategory.
    // Handles overflow if it's the case.
    public boolean movePayment(Category origCategory, Category newCategory, Payment payment) {
        if( (origCategory == null) || (newCategory == null) || (payment == null) ) { return false; }
        if(origCategory.equals(newCategory)) { return false; }

        // Move payment by removing it from original category,
        // And making the payment in the new category.
        removePayment(origCategory, payment);
        makePayment(newCategory, payment);

        return true;
    }

    /**
     * Resets the progress of a category.
     * @param category The category that should be reset.
     */
    public void resetCategory(Category category) {
        if(category == null) { return; }
        // Spent changes, check and handle overflow.
        category.reset(adapter);
    }

    /**
     * Resets the progress of all categories.
     */
    public void resetAllCategories() {
        for(Category category : categories) {
            // Spent amount changes, but all categories will be reset so there will not be an overflow to handle.
            category.fullReset(adapter);
        }
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

    // Category tracker will handle dispose for categories.
    public void dispose() {
        if(categories != null && !categories.isEmpty()) {
            for(Category category : categories) {
                category.dispose();
            }
            categories.clear();
            categories = null;
        }

        if(history != null) {
            history.dispose();
            history = null;
        }

        if(adapter != null) {
            adapter.dispose();
            adapter = null;
        }
    }
}
