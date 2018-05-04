package com.example.bietong.personalcoursetable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by BieTong on 2018/4/24.
 */

public class ScrollChangeListenerScrollView extends ScrollView {

    private ScrollViewListener scrollViewListener = null;

    public ScrollChangeListenerScrollView(Context context) {
        super(context);
    }

    public ScrollChangeListenerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollChangeListenerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollChangeListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChange(this, x, y, oldx, oldy);
        }
    }

    public interface ScrollViewListener {
        void onScrollChange(ScrollChangeListenerScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }
}
