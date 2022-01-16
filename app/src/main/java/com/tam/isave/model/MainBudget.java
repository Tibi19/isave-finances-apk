package com.tam.isave.model;

import com.tam.isave.model.transaction.Payment;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.NumberUtils;

public class MainBudget {

    private double budget;
    private double spent;
    private boolean isHidden;

    public MainBudget(double budget, double spent, boolean isHidden) {
        this.budget = budget;
        this.spent = spent;
        this.isHidden = isHidden;
    }

    public void makePayment(Transaction payment) { spent += Math.abs( payment.getValue() ); }
    public void removePayment(Transaction payment) { spent -= Math.abs( payment.getValue() ); }

    public void addToBudget(double value) { budget += value; }

    public void modify(double newBudget, double newSpent, boolean newIsHidden) {
        this.budget = newBudget;
        this.spent = newSpent;
        this.isHidden = newIsHidden;
    }

    public double getBalance() { return NumberUtils.twoDecimalsRounded(budget - spent); }

    public double getBudgetFormatted() { return NumberUtils.twoDecimalsRounded(budget); }
    public double getSpentFormatted() { return NumberUtils.twoDecimalsRounded(spent); }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public double getSpent() { return spent; }
    public void setSpent(double spent) { this.spent = spent; }

    public boolean isHidden() { return isHidden; }
    public void setHidden(boolean hidden) { isHidden = hidden; }
}
