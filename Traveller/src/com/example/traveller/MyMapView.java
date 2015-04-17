package com.example.traveller;

import android.os.Bundle;

import com.tencent.tencentmap.mapsdk.map.MapView;

public class MyMapView extends MainActivity{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mapView.onCreate(savedInstanceState);
	}
	
	@Override
    protected void onDestroy() {
		mapView.onDestroy();
        super.onDestroy();
    }
 
    @Override
    protected void onPause() {
    	mapView.onPause();
        super.onPause();
    }
 
    @Override
    protected void onResume() {
    	mapView.onResume();
        super.onResume();
    }
 
    @Override
    protected void onStop() {
    	mapView.onStop();
        super.onStop();
    }
}
