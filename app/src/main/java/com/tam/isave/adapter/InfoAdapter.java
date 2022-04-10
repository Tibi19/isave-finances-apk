package com.tam.isave.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tam.isave.databinding.ListviewInfoRowBinding;
import com.tam.isave.model.info.AppInformation;

import java.util.ArrayList;

public class InfoAdapter extends ArrayAdapter<AppInformation> {

    public InfoAdapter(@NonNull Context context, ArrayList<AppInformation> infoArray) {
        super(context, 0, infoArray);
    }

    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // if(convertView != null) { return convertView; }

        ListviewInfoRowBinding infoRowBinding = ListviewInfoRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        AppInformation information = getItem(position);

        infoRowBinding.textInfoTitle.setText(information.getTitle());
        infoRowBinding.textInfoBody.setText(information.getDescription());

        return infoRowBinding.getRoot();
    }

}
