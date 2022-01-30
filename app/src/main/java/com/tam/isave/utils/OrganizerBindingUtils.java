package com.tam.isave.utils;

public class OrganizerBindingUtils {

    private static Runnable updateOrganizerBinding;

    public static void setUpdateBinding(Runnable updateBinding) {
        updateOrganizerBinding = updateBinding;
    }

    public static void updateBinding() {
        updateOrganizerBinding.run();
    }

}
