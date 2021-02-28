package com.tam.isave.model.transaction;

import com.tam.isave.model.category.Category;
import com.tam.isave.utils.Date;

public class Payment extends Transaction {

    public Payment(String name, Date date, double value, int parentId) {
        super(name, value * -1.0, date, parentId);
    }

    // Modifies parent category if it's different and calls the super class method.
    public double modify(String newName, double newValue, Date newDate, int newParentId) {
        return super.modify(newName, -newValue, newDate, newParentId);
    }

    public double getAbsValue() { return Math.abs(getValue()); }

}
