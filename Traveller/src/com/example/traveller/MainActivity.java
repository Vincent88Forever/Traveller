package com.example.traveller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Space;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateFormat;
import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.SyncStateContract.Constants;

public class MainActivity extends Activity {
	
	protected View filepageView,infolistView,aboutmeView,footprintmapView;
	public ViewPager viewPager;//��Ӧ��viewPager
	public int current_page=0;//��ǰҳ����±�
	private List<View> viewList;//view����
	private List<ImageView> imageList;//���ͼƬ
	
	//public BottomBar bottomBar;
	
	private String savedPictureName;//������պ󱣴��ͼƬ����
	private FileOutputStream fos;//�ļ������
	private Bitmap bitmap;
	private File imagesFile;//��������ͼƬ���ļ���
	private File savePositionFile;//�������λ������
	private File[] underImagesFiles;//��ȡimagesFile�µ������ļ���
	private String sdPath=Environment.getExternalStorageDirectory().toString();//��ȡsd����·�� 
	private String pictureFolderPath="/storage/sdcard1/Traveller/images/";//������յ�ͼƬ���ļ��и�·��
	private String positionSaveString="/storage/sdcard1/Traveller/position.txt";//��������ʱ��ȡ���ĵ���λ����Ϣ���ļ�·��
	private String pictureFullPath;//�������Ƭ��ȫ·��
	private String currentPath="/storage/sdcard1/Traveller/images";//���浱ǰ·��
	private int currentLevel=0;//���浱ǰ�ļ��еĲ��
	
	private GridView fileGridView;
	private ArrayList<HashMap<String, Object>> listImageItem;
	private HashMap<String, Object> map;
	private SimpleAdapter SaItemSimpleAdapter;
	
	//��Ƭ��������Ϣ
	private LocationManager locationManager=null;
	private Location location=null;
	private float latitude=29.060973f;//γ��
	private float longitude=117.126208f;//����
	private String address;//λ���������ǵ�������
	private float[][] positionBeen;//���txt�ļ��������������Ϣ������positionBeen[x][0]����γ�ȣ�positionBeen[0][1]���澭��
	public String province;
	public String city;
	private ExifInterface exif;
	
	//����Ѷ��ͼ��ص�ȫ�ֱ���
	public TencentSearch api;
	public MapView mapView=null;
	public MyMapView myMapView=null;
	
	private File savedFile;//����ͼƬ�Ļ����ļ�
	private FileService fileService;
	
	//�ڵ���getAddressʱ�������������һ��������ʱ��ȡ�������꣬Ȼ����ã��ڶ������������ʱ����ȡλ���ļ����������ʱ������getAddress
	private static final int FIRSTMODE=0;
	private static final int SECONDMODE=1;
	
	private String specialLocationString="";
	
	private final String[][] MIME_MapTable={
		    //{��׺����    MIME����}
		    {".3gp",    "video/3gpp"},
		    {".apk",    "application/vnd.android.package-archive"},
		    {".asf",    "video/x-ms-asf"},
		    {".avi",    "video/x-msvideo"},
		    {".bin",    "application/octet-stream"},
		    {".bmp",      "image/bmp"},
		    {".c",        "text/plain"},
		    {".class",    "application/octet-stream"},
		    {".conf",    "text/plain"},
		    {".cpp",    "text/plain"},
		    {".doc",    "application/msword"},
		    {".exe",    "application/octet-stream"},
		    {".gif",    "image/gif"},
		    {".gtar",    "application/x-gtar"},
		    {".gz",        "application/x-gzip"},
		    {".h",        "text/plain"},
		    {".htm",    "text/html"},
		    {".html",    "text/html"},
		    {".jar",    "application/java-archive"},
		    {".java",    "text/plain"},
		    {".jpeg",    "image/jpeg"},
		    {".jpg",    "image/jpeg"},
		    {".js",        "application/x-javascript"},
		    {".log",    "text/plain"},
		    {".m3u",    "audio/x-mpegurl"},
		    {".m4a",    "audio/mp4a-latm"},
		    {".m4b",    "audio/mp4a-latm"},
		    {".m4p",    "audio/mp4a-latm"},
		    {".m4u",    "video/vnd.mpegurl"},
		    {".m4v",    "video/x-m4v"},    
		    {".mov",    "video/quicktime"},
		    {".mp2",    "audio/x-mpeg"},
		    {".mp3",    "audio/x-mpeg"},
		    {".mp4",    "video/mp4"},
		    {".mpc",    "application/vnd.mpohun.certificate"},        
		    {".mpe",    "video/mpeg"},    
		    {".mpeg",    "video/mpeg"},    
		    {".mpg",    "video/mpeg"},    
		    {".mpg4",    "video/mp4"},    
		    {".mpga",    "audio/mpeg"},
		    {".msg",    "application/vnd.ms-outlook"},
		    {".ogg",    "audio/ogg"},
		    {".pdf",    "application/pdf"},
		    {".png",    "image/png"},
		    {".pps",    "application/vnd.ms-powerpoint"},
		    {".ppt",    "application/vnd.ms-powerpoint"},
		    {".prop",    "text/plain"},
		    {".rar",    "application/x-rar-compressed"},
		    {".rc",        "text/plain"},
		    {".rmvb",    "audio/x-pn-realaudio"},
		    {".rtf",    "application/rtf"},
		    {".sh",        "text/plain"},
		    {".tar",    "application/x-tar"},    
		    {".tgz",    "application/x-compressed"}, 
		    {".txt",    "text/plain"},
		    {".wav",    "audio/x-wav"},
		    {".wma",    "audio/x-ms-wma"},
		    {".wmv",    "audio/x-ms-wmv"},
		    {".wps",    "application/vnd.ms-works"},
		    //{".xml",    "text/xml"},
		    {".xml",    "text/plain"},
		    {".z",        "application/x-compress"},
		    {".zip",    "application/zip"},
		    {"",        "*/*"}    
		};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
        api=new TencentSearch(this);//���ڽ�����ַ��Ϣ��
            
    	setViewPage();
        setImageView();
       
        mapView.onCreate(savedInstanceState);
        mapView.getController().setZoom(4);//���õ�ͼ����
       
        try {
			createFiles();//�����ļ��л����ļ�
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        fileService=new FileService(MainActivity.this);
		try {
			getTextContent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		for(int i=0;i<fileService.count;i++)
		{
//			GetAddress(1,positionBeen[i][0], positionBeen[i][1]);
			AddMarker(new LatLng(positionBeen[i][0], positionBeen[i][1]), String.valueOf(i+1));
		}
	    
        setFilePage();
    } 
    
    /*
     * ��MapView���������ڽ��й���
     * @see android.app.Activity#onDestroy()
     */
    protected void onDestroy()
    {
    	mapView.onDestroy();
        super.onDestroy();
    }
    
    protected void onPause()
    {
    	mapView.onPause();
        super.onPause();
    }
    
    protected void onResume()
    {
    	mapView.onResume();
        super.onResume();
    }
    
    protected void onStop()
    {
    	mapView.onStop();
        super.onStop();
    }
    
    //ʹ����ViewPager֮����Ҫ���page��ȥ
    public void setViewPage()
    {
    	viewPager=(ViewPager)findViewById(R.id.viewpager);
        LayoutInflater inflater=getLayoutInflater();
        
        filepageView=inflater.inflate(R.layout.filepage, null);
        infolistView=inflater.inflate(R.layout.infolist, null);
        footprintmapView=inflater.inflate(R.layout.footprintmap, null);
        aboutmeView=inflater.inflate(R.layout.aboutme, null);       
        
        /*
         * ����viewpage���ڲ��ؼ�
         */
        ImageView cameraImageView=(ImageView)infolistView.findViewById(R.id.CameraImageView);
        /*
         * ������ͼ�꣬�������
         */
        cameraImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (savedFile == null) {
					savedFile = new File(pictureFolderPath + "camera.jpg");
				}
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(savedFile));
                startActivityForResult(intent, 1);
			}
		});
        
        mapView=(MapView)footprintmapView.findViewById(R.id.mapview);       
        fileGridView=(GridView)filepageView.findViewById(R.id.filegridView);
        
        viewList=new ArrayList<View>();//��Ҫ��ҳ��ʾ��viewװ��������
        viewList.add(filepageView);
        viewList.add(infolistView);
        viewList.add(footprintmapView);
        viewList.add(aboutmeView);       
        
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
		viewPager.setCurrentItem(1);//��ʼ������ʱĬ����ʾΪ�ڶ�ҳ
		viewPager.setOnPageChangeListener(pageChangeListener);
		
		//����viewPager�Ĺ����л�ʱ��
		ViewPagerScroller scroller = new ViewPagerScroller(getBaseContext());
		scroller.setScrollDuration(0);
		scroller.initViewPagerScroll(viewPager); 
    }
    
    //������������ת�˵���
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
    	
    	//bottomBar.setBottomBar();
    	//bottomBar.setClickEvent(); 	
//		Toast.makeText(getApplicationContext(), String.valueOf(bottomBar.imageList.size()),
//	     Toast.LENGTH_SHORT).show(); 
    	
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
			bitmap = BitmapFactory.decodeFile(pictureFolderPath + "camera.jpg");
        	
            try {
            	if(GetLocation())//��ȡ���˵�ǰλ��
            	{
            		//��������Ƭʱ��ȡ��������λ��д���ļ�
            		String write_str=String.valueOf(latitude)+"/"+String.valueOf(longitude)+"\n";
            		fileService.savePosition(savePositionFile, write_str);
            		
            		//������λ��ת��Ϊ�����ַ��Ϣ
            		GetAddress(FIRSTMODE,latitude,longitude);
            	}
            	else
            	{
            		Toast.makeText(getApplicationContext(), "��ʱ��ȡ��������λ�ã����GPS",
      					     Toast.LENGTH_SHORT).show();
            		saveImagesWithoutGPS();
				}
            	
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
		}
	}
	
	//�����ļ�
	public void createFiles() throws IOException 
	{
		imagesFile=new File(pictureFolderPath);//���������ļ�·��
		//�жϵ�ǰ·�����ļ����Ƿ����
		if(imagesFile.exists())
		{
		}
		else
		{
			imagesFile.mkdirs();
		}
		
		savePositionFile=new File(positionSaveString);
		if(savePositionFile.exists())
		{
			
		}
		else
		{
			savePositionFile.createNewFile();
		}
	}
	
	/*
	 * ��������Ҫ�ж��Ƿ������ļ��У����ҷ��ص�ǰ�������ƬӦ�÷��ص��ļ��е�ַ
	 */
	public String createSubFolder()
	{
		if(province!="")//�����ĳһ��ʡ��
		{
			String provincePath=pictureFolderPath+province+"/";
			File tempProvinceFile=new File(provincePath);
			if(tempProvinceFile.exists())//�鿴�Ƿ���ڸ�ʡ�ݵ��ļ��У��������
			{
				if(city!="")//�������һ������
				{
					String cityPath=provincePath+city+"/";
					File tempCityFile=new File(cityPath);
					if(tempCityFile.exists())//�鿴�ڸ�ʡ���ļ������Ƿ���ڸó��е��ļ��У��������
					{
						
					}
					else
					{
						tempCityFile.mkdirs();//��������ڸó��У��������ļ���
					}
					return cityPath;
				}
				return provincePath;
			}
			else
			{
				tempProvinceFile.mkdirs();//��������ڸ�ʡ�ݣ�������ʡ�ݵ��ļ���
				if(city!="")
				{
					String cityPath=provincePath+city+"/";
					File tempCityFile=new File(cityPath);
					if(tempCityFile.exists())
					{
						
					}
					else
					{
						tempCityFile.mkdirs();
					}
					return cityPath;
				}
				return provincePath;
			}
		}
		return pictureFolderPath+"���й���½����"+"/";
	}
	
	//���δ��GPS���ܣ��������ͼƬ��Ӧ�ñ����λ��
	public void saveImagesWithoutGPS()
	{
		String unknownArea=pictureFolderPath+"δ֪����/";
		File tempFile=new File(unknownArea);
		if(tempFile.exists())
		{
			pictureFullPath=unknownArea+savedPictureName;//����ͼƬ��ȫ·��
            File pictureFile = new File(pictureFullPath); 
        	try {
				fos=new FileOutputStream(pictureFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);//������д���ļ�
        	File cacheImageFile=new File(pictureFolderPath+"camera.jpg");
        	cacheImageFile.delete();
		}
		else
		{
			tempFile.mkdirs();
			pictureFullPath=unknownArea+savedPictureName;//����ͼƬ��ȫ·��
            File pictureFile = new File(pictureFullPath); 
        	try {
				fos=new FileOutputStream(pictureFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);//������д���ļ�
        	File cacheImageFile=new File(pictureFolderPath+"camera.jpg");
        	cacheImageFile.delete();
		}
	}
	
	
	
	//��ȡ��Ƭ����Ϣ
	private void readEXIF(String path) throws IOException
	{
		exif = new ExifInterface(path);
    	/*
    	 * �ڻ�ȡ��Ƭ�ľ�γ��ʱ��������
    	 */
		String datetimeString=exif.getAttribute(ExifInterface.TAG_DATETIME);
        Log.v("latitude",datetimeString);
	}
	
	//ͨ��GPS��NetWork����ȡʵʱλ�õľ�γ��
	private boolean GetLocation()
	{
		locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(location!=null)
			{
				latitude=(float) location.getLatitude();
				longitude=(float) location.getLongitude();
				Log.v("Latitude",String.valueOf(latitude));
				return true;
			}
			else
			{
				LocationListener locationListener=new LocationListener() {
					
					//Provider��״̬�ڿ��á���ʱ�����ú��޷�������״ֱ̬���л�ʱ�����˺���
					@Override
					public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
						// TODO Auto-generated method stub
						
					}
					
					//Provider��enableʱ�����˺���������GPS����
					@Override
					public void onProviderEnabled(String arg0) {
						// TODO Auto-generated method stub
						
					}
					
					//Provider��disableʱ�����˺���������GPS���ر�
					@Override
					public void onProviderDisabled(String arg0) {
						// TODO Auto-generated method stub
						
					}
					
					//������ı�ʱ�����˺��������Provider������ͬ�����꣬���Ͳ��ᱻ����
					@Override
					public void onLocationChanged(Location arg0) {
						// TODO Auto-generated method stub
						if(location!=null)
						{
							Log.e("Map","Location changed: Lat:"+location.getLatitude()+" Lng: "+location.getLongitude());
						}
					}
				};
				
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
				Location location2=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if(location2 != null){     
		               latitude = (float) location2.getLatitude(); //����     
		               longitude = (float) location2.getLongitude(); //γ��
		               Log.v("Latitude",String.valueOf(latitude));
		               return true;
		        }   
			}
		}
		
		return false;
	}
	
	//��ȡposition.txt�ı���������ݣ����������ڶ�ά��������
	private void getTextContent() throws Exception
	{
		positionBeen=new float[200][2];
		positionBeen=fileService.readFileOnLine(positionSaveString);
	}
	
	//���ݾ�γ������ת��Ϊ��ַ��Ϣ
	public void GetAddress(final int mode, float lat,float lon)
	{
		Geo2AddressParam param=new Geo2AddressParam().location(new com.tencent.lbssearch.object.Location().lat(lat).lng(lon));
		api.geo2address(param, new HttpResponseListener() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, BaseObject object) {
				// TODO Auto-generated method stub
				 if(object != null)
				 {
			            Geo2AddressResultObject oj = (Geo2AddressResultObject)object;
			            if(oj.result != null)
			            {
			            	address="";
			                address += oj.result.address;		                
			                getProvinceAndCity(address);
			                switch(mode)
			                {
				                case 0:
					                String tempPath=createSubFolder();
					                pictureFullPath=tempPath+savedPictureName;//����ͼƬ��ȫ·��
					                
					                File pictureFile = new File(pictureFullPath); 
					                
					            	try {
					            		if (!pictureFile.exists()) {
											pictureFile.createNewFile();
										}
										fos=new FileOutputStream(pictureFile);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					            	bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);//������д���ļ�
					            	File cacheImageFile=new File(pictureFolderPath+"camera.jpg");
					            	cacheImageFile.delete();//ɾ�������ļ���camera.jpg��
					            	
					            	//���е�ͼ��ע
					            	AddMarker(new LatLng(latitude, longitude), province);
					            	break;
				                case 1:
				                	specialLocationString=getSpecialLocation();
				                	break;
				                default:
				                	break;
			                }
			                
			            }
				 }
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	//�ӵõ��ĵ�ַ����ȡʡ�ݺ���
	public void getProvinceAndCity(String addr)
    {
    	province="";
    	city="";
    	
    	int end1=addr.indexOf("ʡ");
    	int end2=addr.indexOf("��");
    	if(end1>0)
    	{
    		province=addr.substring(0, end1);
    		if(end2>0)
    		{
    			city=addr.substring(end1+1, end2);
    		}
    	}
    	else
    	{
			if(end2>0)
			{
				province=addr.substring(0, end2);
			}
		}
    }
	
	//�ڵ�ͼ�ϱ�ص�ʱ��ʾ�ľ���ص���Ϣ��Ŀǰ��ֱϽ�к�������
	public String getSpecialLocation()
	{
		String rstString="";
		
		if(province!="")
		{
			if(city!="")
			{
				rstString=city;
			}
			else
			{
				rstString=province;
			}		
		}
		return rstString;
	}
	
	//�ڵ�ͼ����ӱ�ǣ�pName�ǵص����ƣ�
	public void AddMarker(LatLng position, String pName)
    {
//    	Marker markerFix = mapView.addMarker(new MarkerOptions()
//    	.position(position)
//    	.title(pName)
//    	.anchor(0.5f, 0.5f)
//    	.icon(BitmapDescriptorFactory
//    	        .defaultMarker())
//    	        .draggable(true));
//    	markerFix.showInfoWindow();// ����Ĭ����ʾһ��infowinfow
		Marker markerFix = mapView.addMarker(new MarkerOptions()
    	.position(position)
    	.anchor(0.5f, 0.5f)
    	.icon(BitmapDescriptorFactory
    	        .defaultMarker())
    	        .draggable(true));
    	markerFix.showInfoWindow();// ����Ĭ����ʾһ��infowinfow
    }
	
	//�����ļ�ҳ���ʼ������
	private void setFilePage()
	{
		listImageItem=new ArrayList<HashMap<String,Object>>();
		underImagesFiles=imagesFile.listFiles();
		for(int i=0;i<underImagesFiles.length;i++)
		{
			map=new HashMap<String, Object>();
			map.put("ItemimageView", R.drawable.folder);
			map.put("ItemtextView", underImagesFiles[i].getName());
			listImageItem.add(map);
		}
		
		
		SaItemSimpleAdapter=new SimpleAdapter(this, listImageItem, R.layout.file_item, new String[] {"ItemimageView","ItemtextView"}, new int[] {R.id.ItemimageView,R.id.ItemtextView});
		fileGridView.setAdapter(SaItemSimpleAdapter);
		fileGridView.setOnItemClickListener(new ItemClickListener()); 
	}
	
//	//����ļ���
//	private void addFolder(String address)
//	{
//		map=new HashMap<String, Object>();
//		map.put("ItemimageView", R.drawable.folder);
//		map.put("ItemtextView", address);
//		listImageItem.add(map);
//		SaItemSimpleAdapter=new SimpleAdapter(this, listImageItem, R.layout.file_item, new String[] {"ItemimageView","ItemtextView"}, new int[] {R.id.ItemimageView,R.id.ItemtextView});
//		SaItemSimpleAdapter.notifyDataSetChanged();
//		fileGridView.invalidateViews();
//	}
//	
//	//���ͼƬ�ļ�
//	private void addPictureFile(String pictureName)
//	{
//		map=new HashMap<String, Object>();
//		map.put("ItemimageView", R.drawable.image);
//		map.put("ItemtextView", pictureName);
//		listImageItem.add(map);
//		SaItemSimpleAdapter=new SimpleAdapter(this, listImageItem, R.layout.file_item, new String[] {"ItemimageView","ItemtextView"}, new int[] {R.id.ItemimageView,R.id.ItemtextView});
//		SaItemSimpleAdapter.notifyDataSetChanged();
//		fileGridView.invalidateViews();
//	}
//	
	//�����ļ���,���߷�����һ��
	private void enterFolder(String folderName, String backPath)
	{
		//�������gridview
		listImageItem.clear();
		SaItemSimpleAdapter.notifyDataSetChanged();
		
		File firstClassSubFolder=null;
		if(folderName!="")
		{
			currentPath=currentPath+"/"+folderName;
			firstClassSubFolder=new File(currentPath);//����һ���ļ��У���ʡ�����
			currentLevel++;//�����ļ��У���ǰ�ļ��в�μ�1
		}
		else
		{
			firstClassSubFolder=new File(backPath);
			currentLevel--;
		}
		/*
		 * �������һ���ļ��к�ֱ����ͼƬ�б�
		 */
		File[] tempPictureFiles=firstClassSubFolder.listFiles();
		for(int i=0;i<tempPictureFiles.length;i++)
		{
			map=new HashMap<String, Object>();
			if(tempPictureFiles[i].isFile())
			{
				map.put("ItemimageView", R.drawable.image);
			}
			else
			{
				map.put("ItemimageView", R.drawable.folder);
			}
			map.put("ItemtextView", tempPictureFiles[i].getName());
			listImageItem.add(map);
		}
		SaItemSimpleAdapter=new SimpleAdapter(this, listImageItem, R.layout.file_item, new String[] {"ItemimageView","ItemtextView"}, new int[] {R.id.ItemimageView,R.id.ItemtextView});
		fileGridView.setAdapter(SaItemSimpleAdapter);
		fileGridView.invalidateViews();
		
	}
	
	//�������ؼ�,�����ļ�ҳ��ķ�����Ҫ�������£�������һ��ҳ�棨ҳ��ˢ�£�������currentPath
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{	 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0)
        {
        	if(currentLevel>0)
        	{
        		 int lastIndex=currentPath.lastIndexOf("/");//�ҵ����һ����/�����±�
	           	 currentPath=currentPath.substring(0, lastIndex);//����currentPath
	           	 enterFolder("",currentPath);//������һ��ҳ��
	           	 return true;
        	}
        }
        return super.onKeyDown(keyCode, event);
     }
	
	//���ñ���Ӧ�ô��ļ�
	private void openFile(File file){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//����intent��Action���� 
		intent.setAction(Intent.ACTION_VIEW);
		//��ȡ�ļ�file��MIME���� 
		String type = getMIMEType(file);
		//����intent��data��Type���ԡ� 
		intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
		//��ת 
		startActivity(intent);
	}
	
	private String getMIMEType(File file) {
		String type="*/*";
		String fName = file.getName();
		//��ȡ��׺��ǰ�ķָ���"."��fName�е�λ�á� 
		int dotIndex = fName.lastIndexOf(".");
		if(dotIndex < 0){
		return type;
		}
		/* ��ȡ�ļ��ĺ�׺�� */
		String end=fName.substring(dotIndex,fName.length()).toLowerCase();
		if(end=="")return type;
		//��MIME���ļ����͵�ƥ������ҵ���Ӧ��MIME���͡� 
		for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??��������һ�������ʣ����MIME_MapTable��ʲô�� 
		if(end.equals(MIME_MapTable[i][0]))
		type = MIME_MapTable[i][1];
		}
		return type;
	}
	
	private class ItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
		{  
			//�ڱ�����arg2=arg3  
			HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);  
			//��ȡ�������ImageView������
			String tempGetName=(String)item.get("ItemtextView");
			/*
			 * �ж�����������ļ��л����ļ�
			 */
			File tellFile=new File(currentPath+"/"+tempGetName);
			if(tellFile.isDirectory())//������ļ���
			{
				enterFolder((String)item.get("ItemtextView"),"");
			}
			else
			{
				openFile(tellFile);//�����ͼƬ�ļ������ͼƬ
			}
			
		}
	}
	
	

}
