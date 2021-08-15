package com.tam.isave.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tam.isave.databinding.ActivityCategoryHistoryBinding;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.HistoryIdentifier;
import com.tam.isave.view.fragment.HistoryFragment;

public class CategoryHistoryActivity extends AppCompatActivity {

    private ActivityCategoryHistoryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        String categoryName = intent.getStringExtra(Constants.KEY_CATEGORY_NAME);
        binding.textCategoryHistoryName.setText(categoryName + " " + Constants.NAMING_HISTORY);

        startHistoryTransaction(intent.getIntExtra(Constants.KEY_CATEGORY_ID, -1));
    }

    private void startHistoryTransaction(int categoryId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HISTORY_TYPE, HistoryIdentifier.HISTORY_TYPE_CATEGORY);
        bundle.putInt(Constants.KEY_CATEGORY_ID, categoryId);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragmentContainerHistory.getId(), HistoryFragment.class, bundle)
                .commit();
    }

}
