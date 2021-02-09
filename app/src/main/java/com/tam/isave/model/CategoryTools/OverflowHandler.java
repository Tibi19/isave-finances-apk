package com.tam.isave.model.CategoryTools;

import com.tam.isave.utils.NumberUtils;

/**
 * Handles the overflow of a parent category.
 */
public class OverflowHandler {

    private Category parentCategory;

    public OverflowHandler(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    /**
     * Checks if there is overflow.
     * @return true if there is overflow.
     */
    public boolean hasOverflow() {
        double overflow = getOverflow(false);
        return !NumberUtils.isZeroDouble(overflow);
    }

    /**
     * The amount that has to be handled by other categories.
     * If >0.0 a modify goal request should be made to other categories that can help.
     * If 0.0, there's no overflow to handle.
     * If <0.0 a modify goal request should be made to other modified categories.
     * @param shouldUpdateState If false, we are just checking, the state of this category will not be affected.
     * @return The overflow that has to be handled.
     */
    public double getOverflow(boolean shouldUpdateState) {
        boolean flexibleGoal = parentCategory.isFlexibleGoal();
        double endGoal = parentCategory.getEndGoal();
        double spent = parentCategory.getSpent();
        double goalModifier = parentCategory.getGoalModifier();

        double goal = flexibleGoal ? endGoal : parentCategory.getGoal();
        // The difference between the old amount spent over the goal and the current one is the overflow.
        double goalPassed = Math.max(spent - goal, 0.0); // The real amount spent over the goal. Should not be negative.
        double origGoalPassed = parentCategory.getGoalPassed();  // Original stored amount spent over the goal.
        if(shouldUpdateState) { parentCategory.setGoalPassed(goalPassed); } // Update amount spent over the goal with the real value.

        double overflow = goalPassed - origGoalPassed;
        // If goal is not flexible and goal modifier is different than 0,
        // We reset goalModifier and account for it in the overflow.
        if(!flexibleGoal) {
            overflow -= goalModifier;
            if(shouldUpdateState) { parentCategory.setGoalModifier(0.0); }
        }

        return overflow;
    }

    // Default getOverflow() method to be used, overflow should be handled.
    public double getOverflow() {
        return getOverflow(true);
    }

    /**
     * Checks if this category has an overflow.
     * If true, solicits overflow handling from goal adapter.
     * @param adapter The goal adapter that will handle the overflow.
     */
    public void resolveOverflow(GoalAdapter adapter) {
        if(adapter == null) { return; }

        if(hasOverflow()) {
            adapter.handleOverflow(parentCategory);
        }
    }

}
