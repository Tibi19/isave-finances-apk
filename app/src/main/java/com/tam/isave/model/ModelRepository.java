package com.tam.isave.model;

//TODO
// X Continue Category
// X Do Savings
// X Do IntervalTracker
// X Make sure history is always sorted.
// X Make an OverflowHandler class and add it to CategoryTracker's composition.
// X Make an overview sketch of the app so far. (maybe draw.io)
// X Check for Interfaces for makePayment, removePayment, modifyPayment
// X Do dispose functionality whenever needed and call it whenever a remove transaction method is called.
// X Modify spent in category? Think about it and add if needed.
// X Complete sketches to design 1st version of the app.
// X Add spent to modify functionality.
// X Do Controller by following design functionality of home screen.
    /*
     X TODO newDeposit.
     X TODO newCashing.
     X TODO modify goal organizer
     X TODO modify category
     X TODO resets (goal organizer, category, all categories)
     X TODO remove category
     X TODO add category
     X TODO modify transaction
     X TODO remove transaction
     */
// TODO
//  - Try catch for overflow handling - asked stackoverflow.
//  X Clean History for overall history and categories' history
//  X Pass Overflow handler that will handle overflow
//  X Do ROOM Persistence for Category
//  X Do Category ViewModel
//  X Do XML for main activity
//  X Do XMLs for category recyclerviews
//  X Do recyclerview code
//  X Home Activity (X add category button onclick, X instantiate recyclerview, X adapter, X viewmodel, X make data observable)
//  X Prepopulate database with a couple categories and test - tested with adding random categories
//  X Integrate data repository with model repository and use model repository in view model to do all the CRUD
//  X Finish integrating modelRepository with category view model (create live data get categories in modelRepository, udate viewmodel methods to mirror
//      modelRepository functionality)
//  Clean model (X IProgressDisplayable? Many unneeded methods around, but thank yourself from the past for all the comments! Do some more composition)
//      was left at category tracker inclusive
//  Do add category popup
//  Do Fragments
//  Do Payment
//  Initialize other part of the model - category tracker, goal organizer etc. maybe save them in database with embedded entities or relational database?

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tam.isave.data.DataRepository;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryTracker;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.transaction.Cashing;
import com.tam.isave.model.transaction.History;
import com.tam.isave.model.transaction.Payment;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class ModelRepository {

    private double balance;
    GoalOrganizer organizer;
    CategoryTracker tracker;
    DataRepository dataRepository;

    // Singleton of the ModelRepository.
    private static ModelRepository INSTANCE;

    public static synchronized ModelRepository getModelRepository(final Application application) {
        if (INSTANCE == null) {
            INSTANCE = new ModelRepository(application);
        }

        return INSTANCE;
    }

    public ModelRepository(Application application) {
        setupOrganizer();
        setupTracker();
        dataRepository = new DataRepository(application);
    }

    private void setupOrganizer() {
        organizer = new GoalOrganizer();
    }

    private void setupTracker() {
        ArrayList<Category> categories = new ArrayList<>();
        History history = new History();
        tracker = new CategoryTracker(categories, history);
    }

    public LiveData<List<Category>> getCategories() {
        return dataRepository.getCategories();
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
        if(value <= NumberUtils.ZERO_DOUBLE) { return false; }

        Payment payment = new Payment(name, date, value, parentCategory);
        balance = payment.makeTransaction(balance); // Update balance.
        tracker.makePayment(payment);

        if(organizable) {
            organizer.makePayment(payment);
        }

        return true;
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
        if( (date == null) || (name == null) || (value <= NumberUtils.ZERO_DOUBLE) ) { return false; }

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
        if (newGoal <= NumberUtils.ZERO_DOUBLE) { return false; }

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
    public void modifyCategory(Category category, String newName, double newSpent, double newGoal, boolean newIsFlexible) {
        if( (category == null) || (newName == null) ) { return; }
        if( (newSpent <= -NumberUtils.ZERO_DOUBLE) || (newGoal <= NumberUtils.ZERO_DOUBLE) ) { return; }

        tracker.modifyCategory(category, newName, newSpent, newGoal, newIsFlexible);
        dataRepository.updateCategory(category);
    }

    /**
     * Reset the progress of the goal organizer.
     */
    public void resetGoalOrganizer() {
        organizer.reset();
    }

    /**
     * Reset the progress of a category.
     * @param category The category to be reset.
     */
    public void resetCategory(Category category) {
        tracker.resetCategory(category);
        dataRepository.updateCategory(category);
    }

    /**
     * Reset the progress of all categories.
     */
    public void resetAllCategories() {
        tracker.resetAllCategories();
        dataRepository.updateAllCategories(tracker.getCategories());
    }

    /**
     * Removes a category
     * @param category Category to be removed.
     */
    public void removeCategory(Category category) {
        tracker.removeCategory(category);
        dataRepository.deleteCategory(category);
    }

    // Only for testing
    // User shouldn't be able to delete all categories at once
    public void removeAllCategories() {
        dataRepository.deleteAllCategories();
    }

    /**
     * Create a new Category.
     * @param name The name of the category.
     * @param goal The goal of the category.
     * @param hasFlexibleGoal Whether category can change its goal to help other categories.
     */
    public void newCategory(String name, double goal, boolean hasFlexibleGoal) {
        if( (name == null) || (goal <= NumberUtils.ZERO_DOUBLE) ) { return; }
        Category category = new Category(name, goal, hasFlexibleGoal);
        tracker.addCategory(category);
        dataRepository.insertCategory(category);
    }

    /**
     * Modifies a payment.
     * @param payment The payment to be modified.
     * @param newCategory The new parent category of the payment.
     * @param newName The new name of the payment.
     * @param newDate The new date when payment happened.
     * @param newValue The new value of the payment.
     */
    public void modifyPayment(Payment payment, Category newCategory, String newName, Date newDate, double newValue) {
        if( (payment == null) || (newCategory == null) || (newName == null) || (newDate == null) ) { return; }
        if(newValue <= NumberUtils.ZERO_DOUBLE) { return; }

        double valueDifference = payment.modify(newName, newValue, newDate, newCategory);
        tracker.modifyPaymentInParent(payment, valueDifference);
    }

    /**
     * Remove payment only from the organizer.
     * @param payment Payment to be removed.
     */
    public void removePaymentFromOrganizer(Payment payment) {
        if(payment == null) { return; }
        organizer.removePayment(payment);
    }

    /**
     * Remove payment from everywhere.
     * @param payment Payment to be removed.
     */
    public void removePaymentGlobally(Payment payment) {
        if(payment == null) { return; }
        organizer.removePayment(payment);
        tracker.removePaymentGlobally(payment);
    }

    /**
     * Cleans histories of old transactions.
     */
    public void cleanHistories() {
        History.cleanHistories(tracker);
    }
}
