package com.tam.isave.utils;

import java.util.function.BiConsumer;

public class OrganizerBindingUtils {

    private static Runnable updateOrganizerBinding;
    private static BiConsumer<Integer, Integer> resetOrganizerBindingIfTimeChange;

    public static void setUpdateBinding(Runnable updateBinding) {
        updateOrganizerBinding = updateBinding;
    }

    public static void setResetBindingIfTimeChange(BiConsumer<Integer, Integer> resetBindingIfTimeChange) {
        resetOrganizerBindingIfTimeChange = resetBindingIfTimeChange;
    }

    public static void updateBinding() {
        updateOrganizerBinding.run();
    }

    public static void resetBindingIfTimeChange(int originalFirstDayValue, int originalOrganizerDays) {
        resetOrganizerBindingIfTimeChange.accept(originalFirstDayValue, originalOrganizerDays);
    }

}
