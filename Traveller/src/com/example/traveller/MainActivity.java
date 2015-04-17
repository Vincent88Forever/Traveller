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
	public ViewPager viewPager;//对应的viewPager
	public int current_page=0;//当前页码的下标
	private List<View> viewList;//view数组
	private List<ImageView> imageList;//存放图片
	
	//public BottomBar bottomBar;
	
	private String savedPictureName;//相机拍照后保存的图片名称
	private FileOutputStream fos;//文件输出流
	private Bitmap bitmap;
	private File imagesFile;//创建保存图片的文件夹
	private File savePositionFile;//保存地理位置坐标
	private File[] underImagesFiles;//获取imagesFile下的所有文件夹
	private String sdPath=Environment.getExternalStorageDirectory().toString();//获取sd卡的路径 
	private String pictureFolderPath="/storage/sdcard1/Traveller/images/";//存放拍照的图片的文件夹根路径
	private String positionSaveString="/storage/sdcard1/Traveller/position.txt";//保存拍照时获取到的地理位置信息的文件路径
	private String pictureFullPath;//拍摄的照片的全路径
	private String currentPath="/storage/sdcard1/Traveller/images";//保存当前路径
	private int currentLevel=0;//保存当前文件夹的层次
	
	private GridView fileGridView;
	private ArrayList<HashMap<String, Object>> listImageItem;
	private HashMap<String, Object> map;
	private SimpleAdapter SaItemSimpleAdapter;
	
	//照片包含的信息
	private LocationManager locationManager=null;
	private Location location=null;
	private float latitude=29.060973f;//纬度
	private float longitude=117.126208f;//经度
	private String address;//位置描述，非地理坐标
	private float[][] positionBeen;//存放txt文件里的所有坐标信息，例如positionBeen[x][0]保存纬度，positionBeen[0][1]保存经度
	public String province;
	public String city;
	private ExifInterface exif;
	
	//与腾讯地图相关的全局变量
	public TencentSearch api;
	public MapView mapView=null;
	public MyMapView myMapView=null;
	
	private File savedFile;//保存图片的缓存文件
	private FileService fileService;
	
	//在调用getAddress时有两种情况，第一种是拍照时获取到了坐标，然后调用；第二种是软件启动时，读取位置文件里面的坐标时，调用getAddress
	private static final int FIRSTMODE=0;
	private static final int SECONDMODE=1;
	
	private String specialLocationString="";
	
	private final String[][] MIME_MapTable={
		    //{后缀名，    MIME类型}
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
        
        api=new TencentSearch(this);//用于解析地址信息的
            
    	setViewPage();
        setImageView();
       
        mapView.onCreate(savedInstanceState);
        mapView.getController().setZoom(4);//设置地图比例
       
        try {
			createFiles();//创建文件夹或者文件
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
     * 对MapView的生命周期进行管理
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
    
    //使用了ViewPager之后需要添加page进去
    public void setViewPage()
    {
    	viewPager=(ViewPager)findViewById(R.id.viewpager);
        LayoutInflater inflater=getLayoutInflater();
        
        filepageView=inflater.inflate(R.layout.filepage, null);
        infolistView=inflater.inflate(R.layout.infolist, null);
        footprintmapView=inflater.inflate(R.layout.footprintmap, null);
        aboutmeView=inflater.inflate(R.layout.aboutme, null);       
        
        /*
         * 监听viewpage的内部控件
         */
        ImageView cameraImageView=(ImageView)infolistView.findViewById(R.id.CameraImageView);
        /*
         * 点击相机图标，调用相机
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
        
        viewList=new ArrayList<View>();//将要分页显示的view装入数组中
        viewList.add(filepageView);
        viewList.add(infolistView);
        viewList.add(footprintmapView);
        viewList.add(aboutmeView);       
        
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
		viewPager.setCurrentItem(1);//初始化启动时默认显示为第二页
		viewPager.setOnPageChangeListener(pageChangeListener);
		
		//设置viewPager的过度切换时间
		ViewPagerScroller scroller = new ViewPagerScroller(getBaseContext());
		scroller.setScrollDuration(0);
		scroller.initViewPagerScroll(viewPager); 
    }
    
    //添加最下面的跳转菜单栏
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
    	
    	//bottomBar.setBottomBar();
    	//bottomBar.setClickEvent(); 	
//		Toast.makeText(getApplicationContext(), String.valueOf(bottomBar.imageList.size()),
//	     Toast.LENGTH_SHORT).show(); 
    	
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
			bitmap = BitmapFactory.decodeFile(pictureFolderPath + "camera.jpg");
        	
            try {
            	if(GetLocation())//获取到了当前位置
            	{
            		//将拍摄照片时获取到的坐标位置写入文件
            		String write_str=String.valueOf(latitude)+"/"+String.valueOf(longitude)+"\n";
            		fileService.savePosition(savePositionFile, write_str);
            		
            		//将坐标位置转化为具体地址信息
            		GetAddress(FIRSTMODE,latitude,longitude);
            	}
            	else
            	{
            		Toast.makeText(getApplicationContext(), "暂时获取不到地理位置，请打开GPS",
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
	
	//创建文件
	public void createFiles() throws IOException 
	{
		imagesFile=new File(pictureFolderPath);//本机测试文件路径
		//判断当前路径的文件夹是否存在
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
	 * 接下来需要判断是否生成文件夹，并且返回当前拍摄的照片应该返回的文件夹地址
	 */
	public String createSubFolder()
	{
		if(province!="")//如果是某一个省份
		{
			String provincePath=pictureFolderPath+province+"/";
			File tempProvinceFile=new File(provincePath);
			if(tempProvinceFile.exists())//查看是否存在该省份的文件夹，如果存在
			{
				if(city!="")//如果还是一个城市
				{
					String cityPath=provincePath+city+"/";
					File tempCityFile=new File(cityPath);
					if(tempCityFile.exists())//查看在该省份文件夹下是否存在该城市的文件夹，如果存在
					{
						
					}
					else
					{
						tempCityFile.mkdirs();//如果不存在该城市，则建立该文件夹
					}
					return cityPath;
				}
				return provincePath;
			}
			else
			{
				tempProvinceFile.mkdirs();//如果不存在该省份，则建立该省份的文件夹
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
		return pictureFolderPath+"非中国大陆地区"+"/";
	}
	
	//针对未打开GPS功能，所拍摄的图片，应该保存的位置
	public void saveImagesWithoutGPS()
	{
		String unknownArea=pictureFolderPath+"未知地区/";
		File tempFile=new File(unknownArea);
		if(tempFile.exists())
		{
			pictureFullPath=unknownArea+savedPictureName;//构造图片的全路径
            File pictureFile = new File(pictureFullPath); 
        	try {
				fos=new FileOutputStream(pictureFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);//把数据写入文件
        	File cacheImageFile=new File(pictureFolderPath+"camera.jpg");
        	cacheImageFile.delete();
		}
		else
		{
			tempFile.mkdirs();
			pictureFullPath=unknownArea+savedPictureName;//构造图片的全路径
            File pictureFile = new File(pictureFullPath); 
        	try {
				fos=new FileOutputStream(pictureFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);//把数据写入文件
        	File cacheImageFile=new File(pictureFolderPath+"camera.jpg");
        	cacheImageFile.delete();
		}
	}
	
	
	
	//读取照片的信息
	private void readEXIF(String path) throws IOException
	{
		exif = new ExifInterface(path);
    	/*
    	 * 在获取照片的经纬度时出了问题
    	 */
		String datetimeString=exif.getAttribute(ExifInterface.TAG_DATETIME);
        Log.v("latitude",datetimeString);
	}
	
	//通过GPS或NetWork来获取实时位置的经纬度
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
					
					//Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
					@Override
					public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
						// TODO Auto-generated method stub
						
					}
					
					//Provider被enable时触发此函数，比如GPS被打开
					@Override
					public void onProviderEnabled(String arg0) {
						// TODO Auto-generated method stub
						
					}
					
					//Provider被disable时触发此函数，比如GPS被关闭
					@Override
					public void onProviderDisabled(String arg0) {
						// TODO Auto-generated method stub
						
					}
					
					//当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
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
		               latitude = (float) location2.getLatitude(); //经度     
		               longitude = (float) location2.getLongitude(); //纬度
		               Log.v("Latitude",String.valueOf(latitude));
		               return true;
		        }   
			}
		}
		
		return false;
	}
	
	//获取position.txt文本里面的内容，将坐标存放在二维数组里面
	private void getTextContent() throws Exception
	{
		positionBeen=new float[200][2];
		positionBeen=fileService.readFileOnLine(positionSaveString);
	}
	
	//根据经纬度坐标转化为地址信息
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
					                pictureFullPath=tempPath+savedPictureName;//构造图片的全路径
					                
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
					            	bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);//把数据写入文件
					            	File cacheImageFile=new File(pictureFolderPath+"camera.jpg");
					            	cacheImageFile.delete();//删除缓存文件“camera.jpg”
					            	
					            	//进行地图标注
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
	
	//从得到的地址中提取省份和市
	public void getProvinceAndCity(String addr)
    {
    	province="";
    	city="";
    	
    	int end1=addr.indexOf("省");
    	int end2=addr.indexOf("市");
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
	
	//在地图上标地点时显示的具体地点信息，目前有直辖市和市两种
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
	
	//在地图上添加标记（pName是地点名称）
	public void AddMarker(LatLng position, String pName)
    {
//    	Marker markerFix = mapView.addMarker(new MarkerOptions()
//    	.position(position)
//    	.title(pName)
//    	.anchor(0.5f, 0.5f)
//    	.icon(BitmapDescriptorFactory
//    	        .defaultMarker())
//    	        .draggable(true));
//    	markerFix.showInfoWindow();// 设置默认显示一个infowinfow
		Marker markerFix = mapView.addMarker(new MarkerOptions()
    	.position(position)
    	.anchor(0.5f, 0.5f)
    	.icon(BitmapDescriptorFactory
    	        .defaultMarker())
    	        .draggable(true));
    	markerFix.showInfoWindow();// 设置默认显示一个infowinfow
    }
	
	//设置文件页面初始化布局
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
	
//	//添加文件夹
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
//	//添加图片文件
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
	//进入文件夹,或者返回上一级
	private void enterFolder(String folderName, String backPath)
	{
		//首先清空gridview
		listImageItem.clear();
		SaItemSimpleAdapter.notifyDataSetChanged();
		
		File firstClassSubFolder=null;
		if(folderName!="")
		{
			currentPath=currentPath+"/"+folderName;
			firstClassSubFolder=new File(currentPath);//创建一级文件夹，即省级别的
			currentLevel++;//进入文件夹，当前文件夹层次加1
		}
		else
		{
			firstClassSubFolder=new File(backPath);
			currentLevel--;
		}
		/*
		 * 如果进入一级文件夹后直接是图片列表
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
	
	//监听返回键,对于文件页面的返回主要有两件事：返回上一级页面（页面刷新）；更新currentPath
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{	 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0)
        {
        	if(currentLevel>0)
        	{
        		 int lastIndex=currentPath.lastIndexOf("/");//找到最后一个‘/’的下标
	           	 currentPath=currentPath.substring(0, lastIndex);//更新currentPath
	           	 enterFolder("",currentPath);//返回上一级页面
	           	 return true;
        	}
        }
        return super.onKeyDown(keyCode, event);
     }
	
	//调用本地应用打开文件
	private void openFile(File file){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//设置intent的Action属性 
		intent.setAction(Intent.ACTION_VIEW);
		//获取文件file的MIME类型 
		String type = getMIMEType(file);
		//设置intent的data和Type属性。 
		intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
		//跳转 
		startActivity(intent);
	}
	
	private String getMIMEType(File file) {
		String type="*/*";
		String fName = file.getName();
		//获取后缀名前的分隔符"."在fName中的位置。 
		int dotIndex = fName.lastIndexOf(".");
		if(dotIndex < 0){
		return type;
		}
		/* 获取文件的后缀名 */
		String end=fName.substring(dotIndex,fName.length()).toLowerCase();
		if(end=="")return type;
		//在MIME和文件类型的匹配表中找到对应的MIME类型。 
		for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？ 
		if(end.equals(MIME_MapTable[i][0]))
		type = MIME_MapTable[i][1];
		}
		return type;
	}
	
	private class ItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
		{  
			//在本例中arg2=arg3  
			HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);  
			//获取所点击的ImageView的名称
			String tempGetName=(String)item.get("ItemtextView");
			/*
			 * 判断所点击的是文件夹还是文件
			 */
			File tellFile=new File(currentPath+"/"+tempGetName);
			if(tellFile.isDirectory())//如果是文件夹
			{
				enterFolder((String)item.get("ItemtextView"),"");
			}
			else
			{
				openFile(tellFile);//如果是图片文件，则打开图片
			}
			
		}
	}
	
	

}
