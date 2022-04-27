package com.tam.isave.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tam.isave.data.DataRepository;
import com.tam.isave.data.GoalOrganizerPreferences;
import com.tam.isave.data.MainBudgetPreferences;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryTracker;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.model.transaction.History;
import com.tam.isave.model.transaction.Payment;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.NumberUtils;

import java.util.List;

public class ModelRepository {

    private MainBudget mainBudget;
    private GoalOrganizer organizer;
    private CategoryTracker tracker;

    private DataRepository dataRepository;
    private GoalOrganizerPreferences organizerPreferences;
    private MainBudgetPreferences mainBudgetPreferences;

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
        mainBudgetPreferences = new MainBudgetPreferences(application);
        organizerPreferences = new GoalOrganizerPreferences(application);
        setupMainBudget();
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

    private void setupMainBudget() {
        double budget = mainBudgetPreferences.readBudget();
        double spent = mainBudgetPreferences.readSpent();
        boolean isHidden = mainBudgetPreferences.readIsHidden();

        mainBudget = new MainBudget(budget, spent, isHidden);
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

    public void updateOrganizer() {
        if(organizer == null) { return; }
        organizer.update();
    }

    /**
     * Create a new payment.
     * @param date When the payment took place.
     * @param name The name of the payment.
     * @param value The value of the payment.
     * @param parentId In which category should the payment be tracked.
     * @param organizable Whether this payment should also be tracked by the goal organizer.
     */
    public void newPayment(Date date, String name, double value, int parentId, boolean organizable) {
        if( (date == null) || (name == null) ) { return; }
        if(value <= NumberUtils.ZERO_DOUBLE) { return; }

        Payment payment = new Payment(name, date, value, parentId, organizable);
        tracker.makePayment(payment);

        if(organizable) {
            if(organizer != null) { organizer.makePayment(payment); }
            if(mainBudget != null) {
                mainBudget.makePayment(payment);
                mainBudgetPreferences.saveMainBudget(mainBudget);
            }
        }

        dataRepository.insertTransaction(payment);
        // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
        dataRepository.updateAllCategories(tracker.getCategories());
    }

    public void addCashing(double cashingValue, boolean shouldReset) {
        if( cashingValue < -NumberUtils.ZERO_DOUBLE || mainBudget == null ) { return; }
        if(shouldReset) {
            mainBudget.setBudget(cashingValue);
            mainBudget.setSpent(0.0);
        }
        else {
            mainBudget.addToBudget(cashingValue);
        }
        mainBudgetPreferences.saveMainBudget(mainBudget);
    }

    /**
     * Modify goal organizer's state.
     * @param newStart The new start date.
     * @param newEnd The new end date.
     * @param newIntervalsNr The new number of intervals.
     * @param newGoal The new goal.
     */
    public void modifyGoalOrganizer(double newGoal, int newIntervalsNr, Date newStart, Date newEnd, List<Transaction> transactions) {
        if ( (newStart == null) || (newEnd == null) || (transactions == null) || (newIntervalsNr <= 0) ) { return; }
        if (newGoal <= -NumberUtils.ZERO_DOUBLE) { return; }
        if (organizer == null) { return; }

        History newHistory = new History(transactions);
        organizer.modify(newGoal, newIntervalsNr, newStart, newEnd, newHistory);
        organizerPreferences.saveGoalOrganizer(organizer);
    }

    public void modifyMainBudget(double budget, double spent, boolean isHidden) {
        if( budget < -NumberUtils.ZERO_DOUBLE || spent < -NumberUtils.ZERO_DOUBLE) { return; }
        if ( mainBudget == null ) { return; }
        mainBudget.modify(budget, spent, isHidden);
        mainBudgetPreferences.saveMainBudget(mainBudget);
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
        if( (newSpent <= -NumberUtils.ZERO_DOUBLE) || (newGoal <= -NumberUtils.ZERO_DOUBLE) ) { return; }

        tracker.modifyCategory(category, newName, newSpent, newGoal, newIsFlexible);
        // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
        dataRepository.updateAllCategories(tracker.getCategories());
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
        if(tracker == null || category == null) { return; }
        History history = tracker.getHistory().getCategoryHistory(category.getId());
        tracker.resetCategory(category);
        // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
        dataRepository.updateAllCategories(tracker.getCategories());
        dataRepository.updateAllTransactions(history.getHistoryList());
    }

    public void restoreCategory(Category category) {
        if(category == null) { return; }
        category.restore();
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
        if( (name == null) || (goal < -NumberUtils.ZERO_DOUBLE) ) { return; }
        Category category = new Category(name, goal, hasFlexibleGoal);
        tracker.addCategory(category);
        dataRepository.insertCategory(category);

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
    public void modifyPayment(Transaction payment, int newParentId, String newName, Date newDate, double newValue, boolean newIsOrganizable) {
        if( (payment == null) || (newName == null) || (newDate == null) ) { return; }
        if(newValue <= NumberUtils.ZERO_DOUBLE) { return; }

        boolean originalIsOrganizable = payment.isOrganizable();

        double valueDifference = payment.modify(newName, -newValue, newDate, newParentId, newIsOrganizable);
        if (tracker != null) {
            tracker.modifyPaymentInParent(payment, valueDifference);
            // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
            dataRepository.updateAllCategories(tracker.getCategories());
        }

        if(originalIsOrganizable != newIsOrganizable) {
            handlePaymentOrganizableChange(payment, originalIsOrganizable, newIsOrganizable);
        }else if(payment.isOrganizable()) {
            if (organizer != null) {
                organizer.modifyPayment(payment, valueDifference);
            }
            if (mainBudget != null) {
                mainBudget.modifySpent(valueDifference);
                mainBudgetPreferences.saveMainBudget(mainBudget);
            }
        }

        dataRepository.updateTransaction(payment);
    }

    private void handlePaymentOrganizableChange(Transaction payment, boolean originalIsOrganizable, boolean newIsOrganizable) {
        if(!originalIsOrganizable && newIsOrganizable) {
            if(organizer != null) {
                organizer.makePayment(payment);
            }
            if(mainBudget != null) {
                mainBudget.makePayment(payment);
                mainBudgetPreferences.saveMainBudget(mainBudget);
            }
            return;
        }

        if(originalIsOrganizable && !newIsOrganizable) {
            if(organizer != null) {
                organizer.removePayment(payment);
            }
            if(mainBudget != null) {
                mainBudget.removePayment(payment);
                mainBudgetPreferences.saveMainBudget(mainBudget);
            }
        }
    }

    /**
     * Delete Payment.
     * @param payment Payment to be deleted.
     */
    public void deletePayment(Transaction payment) {
        if(payment == null) { return; }

        if(tracker != null) {
            tracker.removePaymentGlobally(payment);
            // Tracker modifications can modify all categories because of overflow handling, all categories should be updated.
            dataRepository.updateAllCategories(tracker.getCategories());
        }

        if(payment.isOrganizable()) {
            if(organizer != null) { organizer.removePayment(payment); }
            if(mainBudget != null) {
                mainBudget.removePayment(payment);
                mainBudgetPreferences.saveMainBudget(mainBudget);
            }
        }

        dataRepository.deleteTransaction(payment);
    }

    public GoalOrganizer getGoalOrganizer() {
        return organizer;
    }

    public MainBudget getMainBudget() {
        return mainBudget;
    }
}
