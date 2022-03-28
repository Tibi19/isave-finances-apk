package com.tam.isave.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

import com.tam.isave.R;

import java.util.ArrayList;

public class TouchDelegateExtensionComposite extends TouchDelegate {

    private final View parentView;
    private final ArrayList<TouchDelegate> delegates = new ArrayList<>();
    private final ArrayList<View> buttons = new ArrayList<>();

    public TouchDelegateExtensionComposite(View parentView) { 
        super(new Rect(), parentView);
        this.parentView = parentView;
    }

    /**
     * Adds a button for which a touch delegate will be created when setupParentDelegate is called.
     * This is because setupParentDelegate will call the parent's post method and only then will button Rects be accurate.
     * @param buttonView - The button for which a touch delegate will be setup.
     */
    public void addButtonToDelegateExtension(View buttonView) {
        if (buttonView == null) { return; }
        buttons.add(buttonView);
    }
    
    public void setupParentDelegateExtension(Context context) {
        parentView.post(
                () -> {
                    setupExtensionDelegates(context);
                    parentView.setTouchDelegate(this);
                }
        );
    }

    public void setupExtensionDelegates(Context context) {
        buttons.forEach(
                button -> {
                    int areaExtensionPx = context.getResources().getDimensionPixelSize(R.dimen.button_hit_area_extension);
                    Rect buttonAreaRect = new Rect();
                    button.getHitRect(buttonAreaRect);

                    buttonAreaRect.top -= areaExtensionPx;
                    buttonAreaRect.left -= areaExtensionPx;
                    buttonAreaRect.bottom += areaExtensionPx;
                    buttonAreaRect.right += areaExtensionPx;

                    delegates.add(new TouchDelegate(buttonAreaRect, button));
                }
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        float x = event.getX();
        float y = event.getY();
        for (TouchDelegate delegate : delegates) {
            event.setLocation(x, y);
            result = delegate.onTouchEvent(event) || result;
        }
        return result;
    }
    
}
