package com.tam.isave.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.R;
import com.tam.isave.databinding.RecyclerHistoryRowBinding;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.NumberUtils;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder>{

    private Context context;
    private List<Transaction> transactions;

    public HistoryAdapter(Context context) {
        this.context = context;
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
        String transactionValueString = String.valueOf(NumberUtils.twoDecimals(transaction.getValue()));
        holder.binding.textTransactionValue.setText(transactionValueString);
        holder.binding.textTransactionDate.setText(transaction.getDate().toString());
    }

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
