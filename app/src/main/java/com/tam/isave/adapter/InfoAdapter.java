package com.tam.isave.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.databinding.RecyclerHistoryRowBinding;
import com.tam.isave.databinding.RecyclerInfoRowBinding;
import com.tam.isave.model.info.AppInformation;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoHolder> {

    private Context context;
    private List<AppInformation> informationList;

    public InfoAdapter(Context context, List<AppInformation> informationList) {
        this.context = context;
        this.informationList = informationList;
    }

    public static class InfoHolder extends RecyclerView.ViewHolder {

        RecyclerInfoRowBinding binding;

        public InfoHolder(@NonNull RecyclerInfoRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerInfoRowBinding infoRowBinding = RecyclerInfoRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new InfoHolder(infoRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoHolder holder, int position) {
        AppInformation info = informationList.get(position);

        holder.binding.textInfoTitle.setText(info.getTitle());
        holder.binding.textInfoBody.setText(info.getDescription());

        removeLastDelimiter(holder.binding, position);
    }

    private void removeLastDelimiter(RecyclerInfoRowBinding binding, int position) {
        if( position != (informationList.size() - 1) ) { return; }

        View delimiter = binding.infoDelimiter;
        delimiter.setAlpha(0.0f);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) delimiter.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        delimiter.setLayoutParams(layoutParams);

        adjustBottomMargins(binding.textInfoBody);
    }

    private void adjustBottomMargins(View bottomLayout) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottomLayout.getLayoutParams();
        int originalStartMargin = layoutParams.getMarginStart();
        layoutParams.setMargins(0, 0, 0, 0);
        layoutParams.setMarginStart(originalStartMargin);
        bottomLayout.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        if (informationList != null) { return informationList.size(); }
        return 0;
    }

}
