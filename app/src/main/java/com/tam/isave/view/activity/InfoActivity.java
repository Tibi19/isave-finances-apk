package com.tam.isave.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tam.isave.adapter.InfoAdapter;
import com.tam.isave.databinding.ActivityInfoBinding;
import com.tam.isave.model.info.AppInformation;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private ActivityInfoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<AppInformation> testInfoArray = new ArrayList<>();

        String testDescription1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam";
        String testDescription2 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
        String testDescription3 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

        testInfoArray.add(new AppInformation("Info1", testDescription1));
        testInfoArray.add(new AppInformation("Info2", testDescription2));
        testInfoArray.add(new AppInformation("Info3", testDescription3));

        InfoAdapter infoAdapter = new InfoAdapter(this, testInfoArray);
        binding.lvInfos.setAdapter(infoAdapter);
    }
}
