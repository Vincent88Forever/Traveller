package com.example.traveller;

import org.apache.http.Header;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.Address2GeoParam;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.result.Address2GeoResultObject;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyLocation extends Activity{
	public  TencentSearch api;
	public String resultAddress="";
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Toast.makeText(getApplicationContext(), "MyLocation has been created",
			     Toast.LENGTH_SHORT).show();
		api=new TencentSearch(this);		
	}
	
	public void GetAddress(float latitude, float longitude)
	{
		Geo2AddressParam param=new Geo2AddressParam().location(new com.tencent.lbssearch.object.Location().lat(latitude).lng(longitude));
		api.geo2address(param, new HttpResponseListener() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, BaseObject object) {
				// TODO Auto-generated method stub
				 if(object != null)
				 {
			            Geo2AddressResultObject oj = (Geo2AddressResultObject)object;
			            if(oj.result != null)
			            {
			                Log.v("demo","address:"+oj.result.address);
			                resultAddress += oj.result.address;
			            }
				 }
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
}
