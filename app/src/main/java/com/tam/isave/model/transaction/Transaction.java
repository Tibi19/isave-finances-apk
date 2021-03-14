package com.tam.isave.model.transaction;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.tam.isave.model.category.Category;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.NumberUtils;

@Entity(tableName = Constants.TABLE_NAME_TRANSACTION)
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = Constants.TRANSACTION_COLUMN_NAME)
    private String name;
    @ColumnInfo(name = Constants.TRANSACTION_COLUMN_VALUE)
    private double value;
    @Ignore
    private Date date;

    @ColumnInfo(name = Constants.TRANSACTION_COLUMN_DATE_VALUE)
    private int dateValue; // The int representation of this object's date class.
    @ColumnInfo(name = Constants.TRANSACTION_COLUMN_PARENT_ID)
    private int parentId; // The id of a parent category.

    // Database constructor.
    public Transaction() {
        this.date = new Date(dateValue);
    }

    // Constructor for transactions belonging to a category.
    public Transaction(String name, double value, Date date, int parentId) {
        this.name = name;
        this.value = value;
        this.date = date;
        this.parentId = parentId;
        dateValue = this.date.getValue();
    }

    // Constructor for transactions who do not have a parent category.
    public Transaction(String name, double value, Date date) {
        this(name, value, date, -1);
    }

    // Makes a transaction, adds to @balance and returns the new balance;
    public double makeTransaction(double balance) { return balance + value; }

    // Removes transaction, removes from @balance and returns the new balance;
    public double removeTransaction(double balance) { return balance - value; }

    /**
     * Modifies transaction and returns the absolute difference between the old value and the new value.
     * The return value should be added to a variable tracking the budget.
     * @param newName The new name.
     * @param newValue The new value.
     * @param newDate The new date.
     * @param newParentId The id of a new parent category.
     * @return A value representing the difference in money spent after modification.
     */
    public double modify(String newName, double newValue, Date newDate, int newParentId) {
        if( (newName != null) && !name.equalsIgnoreCase(newName)) { name = newName; }
        if( (newDate != null) && (date.getValue() != newDate.getValue()) ) { date = newDate; }
        if(newParentId != parentId) { parentId = newParentId; }

        if(!NumberUtils.isSameDoubles(value, newValue)) {
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

    public int getDateValue() {
        return dateValue;
    }

    public void setDateValue(int dateValue) {
        this.dateValue = dateValue;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
