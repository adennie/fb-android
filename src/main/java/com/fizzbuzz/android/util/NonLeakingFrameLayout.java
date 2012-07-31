package com.fizzbuzz.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class NonLeakingFrameLayout
        extends FrameLayout {

    public NonLeakingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context.getApplicationContext(), attrs, defStyle);
    }

    public NonLeakingFrameLayout(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
    }

    public NonLeakingFrameLayout(Context context) {
        super(context.getApplicationContext());
    }

}
