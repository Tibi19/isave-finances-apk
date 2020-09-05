package com.tam.isave;

public class Cashing extends Transaction {

    private boolean isSalary;

    public Cashing(String name, Date date, double value, boolean isSalary) {
        super(name, value, date);
        this.isSalary = isSalary;
    }

    public boolean isSalary() {
        return isSalary;
    }

    public void setSalary(boolean salary) {
        isSalary = salary;
    }
}
