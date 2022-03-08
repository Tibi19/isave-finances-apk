package com.tam.isave.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.tam.isave.BuildConfig;
import com.tam.isave.data.DataConverter;
import com.tam.isave.databinding.ActivityGlobalHistoryBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.HistoryIdentifier;
import com.tam.isave.utils.LiveDataUtils;
import com.tam.isave.view.dialog.ErrorBuilder;
import com.tam.isave.view.fragment.HistoryFragment;
import com.tam.isave.viewmodel.CategoryViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
                                transactions -> exportWithIntent(transactions, categories)
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

        Uri transactionsUri = FileProvider.getUriForFile(
                getApplicationContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                transactionsCSV
        );

        shareIntent.setType("text/csv");
        shareIntent.putExtra(Intent.EXTRA_STREAM, transactionsUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, Constants.EXPORT_INTENT_TITLE));
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
