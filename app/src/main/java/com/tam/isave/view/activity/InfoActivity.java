package com.tam.isave.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.adapter.InfoAdapter;
import com.tam.isave.databinding.ActivityInfoBinding;
import com.tam.isave.model.info.AppInformation;
import com.tam.isave.model.info.AppInformationConverter;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private ActivityInfoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView informationRecycler = binding.recyclerInfos;
        InfoAdapter infoAdapter = new InfoAdapter(this, AppInformationConverter.getInformationListFromJson(this));
        informationRecycler.setAdapter(infoAdapter);
        informationRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
