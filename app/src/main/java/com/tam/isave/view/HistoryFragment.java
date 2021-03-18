package com.tam.isave.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.adapter.HistoryAdapter;
import com.tam.isave.databinding.FragmentHistoryBinding;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.HistoryIdentifier;
import com.tam.isave.viewmodel.TransactionViewModel;

import java.util.Objects;

/**
 * Requires a Bundle with a history type.
 * If history type is a category history, the bundle needs a category id.
 * If history type is an interval history, the bundle needs a start date value and an end date value.
 */
public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryAdapter historyAdapter;
    private TransactionViewModel transactionViewModel;
    private HistoryIdentifier historyIdentifier;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHistoryIdentifier(savedInstanceState);
        setupHistoryAdapter();
        setupTransactionViewModel();
    }

    private void setupHistoryIdentifier(Bundle savedInstanceState) {
        if (savedInstanceState == null) { return; }

        String historyType = savedInstanceState.getString(Constants.KEY_HISTORY_TYPE);
        switch(Objects.requireNonNull(historyType)) {
            case HistoryIdentifier.HISTORY_TYPE_GLOBAL:
                this.historyIdentifier = new HistoryIdentifier();
                break;
            case HistoryIdentifier.HISTORY_TYPE_CATEGORY:
                int categoryId = savedInstanceState.getInt(Constants.KEY_CATEGORY_ID);
                this.historyIdentifier = new HistoryIdentifier(categoryId);
                break;
            case HistoryIdentifier.HISTORY_TYPE_INTERVAL:
                int startDateValue = savedInstanceState.getInt(Constants.KEY_START_DATE_VALUE);
                int endDateValue = savedInstanceState.getInt(Constants.KEY_END_DATE_VALUE);
                this.historyIdentifier = new HistoryIdentifier(startDateValue, endDateValue);
                break;
        }
    }

    private void setupHistoryAdapter() {
        // Instantiate recyclerview and its adapter.
        RecyclerView historyRecycler = binding.recyclerHistory;
        historyAdapter = new HistoryAdapter(getContext());
        // Set recycler's adapter.
        historyRecycler.setAdapter(historyAdapter);
        // Set recycler's layout manager.
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupTransactionViewModel() {
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        // Setup Observer depending on history type.
        switch(historyIdentifier.getHistoryType()) {
            case HistoryIdentifier.HISTORY_TYPE_GLOBAL:
                transactionViewModel
                        .getTransactions()
                        .observe(getViewLifecycleOwner(), transactions -> historyAdapter.setTransactions(transactions));
                break;
            case HistoryIdentifier.HISTORY_TYPE_CATEGORY:
                transactionViewModel
                        .getCategoryTransactions(historyIdentifier.getCategoryId())
                        .observe(getViewLifecycleOwner(), transactions -> historyAdapter.setTransactions(transactions));
                break;
            case HistoryIdentifier.HISTORY_TYPE_INTERVAL:
                transactionViewModel
                        .getIntervalTransactions(historyIdentifier.getStartDateValue(), historyIdentifier.getEndDateValue())
                        .observe(getViewLifecycleOwner(), transactions -> historyAdapter.setTransactions(transactions));
        }
    }
}
