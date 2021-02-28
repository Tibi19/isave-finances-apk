package com.tam.isave.view;

import android.os.Bundle;
import com.tam.isave.databinding.ActivityHomeBinding;

import androidx.appcompat.app.AppCompatActivity;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding homeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        if (savedInstanceState == null) {
            startCategoriesTransaction();
        }
    }

    private void startCategoriesTransaction() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(homeBinding.fragmentContainerCategories.getId(), CategoriesFragment.class, null)
                .commit();
    }

}