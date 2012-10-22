package csci498.thevoiceless.lunchlist;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class RestaurantMap extends MapActivity
{
	public static final String LATITUDE_KEY = "csci498.thevoiceless.lunchlist.LATITUDE";
	public static final String LONGITUDE_KEY = "csci498.thevoiceless.lunchlist.LONGITUDE";
	public static final String NAME_KEY = "csci498.thevoiceless.lunchlist.NAME";
	private static final int DEFAULT_ZOOM = 17;
	private static final double DEGREES_MULTIPLIER = 1000000.0;
	
	private double latitude, longitude;
	private String name;
	private MapView map;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setDataMembers();
		
		setContentView(R.layout.map);
	}
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	private void setDataMembers()
	{
		latitude = getIntent().getDoubleExtra(LATITUDE_KEY, 0);
		longitude = getIntent().getDoubleExtra(LONGITUDE_KEY, 0);
		name = getIntent().getStringExtra(NAME_KEY);
		
		map = (MapView) findViewById(R.id.map);
		map.getController().setZoom(DEFAULT_ZOOM);
		
		GeoPoint location = new GeoPoint((int)(latitude * DEGREES_MULTIPLIER), (int)(longitude * DEGREES_MULTIPLIER));
		
		map.getController().setCenter(location);
		map.setBuiltInZoomControls(true);
	}
	
}
