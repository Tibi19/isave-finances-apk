package com.tam.isave.data;

import android.content.Context;
import android.os.Environment;

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
        String storagePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        String transactionsFilePrefix = Constants.EXPORT_FILE_NAME_START + Date.today().getValue() + "_";
        String transactionsFileSuffix = ".csv";

        File transactionsCSV = File.createTempFile(
                transactionsFilePrefix,
                transactionsFileSuffix,
                new File(storagePath)
        );

        FileWriter writer = new FileWriter(transactionsCSV);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        bufferedWriter.write(Constants.EXPORT_HEADER);

        transactions.forEach( transaction -> writeTransactionToBuffer(bufferedWriter, transaction, categories) );

        bufferedWriter.close();

        return transactionsCSV;
    }

    private void writeTransactionToBuffer(BufferedWriter bufferedWriter, Transaction transaction, List<Category> categories) {
        String name = transaction.getName();
        String value = String.valueOf( Math.abs(transaction.getValue()) );
        String date = transaction.getDate().toString();
        Category categoryObject = CategoryUtils.getCategoryById(categories, transaction.getParentId());
        String category = categoryObject != null ? categoryObject.getName() : Constants.NAMING_NO_CATEGORY;

        String transactionLine = String.join(
                ",",
                name,
                value,
                date,
                category,
                "\n"
        );

        try { bufferedWriter.write(transactionLine); }
        catch (IOException e) { ErrorBuilder.exportWriteException(context); }
    }

}
