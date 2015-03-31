package com.example.traveller;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateFormat;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

public class MainActivity extends ActionBarActivity {
	
	private View filepageView,infolistView,aboutmeView,footprintmapView;
	private ViewPager viewPager;//对应的viewPager
	private int current_page=0;//当前页码的下标
	private List<View> viewList;//view数组
	private List<ImageView> imageList;//存放图片
	
	private String savedPictureName;//相机拍照后保存的图片名称
	private FileOutputStream fos;//文件输出流
	private File ImagesFile;//创建保存图片的文件
	private String PictureFilePath="/storage/sdcard1/Traveller/images/";//存放拍照的图片的文件夹路径
	private String PicturePath;//拍摄的照片的全路径
	//private ImageView pictureImageView;//用来直接显示拍摄的图片的ImageView，不过貌似用不上了！
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
        CreateFile();
        setImageView();
        setViewPage();
     
        
    } 
    
    public void setViewPage()
    {
    	viewPager=(ViewPager)findViewById(R.id.viewpager);
        LayoutInflater inflater=getLayoutInflater();
        
        filepageView=inflater.inflate(R.layout.filepage, null);
        infolistView=inflater.inflate(R.layout.infolist, null);
        aboutmeView=inflater.inflate(R.layout.aboutme, null);
        footprintmapView=inflater.inflate(R.layout.footprintmap, null);
        
        /*
         * 监听viewpage的内部控件
         */
        ImageView cameraImageView=(ImageView)infolistView.findViewById(R.id.CameraImageView);
        //不需要直接在页面显示图片了，拍摄完之后直接保存
        //pictureImageView=(ImageView)infolistView.findViewById(R.id.PictureImageView);
        cameraImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                startActivityForResult(intent, 1);
			}
		});
        
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
		//设置viewPager的过度切换时间
		ViewPagerScroller scroller = new ViewPagerScroller(getBaseContext());
		scroller.setScrollDuration(0);
		scroller.initViewPagerScroll(viewPager); 
    }
    
    public void setImageView()
    {
    	imageList=new ArrayList<ImageView>();
    	ImageView FIM=(ImageView)findViewById(R.id.FileImageView);//文件
    	ImageView MIM=(ImageView)findViewById(R.id.MessageImageView);//动态信息   	
    	ImageView LIM=(ImageView)findViewById(R.id.LocationImageView);//定位   	
    	ImageView AIM=(ImageView)findViewById(R.id.MeImageView);//关于自己
    	imageList.add(FIM);
    	imageList.add(MIM);
    	imageList.add(LIM);
    	imageList.add(AIM);
    	
    	//监听图片点击事件
    	FIM.setOnClickListener(handler);
    	LIM.setOnClickListener(handler);
    	AIM.setOnClickListener(handler);
    	MIM.setOnClickListener(handler);
    	
    	/*
    	 * 一下是为了使得点击效果添加的，如果点击到了相应的textView上，也可以
    	 */
    	TextView fileTV=(TextView)findViewById(R.id.fileTextView);
    	TextView homeTV=(TextView)findViewById(R.id.homeTextView);
    	TextView locationTV=(TextView)findViewById(R.id.locationTextView);
    	TextView userTV=(TextView)findViewById(R.id.userTextView);
    	
    	fileTV.setOnClickListener(handler);
    	homeTV.setOnClickListener(handler);
    	locationTV.setOnClickListener(handler);
    	userTV.setOnClickListener(handler);
    	
    }
    
    //图片点击监听
	View.OnClickListener handler=new View.OnClickListener(){
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.FileImageView:
				case R.id.fileTextView:
					initialImage();
					imageList.get(0).setImageResource(R.drawable.file_c);
					viewPager.setCurrentItem(0);//跳转到指定页面
					break;
				case R.id.MessageImageView:
				case R.id.homeTextView:
					initialImage();
					imageList.get(1).setImageResource(R.drawable.home_c);
					viewPager.setCurrentItem(1);
					break;
				case R.id.LocationImageView:
				case R.id.locationTextView:
					initialImage();
					imageList.get(2).setImageResource(R.drawable.location_c);
					viewPager.setCurrentItem(2);
					break;
				case R.id.MeImageView:
				case R.id.userTextView:
					initialImage();
					imageList.get(3).setImageResource(R.drawable.user_c);
					viewPager.setCurrentItem(3);
					break;
				case R.id.CameraImageView:
					  
					break;
				default:
					break;
					
			}
		}
	};
    
    //将四个ImageView全部置为最初始的状态
    public void initialImage()
    {
    	imageList.get(0).setImageResource(R.drawable.file);
    	imageList.get(1).setImageResource(R.drawable.home);
    	imageList.get(2).setImageResource(R.drawable.location);
    	imageList.get(3).setImageResource(R.drawable.user);
    }
    
    private OnPageChangeListener pageChangeListener=new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			current_page=arg0;
			switch(current_page)
			{		
				case 0:
					initialImage();
					imageList.get(0).setImageResource(R.drawable.file_c);
					break;
				case 1:
					initialImage();
					imageList.get(1).setImageResource(R.drawable.home_c);
					break;
				case 2:
					initialImage();
					imageList.get(2).setImageResource(R.drawable.location_c);
					break;
				case 3:
					initialImage();
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
	
	//拿到拍摄的照片
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK)
		{
			String sdStatus=Environment.getExternalStorageState();//表明对象是否存在并具有读/写权限
			if(!sdStatus.equals(Environment.MEDIA_MOUNTED))//检测sd卡是否可用
			{
				return;
			}
			new DateFormat();
			savedPictureName=DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance())+ ".jpg";
			Toast.makeText(this, savedPictureName, Toast.LENGTH_LONG).show();
			Bundle bundle = data.getExtras();  
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  
		    
            fos=null; 
            PicturePath=PictureFilePath+savedPictureName;//构造图片的全路径
            
            try {
            	fos=new FileOutputStream(PicturePath);
            	bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);//把数据写入文件
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				try {
					fos.flush();
					fos.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
            
//            try {
//				pictureImageView.setImageBitmap(bitmap);//将图片显示在ImageView里
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
		}
	}
	
	//创建保存图片的路径
	protected  void CreateFile() 
	{
		ImagesFile=new File(PictureFilePath);//本机测试文件路径
		ImagesFile.mkdirs();
	}
	

}
