package com.tam.isave.view;

import android.os.Bundle;
import android.view.View;

import com.tam.isave.R;
import com.tam.isave.adapter.CategoryAdapter;
import com.tam.isave.model.category.Category;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.viewmodel.CategoryViewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView categoryRecycler;
    private CategoryAdapter categoryAdapter;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initCategoryController();
        setupCategoryRecycler();
        setupCategoryViewModel();
    }

    private void setupCategoryRecycler() {
        // Instantiate recyclerview and its adapter.
        categoryRecycler = findViewById(R.id.recycler_category);
        categoryAdapter = new CategoryAdapter(this);
        // Set recycler's adapter.
        categoryRecycler.setAdapter(categoryAdapter);
        // Set recycler's layout manager.
        categoryRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupCategoryViewModel() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        // Observer
        categoryViewModel.getCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                categoryAdapter.setCategories(categories);
            }
        });
    }

    private void initCategoryController(){
        // Set functionality of add category button.
        findViewById(R.id.button_add_category).setOnClickListener(this);
        findViewById(R.id.button_home_menu).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_add_category:
                addCategory();
                break;
            case R.id.button_home_menu:
                // DEBUGGING: Menu button deletes all instances of category from database.
                categoryViewModel.deleteAll();
                break;
        }
    }

    private void addCategory() {
        DebugUtils.makeToast(this, "added category");
        Category category = getRandomCategory();
        categoryViewModel.addCategory(category.getName(), category.getGoal(), category.isFlexibleGoal());
    }

    private Category getRandomCategory() {
        int randomGoal = DebugUtils.getRandomIntInRange(150, 800);
        Category category = new Category(DebugUtils.getRandomCategoryName(), randomGoal);
        category.setSpent(DebugUtils.getRandomDouble(randomGoal));

        return category;
    }
}