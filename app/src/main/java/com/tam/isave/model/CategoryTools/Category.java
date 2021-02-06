package com.tam.isave.model.CategoryTools;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.tam.isave.model.TransactionTools.History;
import com.tam.isave.model.IProgressDisplayable;
import com.tam.isave.model.TransactionTools.Payment;
import com.tam.isave.utils.Constants;
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
public class Category implements IProgressDisplayable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = Constants.COLUMN_NAME)
    private String name;
    @ColumnInfo(name = Constants.COLUMN_SPENT)
    private double spent;
    @ColumnInfo(name = Constants.COLUMN_GOAL)
    private double goal;

    @ColumnInfo(name = Constants.COLUMN_GOAL_MODIFIER)
    private double goalModifier;
    @ColumnInfo(name = Constants.COLUMN_GOAL_PASSED)
    private double goalPassed;
    @ColumnInfo(name = Constants.COLUMN_HAS_FLEXIBLE_GOAL)
    private boolean hasFlexibleGoal;

    @Ignore
    private History history;

    public Category() {
    }

    @Ignore
    public Category(String name, double goal, boolean hasFlexibleGoal) {
        this.name = name;
        this.goal = goal;
        this.hasFlexibleGoal = hasFlexibleGoal;

        history = new History();

        this.spent = 0.0;
        this.goal = 0.0;
        this.goalModifier = 0.0;
        this.goalPassed = 0.0;
    }

    @Ignore
    public Category(String name, double goal) {
        this(name, goal, true);
    }

    // Adds payment to category history and adds to total spent.

    // Returns whether there is overflow.
    public void makePayment(Payment payment, GoalAdapter adapter) {
        history.addTransaction(payment);
        // Spent changes, there might be overflow.
        spent += payment.getAbsValue();

        resolveOverflow(adapter); // Spent changes, there might be overflow to be handled.
    }

    // Removes a transaction if it exists in object's history and removes from total spent.
    public void removePayment(Payment payment, GoalAdapter adapter) {
        if (!history.hasTransaction(payment)) { return; }

        history.removeTransaction(payment);
        if(payment.getParentCategory() == this) {
            payment.setParentCategory(null);
        }
        spent -= payment.getAbsValue();

        resolveOverflow(adapter); // Spent changes, there might be overflow to be handled
    }

    // Adds to spent amount the difference in value of the modified payment.
    public void modifyPayment(Payment payment, double valueDiff, GoalAdapter adapter) {
        if( (valueDiff < NumberUtils.ZERO_DOUBLE) && (valueDiff > -NumberUtils.ZERO_DOUBLE) ) { return; }
        if (!history.hasTransaction(payment)) { return; }

        history.modifyTransaction(payment);
        spent += valueDiff;

        resolveOverflow(adapter); // Spent changes, there might be overflow to be handled.
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
        boolean changeFlexibility = this.hasFlexibleGoal != hasFlexibleGoal;

        if(changeSpent) {
            this.spent = spent;
        }
        if(changeGoal) {
            this.goal = goal;
        }
        this.hasFlexibleGoal = hasFlexibleGoal;

        if (changeSpent || changeGoal || changeFlexibility) {
            resolveOverflow(adapter);
        }
    }

    /**
     * Resets state relating to progress:
     * Amount spent and transactions history.
     * If goalPassed > 0, there will be a negative overflow which should be handled.
     * After handling, goalPassed will also be reset.
     * @param adapter The goal adapter that would handle the overflow.
     */
    public void reset(GoalAdapter adapter) {
        this.spent = 0.0;
        history.reset();

        resolveOverflow(adapter); // Progress changes, there might be overflow to be handled.
    }

    /**
     * Resets the entire state.
     * Doesn't account for overflow.
     * To be used when resetting all categories of a tracker.
     * @param adapter The goal adapter that would handle the overflow
     */
    public void fullReset(GoalAdapter adapter) {
        reset(adapter);
        goalModifier = 0.0;
        goalPassed = 0.0;
    }

    /**
     * Checks if this category has an overflow.
     * If true, solicits overflow handling from goal adapter.
     * @param adapter The goal adapter that will handle the overflow.
     */
    private void resolveOverflow(GoalAdapter adapter) {
        if(adapter == null) { return; }

        if (hasOverflow()) {
            adapter.handleOverflow(this);
        }
    }

    // To be used in case the goal is passed for another category (it overflows).
    // In which case, a request can be made to a flexible object of this class to modify its end goal.
    //
    // In case goal or spent amount changes, goal might need to be reset or adjusted by increasing it (decreasing goalModifier).
    // In which case, the same request can be made to a modified object of this class.
    // Parameter should be negative based on a negative overflow.
    //
    // Add @modifyRequest to this.goalModifier.
    public boolean modifyGoal(double modifyRequest) {
        if(!hasFlexibleGoal) {
            return false;
        }

        // If goal for this object was passed and trying to increase goal modifier, stop method.
        // Goal modifier can be decreased.
        if(goalPassed > NumberUtils.ZERO_DOUBLE && modifyRequest > NumberUtils.ZERO_DOUBLE) {
            return false;
        }

        goalModifier += modifyRequest;
        return true;
    }

    /**
     * The amount that has to be handled by other categories.
     * If >0.0 a modify goal request should be made to other categories that can help.
     * If 0.0, there's no overflow to handle.
     * If <0.0 a modify goal request should be made to other modified categories.
     * @param shouldUpdateState If false, we are just checking, the state of this category will not be affected.
     * @return The overflow that has to be handled.
     */
    private double getOverflow(boolean shouldUpdateState) {
        double goal = hasFlexibleGoal ? getEndGoal() : this.goal;
        // The difference between the old amount spent over the goal and the current one is the overflow.
        double goalPassed = Math.max(spent - goal, 0.0); // The real amount spent over the goal. Should not be negative.
        double origGoalPassed = this.goalPassed;  // Original stored amount spent over the goal.
        if(shouldUpdateState) { this.goalPassed = goalPassed; } // Update amount spent over the goal with the real value.

        double overflow = goalPassed - origGoalPassed;
        // If goal is not flexible and goal modifier is different than 0,
        // We reset goalModifier and account for it in the overflow.
        if(!hasFlexibleGoal) {
            overflow -= goalModifier;
            if(shouldUpdateState) { goalModifier = 0.0; }
        }

        return overflow;
    }

    // Default getOverflow() method to be used, overflow should be handled.
    public double getOverflow() {
        return getOverflow(true);
    }

    // Checks if there is unhandled overflow.
    public boolean hasOverflow() {
        double overflow = getOverflow(false);
        return (overflow > NumberUtils.ZERO_DOUBLE) || (overflow < NumberUtils.ZERO_DOUBLE);
    }

    // Goal after goal modification.
    public double getEndGoal() {
        return goal - goalModifier;
    }

    // End goal to be displayed after modified by this.goalModifier.
    // Returns the end goal and specifies how the goal was modified in case this.goalModifier > 0;
    // Returns a String in format gg.gg (-mm.mm)
    // Returns the progress - a comparison between @totalSpent and end goal.
    public String getEndGoalString() {
        double endGoalTwoDecimals = NumberUtils.twoDecimals(getEndGoal());

        if(goalModifier <= NumberUtils.ZERO_DOUBLE) {
            return String.valueOf(endGoalTwoDecimals);
        }

        String modifierString = " (-" + NumberUtils.twoDecimals(goalModifier) + ")";
        return "" + endGoalTwoDecimals + modifierString;
    }

    @Override
    public String getInfoAboutProgress() {
        return getName();
    }

    // Returns a String in format "tt.tt / gg.gg"
    // If goal has been modified, format will be "tt.tt / gg.gg (-mm.mm)"
    @Override
    public String getProgress() {
        return NumberUtils.twoDecimals(spent) + " / " + getEndGoalString();
    }

    public String getName() {
        return name;
    }

    public History getHistory() {
        return history;
    }

    public void setName(String name) {
        this.name = name;
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
        this.goalModifier = goalModifier;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGoalPassed(double goalPassed) {
        this.goalPassed = goalPassed;
    }

    public void setHasFlexibleGoal(boolean hasFlexibleGoal) {
        this.hasFlexibleGoal = hasFlexibleGoal;
    }

    public int getId() {
        return id;
    }

    public double getGoalPassed() {
        return goalPassed;
    }

    public boolean isHasFlexibleGoal() {
        return hasFlexibleGoal;
    }

    // If goal is inherently flexible and if it hasn't been passed.
    // To be used when modifying goal by decreasing it.
    public boolean isFlexible() {
        return hasFlexibleGoal;
    }

    /**
     * Change whether this category's goal is flexible or not.
     */
    public void modifyFlexibility() {
        this.hasFlexibleGoal = !this.hasFlexibleGoal;
    }

    // If category can help with overflow when another category passes its goal.
    // If goal is flexible and if it hasn't been passed.
    // To be used when increasing goal modifier.
    public boolean canHelp() {
        return hasFlexibleGoal && (goalPassed <= NumberUtils.ZERO_DOUBLE);
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

    public void setFlexibility(boolean hasFlexibleGoal) {
        this.hasFlexibleGoal = hasFlexibleGoal;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public void dispose() {
        name = null;
        if(history != null) {
            history.dispose();
            history = null;
        }
    }
}
