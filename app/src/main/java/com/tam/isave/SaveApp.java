package com.tam.isave;

//TODO
// X Continue Category
// X Do Savings
// X Do IntervalTracker
// X Make sure history is always sorted.
// X Make an OverflowHandler class and add it to CategoryTracker's composition.
// ~ Make an overview sketch of the app so far. (maybe draw.io)
// ~ Check for Interfaces for makePayment, removePayment, modifyPayment
// X Do dispose functionality whenever needed and call it whenever a remove transaction method is called.
// X Modify spent in category? Think about it and add if needed.
// X Complete sketches to design 1st version of the app.
// X Add spent to modify functionality.
// Do SaveHome by following design functionality of home screen.
// Clean History for overall history and categories' history
// Go on to Activities

// X Do newDeposit.
// X Do newCashing.
// X Do modify goal organizer
// X Do modify category
// Do resets (goal organizer, category, all categories)
// Do remove category
// Do add category
// Do modify transaction
// Do remove transaction
import java.util.ArrayList;

public class SaveApp {

    private double balance;
    GoalOrganizer organizer;
    CategoryTracker tracker;

    public SaveApp() {
        setupOrganizer();
        setupTracker();
    }

    private void setupOrganizer() {
        organizer = new GoalOrganizer();
    }

    private void setupTracker() {
        ArrayList<Category> categories = new ArrayList<>();
        History history = new History();
        tracker = new CategoryTracker(categories, history);
    }

    /**
     * Create a new payment.
     * @param date When the payment took place.
     * @param name The name of the payment.
     * @param value The value of the payment.
     * @param parentCategory In which category should the payment be tracked.
     * @param organizable Whether this payment should also be tracked by the goal organizer.
     * @return True if a payment has been created successfully.
     */
    public boolean newPayment(Date date, String name, double value, Category parentCategory, boolean organizable) {
        if( (date == null) || (name == null) || (parentCategory == null) ) { return false; }
        if(value <= Utils.ZERO_DOUBLE) { return false; }

        Payment payment = new Payment(name, date, value, parentCategory);
        balance = payment.makeTransaction(balance); // Update balance.
        tracker.makePayment(payment);

        if(organizable) {
            organizer.makePayment(payment);
        }

        return true;
    }

    /**
     * Create a new deposit.
     * @param date When the deposit took place.
     * @param name The name of the deposit.
     * @param value The value of the deposit.
     * @param parentVault In what vault should the deposit be tracked.
     * @param organizable Whether this deposit should also be tracked by the goal organizer.
     * @return True if a deposit has been created successfully.
     */
    public boolean newDeposit(Date date, String name, double value, Vault parentVault, boolean organizable) {
        if(parentVault == null) { return false; }
        // Deposit is just a payment in a vault.
        return newPayment(date, name, value, parentVault, organizable);
    }

    /**
     * Creates a new cashing
     * @param date When the cashing took place.
     * @param name The name of the cashing.
     * @param value The value of the cashing.
     * @param modifiesOrganizer Whether the cashing should modify the goal of the organizer.
     * @return True if a cashing has been created successfully.
     */
    public boolean newCashing(Date date, String name, double value, boolean modifiesOrganizer) {
        if( (date == null) || (name == null) || (value <= Utils.ZERO_DOUBLE) ) { return false; }

        Cashing cashing = new Cashing(name, date, value);
        balance = cashing.makeTransaction(balance);

        if(modifiesOrganizer) {
            organizer.modify(organizer.getGlobalGoal() + cashing.getValue());
        }

        return true;
    }

    /**
     * Modify goal organizer's state.
     * @param newStart The new start date.
     * @param newEnd The new end date.
     * @param newIntervalsNr The new number of intervals.
     * @param newGoal The new goal.
     * @return True if goal organizer has been successfully modified.
     */
    public boolean modifyGoalOrganizer(Date newStart, Date newEnd, int newIntervalsNr, double newGoal) {
        if ( (newStart == null) || (newEnd == null) || (newIntervalsNr <= 0) ) { return false; }
        if (newGoal <= Utils.ZERO_DOUBLE) { return false; }

        organizer.modify(newGoal, newIntervalsNr, newStart, newEnd);
        return true;
    }

    /**
     * Modifies the state of a category.
     * @param category The category to be modified.
     * @param newName The new name of the category.
     * @param newSpent The new amount spent of the category.
     * @param newGoal The new goal of the category.
     * @param newIsFlexible The new flexibility of the category.
     * @return True if the category has been modified.
     */
    public boolean modifyCategory(Category category, String newName, double newSpent, double newGoal, boolean newIsFlexible) {
        if( (category == null) || (newName == null) ) { return false; }
        if( (newSpent <= -Utils.ZERO_DOUBLE) || (newGoal <= -Utils.ZERO_DOUBLE) ) { return false; }

        category.modify(newName, newSpent, newGoal, newIsFlexible);
        return true;
    }
}
