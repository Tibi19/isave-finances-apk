package com.tam.isave.model.transaction;

import com.tam.isave.model.category.Category;
import com.tam.isave.utils.Date;

public class Payment extends Transaction {

    private Category parentCategory;

    public Payment(String name, Date date, double value, Category parentCategory) {
        super(name, value * -1.0, date);
        this.parentCategory = parentCategory;
    }

    // Modifies parent category if it's different and calls the super class method.
    public double modify(String newName, double newValue, Date newDate, Category newParentCategory) {
        if ( (newParentCategory != null) && !parentCategory.equals(newParentCategory)) {
            parentCategory = newParentCategory;
        }
        return super.modify(newName, -newValue, newDate);
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public double getAbsValue() { return Math.abs(getValue()); }

    public void dispose() {
        parentCategory = null;
    }
}
