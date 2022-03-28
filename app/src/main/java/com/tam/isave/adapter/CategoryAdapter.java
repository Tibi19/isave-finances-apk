package com.tam.isave.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.R;
import com.tam.isave.databinding.RecyclerCategoryRowBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.utils.ButtonAreaExtensionUtils;
import com.tam.isave.utils.Constants;
import com.tam.isave.view.activity.CategoryHistoryActivity;

import java.util.List;
import java.util.function.Consumer;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private Context context;
    private List<Category> categories;
    private Consumer<Category> deleteItemData;
    private Consumer<Category> editItemData;
    private Consumer<Category> resetItemData;

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
        holder.binding.btnMenu.setOnClickListener( listener -> setOptionsMenu(holder.binding, position));

        ButtonAreaExtensionUtils.extendHitAreaOfButtons(context, holder.binding.btnMenu);
    }

    private void setOptionsMenu(RecyclerCategoryRowBinding binding, int position) {
        PopupMenu menu = new PopupMenu(context, binding.btnMenu);
        menu.inflate(R.menu.category_menu);
        menu.setOnMenuItemClickListener(listener -> {
            switch(listener.getItemId()) {
                case R.id.action_history:
                    startHistoryActivity(categories.get(position));
                    return true;
                case R.id.action_edit:
                    editItemData.accept(categories.get(position));
                    return true;
                case R.id.action_delete:
                    deleteItemData.accept(categories.get(position));
                    return true;
                case R.id.action_reset:
                    resetItemData.accept(categories.get(position));
                    return true;
                default: return false;
            }
        });
        menu.show();
    }

    private void startHistoryActivity(Category category) {
        Intent startCategoryHistoryIntent = new Intent(context, CategoryHistoryActivity.class);
        startCategoryHistoryIntent.putExtra(Constants.KEY_CATEGORY_ID, category.getId());
        startCategoryHistoryIntent.putExtra(Constants.KEY_CATEGORY_NAME, category.getName());
        context.startActivity(startCategoryHistoryIntent);
    }

    public void setEditItemData(Consumer<Category> callback) {
        editItemData = callback;
    }
    public void setDeleteItemData(Consumer<Category> callback) {
        deleteItemData = callback;
    }
    public void setResetItemData(Consumer<Category> callback) { resetItemData = callback; }

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
