package com.tam.isave.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.R;
import com.tam.isave.databinding.RecyclerHistoryRowBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryUtils;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.LiveDataUtils;
import com.tam.isave.utils.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder>{

    private Context context;
    private HashMap<Integer, String> categoriesIdToNameMap;
    private List<Transaction> transactions;
    private Consumer<Transaction> deleteItemData;
    private Consumer<Transaction> editItemData;

    public HistoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categoriesIdToNameMap = CategoryUtils.getCategoriesIdToNameMap(categories);
    }

    public static class HistoryHolder extends RecyclerView.ViewHolder{

        RecyclerHistoryRowBinding binding;

        public HistoryHolder(@NonNull RecyclerHistoryRowBinding historyRowBinding) {
            super(historyRowBinding.getRoot());
            binding = historyRowBinding;
        }
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerHistoryRowBinding historyRowBinding = RecyclerHistoryRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new HistoryHolder(historyRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        if (transactions == null) {
            holder.binding.textTransactionName.setText(R.string.error);
            return;
        }

        Transaction transaction = transactions.get(position);

        holder.binding.textTransactionName.setText(transaction.getName());

        String transactionValueString = String.valueOf(NumberUtils.twoDecimalsRounded(transaction.getValue()));
        holder.binding.textTransactionValue.setText(transactionValueString);

        holder.binding.textTransactionDate.setText(transaction.getDate().getFormatDDMMMYY());

        if(categoriesIdToNameMap != null) {
            String categoryName = categoriesIdToNameMap.getOrDefault(
                    transaction.getParentId(),
                    Constants.NAMING_NO_CATEGORY
            );
            holder.binding.textTransactionCategory.setText(categoryName);
        }

        holder.binding.btnMenu.setOnClickListener(listener -> setOptionsMenu(holder.binding, position));

        removeLastDelimiter(holder.binding, position);
    }

    private void removeLastDelimiter(RecyclerHistoryRowBinding binding, int position) {
        if( position != (transactions.size() - 1) ) { return; }

        View delimiter = binding.transactionDelimiter;
        delimiter.setAlpha(0.0f);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) delimiter.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        delimiter.setLayoutParams(layoutParams);

        adjustBottomMargins(binding.layoutTransactionDetails);
    }

    private void adjustBottomMargins(View bottomLayout) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottomLayout.getLayoutParams();
        int originalStartMargin = layoutParams.getMarginStart();
        layoutParams.setMargins(0, 0, 0, 0);
        layoutParams.setMarginStart(originalStartMargin);
        bottomLayout.setLayoutParams(layoutParams);
    }

    private void setOptionsMenu(RecyclerHistoryRowBinding binding, int position) {
        PopupMenu menu = new PopupMenu(context, binding.btnMenu);
        menu.inflate(R.menu.transaction_menu);
        menu.setOnMenuItemClickListener(listener -> {
            switch(listener.getItemId()) {
                case R.id.action_transaction_edit:
                    editItemData.accept(transactions.get(position));
                    return true;
                case R.id.action_transaction_delete:
                    deleteItemData.accept(transactions.get(position));
                    return true;
                default: return false;
            }
        });
        menu.show();
    }

    public void setDeleteItemData(Consumer<Transaction> callback) {
        deleteItemData = callback;
    }
    public void setEditItemData(Consumer<Transaction> callback) { editItemData = callback; }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (transactions != null) {
            return transactions.size();
        }
        return 0;
    }
}
