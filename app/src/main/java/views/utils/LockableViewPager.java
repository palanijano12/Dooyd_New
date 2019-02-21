package views.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**
 * Created by Balamurugan on 16-11-2016.
 */
public class LockableViewPager extends ViewPager
{
    private boolean swipeLocked;

    public LockableViewPager(Context context)
    {
        super(context);
        postInitViewPager(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        postInitViewPager(context);
    }

    public boolean getSwipeLocked()
    {
        return swipeLocked;
    }

    public void setSwipeLocked(boolean swipeLocked)
    {
        this.swipeLocked = swipeLocked;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return !swipeLocked && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return !swipeLocked && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction)
    {
        return !swipeLocked && super.canScrollHorizontally(direction);
    }

    private ScrollerCustomDuration mScroller = null;

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager(Context context)
    {
        try
        {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new ScrollerCustomDuration(context, (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception ignored)
        {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor)
    {
        mScroller.setScrollDurationFactor(scrollFactor);
    }
}
