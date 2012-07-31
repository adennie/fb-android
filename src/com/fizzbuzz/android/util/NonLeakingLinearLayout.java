package com.fizzbuzz.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class NonLeakingLinearLayout
        extends LinearLayout {

    public NonLeakingLinearLayout(Context context) {
        super(context.getApplicationContext());
        // TODO Auto-generated constructor stub
    }

    public NonLeakingLinearLayout(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
        // TODO Auto-generated constructor stub
    }

    public NonLeakingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context.getApplicationContext(), attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

}
