package com.tam.isave.data;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryUtils;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.Date;
import com.tam.isave.view.dialog.ErrorBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataConverter {

    Context context;

    public DataConverter(Context context) {
        this.context = context;
    }

    public File getTransactionsCSV(List<Transaction> transactions, List<Category> categories) throws IOException {
        String storagePath = getStoragePath();
        String csvFilePath = getCsvFilePath(storagePath);
        File transactionsCSV = new File(csvFilePath);
        transactionsCSV.createNewFile();

        FileWriter writer = new FileWriter(transactionsCSV);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        transactions.forEach( transaction -> writeTransactionToBuffer(bufferedWriter, transaction, categories) );

        return transactionsCSV;
    }

    private void writeTransactionToBuffer(BufferedWriter bufferedWriter, Transaction transaction, List<Category> categories) {
        String name = transaction.getName();
        String value = String.valueOf(transaction.getValue());
        String date = transaction.getDate().toString();
        Category categoryObject = CategoryUtils.getCategoryById(categories, transaction.getParentId());
        String category = categoryObject != null ? categoryObject.getName() : Constants.NAMING_NO_CATEGORY;

        String transactionLine = String.join(
                ",",
                name,
                value,
                date,
                category
        );

        try { bufferedWriter.write(transactionLine); }
        catch (IOException e) { ErrorBuilder.exportWriteException(context); }
    }

    private String getCsvFilePath(String storagePath) {
        String fileName = Constants.EXPORT_FILE_NAME_START + Date.today().getValue() + ".csv";
        return storagePath + File.separator + fileName;
    }

    private String getStoragePath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            return storageManager.getPrimaryStorageVolume().getDirectory().getAbsolutePath();
        }

        // storagePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
