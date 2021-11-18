package com.tam.isave.view.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.adapter.HistoryAdapter;
import com.tam.isave.databinding.FragmentHistoryBinding;
import com.tam.isave.databinding.PopupEditPaymentBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.model.category.CategoryUtils;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.HistoryIdentifier;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.view.dialog.CategorySpinnerPicker;
import com.tam.isave.view.dialog.EditTextDatePicker;
import com.tam.isave.viewmodel.CategoryViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

import java.util.List;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHistoryIdentifier();
    }

    private void setupHistoryIdentifier() {
        Bundle args = getArguments();
        if (args == null) { return; }

        String historyType = args.getString(Constants.KEY_HISTORY_TYPE);
        switch(Objects.requireNonNull(historyType)) {
            case HistoryIdentifier.HISTORY_TYPE_GLOBAL:
                this.historyIdentifier = new HistoryIdentifier();
                break;
            case HistoryIdentifier.HISTORY_TYPE_CATEGORY:
                int categoryId = args.getInt(Constants.KEY_CATEGORY_ID);
                this.historyIdentifier = new HistoryIdentifier(categoryId);
                break;
            case HistoryIdentifier.HISTORY_TYPE_INTERVAL:
                int startDateValue = args.getInt(Constants.KEY_START_DATE_VALUE);
                int endDateValue = args.getInt(Constants.KEY_END_DATE_VALUE);
                this.historyIdentifier = new HistoryIdentifier(startDateValue, endDateValue);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);

        if(historyIdentifier.getHistoryType().equals(HistoryIdentifier.HISTORY_TYPE_INTERVAL)) {
            binding.recyclerHistory.setNestedScrollingEnabled(false);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTransactionViewModel();
        setupHistoryAdapter();
    }

    private void setupHistoryAdapter() {
        // Instantiate recyclerview and its adapter.
        RecyclerView historyRecycler = binding.recyclerHistory;
        historyAdapter = new HistoryAdapter(getContext());
        // Give adapter the methods for editing and deleting transaction data.
        historyAdapter.setDeleteItemData( (transaction) -> transactionViewModel.deletePayment(transaction) );
        historyAdapter.setEditItemData( (transaction) -> showEditTransactionPopup(transaction) );
        // Set recycler's adapter.
        historyRecycler.setAdapter(historyAdapter);
        // Set recycler's layout manager.
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showEditTransactionPopup(Transaction transaction) {
        AlertDialog editTransactionDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        PopupEditPaymentBinding editPaymentBinding = PopupEditPaymentBinding.inflate(getLayoutInflater());
        List<Category> categories = new ViewModelProvider(this).get(CategoryViewModel.class).getCategories().getValue();
        double absTransactionValue = Math.abs( NumberUtils.twoDecimalsRounded(transaction.getValue()) );

        builder.setView(editPaymentBinding.getRoot());
        editTransactionDialog = builder.create();
        editTransactionDialog.setCancelable(true);
        editTransactionDialog.getWindow().setGravity(Gravity.BOTTOM);

        EditTextDatePicker.build(getActivity(), editPaymentBinding.etEditPaymentDate, transaction.getDate());
        CategorySpinnerPicker.build(getActivity(), editPaymentBinding.spinEditPaymentCategories, categories,
                CategoryUtils.getCategoryById(categories, transaction.getParentId()) );

        editPaymentBinding.etEditPaymentName.setText(transaction.getName());
        editPaymentBinding.etEditPaymentValue.setText(String.valueOf(absTransactionValue));
        editPaymentBinding.buttonEditPaymentyCancel.setOnClickListener(listener -> editTransactionDialog.dismiss());
        editPaymentBinding.buttonEditPaymentSubmit.setOnClickListener(listener -> {
            transactionViewModel.editPayment(transaction, editPaymentBinding, categories);
            editTransactionDialog.dismiss();
        });

        editTransactionDialog.show();
    }

    private void setupTransactionViewModel() {
        if(historyIdentifier == null) { return; }

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
