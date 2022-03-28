package com.tam.isave.utils;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ButtonAreaExtensionUtils {

    public static void extendHitAreaOfButtons(Context context, View... buttonViews) {
        // We extend touch delegates to extend hit area of buttons, but only 1 touch delegate is allowed per parent.
        // So for each parent, we will use 1 touch delegate composite (extends touch delegate).
        // Each composite has 1 extension touch delegate per button.
        getTouchDelegateExtensionComposites(buttonViews).forEach(
                extensionComposite -> extensionComposite.setupParentDelegateExtension(context)
        );
    }

    private static Collection<TouchDelegateExtensionComposite> getTouchDelegateExtensionComposites(View... buttonViews) {
        HashMap<View, TouchDelegateExtensionComposite> parentToExtensionCompositeMap = new HashMap<>();
        for(View buttonView : buttonViews) {
            View buttonParent = (View) buttonView.getParent();

            if(parentToExtensionCompositeMap.containsKey(buttonParent)) {
                parentToExtensionCompositeMap.get(buttonParent).addButtonToDelegateExtension(buttonView);
                continue;
            }

            TouchDelegateExtensionComposite touchDelegateExtensionComposite = new TouchDelegateExtensionComposite(buttonParent);
            parentToExtensionCompositeMap.put(buttonParent, touchDelegateExtensionComposite);
            touchDelegateExtensionComposite.addButtonToDelegateExtension(buttonView);
        }

        return parentToExtensionCompositeMap.values();
    }

}
