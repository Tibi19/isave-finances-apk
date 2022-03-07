package com.tam.isave.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.tam.isave.BuildConfig;
import com.tam.isave.data.DataConverter;
import com.tam.isave.databinding.ActivityGlobalHistoryBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryUtils;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.utils.HistoryIdentifier;
import com.tam.isave.utils.LiveDataUtils;
import com.tam.isave.view.dialog.ErrorBuilder;
import com.tam.isave.view.fragment.HistoryFragment;
import com.tam.isave.viewmodel.CategoryViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class GlobalHistoryActivity extends AppCompatActivity {

    private ActivityGlobalHistoryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGlobalHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupExportButton();
        startHistoryTransaction();
    }

    private void setupExportButton() {
        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        binding.btnExport.setOnClickListener(
                listener -> LiveDataUtils.observeOnce(
                        categoryViewModel.getCategories(),
                        categories -> LiveDataUtils.observeOnce(
                                transactionViewModel.getTransactions(),
                                transactions -> exportOnStoragePermissions(transactions, categories)
                        )
                )
        );
    }

    private void exportWithIntent(List<Transaction> transactions, List<Category> categories) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        DataConverter converter = new DataConverter(getApplicationContext());
        File transactionsCSV;
        try {
            transactionsCSV = converter.getTransactionsCSV(transactions, categories);
        } catch (IOException e) {
            e.printStackTrace();
            ErrorBuilder.exportFileException(getApplicationContext());
            return;
        }

        // TEST FILE CONTENTS
        try {
            Scanner fileReader = new Scanner(transactionsCSV);
            Log.d("CSVFILE", "starting loop? " + fileReader.hasNextLine());
            while(fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                Log.d("CSVFILE", line);
            }
            fileReader.close();
        } catch (IOException e) {
            Log.d("CSVFILE", "ERROR!!!!");
            e.printStackTrace();
        }

        Uri transactionsUri = FileProvider.getUriForFile(
                getApplicationContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                transactionsCSV
        );

        shareIntent.setDataAndType(transactionsUri, "text/csv");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, Constants.EXPORT_INTENT_TITLE));
    }

    private void exportOnStoragePermissions(List<Transaction> transactions, List<Category> categories) {
        Context context = getApplicationContext();

//        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
//                new ActivityResultContracts.RequestPermission(),
//                isGranted -> {
//                    if(isGranted) { exportWithIntent(); }
//                    else { ErrorBuilder.exportFileException(context); }
//                }
//        );

        if(ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            exportWithIntent(transactions, categories);
        } else {
            requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
            CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
            LiveDataUtils.observeOnce(
                    categoryViewModel.getCategories(),
                    categories -> LiveDataUtils.observeOnce(
                            transactionViewModel.getTransactions(),
                            transactions -> exportWithIntent(transactions, categories)
                    )
            );
        }
    }

    private void startHistoryTransaction() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HISTORY_TYPE, HistoryIdentifier.HISTORY_TYPE_GLOBAL);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragmentContainerHistory.getId(), HistoryFragment.class, bundle)
                .commit();
    }
}
