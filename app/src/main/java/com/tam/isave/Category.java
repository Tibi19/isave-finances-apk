package com.tam.isave;

// Categorises payments and tracks them in a history.
// Stores how much money is spent on products/services that are part of the category.
// Has a goal and compares total spent to that goal.
//
// Goal can be decreased when other categories pass their goals (are overflowing) to help reach the saving goal of the user;
// A modify request will be made by a parent category tracker to all categories that can modify their goals. (those can help)
// When overflow is requested, the class assumes it will be handled. (a modify request will be made by the parent category tracker).
//
// As a result of goal change/modification or removal of payments, goal modification might need to be adjusted or reset (decreased).
// In this case, a negative overflow can be requested and a modify request should be made
// By the parent category tracker to all categories that have been previously modified. (handling overflow)
public class Category implements IProgressDisplayable{

    private String name;
    private double spent;
    private double goal;

    private History history;

    private double goalModifier;
    private double goalPassed;
    private boolean hasFlexibleGoal;

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

    public Category(String name, double goal) {
        this(name, goal, true);
    }

    // Adds payment to category history and adds to total spent.
    // Spent changes, there might be overflow to be handled.
    // Returns whether there is overflow.
    public boolean makePayment(Payment payment) {
        history.addTransaction(payment);
        spent += Math.abs(payment.getValue());

        return hasOverflow();
    }

    // Removes a transaction if it exists in object's history and removes from total spent.
    // Spent changes, there might be overflow to be handled.
    // Returns whether there is overflow.
    public boolean removePayment(Payment payment) {
        if (!history.hasTransaction(payment)) { return false; }

        payment.setParentCategory(null);
        spent -= payment.getAbsValue();

        return hasOverflow();
    }

    // Adds to spent amount the difference in value of the modified payment.
    // Spent changes, there might be overflow to be handled.
    // Returns whether there is overflow.
    public boolean modifyPayment(Payment payment, double valueDiff) {
        if( (valueDiff < Utils.ZERO_DOUBLE) && (valueDiff > -Utils.ZERO_DOUBLE) ) { return false; }
        if (!history.hasTransaction(payment)) { return false; }

        history.modifyTransaction(payment);
        spent += valueDiff;

        return hasOverflow();
    }

    /**
     * Modify the state of category.
     * @param name The new name of the category.
     * @param spent The new spent amount of the category.
     * @param goal The new goal of the category.
     * @param hasFlexibleGoal The new flexibility of this category's goal.
     * @return Whether there is overflow that should be handled.
     */
    @SuppressWarnings("SimplifiableConditionalExpression") // Warning suppressed for readability.
    public boolean modify(String name, double spent, double goal, boolean hasFlexibleGoal) {
        if(!this.name.equalsIgnoreCase(name)) { this.name = name; }

        boolean changeSpent = !Utils.sameDoubles(this.spent, spent);
        boolean changeGoal = !Utils.sameDoubles(this.goal, goal);
        boolean changeFlexibility = this.hasFlexibleGoal != hasFlexibleGoal;

        if(changeSpent) {
            this.spent = spent;
        }
        if(changeGoal) {
            this.goal = goal;
        }
        this.hasFlexibleGoal = hasFlexibleGoal;

        return (changeSpent || changeGoal || changeFlexibility) ? hasOverflow() : false;
    }

    // To be used in case the salary has been cashed in or progress should be reset.
    // In which case, progress will be reset for the new month.
    //
    // Resets this.totalSpent and this.goalModifier to 0.
    public void reset() {
        this.spent = 0.0;
        this.goalModifier = 0.0;
        this.goalPassed = 0.0;
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
        // It can be decreased.
        if(goalPassed > Utils.ZERO_DOUBLE && modifyRequest > Utils.ZERO_DOUBLE) {
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
        return (overflow > Utils.ZERO_DOUBLE) || (overflow < Utils.ZERO_DOUBLE);
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
        double endGoalTwoDecimals = Utils.twoDecimals(getEndGoal());

        if(goalModifier <= Utils.ZERO_DOUBLE) {
            return String.valueOf(endGoalTwoDecimals);
        }

        String modifierString = " (-" + Utils.twoDecimals(goalModifier) + ")";
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
        return Utils.twoDecimals(spent) + " / " + getEndGoalString();
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
        return hasFlexibleGoal && (goalPassed <= Utils.ZERO_DOUBLE);
    }

    // If category should adjust its goal modification
    // As a result of another category subtracting its overflow. (there is negative overflow)
    // Whether goal has been modified.
    // To be used when decreasing goal modifier.
    public boolean isModified() {
        return goalModifier > Utils.ZERO_DOUBLE;
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
