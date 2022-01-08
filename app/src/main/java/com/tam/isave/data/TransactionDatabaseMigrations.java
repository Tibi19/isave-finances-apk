package com.tam.isave.data;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.tam.isave.utils.Constants;

public class TransactionDatabaseMigrations extends Migration {

    private static final int VERSION_BEFORE_ORGANIZABLE_COLUMN = 2;
    private static final int VERSION_ADDED_ORGANIZABLE_COLUMN = 3;
    public static Migration getAddOrganizableColumnMigration() {
        return new TransactionDatabaseMigrations(VERSION_BEFORE_ORGANIZABLE_COLUMN, VERSION_ADDED_ORGANIZABLE_COLUMN);
    }

    private int startVersion;
    private int endVersion;

    public TransactionDatabaseMigrations(int startVersion, int endVersion) {
        super(startVersion, endVersion);
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        if(startVersion == VERSION_BEFORE_ORGANIZABLE_COLUMN && endVersion == VERSION_ADDED_ORGANIZABLE_COLUMN) {
            addOrganizableColumn(database);
        }
    }

    private void addOrganizableColumn(SupportSQLiteDatabase database) {
        String sql = "ALTER TABLE " +
                Constants.TABLE_NAME_TRANSACTION +
                " ADD COLUMN " +
                Constants.TRANSACTION_COLUMN_ORGANIZABLE +
                " INTEGER NOT NULL DEFAULT 1";
        database.execSQL(sql);
    }
}
