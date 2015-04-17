package com.example.traveller;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class Container extends MainActivity{
	public List<ImageView> imageList;
	public List<TextView> textList;
	public ViewPager viewPager;
	public View filepageView,infolistView,aboutmeView,footprintmapView;
	public GridView fileGridView;
	public List<View> viewList;//view数组
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setViewPager();
		setBottomBar();
		setImage();
		setClickEvent();
	}
	
	public void setViewPager()
	{
		viewPager=(ViewPager)findViewById(R.id.viewpager);
		LayoutInflater inflater=getLayoutInflater();
		
		filepageView=inflater.inflate(R.layout.filepage, null);
        infolistView=inflater.inflate(R.layout.infolist, null);
        aboutmeView=inflater.inflate(R.layout.aboutme, null);
        footprintmapView=inflater.inflate(R.layout.footprintmap, null);
        
        fileGridView=(GridView)filepageView.findViewById(R.id.filegridView);
        
        viewList=new ArrayList<View>();//将要分页显示的view装入数组中
        viewList.add(filepageView);
        viewList.add(infolistView);
        viewList.add(aboutmeView);
        viewList.add(footprintmapView);
        
        //pageView的适配器
        PagerAdapter pagerAdapter=new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0==arg1;
				
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return viewList.size();
			}
			
			//从当前container中删除指定位置（position）的View;
			public void destroyItem(ViewGroup container, int position,  
                    Object object)
			{
				container.removeView(viewList.get(position));
//				Toast.makeText(getApplicationContext(), "destroy:"+String.valueOf(position),
//					     Toast.LENGTH_SHORT).show();
			}
			
			//做了两件事，第一：将当前视图添加到container中，第二：返回当前View
			public Object instantiateItem(ViewGroup container, int position)
			{
				container.addView(viewList.get(position)); 
				return viewList.get(position);
			}
			
		};
		
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(pageChangeListener);
	}
	
	//设置底部菜单栏
	public void setBottomBar()
	{
		imageList=new ArrayList<ImageView>();
		ImageView fileIV=(ImageView)findViewById(R.id.FileImageView);
		ImageView messageIV=(ImageView)findViewById(R.id.MessageImageView);//动态信息   	
    	ImageView locationIV=(ImageView)findViewById(R.id.LocationImageView);//定位   	
    	ImageView aboutIV=(ImageView)findViewById(R.id.MeImageView);//关于自己
    	
    	imageList.add(fileIV);
    	imageList.add(messageIV);
    	imageList.add(locationIV);
    	imageList.add(aboutIV);
    	
    	TextView fileTV=(TextView)findViewById(R.id.fileTextView);
    	TextView homeTV=(TextView)findViewById(R.id.homeTextView);
    	TextView locationTV=(TextView)findViewById(R.id.locationTextView);
    	TextView userTV=(TextView)findViewById(R.id.userTextView);
    	
    	textList.add(fileTV);
    	textList.add(homeTV);
    	textList.add(locationTV);
    	textList.add(userTV);
	}
	
	//为ImageView添加图片
	public void setImage()
	{
		imageList.get(0).setImageResource(R.drawable.file);
    	imageList.get(1).setImageResource(R.drawable.home);
    	imageList.get(2).setImageResource(R.drawable.location);
    	imageList.get(3).setImageResource(R.drawable.user);
	}
	
	//为底部菜单栏添加点击事件
	public void setClickEvent()
	{
		for(int i=0;i<imageList.size();i++)
		{
			imageList.get(i).setOnClickListener(handler);
		}
		for(int i=0;i<textList.size();i++)
		{
			textList.get(i).setOnClickListener(handler);
		}
	}
	
	 //图片点击监听
	View.OnClickListener handler=new View.OnClickListener(){
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.FileImageView:
				case R.id.fileTextView:
					setImage();
					imageList.get(0).setImageResource(R.drawable.file_c);
					viewPager.setCurrentItem(0);//跳转到指定页面
					break;
				case R.id.MessageImageView:
				case R.id.homeTextView:
					setImage();
					imageList.get(1).setImageResource(R.drawable.home_c);
					viewPager.setCurrentItem(1);
					break;
				case R.id.LocationImageView:
				case R.id.locationTextView:
					setImage();
					imageList.get(2).setImageResource(R.drawable.location_c);
					viewPager.setCurrentItem(2);
					break;
				case R.id.MeImageView:
				case R.id.userTextView:
					setImage();
					imageList.get(3).setImageResource(R.drawable.user_c);
					viewPager.setCurrentItem(3);
					break;
				default:
					
					break;
					
			}
		}
	};
		
		public OnPageChangeListener pageChangeListener=new OnPageChangeListener() {		
				@Override
				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					current_page=arg0;
					switch(current_page)
					{		
						case 0:
							setImage();
							imageList.get(0).setImageResource(R.drawable.file_c);
							break;
						case 1:
							setImage();
							imageList.get(1).setImageResource(R.drawable.home_c);
							break;
						case 2:
							setImage();
							imageList.get(2).setImageResource(R.drawable.location_c);
							break;
						case 3:
							setImage();
							imageList.get(3).setImageResource(R.drawable.user_c);				
							break;
						default:
							break;
					}
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
		};
}
