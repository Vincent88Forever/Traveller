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
	private ViewPager viewPager;//��Ӧ��viewPager
	private int current_page=0;//��ǰҳ����±�
	private List<View> viewList;//view����
	private List<ImageView> imageList;//���ͼƬ
	
	private String savedPictureName;//������պ󱣴��ͼƬ����
	private FileOutputStream fos;//�ļ������
	private File ImagesFile;//��������ͼƬ���ļ�
	private String PictureFilePath="/storage/sdcard1/Traveller/images/";//������յ�ͼƬ���ļ���·��
	private String PicturePath;//�������Ƭ��ȫ·��
	//private ImageView pictureImageView;//����ֱ����ʾ�����ͼƬ��ImageView������ò���ò����ˣ�
	
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
         * ����viewpage���ڲ��ؼ�
         */
        ImageView cameraImageView=(ImageView)infolistView.findViewById(R.id.CameraImageView);
        //����Ҫֱ����ҳ����ʾͼƬ�ˣ�������֮��ֱ�ӱ���
        //pictureImageView=(ImageView)infolistView.findViewById(R.id.PictureImageView);
        cameraImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                startActivityForResult(intent, 1);
			}
		});
        
        viewList=new ArrayList<View>();//��Ҫ��ҳ��ʾ��viewװ��������
        viewList.add(filepageView);
        viewList.add(infolistView);
        viewList.add(aboutmeView);
        viewList.add(footprintmapView);
        
        //pageView��������
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
			
			//�ӵ�ǰcontainer��ɾ��ָ��λ�ã�position����View;
			public void destroyItem(ViewGroup container, int position,  
                    Object object)
			{
				container.removeView(viewList.get(position));
//				Toast.makeText(getApplicationContext(), "destroy:"+String.valueOf(position),
//					     Toast.LENGTH_SHORT).show();
			}
			
			//���������£���һ������ǰ��ͼ��ӵ�container�У��ڶ������ص�ǰView
			public Object instantiateItem(ViewGroup container, int position)
			{
				container.addView(viewList.get(position)); 
				return viewList.get(position);
			}
			
		};
		
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(pageChangeListener);
		//����viewPager�Ĺ����л�ʱ��
		ViewPagerScroller scroller = new ViewPagerScroller(getBaseContext());
		scroller.setScrollDuration(0);
		scroller.initViewPagerScroll(viewPager); 
    }
    
    public void setImageView()
    {
    	imageList=new ArrayList<ImageView>();
    	ImageView FIM=(ImageView)findViewById(R.id.FileImageView);//�ļ�
    	ImageView MIM=(ImageView)findViewById(R.id.MessageImageView);//��̬��Ϣ   	
    	ImageView LIM=(ImageView)findViewById(R.id.LocationImageView);//��λ   	
    	ImageView AIM=(ImageView)findViewById(R.id.MeImageView);//�����Լ�
    	imageList.add(FIM);
    	imageList.add(MIM);
    	imageList.add(LIM);
    	imageList.add(AIM);
    	
    	//����ͼƬ����¼�
    	FIM.setOnClickListener(handler);
    	LIM.setOnClickListener(handler);
    	AIM.setOnClickListener(handler);
    	MIM.setOnClickListener(handler);
    	
    	/*
    	 * һ����Ϊ��ʹ�õ��Ч����ӵģ�������������Ӧ��textView�ϣ�Ҳ����
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
    
    //ͼƬ�������
	View.OnClickListener handler=new View.OnClickListener(){
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.FileImageView:
				case R.id.fileTextView:
					initialImage();
					imageList.get(0).setImageResource(R.drawable.file_c);
					viewPager.setCurrentItem(0);//��ת��ָ��ҳ��
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
    
    //���ĸ�ImageViewȫ����Ϊ���ʼ��״̬
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
	
	//�õ��������Ƭ
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK)
		{
			String sdStatus=Environment.getExternalStorageState();//���������Ƿ���ڲ����ж�/дȨ��
			if(!sdStatus.equals(Environment.MEDIA_MOUNTED))//���sd���Ƿ����
			{
				return;
			}
			new DateFormat();
			savedPictureName=DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance())+ ".jpg";
			Toast.makeText(this, savedPictureName, Toast.LENGTH_LONG).show();
			Bundle bundle = data.getExtras();  
            Bitmap bitmap = (Bitmap) bundle.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ  
		    
            fos=null; 
            PicturePath=PictureFilePath+savedPictureName;//����ͼƬ��ȫ·��
            
            try {
            	fos=new FileOutputStream(PicturePath);
            	bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);//������д���ļ�
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
//				pictureImageView.setImageBitmap(bitmap);//��ͼƬ��ʾ��ImageView��
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
		}
	}
	
	//��������ͼƬ��·��
	protected  void CreateFile() 
	{
		ImagesFile=new File(PictureFilePath);//���������ļ�·��
		ImagesFile.mkdirs();
	}
	

}
