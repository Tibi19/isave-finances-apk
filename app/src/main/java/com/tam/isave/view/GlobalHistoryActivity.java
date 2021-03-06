package com.tam.isave.view;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tam.isave.databinding.ActivityGlobalHistoryBinding;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.HistoryIdentifier;

public class GlobalHistoryActivity extends AppCompatActivity {

    private ActivityGlobalHistoryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGlobalHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startHistoryTransaction();
    }

    private void startHistoryTransaction() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HISTORY_TYPE, HistoryIdentifier.HISTORY_TYPE_GLOBAL);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragmentContainerHistory.getId(), HistoryFragment.class, bundle)
                .commit();
    }
}
