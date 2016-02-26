package uk.co.aspectica.whereami;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.aspectica.whereami.gps.GPSLocation;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity
{
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	static FragmentManager mFragmentManager;
	
	static GPSLocation mGpsLocation = new GPSLocation();
	
	static GPSAddressFragment mGpsFragment;
	
	static MapFragment mMapFragment;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.main);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// initialising the object of the FragmentManager.
	    mFragmentManager = getSupportFragmentManager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
	        
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    // Handle item selection
	    switch (item.getItemId())
	    {
	    case R.id.preferences:
	    {
	    	Intent settingsIntent = new Intent(this, SettingsActivity.class);
	        startActivity(settingsIntent);
	        
	        return super.onOptionsItemSelected(item);
	    }
	    case R.id.send_gps:
	    {
	    	final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "My Location");
            intent.putExtra(Intent.EXTRA_TEXT, "My location: " +
            				mGpsLocation.getAddressLine() + " " +
            				mGpsLocation.getCity() + " " +
            				mGpsLocation.getPostalCode() + " " +
            				mGpsLocation.getCountry() + "\n" +
//            				mGpsLocation.getFeatureName() + "\n" +
//            				mGpsLocation.getPhone() + "\n" +
//            				mGpsLocation.getUrl() + "\n" +
//            				mGpsLocation.getExtras() + "\n" +
            				"Time: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n" + 
            				"URL: http://www.google.com/maps/?q=" + mGpsLocation.getLatitude() + "," + mGpsLocation.getLongitude() +
            				"\n\n\nSent by Where Am I? app (https://play.google.com/store/apps/details?id=uk.co.aspectica.whereami)");            	
            startActivity(Intent.createChooser(intent, "Share Current Location"));
            
	        return super.onOptionsItemSelected(item);
	    }
	    case R.id.refresh_gps:
	    {
	    	if (mGpsFragment != null)
	    	{
	    		mGpsFragment.setup();
	    	}
	    	if (mMapFragment != null)
	    	{
	    		mMapFragment.setUpMapIfNeeded();
	    	}
	    	return super.onOptionsItemSelected(item);
	    }
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter 
	{
		public SectionsPagerAdapter(FragmentManager fm) 
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position) 
		{
			// getItem is called to instantiate the fragment for the given page.
			if (position == 0)
			{
				Fragment fragment = new GPSAddressFragment();
				Bundle args = new Bundle();
				fragment.setArguments(args);
				return fragment;
			}
			else if (position == 1)
			{
				Fragment fragment = new MapFragment();
				Bundle args = new Bundle();
				fragment.setArguments(args);
				return fragment;
			}
			return null;
		}

		@Override
		public int getCount()
		{
			// Show total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}
	
	//************************** GPS ADDRESS FRAGMENT ******************************************* START

	public static class GPSAddressFragment extends Fragment implements AdListener
	{
		private View mRootView;
		private Handler mHandler;
	    private LocationManager mLocationManager;
	    private boolean mGeocoderAvailable;
	    private boolean mUseFine = false;
	    private boolean mUseNetwork = false;
	    private AdLayout mAdview_banner;
		
		public GPSAddressFragment() 
		{
			mGpsFragment = this;
		}

		@Override
		public void onPause()
		{
			super.onPause();
		}

		@Override
		public void onResume()
		{
			super.onResume();
			
	        setup();
			
			mAdview_banner.loadAd(new AdTargetingOptions()); // async task to retrieve an ad
		}
		
		@Override
		public void onStop() 
		{
			super.onStop();
			
			mLocationManager.removeUpdates(listener);
		}
		
		@Override
		public void onStart()
		{
			super.onStart();
			
			LocationManager locationManager = (LocationManager) mRootView.getContext().getSystemService(Context.LOCATION_SERVICE);
	        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	        if (!gpsEnabled) 
	        {
	            // Build an alert dialog here that requests that the user enable
	            // the location services, then when the user clicks the "OK" button,
	            // call enableLocationSettings()
	            new EnableGpsDialogFragment().show(mFragmentManager, "enableGpsDialog");
	        }
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			mRootView = inflater.inflate(R.layout.fragment_gps_address, container, false);
			
			mGeocoderAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent();

			// Get a reference to the LocationManager object.
	        mLocationManager = (LocationManager) mRootView.getContext().getSystemService(Context.LOCATION_SERVICE);
	        
	        if (mLocationManager != null)
	        {
	        	mUseFine = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        	mUseNetwork = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	        }
	        
	        // Handler for updating text fields on the UI like the lat/long and address.
	        mHandler = new Handler() 
	        {
	            public void handleMessage(Message msg) 
	            {
	                switch (msg.what)
	                {
	                    case Consts.UPDATE_ADDRESS:
	                    {
	                    	mGpsLocation.update(((GPSLocation) msg.obj));
	                    	
	        				TextView textview = (TextView) mRootView.findViewById(R.id.fieldCountry);
	        				textview.setText(mGpsLocation.getCountry());

	        				textview = (TextView) mRootView.findViewById(R.id.fieldCity);
	        				textview.setText(mGpsLocation.getCity());

	        				textview = (TextView) mRootView.findViewById(R.id.fieldPostalCode);
	        				textview.setText(mGpsLocation.getPostalCode());

	        				textview = (TextView) mRootView.findViewById(R.id.fieldAddressLine);
	        				textview.setText(mGpsLocation.getAddressLine());
	        				
	        				textview = (TextView) mRootView.findViewById(R.id.fieldTimezone);
	        				textview.setText(mGpsLocation.getTimeZone().getDisplayName());
	                        break;
	                    }
	                    case Consts.UPDATE_LATLNG:
	                    {
	                    	String stringLatitude = String.valueOf(msg.obj).split(",")[0];
	        				TextView textview = (TextView) mRootView.findViewById(R.id.fieldLatitude);
	        				textview.setText("51.9873652");
	       
	        				String stringLongitude = String.valueOf(msg.obj).split(",")[1];
	        				textview = (TextView) mRootView.findViewById(R.id.fieldLongitude);
	        				textview.setText("-2.134568");

	        				mGpsLocation.update(stringLatitude, stringLongitude);
	        				mMapFragment.setUpMapIfNeeded();
	                        break;
	                    }
	                }
	            }
	        };
			
			// debug
		    AdRegistration.enableTesting(false);
		    AdRegistration.enableLogging(false);
			
			try
			{
				AdRegistration.setAppKey(Consts.AMAZON_KEY);
			}
			catch (Exception e) 
			{
				Log.e("MainActivity", "Exception thrown: " + e.toString());
			}
		    this.mAdview_banner = (AdLayout) mRootView.findViewById(R.id.AmazonAd);
		    this.mAdview_banner.setListener(this);
		    this.mAdview_banner.setTimeout(20000); // 20 seconds
			
			return mRootView;
		}

		// Set up fine and/or coarse location providers depending on whether the fine provider or
	    // both providers button is pressed.
	    public void setup() 
	    {
	        Location gpsLocation = null;
	        Location networkLocation = null;
	        if (mLocationManager != null)
	        {
	        	mLocationManager.removeUpdates(listener);
	        }
	        // Get fine location updates only.
	        if (mUseFine) 
	        {
	            // Request updates from just the fine (gps) provider.
	            gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, R.string.not_support_gps);
	            // Update the UI immediately if a location is obtained.
	            if (gpsLocation != null)
	            {
	            	updateUILocation(gpsLocation);
	            }
	        }
	        if (mUseNetwork) 
	        {
	            // Get coarse and fine location updates.
	            // Request updates from both fine (gps) and coarse (network) providers.
	            //gpsLocation = requestUpdatesFromProvider(
	            //        LocationManager.GPS_PROVIDER, R.string.not_support_gps);
	            networkLocation = requestUpdatesFromProvider(
	                    LocationManager.NETWORK_PROVIDER, R.string.not_support_network);
	        }
            // If both providers return last known locations, compare the two and use the better
            // one to update the UI.  If only one provider returns a location, use it.
            if (gpsLocation != null && networkLocation != null) 
            {
                updateUILocation(getBetterLocation(gpsLocation, networkLocation));
            } 
            else if (gpsLocation != null)
            {
                updateUILocation(gpsLocation);
            }
            else if (networkLocation != null)
            {
                updateUILocation(networkLocation);
	        }
	    }
    
	    private void doReverseGeocoding(Location location)
	    {
	        // Since the geocoding API is synchronous and may take a while.  You don't want to lock
	        // up the UI thread.  Invoking reverse geocoding in an AsyncTask.
	        (new ReverseGeocodingTask(mRootView.getContext())).execute(new Location[] {location});
	    }
	    
	    private Location requestUpdatesFromProvider(final String provider, final int errorResId) 
	    {
	        Location location = null;
	        if (mLocationManager.isProviderEnabled(provider))
	        {
	            mLocationManager.requestLocationUpdates(provider, Consts.TEN_SECONDS, Consts.TEN_METERS, listener);
	            location = mLocationManager.getLastKnownLocation(provider);
	        } 
	        else
	        {
	            Toast.makeText(mRootView.getContext(), errorResId, Toast.LENGTH_LONG).show();
	        }
	        return location;
	    }

	    private void updateUILocation(Location location)
	    {
	        // We're sending the update to a handler which then updates the UI with the new
	        // location.
	        Message.obtain(mHandler,
	                Consts.UPDATE_LATLNG,
	                location.getLatitude() + "," + location.getLongitude()).sendToTarget();
	
	        // Bypass reverse-geocoding only if the Geocoder service is available on the device.
	        if (mGeocoderAvailable)
	        {
	        	doReverseGeocoding(location);
	        }
	    }

	    private final LocationListener listener = new LocationListener()
	    {
	        @Override
	        public void onLocationChanged(Location location) 
	        {
	            // A new location update is received.  Do something useful with it.  Update the UI with
	            // the location update.
	            updateUILocation(location);
	        }
	
	        @Override
	        public void onProviderDisabled(String provider)
	        {
	        }
	
	        @Override
	        public void onProviderEnabled(String provider)
	        {
	        }
	
	        @Override
	        public void onStatusChanged(String provider, int status, Bundle extras)
	        {
	        }
	    };
	
	    /** Determines whether one Location reading is better than the current Location fix.
	      * Code taken from
	      * http://developer.android.com/guide/topics/location/obtaining-user-location.html
	      *
	      * @param newLocation  The new Location that you want to evaluate
	      * @param currentBestLocation  The current Location fix, to which you want to compare the new
	      *        one
	      * @return The better Location object based on recency and accuracy.
	      */
	    protected Location getBetterLocation(Location newLocation, Location currentBestLocation) 
	    {
	        if (currentBestLocation == null)
	        {
	            // A new location is always better than no location
	            return newLocation;
	        }
	
	        // Check whether the new location fix is newer or older
	        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
	        boolean isSignificantlyNewer = timeDelta > Consts.TWO_MINUTES;
	        boolean isSignificantlyOlder = timeDelta < -Consts.TWO_MINUTES;
	        boolean isNewer = timeDelta > 0;
	
	        // If it's been more than two minutes since the current location, use the new location
	        // because the user has likely moved.
	        if (isSignificantlyNewer) 
	        {
	            return newLocation;
	        // If the new location is more than two minutes older, it must be worse
	        } 
	        else if (isSignificantlyOlder) 
	        {
	            return currentBestLocation;
	        }
	
	        // Check whether the new location fix is more or less accurate
	        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
	        boolean isLessAccurate = accuracyDelta > 0;
	        boolean isMoreAccurate = accuracyDelta < 0;
	        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
	
	        // Check if the old and new location are from the same provider
	        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
	                currentBestLocation.getProvider());
	
	        // Determine location quality using a combination of timeliness and accuracy
	        if (isMoreAccurate) {
	            return newLocation;
	        } else if (isNewer && !isLessAccurate) {
	            return newLocation;
	        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	            return newLocation;
	        }
	        return currentBestLocation;
	    }
	
	    /** Checks whether two providers are the same */
	    private boolean isSameProvider(String provider1, String provider2) {
	        if (provider1 == null) {
	          return provider2 == null;
	        }
	        return provider1.equals(provider2);
	    }
	
	    // AsyncTask encapsulating the reverse-geocoding API.  Since the geocoder API is blocked,
	    // we do not want to invoke it from the UI thread.
	    private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> 
	    {
	        Context mContext;
	
	        public ReverseGeocodingTask(Context context) 
	        {
	            super();
	            mContext = context;
	        }
	
	        @Override
	        protected Void doInBackground(Location... params)
	        {
	            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
	
		    	GPSLocation gpsLocation = new GPSLocation();

	            Location loc = params[0];
	            List<Address> addresses = null;
	            try
	            {
	                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
	            } 
	            catch (IOException e) 
	            {
	                e.printStackTrace();
	                // Update address field with the exception.
	                gpsLocation.setAddressLine(e.toString());
	            }
	            if (addresses != null && addresses.size() > 0)
	            {
	                Address address = addresses.get(0);
	                gpsLocation.setAddressLine(address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "");
	                gpsLocation.setCity(address.getLocality());
	                gpsLocation.setCountry(address.getCountryName());
	                gpsLocation.setPostalCode(address.getPostalCode());
	                gpsLocation.setFeatureName(address.getFeatureName());
	                gpsLocation.setPhone(address.getPhone());
	                gpsLocation.setExtras(address.getExtras());
	                //gpsLocation.setSubLocality(address.getSubLocality());
	                gpsLocation.setUrl(address.getUrl());
	                // Update address field on UI.
	            }
                Message.obtain(mHandler, Consts.UPDATE_ADDRESS, gpsLocation).sendToTarget();
	            return null;
	        }
	    }
	
	    /**
	     * Dialog to prompt users to enable GPS on the device.
	     */
	    @SuppressLint("ValidFragment")
		private class EnableGpsDialogFragment extends DialogFragment 
		{
	
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState)
	        {
	            return new AlertDialog.Builder(getActivity())
	                    .setTitle(R.string.enable_gps)
	                    .setMessage(R.string.enable_gps_dialog)
	                    .setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which)
	                        {
	                            enableLocationSettings();
	                        }
	                    }).create();
	        }
	    }
	    
	 // Method to launch Settings
	    private void enableLocationSettings()
	    {
	        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	        startActivity(settingsIntent);
	    }
    
		@Override
		public void onAdCollapsed(Ad arg0) 
		{
		}

		@Override
		public void onAdDismissed(Ad arg0) 
		{
		}

		@Override
		public void onAdExpanded(Ad arg0) 
		{
		}

		@Override
		public void onAdFailedToLoad(Ad arg0, AdError arg1)
		{
		}

		@Override
		public void onAdLoaded(Ad arg0, AdProperties arg1) 
		{
		}
	}
	
	//************************** GPS ADDRESS FRAGMENT ******************************************* END
	
	//************************** MAP FRAGMENT ******************************************* START

	public static class MapFragment extends Fragment
	{
		private View mRootView;
		
		private GoogleMap mGoogleMap;
		
		public MapFragment() 
		{
			mMapFragment = this;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_map, container, false);

			return mRootView;
		}

		@Override
		public void onStart()
		{
			super.onStart();
		}

		@Override
		public void onResume()
		{
			super.onResume();

			setUpMapIfNeeded();
		}		
		
		public void setUpMapIfNeeded()
		{
		    // Do a null check to confirm that we have not already instantiated the map.
		    //if (mGoogleMap == null) 
		    {
		        // Try to obtain the map from the SupportMapFragment.
		        mGoogleMap = ((SupportMapFragment) MainActivity.mFragmentManager.findFragmentById(R.id.map)).getMap();
		        // Check if we were successful in obtaining the map.
		        if (mGoogleMap != null)
		        {
		            setUpMap();
		        }
		    }
		}
		
		private void setUpMap() 
		{
		    // For showing a move to my loction button
			mGoogleMap.clear();
		    mGoogleMap.setMyLocationEnabled(true);
		    // For dropping a marker at a point on the Map
		    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mGpsLocation.getLatitude(), mGpsLocation.getLongitude())).title("Me").snippet("Me"));
		    // For zooming automatically to the Dropped PIN Location
		    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mGpsLocation.getLatitude(), mGpsLocation.getLongitude()), 14.0f));
		    
		    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(mGpsLocation.getLatitude(), mGpsLocation.getLongitude()));
		    CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

		    mGoogleMap.moveCamera(center);
		    mGoogleMap.animateCamera(zoom);
		    
		    mGoogleMap.setBuildingsEnabled(true);
		    
		    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mRootView.getContext());
		    String mapType = prefs.getString("map_type", "Normal");
		    if ("Normal".equals(mapType))
		    {
		    	mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);		    	
		    }
		    else if ("Satellite".equals(mapType))
		    {
		    	mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);		    	
		    }
		    else if ("Hybrid".equals(mapType))
		    {
		    	mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);		    	
		    }
		    else if ("Terrain".equals(mapType))
		    {
		    	mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);		    	
		    }
		    //Toast.makeText(mRootView.getContext(), "setup map " + mGpsLocation.getLatitude() + " " + mGpsLocation.getLongitude(), Toast.LENGTH_SHORT).show();
		    
		    Boolean showTraffic = prefs.getBoolean("show_traffic", false);
		    mGoogleMap.setTrafficEnabled(showTraffic);
		}
	}
		
	//************************** MAP FRAGMENT ******************************************* END
}

