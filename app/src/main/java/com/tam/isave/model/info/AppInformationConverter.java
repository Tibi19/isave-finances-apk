package com.tam.isave.model.info;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppInformationConverter {

    private static final String APP_INFO_JSON_FILE_NAME = "InformationList.json";

    public static List<AppInformation> getInformationListFromJson(Context context) {
        String informationJson = getInformationJson(context);
        AppInformation[] informationElements = new Gson().fromJson(informationJson, AppInformation[].class);
        return Arrays.asList(informationElements);
    }

    private static String getInformationJson(Context context) {
        String jsonString;

        try {
            InputStream assetsStream = context.getAssets().open(APP_INFO_JSON_FILE_NAME);
            int size = assetsStream.available();
            byte[] buffer = new byte[size];
            assetsStream.read(buffer);
            assetsStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }

}
