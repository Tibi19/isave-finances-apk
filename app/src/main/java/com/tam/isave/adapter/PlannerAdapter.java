package com.tam.isave.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.databinding.RecyclerPlannerRowBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.utils.EditTextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.PlanCategoryHolder>{

    private List<String> categoriesNames;
    private List<PlanCategoryHolder> plannerHolders;
    private Consumer<Double> remainingAmountUpdater;

    public PlannerAdapter(List<Category> categories) {
        plannerHolders = new ArrayList<>();
        categoriesNames = new ArrayList<>();
        for(Category category : categories) { categoriesNames.add(category.getName()); }
    }

    public static class PlanCategoryHolder extends RecyclerView.ViewHolder {
        RecyclerPlannerRowBinding binding;
        public PlanCategoryHolder(@NonNull RecyclerPlannerRowBinding plannerRowBinding) {
            super(plannerRowBinding.getRoot());
            binding = plannerRowBinding;
        }
    }

    @NonNull
    @Override
    public PlanCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerPlannerRowBinding plannerRowBinding = RecyclerPlannerRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        PlanCategoryHolder planCategoryHolder = new PlanCategoryHolder(plannerRowBinding);
        plannerHolders.add(planCategoryHolder);

        return planCategoryHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlanCategoryHolder holder, int position) {
        if(categoriesNames == null || categoriesNames.isEmpty()) {
            holder.binding.tvPlanCategoryName.setText("Error");
            return;
        }

        String categoryName = categoriesNames.get(position);
        holder.binding.tvPlanCategoryName.setText(categoryName);
        EditTextUtils.setOnTextChangedListener(
                holder.binding.etPlanCategoryBudget,
                () -> {
                    if(remainingAmountUpdater == null) { return; }
                    double totalOfPlannedBudgets = getTotalOfPlannedBudgets();
                    remainingAmountUpdater.accept(totalOfPlannedBudgets);
                }
        );
    }

    public double getTotalOfPlannedBudgets() {
        double total = 0.0;
        for(PlanCategoryHolder plannerHolder : plannerHolders) {
            String plannedBudgetString = plannerHolder.binding.etPlanCategoryBudget.getText().toString();
            total += plannedBudgetString.isEmpty() ? 0.0 : Double.parseDouble(plannedBudgetString);
        }
        return total;
    }

    public void setRemainingAmountUpdater(Consumer<Double> callback) { remainingAmountUpdater = callback; }

    @Override
    public int getItemCount() {
        if(categoriesNames != null && !categoriesNames.isEmpty()) {
            return categoriesNames.size();
        }
        return 0;
    }

    public double getCategoryPlannedBudget(String categoryName) {
        int position = -1;
        for(String categoryNameElement : categoriesNames) {
            if( categoryName.equalsIgnoreCase(categoryNameElement) ) {
                position = categoriesNames.indexOf(categoryNameElement);
                break;
            }
        }

        if(position < 0) { return 0.0; }

        PlanCategoryHolder holder = plannerHolders.get(position);
        String plannedBudgetString = holder.binding.etPlanCategoryBudget.getText().toString();

        return plannedBudgetString.isEmpty() ? -1.0 : Double.parseDouble(plannedBudgetString);
    }

}
