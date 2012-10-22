package csci498.thevoiceless.lunchlist;

import android.os.Bundle;
import com.google.android.maps.MapActivity;

public class RestaurantMap extends MapActivity
{
	public static final String LATITUDE_KEY = "csci498.thevoiceless.lunchlist.LATITUDE";
	public static final String LONGITUDE_KEY = "csci498.thevoiceless.lunchlist.LONGITUDE";
	public static final String NAME_KEY = "csci498.thevoiceless.lunchlist.NAME";
	
	private double latitude, longitude;
	private String name;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		latitude = getIntent().getDoubleExtra(LATITUDE_KEY, 0);
		longitude = getIntent().getDoubleExtra(LONGITUDE_KEY, 0);
		name = getIntent().getStringExtra(NAME_KEY);
		setContentView(R.layout.map);
	}
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
}
