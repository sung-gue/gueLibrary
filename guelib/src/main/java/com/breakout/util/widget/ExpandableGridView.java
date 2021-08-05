package com.breakout.util.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


public class ExpandableGridView extends GridView {
    protected Context _context;
    private boolean expanded = true;

    public ExpandableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ExpandableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableGridView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        _context = context;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // HACK! TAKE THAT ANDROID!
        if (isExpanded()) {
            // Calculate entire height by providing a very large height hint.
            // But do not use the highest 2 bits of this integer; those are
            // reserved for the MeasureSpec mode.
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            android.view.ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

}