package com.tam.isave.model.category;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.tam.isave.model.transaction.History;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.IdGenerator;
import com.tam.isave.utils.NumberUtils;

// Categorises payments and tracks them in a history.
// Stores how much money is spent on products/services that are part of the category.
// Has a goal and compares total spent to that goal.
//
// Goal can be decreased when other categories pass their goals (are overflowing) to help reach the saving goal of the user;
// A modify request will be made by a parent category tracker to all categories that can modify their goals. (those can help)
// When overflow is requested, the class assumes it will be handled. (a modify request will be made by the parent category tracker).
//
// As a result of goal change / modification or removal of payments, goal modification might need to be adjusted or reset (decreased).
// In this case, a negative overflow can be requested and a modify request should be made
// By the parent category tracker to all categories that have been previously modified. (handling overflow)
@Entity(tableName = Constants.TABLE_NAME_CATEGORY)
public class Category{

    @PrimaryKey()
    private int id;

    @ColumnInfo(name = Constants.CATEGORY_COLUMN_NAME)
    private String name;
    @ColumnInfo(name = Constants.CATEGORY_COLUMN_SPENT)
    private double spent;
    @ColumnInfo(name = Constants.CATEGORY_COLUMN_GOAL)
    private double goal;

    @ColumnInfo(name = Constants.CATEGORY_COLUMN_GOAL_MODIFIER)
    private double goalModifier;
    @ColumnInfo(name = Constants.CATEGORY_COLUMN_GOAL_PASSED)
    private double goalPassed;
    @ColumnInfo(name = Constants.CATEGORY_COLUMN_FLEXIBLE_GOAL)
    private boolean flexibleGoal;

    @Ignore
    private History history;
    @Ignore
    private OverflowHandler overflowHandler;

    public Category() {
        overflowHandler = new OverflowHandler(this);
    }

    @Ignore
    public Category(String name, double goal, boolean flexibleGoal) {
        this();

        this.name = name;
        this.goal = goal;
        this.flexibleGoal = flexibleGoal;

        history = new History();
        id = IdGenerator.makeId();

        this.spent = 0.0;
        this.goalModifier = 0.0;
        this.goalPassed = 0.0;
    }

    @Ignore
    public Category(String name, double goal) {
        this(name, goal, true);
    }

    // Adds payment to category history and adds to total spent.

    // Returns whether there is overflow.
    public void makePayment(Transaction payment, GoalAdapter adapter) {
        history.addTransaction(payment);
        // Spent changes, there might be overflow.
        spent += Math.abs(payment.getValue());

        overflowHandler.resolveOverflow(adapter); // Spent changes, there might be overflow to be handled.
    }

    // Removes a transaction if it exists in object's history and removes from total spent.
    public void removePayment(Transaction payment, GoalAdapter adapter) {
        if (!history.hasTransaction(payment)) { return; }

        history.removeTransaction(payment);
        if(payment.getParentId() == id) {
            payment.setParentId(-1);
        }
        spent -= Math.abs(payment.getValue());

        overflowHandler.resolveOverflow(adapter); // Spent changes, there might be overflow to be handled
    }

    // Adds to spent amount the difference in value of the modified payment.
    public void modifyPayment(Transaction payment, double valueDiff, GoalAdapter adapter) {
        if( NumberUtils.isZeroDouble(valueDiff) ) { return; }
        if (!history.hasTransaction(payment)) { return; }

        spent -= valueDiff;

        overflowHandler.resolveOverflow(adapter); // Spent changes, there might be overflow to be handled.
    }

    /**
     * Modify the state of category.
     * @param name The new name of the category.
     * @param spent The new spent amount of the category.
     * @param goal The new goal of the category.
     * @param hasFlexibleGoal The new flexibility of this category's goal.
     * @param adapter The goal adapter that would handle the overflow.
     */
    public void modify(String name, double spent, double goal, boolean hasFlexibleGoal, GoalAdapter adapter) {
        if(!this.name.equalsIgnoreCase(name)) { this.name = name; }

        boolean changeSpent = !NumberUtils.isSameDoubles(this.spent, spent);
        boolean changeGoal = !NumberUtils.isSameDoubles(this.goal, goal);
        boolean changeFlexibility = this.flexibleGoal != hasFlexibleGoal;

        if(changeSpent) { this.spent = spent; }
        if(changeGoal) { this.goal = goal; }
        this.flexibleGoal = hasFlexibleGoal;

        if (changeSpent || changeGoal || changeFlexibility) { overflowHandler.resolveOverflow(adapter); }
    }

    /**
     * Resets state relating to progress - amount spent.
     * If goalPassed > 0, there will be a negative overflow which should be handled.
     * After handling, goalPassed will also be reset.
     * @param adapter The goal adapter that would handle the overflow.
     */
    public void reset(GoalAdapter adapter) {
        this.spent = 0.0;
        overflowHandler.resolveOverflow(adapter); // Progress changes, there might be overflow to be handled.
        //releasePayments();
    }

    public void restore() {
        this.goalModifier = 0.0;
    }

    private void releasePayments() {
        for(Transaction transaction : history.getHistoryList()) { transaction.setParentId(-1); }
        history.reset();
    }

    public void resetOverflowModifications() {
        goalModifier = 0.0;
        goalPassed = 0.0;
    }

    /**
     * Resets the entire state.
     * To be used when resetting all categories of a tracker.
     */
    public void fullReset() {
        spent = 0.0;
        resetOverflowModifications();
    }

    // To be used in case the goal is passed for another category (it overflows).
    // In which case, a request can be made to a flexible object of this class to modify its end goal.
    //
    // In case goal or spent amount changes, goal might need to be reset or adjusted by increasing it (decreasing goalModifier).
    // In which case, the same request can be made to a modified object of this class.
    // Parameter should be negative based on a negative overflow.
    //
    // Add @modifyRequest to this.goalModifier.
    public void modifyGoal(double modifyRequest) {
        if(!flexibleGoal) { return; }

        // If goal for this object was passed and trying to increase goal modifier, stop method.
        // Goal modifier can be decreased.
        if(goalPassed > NumberUtils.ZERO_DOUBLE && modifyRequest > NumberUtils.ZERO_DOUBLE) { return; }

        goalModifier += modifyRequest;

        if(goalModifier < -NumberUtils.ZERO_DOUBLE) { goalModifier = 0.0; }
    }

    // Goal after goal modification.
    public double getEndGoal() {
        return goal - goalModifier;
    }

    /**
     * The amount left that can be spent.
     * Also offers information regarding goal modification.
     * To be used for displaying category progress.
     * @return a string in format "ll.ll (-mm.mm)" or "ll.ll" if goal was not modified.
     */
    public String getProgress() {
        String leftAmountString = String.valueOf(getLeftAmountTwoDecimals());
        if (!NumberUtils.isZeroDouble(goalModifier)) {
            leftAmountString += " (-" + NumberUtils.twoDecimalsRounded(goalModifier) + ")";
        }

        return leftAmountString;
    }

    public double getLeftAmountTwoDecimals() {
        return NumberUtils.twoDecimalsRounded(getLeftAmount());
    }

    public double getLeftAmount() {
        return getEndGoal() - spent;
    }

    // If category can help with overflow when another category passes its goal.
    // If goal is flexible and if it hasn't been passed.
    // To be used when increasing goal modifier.
    public boolean canHelp() {
        return flexibleGoal
                && (goalPassed <= NumberUtils.ZERO_DOUBLE)
                && !NumberUtils.isZeroDouble(getLeftAmountTwoDecimals());
    }

    // If category should adjust its goal modification
    // As a result of another category subtracting its overflow. (there is negative overflow)
    // Whether goal has been modified.
    // To be used when decreasing goal modifier.
    public boolean isModified() {
        return goalModifier > NumberUtils.ZERO_DOUBLE;
    }

    // If category can handle an overflow based on its sign.
    // If it's positive overflow, it will be able to handle if it's available to help (canHelp())
    // If it's negative overflow, it will be able to handle if it has been modified (isModified())
    public boolean canHandleOverflow(boolean isPositiveOverflow) {
        return isPositiveOverflow ? canHelp() : isModified();
    }

    public boolean hasOverflow() {
        return overflowHandler.hasOverflow();
    }

    public double getOverflow() {
        return overflowHandler.getOverflow();
    }

    /******************* GETTERS AND SETTERS *******************/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public double getGoalModifier() {
        return goalModifier;
    }

    public void setGoalModifier(double goalModifier) {
        this.goalModifier = goalModifier < -NumberUtils.ZERO_DOUBLE ? 0.0 : goalModifier;
    }

    public double getGoalPassed() {
        return goalPassed;
    }

    public void setGoalPassed(double goalPassed) {
        this.goalPassed = goalPassed;
    }

    public boolean isFlexibleGoal() {
        return flexibleGoal;
    }

    public void setFlexibleGoal(boolean flexibleGoal) {
        this.flexibleGoal = flexibleGoal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
