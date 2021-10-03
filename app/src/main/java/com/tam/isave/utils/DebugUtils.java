package com.tam.isave.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.Random;

public class DebugUtils {

    public static Context context;

    public static int toastedIterations = 0;

    public static String categoryNames[] = {"Groceries", "Toys", "Clothing", "Sweets", "Notebooks", "Extra", "Health",
            "Hygiene", "Learning", "Self-Development", "Others", "Going out", "Take out", "Tobacco"};
    public static Random random = new Random();

    public static void makeToast(Context context, String text) {
        text += (" toastCount " + toastedIterations++);
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void makeToast(String text) {
        makeToast(context, text);
    }

    public static String getRandomCategoryName() {
        return categoryNames[random.nextInt(categoryNames.length)];
    }

    /**
     * Generates a random double between [0, maxValue).
     * @param maxValue The maximum value.
     * @return a value between [0, maxValue)
     */
    public static double getRandomDouble(int maxValue) {
        return getRandomInt(maxValue) + random.nextDouble();
    }

    /**
     * Generates random double from minValue to maxValue.
     * @param minValue The minimum value.
     * @param maxValue The maximum value.
     * @return a random double value between minValue and maxValue
     */
    public static double getRandomDoubleInRange(int minValue, int maxValue) {
        return getRandomIntInRange(minValue, maxValue) + random.nextDouble();
    }

    /**
     * Generates random int from 0 to maxValue.
     * @param maxValue The maximum value.
     * @return a value between 0 and maxvalue.
     */
    public static int getRandomInt(int maxValue) {
        return random.nextInt(maxValue + 1);
    }

    public static int getRandomIntInRange(int minValue, int maxValue) {
        int rangeMaxValue = maxValue - minValue + 1;
        return minValue + random.nextInt(rangeMaxValue);
    }
}
