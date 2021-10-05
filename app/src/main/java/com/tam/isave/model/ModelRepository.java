package com.tam.isave.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tam.isave.data.DataRepository;
import com.tam.isave.data.GoalOrganizerPreferences;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryTracker;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.model.transaction.Cashing;
import com.tam.isave.model.transaction.History;
import com.tam.isave.model.transaction.Payment;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.utils.NumberUtils;

import java.util.List;

public class ModelRepository {

    private double balance;
    GoalOrganizer organizer;
    CategoryTracker tracker;
    DataRepository dataRepository;
    GoalOrganizerPreferences organizerPreferences;

    // Singleton of the ModelRepository.
    private static ModelRepository INSTANCE;

    public static synchronized ModelRepository getModelRepository(final Application application) {
        if (INSTANCE == null) {
            INSTANCE = new ModelRepository(application);
        }

        return INSTANCE;
    }

    private ModelRepository(Application application) {
        tracker = new CategoryTracker();
        dataRepository = new DataRepository(application);
        organizerPreferences = new GoalOrganizerPreferences(application);
        setupOrganizer();
    }

    public void setupTrackerCategories(List<Category> categories) {
        tracker.setupCategories(categories);
    }

    public void setupTrackerTransactions(List<Transaction> transactions) {
        if(tracker == null) { return; }

        History history = new History(transactions);
        tracker.setupHistory(history);
    }

    public void setupGoalOrganizerTransactions(List<Transaction> transactions) {
        if(organizer == null) { return; }

        History history = new History(transactions);
        organizer.setupHistory(history);
    }

    public LiveData<List<Category>> getCategories() {
        return dataRepository.getCategories();
    }

    public LiveData<List<Interval>> getIntervals() {
        return dataRepository.getIntervals();
    }

    public LiveData<List<Transaction>> getTransactions() {
        return dataRepository.getTransactions();
    }

    public LiveData<List<Transaction>> getCategoryTransactions(int categoryId) {
        return dataRepository.getCategoryTransactions(categoryId);
    }

    public LiveData<List<Transaction>> getIntervalTransactions(int startDateValue, int endDateValue) {
        return dataRepository.getIntervalTransactions(startDateValue, endDateValue);
    }

    public LiveData<List<Transaction>> getGlobalOrganizerTransactions(int startDateValue, int numberOfDays) {
        Date endDate = new Date(startDateValue).addDays(numberOfDays - 1);
        return dataRepository.getIntervalTransactions(startDateValue, endDate.getValue());
    }

    private void setupOrganizer() {
        double globalGoal = organizerPreferences.readGlobalGoal();
        int globalDays = organizerPreferences.readGlobalDays();
        int firstDayValue = organizerPreferences.readFirstDayValue();
        int intervalsCount = organizerPreferences.readIntervalsCount();

        if(globalGoal < 0.0 || globalDays < 0 || firstDayValue < 0 || intervalsCount < 0) {
            organizer = new GoalOrganizer();
            return;
        }

        organizer = new GoalOrganizer(globalGoal, intervalsCount, firstDayValue, globalDays);
    }

    /**
     * Create a new payment.
     * @param date When the payment took place.
     * @param name The name of the payment.
     * @param value The value of the payment.
     * @param parentId In which category should the payment be tracked.
     * @param organizable Whether this payment should also be tracked by the goal organizer.
     * @return True if a payment has been created successfully.
     */
    public boolean newPayment(Date date, String name, double value, int parentId, boolean organizable) {
        if( (date == null) || (name == null) ) { return false; }
        if(value <= NumberUtils.ZERO_DOUBLE) { return false; }

        Payment payment = new Payment(name, date, value, parentId);
        balance = payment.makeTransaction(balance); // Update balance.
        tracker.makePayment(payment);

        if(organizable && organizer != null) {
            organizer.makePayment(payment);
        }

        dataRepository.insertTransaction(payment);
        // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
        dataRepository.updateAllCategories(tracker.getCategories());

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

        if(modifiesOrganizer && organizer != null) {
            organizer.modify(organizer.getGlobalGoal() + cashing.getValue());
        }

        dataRepository.insertTransaction(cashing);

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
    public void modifyGoalOrganizer(double newGoal, int newIntervalsNr, Date newStart, Date newEnd) {
        if ( (newStart == null) || (newEnd == null) || (newIntervalsNr <= 0) ) { return; }
        if (newGoal <= NumberUtils.ZERO_DOUBLE) { return; }
        if (organizer == null) { return; }

        organizer.modify(newGoal, newIntervalsNr, newStart, newEnd);
        organizerPreferences.saveGoalOrganizer(organizer);
        // dataRepository.updateAllIntervals(organizer.getIntervals());
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
        // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
        dataRepository.updateAllCategories(tracker.getCategories());
    }

    /**
     * Reset the progress of the goal organizer.
     */
    public void resetGoalOrganizer() {
        if (organizer == null) { return; }
        organizer.reset();
        organizerPreferences.saveGoalOrganizer(organizer);
    }

    public void deleteGoalOrganizer() {
        organizer = new GoalOrganizer();
        organizerPreferences.deleteGoalOrganizer();
    }

    /**
     * Reset the progress of a category.
     * @param category The category to be reset.
     */
    public void resetCategory(Category category) {
        tracker.resetCategory(category);
        // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
        dataRepository.updateAllCategories(tracker.getCategories());
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
        // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
        dataRepository.updateAllCategories(tracker.getCategories());
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

        // TODO add overflow handling update when adding category.
        // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
        dataRepository.updateAllCategories(tracker.getCategories());
    }

    /**
     * Modifies a payment.
     * @param payment The payment to be modified.
     * @param newParentId The new parent category of the payment.
     * @param newName The new name of the payment.
     * @param newDate The new date when payment happened.
     * @param newValue The new value of the payment.
     */
    public void modifyPayment(Transaction payment, int newParentId, String newName, Date newDate, double newValue) {
        if( (payment == null) || (newName == null) || (newDate == null) ) { return; }
        if(newValue <= NumberUtils.ZERO_DOUBLE) { return; }

        double valueDifference = payment.modify(newName, -newValue, newDate, newParentId);
        if (tracker != null) {
            tracker.modifyPaymentInParent(payment, valueDifference);
            // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
            dataRepository.updateAllCategories(tracker.getCategories());
        }
        if (organizer != null) {
            organizer.modifyPayment(payment, valueDifference);
        }

        dataRepository.updateTransaction(payment);
    }

    /**
     * Remove payment only from the organizer.
     * @param payment Payment to be removed.
     */
    public void removePaymentFromOrganizer(Payment payment) {
        if(payment == null || organizer == null) { return; }
        organizer.removePayment(payment);
    }

    /**
     * Delete Payment.
     * @param payment Payment to be deleted.
     */
    public void deletePayment(Transaction payment) {
        if(payment == null) { return; }
        if(organizer != null) { organizer.removePayment(payment); }
        if(tracker != null) {
            tracker.removePaymentGlobally(payment);
            // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
            dataRepository.updateAllCategories(tracker.getCategories());
        }
        dataRepository.deleteTransaction(payment);
    }

    /**
     * Delete Cashing.
     * @param cashing Cashing to be deleted.
     */
    public void deleteCashing(Transaction cashing) {
        if(cashing == null) { return; }
        dataRepository.deleteTransaction(cashing);
    }

    /**
     * Cleans histories of old transactions.
     */
    public void cleanHistories() {
        History.cleanHistories(tracker);
    }

    public GoalOrganizer getGoalOrganizer() {
        return organizer;
    }
}
