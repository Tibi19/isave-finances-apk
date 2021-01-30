package com.tam.isave.model.TransactionTools;

import com.tam.isave.utils.Date;
import com.tam.isave.utils.Utils;

public class Transaction {

    private String name;
    private double value;
    private Date date;

    public Transaction(String name, double value, Date date) {
        this.name = name;
        this.value = value;
        this.date = date;
    }

    // Makes a transaction, adds to @balance and returns the new balance;
    public double makeTransaction(double balance) { return balance + value; }

    // Removes transaction, removes from @balance and returns the new balance;
    public double removeTransaction(double balance) { return balance - value; }

    // Modifies transaction and returns the absolute difference between the old value and the new value.
    // When this method is called, the return value should be added to a spent variable.
    public double modify(String newName, double newValue, Date newDate) {
        if( (newName != null) && !name.equalsIgnoreCase(newName)) { name = newName; }
        if( (newDate != null) && (date.getValue() != newDate.getValue()) ) { date = newDate; }

        if(!Utils.isSameDoubles(value, newValue)) {
            double origValue = value;
            value = newValue;
            return Math.abs(origValue) - Math.abs(newValue);
        }

        return 0.0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void dispose() {
        name = null;
        if(date != null) {
            date.dispose();
            date = null;
        }
    }
}
