package com.tam.isave.model.transaction;

import com.tam.isave.utils.Date;

public class Cashing extends Transaction {

    public Cashing(String name, Date date, double value, boolean organizable) {
        super(name, value, date, organizable);
    }

}
