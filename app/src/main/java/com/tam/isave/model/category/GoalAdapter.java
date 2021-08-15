package com.tam.isave.model.category;

import com.tam.isave.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

// Handles overflow by making modify requests to categories that can handle the overflow.
public class GoalAdapter {

    private List<Category> categories;

    // Whether positive overflow handling should be done only by categories that follow a solicitor.
    private boolean orderedHandling;

    public GoalAdapter(List<Category> categories, boolean orderedHandling) {
        this.categories = categories;
        this.orderedHandling = orderedHandling;
    }

    // Handles overflow of a solicitor.
    // @solicitor: category that produced the overflow and solicits handling.
    // Makes modify requests to categories that can handle the overflow.
    public void handleOverflow(Category solicitor) {
        double overflow = solicitor.getOverflow();
        if(NumberUtils.isZeroDouble(overflow)) { return; }

        ArrayList<Category> handlers = new ArrayList<Category>(); // Categories that can handle a part of the overflow.
        double goalsTotal = 0.0; // The goals total of all handlers.
        boolean isPositiveOverflow = overflow > NumberUtils.ZERO_DOUBLE;
        boolean orderedHandling = this.orderedHandling && isPositiveOverflow; // Ordered handling can be done only if overflow is positive.

        // If it's an ordered handling, iteration will start immediately after solicitor.
        int i = orderedHandling ? (categories.indexOf(solicitor) + 1) : 0;
        for( ; i < categories.size(); i++) {
            Category category = categories.get(i);
            if(category.equals(solicitor)) { continue; } // Solicitor can't handle its own request.
            if(!category.canHandleOverflow(isPositiveOverflow)) { continue; } // If category can't handle this type of overflow, continue.

            handlers.add(category);
            goalsTotal += category.getEndGoal(); // Add category's goal to the total of goals.
        }

        if(handlers.size() <= 0) { return; }
        // Handle overflow by making personalized request for each handler.
        // Request is based on handler's share in the goals total of handlers.
        for(Category handler : handlers) {
            double modifyRequest = getModifyRequest(overflow, handler.getEndGoal(), goalsTotal);
            handler.modifyGoal(modifyRequest);
        }

        // If there are new overflows as a result of the handling, start recursion.
        handleAllOverflows();
    }

    // Recursion by calling handleOverflow(), while handleOverflow() calls this method.
    // Recursion stops when no more categories that have overflow are found.
    public void handleAllOverflows() {
        for(Category category : categories) {
            if(category.hasOverflow()) {
                handleOverflow(category);
                break;
            }
        }
    }

    // The modify request to be solicited from a handler based on a rapport:
    // Between the overflow and the handler's share in a sum of goals of all handlers.
    private double getModifyRequest(double overflow, double handlerGoal, double goalsTotal) {
        return overflow * (handlerGoal / goalsTotal);
    }

}
