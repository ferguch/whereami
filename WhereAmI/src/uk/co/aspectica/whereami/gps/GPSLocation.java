package uk.co.aspectica.whereami.gps;

import java.util.TimeZone;

import android.os.Bundle;

public class GPSLocation 
{
	//Context context;
	private boolean canGetLocation = false;
	private String addressLine;
	private String city;
	private String postalCode;
	private String country;
	private double latitude;
	private double longitude;
	private TimeZone timeZone = TimeZone.getDefault();
	private String featureName;
	private String phone;
	private String url;
	private Bundle extras;
	
	public String getFeatureName() 
	{
		return featureName;
	}
	public void setFeatureName(String featureName) 
	{
		this.featureName = featureName;
	}
	public String getPhone()
	{
		return phone;
	}
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public Bundle getExtras() 
	{
		return extras;
	}
	public void setExtras(Bundle extras) 
	{
		this.extras = extras;
	}
	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
	}
	public boolean isCanGetLocation() 
	{
		return canGetLocation;
	}
	public void setCanGetLocation(boolean canGetLocation)
	{
		this.canGetLocation = canGetLocation;
	}
	public String getAddressLine() 
	{
		if (addressLine != null)
		{
			return addressLine;
		}
		return "";
	}
	public void setAddressLine(String addressLine) 
	{
		this.addressLine = addressLine;
	}
	public String getCity()
	{
		if (city != null)
		{
			return city;
		}
		return city;
	}
	public void setCity(String city) 
	{
		this.city = city;
	}
	public String getPostalCode() 
	{
		if (postalCode != null)
		{
			return postalCode;
		}
		return "";
	}
	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}
	public String getCountry() 
	{
		if (country != null)
		{
			return country;
		}
		return "";
	}
	public void setCountry(String country)
	{
		this.country = country;
	}
	public double getLatitude() 
	{
		return latitude;
	}
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
	public double getLongitude()
	{
		return longitude;
	}
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	public TimeZone getTimeZone() 
	{
		return timeZone;
	}
	public void update(GPSLocation gpsLocation)
	{
		this.addressLine = gpsLocation.addressLine;
		this.city = gpsLocation.city;
		this.postalCode = gpsLocation.postalCode;
		this.country = gpsLocation.country;
	}
	public void update(String stringLatitude, String stringLongitude)
	{
		this.latitude = Double.parseDouble(stringLatitude);
		this.longitude = Double.parseDouble(stringLongitude);
	}
}
