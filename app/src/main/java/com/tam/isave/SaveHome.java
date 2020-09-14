package com.tam.isave;

// TODO
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
// Do SaveHome
// Clean History for overall history and categories' history
// Go on to Activities

import java.util.ArrayList;

public class SaveHome {

    private double balance;
    GoalOrganizer organizer;
    CategoryTracker tracker;

    public SaveHome() {
        setupOrganizer();
        setupTracker();
    }

    // Sets up goal organizer.
    private void setupOrganizer() {
        organizer = new GoalOrganizer();
    }

    // Sets up category tracker.
    private void setupTracker() {
        ArrayList<Category> categories = new ArrayList<>();
        History history = new History();
        tracker = new CategoryTracker(categories, history);
    }

    // Creates a new payment based on params.
    // Makes payment in tracker.
    // Makes payment in organizer if it's organizable.
    public void newPayment(Date date, String name, double value, Category parentCategory, boolean organizable) {
        if( (date == null) || (name == null) || (parentCategory == null) ) { return; }
        if(value <= Utils.ZERO_DOUBLE) { return; }

        Payment payment = new Payment(name, date, value, parentCategory);
        balance = payment.makeTransaction(balance); // Update balance.
        tracker.makePayment(payment);

        if(organizable) {
            organizer.makePayment(payment);
        }
    }

}
