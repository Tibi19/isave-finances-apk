package com.tam.isave.model.category;

import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.model.transaction.History;
import com.tam.isave.model.transaction.Payment;
import com.tam.isave.model.transaction.Transaction;

import java.util.List;

// Tracks a list of categories.
// Keeps track of all payments made in these categories in a history.
// Calls goal adapter to handle any overflow that happens between them.
public class CategoryTracker {

    private List<Category> categories;
    private History history;
    private GoalAdapter goalAdapter;

    public CategoryTracker(List<Category> categories, History history, boolean orderedHandling) {
        setup(categories, history, orderedHandling);
    }

    // Default constructor, does not have ordered handling.
    public CategoryTracker(List<Category> categories, History history) {
        setup(categories, history, false);
    }

    public CategoryTracker() {}

    public void setup(List<Category> categories, History history, boolean orderedHandling) {
        this.categories = categories;
        this.history = history;
        this.goalAdapter = new GoalAdapter(categories, orderedHandling);
        setupCategoryHistories();
    }

    /**
     * Default category initializer without ordered handling (for normal categories).
     * Sets up the categories of the tracker if they haven't been set up already.
     * @param categories The categories to be tracked.
     */
    public void setupCategories(List<Category> categories) {
        setupCategories(categories, false);
    }

    /**
     * Sets up the categories of the tracker if they haven't been set up already.
     * @param categories The categories to be tracked.
     * @param orderedHandling If the tracker should have ordered handling (for intervals).
     */
    public void setupCategories(List<Category> categories, boolean orderedHandling) {
        if(this.categories != null || categories == null) { return; }
        this.categories = categories;
        this.goalAdapter = new GoalAdapter(categories, orderedHandling);

        if(this.history != null) {
            setupCategoryHistories();
        }
    }

    /**
     * Sets up the history of the tracker.
     * @param history The history of transactions.
     */
    public void setupHistory(History history) {
        if(this.history != null || history == null) { return; }

        this.history = history;

        if(this.categories != null) {
            setupCategoryHistories();
        }
    }

    private void setupCategoryHistories() {
        for(Category category : categories) {
            History categoryHistory = category.getHistory();
            if(categoryHistory != null && !categoryHistory.isEmpty()) { continue; }
            category.setHistory(history.getCategoryHistory(category.getId()));
        }
    }

    public void addCategory(Category category) {
        if( (category == null) || (categories.contains(category)) ) { return; }
        categories.add(category);
    }

    public void addCategories(List<Category> categories) {
        if( (categories == null) || (categories.isEmpty()) ) { return; }
        this.categories.addAll(categories);
    }

    public Category getCategory(int index) {
        if(categories.isEmpty()) { return null; }
        if(index < 0 || index >= categories.size()) { return null; }
        return categories.get(index);
    }

    // Remove category from list of categories;
    public void removeCategory(Category category) {
        if( (category == null) || !categories.contains(category) ) { return; }
        // First reset category to handle overflow if it's the case.
        category.reset(goalAdapter);
        categories.remove(category);
    }

    // Change name, spent, goal of target category
    // By calling its modify method.
    public void modifyCategory(Category category, String name, double spent, double goal, boolean hasFlexibleGoal) {
        category.modify(name, spent, goal, hasFlexibleGoal, goalAdapter);
    }

    // Make payment in target category and add to history.
    // Assign category as payment's parent category if @assign is true.
    public void makePayment(Category category, Transaction payment, boolean assign) {
        if( (category == null) || (payment == null) ) { return; }
        if(history.hasTransaction(payment)) { return; }

        if(assign) {
            assignCategory(category, payment);
        }
        history.addTransaction(payment);
        // Spent changes, handle overflow if it's the case.
        category.makePayment(payment, goalAdapter);
    }

    // Make payment for target category but don't assign it.
    public void makePayment(Category category, Transaction payment) {
        makePayment(category, payment, false);
    }

    // Make payment in payment's parent category.
    public void makePayment(Payment payment) {
        if(payment == null) { return; }
        Category paymentCategory = getCategoryById(payment.getParentId());
        makePayment(paymentCategory, payment, false);
    }

    /**
     * Removes payment only from target category.
     * @param category The category where to remove the payment from.
     * @param payment The payment to be removed.
     */
    public void removePayment(Category category, Transaction payment) {
        if( (category == null) || (payment == null) ) { return; }
        if(!history.hasTransaction(payment)) { return; }

        category.removePayment(payment, goalAdapter);
        history.removeTransaction(payment);
    }

    /**
     * Remove payment from its category and have history dispose of it.
     * @param payment The payment to be removed.
     */
    public void removePaymentGlobally(Transaction payment) {
        if(payment == null) { return; }

        Category category = getCategoryById(payment.getParentId());

        if(category != null) {
            category.removePayment(payment, goalAdapter);
        }

        history.removeTransaction(payment);
    }

    /**
     * Modify payment in its parent category.
     * @param payment The payment to be modified.
     * @param valueDiff The value difference of the payment after modification.
     */
    public void modifyPaymentInParent(Transaction payment, double valueDiff) {
        if(payment == null) { return; }
        Category origCategory = getPaymentCategoryByHistory(payment);
        Category currentParentCategory = getCategoryById(payment.getParentId());
        if(movePayment(origCategory, currentParentCategory, payment)) { return; }
        modifyPayment(currentParentCategory, payment, valueDiff);
    }

    // Return the category where payment parameter is found.
    // Checks for payment in category history.
    private Category getPaymentCategoryByHistory(Transaction payment) {
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
    public void modifyPaymentInInterval(Interval interval, Transaction payment, double valueDiff) {
        if( (interval == null) || (payment == null) ) { return; }
        if(!interval.getHistory().hasTransaction(payment)) { return; }

        modifyPayment(interval, payment, valueDiff);
    }

    // Modify payment in target category and update history.
    private void modifyPayment(Category category, Transaction payment, double valueDiff) {
        if(category == null || payment == null) { return; }

        history.modifyTransaction(payment);
        category.modifyPayment(payment, valueDiff, goalAdapter);
    }

    // Moves @payment from @origCategory to @newCategory.
    // Handles overflow if it's the case.
    public boolean movePayment(Category origCategory, Category newCategory, Transaction payment) {
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
        category.reset(goalAdapter);
    }

    /**
     * Resets the progress of all categories.
     */
    public void resetAllCategories() {
        for(Category category : categories) {
            // Spent amount changes, but all categories will be reset so there will not be an overflow to handle.
            category.fullReset(goalAdapter);
        }
    }

    // Assigns @category to @payment as its parent category.
    public void assignCategory(Category category, Transaction payment) {
        if(payment == null) { return; }
        payment.setParentId(category.getId());
    }

    // Sets payment's parentId to -1.
    // Payment won't have a parent category anymore.
    public void releaseCategory(Payment payment) {
        if(payment == null) { return; }
        payment.setParentId(-1);
    }

    public Category getCategoryById(int categoryId) {
        for (Category category : categories) {
            if (category.getId() == categoryId) {
                return category;
            }
        }

        return null;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

}
