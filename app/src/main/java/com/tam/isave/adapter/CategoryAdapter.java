package com.tam.isave.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.R;
import com.tam.isave.model.category.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_category_row, parent, false);

        return new CategoryHolder(categoryView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        if (categories == null) {
            holder.nameView.setText(R.string.error);
            return;
        }

        Category category = categories.get(position);
        holder.nameView.setText(category.getName());
        holder.progressView.setText(category.getProgress());
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (categories != null)
            return categories.size();
        return 0;
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameView;
        public TextView progressView;

        public Button historyButton;
        public Button editButton;
        public Button resetButton;
        public Button deleteButton;

        public CategoryHolder(@NonNull View categoryView) {
            super(categoryView);

            // Initialize category description views.
            nameView = categoryView.findViewById(R.id.text_category_name);
            progressView = categoryView.findViewById(R.id.text_category_progress);
            // Initialize category controller views.
            historyButton = categoryView.findViewById(R.id.button_category_history);
            editButton = categoryView.findViewById(R.id.button_category_edit);
            resetButton = categoryView.findViewById(R.id.button_category_reset);
            deleteButton = categoryView.findViewById(R.id.button_category_delete);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_category_history:
                    break;
                case R.id.button_category_edit:
                    break;
                case R.id.button_category_reset:
                    break;
                case R.id.button_category_delete:
                    break;
            }
        }

    }
}
