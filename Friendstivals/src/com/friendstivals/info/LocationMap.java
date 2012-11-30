package com.friendstivals.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;

import com.friendstivals.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationMap extends MapActivity implements LocationListener {
	private GeoPoint point;
	private String direc;
	private String festivalName;
	private MyOverlay itemizedoverlay;
	private List<Overlay> mapOverlays;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map);

		Bundle b = getIntent().getExtras();
		direc = b.getString("address");
		festivalName = b.getString("festival_name"); 
		MapView mapView = (MapView) findViewById(R.id.mapview);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.ic_launcher);
		itemizedoverlay = new MyOverlay(drawable, this);
		search(direc);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {}	

	public void onProviderEnabled(String provider) {}

	private void search(String direccion){
		MapView mapView = (MapView) findViewById(R.id.mapview);
		MapController controller = mapView.getController();
		Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = geoCoder.getFromLocationName(direccion + ",Chile",5);
			if(addresses.size() > 0){
				point = new GeoPoint((int)(addresses.get(0).getLatitude()*1E6),(int) (addresses.get(0).getLongitude() * 1E6));
				OverlayItem overlayitem = new OverlayItem(point, festivalName, direc);
				controller.animateTo(point);
				controller.setZoom(12);
				itemizedoverlay.addOverlay(overlayitem);
				mapOverlays.add(itemizedoverlay);
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Accion cuando el servicio de GPS no esta encendido. 
	 */
	public void onProviderDisabled(String provider) {
		Intent intent = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	@SuppressWarnings("rawtypes")
	class MyOverlay extends ItemizedOverlay {
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		private Context mContext;

		public MyOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
		}

		public MyOverlay(Drawable marker) {
			super(boundCenterBottom(marker));
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}

		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
			populate();
		}
		
		@Override
		protected boolean onTap(int index) {
		  OverlayItem item = mOverlays.get(index);
		  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		  dialog.setTitle(item.getTitle());
		  dialog.setMessage(item.getSnippet());
		  dialog.show();
		  return true;
		}
	}	
public void onLocationChanged(Location location) {
}
}