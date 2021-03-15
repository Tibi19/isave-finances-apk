package com.tam.isave.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.tam.isave.R;
import com.tam.isave.databinding.RecyclerCategoryRowBinding;
import com.tam.isave.model.category.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder{

        RecyclerCategoryRowBinding binding;

        public CategoryHolder(@NonNull RecyclerCategoryRowBinding categoryRowBinding) {
            super(categoryRowBinding.getRoot());
            binding = categoryRowBinding;
        }
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerCategoryRowBinding categoryRowBinding = RecyclerCategoryRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new CategoryHolder(categoryRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        if (categories == null) {
            holder.binding.textCategoryName.setText(R.string.error);
            return;
        }

        Category category = categories.get(position);
        holder.binding.textCategoryName.setText(category.getName());
        holder.binding.textCategoryProgress.setText(category.getProgress());
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (categories != null) {
            return categories.size();
        }
        return 0;
    }

}
