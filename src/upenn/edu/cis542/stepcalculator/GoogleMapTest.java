package upenn.edu.cis542.stepcalculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;



public class GoogleMapTest extends MapActivity {
    

	private MapView mapView;
	private MapController mapController;
	private String provider;
	private LocationManager locationManager;
	private LocationListener locationListener;
	Location location;
	LocationManager manager;
	GeoPoint geoPoint,startPoint;//start Point indicates where you start
	Bitmap bitmap;
	ArrayList geoArrayList;

	private GeoPoint gpoint1, gpoint2, gpoint3;// 连线的点 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemaptest);
        mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);
		
		mapController = mapView.getController();
		mapController.setZoom(13);
		openGPSSettings() ;
		getLocation();
		
		manager=(LocationManager)getSystemService(LOCATION_SERVICE);
	    location=getLastKnownLocation( );
	    //initiate position point array
	    geoArrayList = new ArrayList<GeoPoint>();
	    
	    gpoint1 = new GeoPoint((int) (53.477384 * 1000000),   
                (int) (68.158216 * 1000000));   
        gpoint2 = new GeoPoint((int) (34.488967 * 1000000),   
                (int) (118.144277 * 1000000));   
        gpoint3 = new GeoPoint((int) (68.491091 * 1000000),   
                (int) (130.136781 * 1000000)); 
       
        
     //   geoArrayList.add(gpoint1);
     //   geoArrayList.add(gpoint2);
     //   geoArrayList.add(gpoint3);
	    
	    if(location!=null){
	    	Toast.makeText(GoogleMapTest.this, "I get location!"+ location.getLatitude() +" "+location.getLatitude(), 10).show();
	    //	updateToNewLocation(location);
	    	getPoint(location);
	    	startPoint = geoPoint;
	    }
	    else
	    	Toast.makeText(GoogleMapTest.this, "no location to show",10).show();
	    
	    
        bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.mapmarker);//生成bitmap位图
   
        
        mapView.setBuiltInZoomControls(true);//实现放大缩小功能
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 6, new LocationListener() {
			
			@Override 
				public void onStatusChanged(String provider, int status, Bundle extras) {
					// TODO Auto-generated method stub
					
				}
				 
				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					Toast.makeText(GoogleMapTest.this, "location change", 3).show();
				    getPoint(location);
				}
			});
    }
    public void getPoint(Location location)
    {
    	 mapController=mapView.getController();
         //注意参数是纬度，经度。E6为10的6次方
//       GeoPoint geoPoint=new GeoPoint((int)(37.898989*1E6), (int)(122.989898*1E6));
         geoPoint=new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
         geoArrayList.add(geoPoint);
         
         Log.d("geoPoint","get point!!!geopoint = "+geoPoint);
         //定位到哪个点
         mapController.animateTo(geoPoint);
         //获得悬浮图层
         List<Overlay> list = mapView.getOverlays();//获得MapView上原有的overlay对象
         list.clear();//清除所有的overlay对象
         list.add(new MyOverLay());//加新获取的overlay对象
    }

	class MyOverLay extends Overlay
	{  
		//画标记的方法
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);
			Projection projection=mapView.getProjection();
			// 画笔   
            Paint paint = new Paint();   
            paint.setColor(Color.BLUE);   
            paint.setDither(true);   
            paint.setStyle(Paint.Style.STROKE);   
            paint.setStrokeJoin(Paint.Join.ROUND);   
            paint.setStrokeCap(Paint.Cap.ROUND);   
            paint.setStrokeWidth(4);   
           // Projection projection = mapView.getProjection();  
            
            //if drawing path
            Point p1 = new Point();  
            Path path = new Path();  
            if(geoArrayList.size()>1){
	            //receive routh points from geoArrayList
				Iterator<GeoPoint> it = geoArrayList.iterator();
				//set the first point
				//if it has first point
				if(it.hasNext()){
					projection.toPixels(it.next(), p1); 
					path.moveTo(p1.x, p1.y);   
			
				//set the rest points(from the 2nd)
				while (it.hasNext()){
					projection.toPixels(it.next(), p1);  
					path.lineTo(p1.x, p1.y);  
	           
				}
				}
	 
	            canvas.drawPath(path, paint);// 画出路径   
            }
			Toast.makeText(GoogleMapTest.this,"draw!!!!geopoint size is : "+ geoArrayList.size(),10).show();
			Point point=new Point();//定义输出的像素点
			projection.toPixels(startPoint, point);//地理坐标转为像素坐标
			//绘制图片
			//bitmap, left, top, paint
			canvas.drawBitmap(bitmap, point.x-(bitmap.getWidth()/2), point.y-(bitmap.getHeight()), null);
		} 
	}
   
    private void openGPSSettings() {
    	LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
    		Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	else{
	    	Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
	    	Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
	    	startActivityForResult(intent,0); //此为设置完成后返回到获取界面    
    	}
    }
    
    private void getLocation()    {
    	// 获取位置管理服务
        String serviceName = Context.LOCATION_SERVICE;
    	locationManager = (LocationManager) this.getSystemService(serviceName);
    	//locationManager.setTestProviderEnabled("gps", true);
    	
    	// 查找到服务信息
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	// 高精度
    	criteria.setAltitudeRequired(false);
    	criteria.setBearingRequired(false);
    	criteria.setCostAllowed(true); 
    	criteria.setPowerRequirement(Criteria.POWER_LOW);
    	// 低功耗
    		
    	provider = locationManager.getBestProvider(criteria, true);
    	
    	
    	locationListener=new LocationListener(){
        	public void onLocationChanged(Location location){
        		updateToNewLocation(location);
        		//locationManager.removeUpdates(this);
        		//locationManager.setTestProviderEnabled(provider, false);
        	}
        	
        	public void onProviderDisabled(String provider){
        		updateToNewLocation(null);
        	}
        	
        	public void onProviderEnabled(String provider){	
        		
        	}
        	public void onStatusChanged(String provider,int status,Bundle extras){
        		
        	}
        };
        
    	// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米 
    	//locationManager.requestLocationUpdates(provider, 5*1000, 0,locationListener);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,0,locationListener);
          locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,0,locationListener);
        
    }

    private Location getLastKnownLocation() {
		List<String> providers = manager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = manager.getLastKnownLocation(provider);
		 //  Toast.makeText(GoogleMapTest.this,"last known location, provider: "+ provider + "location: "+l,10).show();

			if (l == null) {
				continue;
			}
			if (bestLocation == null
					|| l.getAccuracy() < bestLocation.getAccuracy()) {
			//   Toast.makeText(GoogleMapTest.this,"found best last known location: "+l,10).show();
				bestLocation = l;
			}
		}
		if (bestLocation == null) {
			return null;
		}
		return bestLocation;
	}

    
    
    private void updateToNewLocation(Location location) {
    	Toast.makeText(this, "update", 10).show();
    	if (location != null) {
    		double  latitude = (double)(location.getLatitude()*1E6); 
    		double longitude= (double)(location.getLongitude()*1E6);
    		Toast.makeText(this, "维度：" +  latitude+ "\n经度" + longitude, Toast.LENGTH_SHORT).show();
    		
    		GeoPoint p = new GeoPoint((int)latitude, (int) longitude);
    		mapController.animateTo(p);

    	} else {
    		Toast.makeText(this, "无法获取地理信息", Toast.LENGTH_SHORT).show();
    	}
    }
    
   
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onDestroy(){
		locationManager.removeUpdates(locationListener);
		//locationManager.setTestProviderEnabled(provider, false);
		super.onDestroy();
	}
	public void resetPosition(View view){
		double  latitude = (double)(location.getLatitude()*1E6); 
		double longitude= (double)(location.getLongitude()*1E6);
	//	Toast.makeText(this, "维度：" +  latitude+ "\n经度" + longitude, Toast.LENGTH_SHORT).show();
		
		GeoPoint p = new GeoPoint((int)latitude, (int) longitude);
		Log.d("geoPoint","geopoint = "+ p);
		mapController.animateTo(p);

	}
	public void gotoHistoryItem(View view){
		  Intent newAct = new Intent();
          newAct.setClass(this, HistoryActivity.class );
          startActivity( newAct );
	}
    
}