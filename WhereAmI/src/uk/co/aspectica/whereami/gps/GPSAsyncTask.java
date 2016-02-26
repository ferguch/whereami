package uk.co.aspectica.whereami.gps;

import uk.co.aspectica.whereami.MainActivity.GPSAddressFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

public class GPSAsyncTask extends AsyncTask<String, GPSLocation, GPSLocation>
{
	private View uiView;
	
	private GPSAddressFragment fragment;
	
	private GPSTracker gpsTracker;
	
	public GPSAsyncTask(View uiView, GPSAddressFragment fragment)
	{
		this.uiView = uiView;
		this.fragment = fragment;
	}
	
	@Override
	protected GPSLocation doInBackground(String... value) 
	{
		//gpsTracker = new GPSTracker(uiView);
		
		//publishProgress(gpsTracker.getGPSLocation());
		
		//return gpsTracker.getGPSLocation();
		return null;
	}

	@Override
	protected void onPostExecute(GPSLocation result) 
	{
		//fragment.updateGPSLocation(result);
	}

	@Override
	protected void onPreExecute() 
	{
	}

	@Override
	protected void onProgressUpdate(GPSLocation... values)
	{
	}

}
