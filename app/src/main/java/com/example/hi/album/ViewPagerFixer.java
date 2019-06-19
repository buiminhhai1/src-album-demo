package com.example.hi.album;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerFixer extends ViewPager {
    public ViewPagerFixer(Context context)
    {
        super(context);
    }

    public ViewPagerFixer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        try
        {
            return super.onInterceptTouchEvent(ev);
        }
        catch (IllegalArgumentException e)
        {
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }
        return false;
    }
}
