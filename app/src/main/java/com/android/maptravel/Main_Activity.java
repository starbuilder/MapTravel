package com.android.maptravel;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.android.maptravel.R.color;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Main_Activity extends ActionBarActivity{
	private DrawerLayout layDrawer;
	private Button[] bt_mapType;  
	GoogleMap googleMap;
	int curMapType = 0;
	int curLatitude,curLongitude;
	int curZoom = 21;
	LocationManager locationMgr;
	Button bt_takeptr;
	boolean istrack = false;
	TextView tv_gpsMsg;
	ListView mListView;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	List<Address> maddressList = new ArrayList<Address>();
	AddressAdapter mAddressAdapter;
	boolean isClickMyLocation = false;
    Toolbar mToolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        initActionBar();
		bt_mapType = new Button[5];
		bt_mapType[0] = (Button)findViewById(R.id.button1);
		bt_mapType[1] = (Button)findViewById(R.id.button2);
		bt_mapType[2] = (Button)findViewById(R.id.button3);
		bt_mapType[3] = (Button)findViewById(R.id.button4);
		bt_mapType[4] = (Button)findViewById(R.id.bt_streetview);
		bt_takeptr = (Button)findViewById(R.id.button7);
		tv_gpsMsg = (TextView)findViewById(R.id.gps_msg);
		mListView = (ListView)findViewById(R.id.listView1);
		mAddressAdapter = new AddressAdapter(this);
		mListView.setAdapter(mAddressAdapter);
		String[] mapType_array = getResources().getStringArray(R.array.map_type_array);
		for(int i=0;i<mapType_array.length;i++)
		{
			bt_mapType[i].setText(mapType_array[i]);
			bt_mapType[i].setTag(i);
			bt_mapType[i].setSelected(i==curMapType);
			bt_mapType[i].setOnClickListener(new changeMapTypeEvent());		
		}
		bt_mapType[4].setTag(4);
		bt_mapType[4].setOnClickListener(new changeMapTypeEvent());	
		initDrawer();
		initilizeMap();
		mDrawerToggle.onDrawerOpened(findViewById(R.id.drawer));
		bt_takeptr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(layDrawer.isShown())
					layDrawer.closeDrawers();
				takeMapPtr();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}
	private void takeMapPtr(){
		if(googleMap!=null)
			googleMap.snapshot( new GoogleMap.SnapshotReadyCallback() {

				@Override
				public void onSnapshotReady(Bitmap ptr) {
					// TODO Auto-generated method stub
					if(ptr!=null)
						showMapDialog(ptr);

				}
			});
	}
	public void showMapDialog (Bitmap ptr){
		AlertDialog.Builder ab =new AlertDialog.Builder(this);
		ImageView iv = new ImageView(this);
		ab.setView(iv);
		iv.setScaleType(ScaleType.FIT_CENTER);
		iv.setImageBitmap(ptr);
		ab.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		ab.create().show();
	}
	private class AddressAdapter extends BaseAdapter{
		Context context;
		public AddressAdapter(Context context){
			this.context = context;

		}  
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return maddressList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		public class ViewHolder{
			TextView tv1;
			TextView tv2;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh;
			if(convertView==null)
			{
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.addresstipitem, null);
				vh.tv1 = (TextView)convertView.findViewById(R.id.textView1);
				vh.tv2 = (TextView)convertView.findViewById(R.id.textView2);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			if(position<maddressList.size())
			{
				Address address = maddressList.get(position);
				if(address.getMaxAddressLineIndex()>0)
				{
					vh.tv1.setText(address.getAddressLine(0));
					vh.tv2.setText(address.getUrl());
				}
			}
			return convertView;
		}

	}
	public List<Address>  getAddress(String KeyWorld){
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> address = new ArrayList<Address>();
		try{
			address = geocoder.getFromLocationName(KeyWorld, 1);    
		}catch(Exception e){

		}
		return  address;
	}
	public Address getAddress(double latitude, double longitude){
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try{
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			Address address = addresses.get(0);
			return address;

		}catch(Exception e){

		}
		return null;

	}
	public void initActionBar(){		
		mTitle  = getTitle();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

	}
	public void initDrawer(){
		layDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		layDrawer.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
				layDrawer, /* DrawerLayout object */
				R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open, /* "open drawer" description for accessibility */
				R.string.drawer_close /* "close drawer" description for accessibility */
				){
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}
		};
		layDrawer.setDrawerListener(mDrawerToggle);
	}
	private void initilizeMap() {
		Fragment mFragment = getFragmentManager().findFragmentById(
				R.id.map);
		if (googleMap == null) {
			googleMap = ((MapFragment) mFragment).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}else{


				setmapType(curMapType);
				// Showing / hiding your current location
				googleMap.setMyLocationEnabled(true);

				// Enable / Disable zooming controls
				googleMap.getUiSettings().setZoomControlsEnabled(false);

				// Enable / Disable my location button
				googleMap.getUiSettings().setMyLocationButtonEnabled(true);

				// Enable / Disable Compass icon
				googleMap.getUiSettings().setCompassEnabled(true);

				// Enable / Disable Rotate gesture
				googleMap.getUiSettings().setRotateGesturesEnabled(true);

				// Enable / Disable zooming functionality
				googleMap.getUiSettings().setZoomGesturesEnabled(true);

				if(getLocationProvider())
				{
					Location mylocation = googleMap.getMyLocation();
					if(mylocation!=null)
						setCarmaraLocation(mylocation.getLatitude(), mylocation.getLongitude(),curZoom);
				}
				View locationButton = ((View) mFragment.getView().findViewById(1).getParent()).findViewById(2);
				RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
				rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
				rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
				rlp.setMargins(0, 0, 30, 30);
                googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location mylocation) {
                        // TODO Auto-generated method stub
                        setCarmaraLocation(mylocation.getLatitude(),mylocation.getLongitude(),curZoom);
                    }
                });
                googleMap.setOnMapClickListener(new OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng arg0) {
                        // TODO Auto-generated method stub

                        addMarker(arg0.latitude,arg0.longitude,getAddress(arg0.latitude,arg0.longitude));
                        setCarmaraLocation(arg0.latitude,arg0.longitude,curZoom);

                    }
                });
                googleMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

                    @Override
                    public boolean onMyLocationButtonClick() {
                        // TODO Auto-generated method stub
                        isClickMyLocation = true;
                        return false;
                    }
                });
                googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker arg0) {
                        // TODO Auto-generated method stub

                        toStreetView(arg0.getPosition().latitude,arg0.getPosition().longitude);
                        return false;
                    }
                });


            }
		}
	}
	String provider = "";
	private boolean getLocationProvider() {
		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
			return true;
		}
		return false;
	}
	
	public void clearMarker(){
		if(googleMap!=null)	
			googleMap.clear();
	}
	List<MarkerOptions> mMarkerList;
	public void addMarker(double latitude, double longitude,Address maddress ){

		MarkerOptions marker = new MarkerOptions().position(
				new LatLng(latitude, longitude));
		if(maddress!=null)
		{
			if(maddress.getMaxAddressLineIndex()!=-1)
				marker.title(maddress.getAddressLine(0));
			marker.snippet(maddress.getUrl());
		}
		marker.flat(true);
		marker.draggable(true);
		googleMap.clear();
		googleMap.addMarker(marker);
		
	}
	//�]�w��e��m
	public void setCarmaraLocation(double latitude, double longitude,int zoomValue){	
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(latitude,longitude)).zoom(zoomValue).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
		googleMap.stopAnimation();
	}
	private class changeMapTypeEvent implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int type = (Integer) v.getTag();
			if(curMapType != type&&type!=4)
			{
				curMapType = type;
				if(bt_mapType!=null)
					for(int i = 0;i<bt_mapType.length;i++)
					{
						bt_mapType[i].setSelected(type==i);
					}
				setmapType(curMapType);
				if(layDrawer.isShown())
					layDrawer.closeDrawers();
			}else if(type == 4){
				setmapType(type);
				if(layDrawer.isShown())
					layDrawer.closeDrawers();
			}
		}

	}
	/**�]�wgoogle map����*/
	private void setmapType(int type){
		switch (type) {
		case 0:
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case 1:
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case 2:
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case 3:
			googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		case 4:
			Location mylocation = googleMap.getMyLocation();
			if(mylocation!=null)
				toStreetView(mylocation.getLatitude(), mylocation.getLongitude());
			break;
		default:
			break;
		}
	}

	/**��*/
	private void toStreetView(double latitude, double longitude){
		String struri = "google.streetview:cbll="+latitude+","+longitude;
		Intent it = new Intent(Intent.ACTION_VIEW,Uri.parse(struri));
		startActivity(it);
	}
	

}
