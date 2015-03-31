package com.example.traveller;

import java.text.DateFormat.Field;

import android.content.Context;
import android.graphics.Interpolator;
import android.support.v4.view.ViewPager;
import android.widget.Scroller;

//用来控制ViewPager页面跳转之间的时间
public class ViewPagerScroller extends Scroller{
	private int mScrollDuration = 2000;             // 滑动速度
	  
    /**
     * 设置速度速度
     * @param duration
     */
    public void setScrollDuration(int duration){
        this.mScrollDuration = duration;
    }
    
	public ViewPagerScroller(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
  
    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
  
      
      
    public void initViewPagerScroll(ViewPager viewPager) {
        try {
            java.lang.reflect.Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(viewPager, this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
