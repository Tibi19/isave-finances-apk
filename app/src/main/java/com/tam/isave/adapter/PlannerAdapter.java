package com.tam.isave.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.databinding.RecyclerPlannerRowBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.utils.EditTextUtils;
import com.tam.isave.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.PlanCategoryHolder>{

    private List<Category> categories;
    private List<PlanCategoryHolder> plannerHolders;
    private Consumer<Double> remainingAmountUpdater;

    public PlannerAdapter(List<Category> categories) {
        plannerHolders = new ArrayList<>();
        this.categories = categories;
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
        if(categories == null || categories.isEmpty()) {
            holder.binding.tvPlanCategoryName.setText("Error");
            return;
        }

        Category category = categories.get(position);
        holder.binding.tvPlanCategoryName.setText(category.getName());
        double categoryBudget = NumberUtils.twoDecimalsRounded(category.getGoal());
        holder.binding.etPlanCategoryBudget.setHint(String.valueOf(categoryBudget));

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
        if(categories != null && !categories.isEmpty()) {
            return categories.size();
        }
        return 0;
    }

    public double getCategoryPlannedBudget(String categoryName) {
        int position = -1;
        for(Category category : categories) {
            if( category.getName().equalsIgnoreCase(categoryName) ) {
                position = categories.indexOf(category);
                break;
            }
        }

        if(position < 0) { return 0.0; }

        PlanCategoryHolder holder = plannerHolders.get(position);
        String plannedBudgetString = holder.binding.etPlanCategoryBudget.getText().toString();

        return plannedBudgetString.isEmpty() ? -1.0 : Double.parseDouble(plannedBudgetString);
    }

}
