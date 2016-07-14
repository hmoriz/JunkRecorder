package jp.ac.titech.itpro.hmoriz.junkrecorder;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by hmoriz on 2016/07/13.
 */

// 端的にいうと、スワイプの有効・無効を切り替えられるようにしただけのViewPager
public class JunkViewPager extends ViewPager{
    boolean mSwipeEnabled = false;
    public JunkViewPager(Context context) {
        super(context);
    }

    public JunkViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("JunkViewPager", "Constructor");
    }

    public void EnableSwipe(boolean flag){
        mSwipeEnabled = flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mSwipeEnabled && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mSwipeEnabled && super.onInterceptTouchEvent(ev);
    }
}
