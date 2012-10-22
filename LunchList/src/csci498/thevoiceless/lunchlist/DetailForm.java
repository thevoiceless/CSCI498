package csci498.thevoiceless.lunchlist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DetailForm extends Activity
{
	EditText name = null;
	EditText address = null;
	RadioGroup typeGroup = null;
	TextView location = null;
	EditText notes = null;
	EditText feed = null;
	RestaurantHelper dbHelper = null;
	String restaurantId = null;
	LocationManager locManager = null;
	double latitude, longitude;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_form);
		
		setDataMembers();
	}
	
	@Override
	public void onPause()
	{
		locManager.removeUpdates(onLocationChange);
		super.onPause();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		dbHelper.close();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if (latitude == 0 && longitude == 0)
		{
			menu.findItem(R.id.menu_map).setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		new MenuInflater(this).inflate(R.menu.details_option, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.menu_save)
		{
			saveRestaurant();
			return true;
		}
		else if (item.getItemId() == R.id.menu_location)
		{
			Toast.makeText(DetailForm.this, R.string.loc_attempting, Toast.LENGTH_LONG).show();
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
			return true;
		}
		else if (item.getItemId() == R.id.menu_map)
		{
			Intent i = new Intent(this, RestaurantMap.class);
			i.putExtra(RestaurantMap.LATITUDE_KEY, latitude);
			i.putExtra(RestaurantMap.LONGITUDE_KEY, longitude);
			i.putExtra(RestaurantMap.NAME_KEY, name.getText().toString());
			startActivity(i);
			return true;
		}
		else if(item.getItemId() == R.id.menu_feed)
		{
			tryToOpenFeed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);
		
		state.putString("name", name.getText().toString());
		state.putString("address", address.getText().toString());
		state.putInt("type", typeGroup.getCheckedRadioButtonId());
		state.putString("notes", notes.getText().toString());
		state.putString("feed", feed.getText().toString());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle state)
	{
		super.onRestoreInstanceState(state);
		
		name.setText(state.getString("name"));
		address.setText(state.getString("address"));
		typeGroup.check(state.getInt("type"));
		notes.setText(state.getString("notes"));
		feed.setText(state.getString("feed"));
	}
	
	private void saveRestaurant()
	{
		if (name.getText().toString().length() > 0)
		{
			Restaurant.Type type = null;
			switch(typeGroup.getCheckedRadioButtonId())
			{
				case R.id.sitdownRadio:
					type = Restaurant.Type.SIT_DOWN;
					break;
				case R.id.takeoutRadio:
					type = Restaurant.Type.TAKE_OUT;
					break;
				case R.id.deliveryRadio:
					type = Restaurant.Type.DELIVERY;
					break;
			}
			
			if(restaurantId == null)
			{
				dbHelper.insert(name.getText().toString(),
						address.getText().toString(),
						type,
						notes.getText().toString(),
						feed.getText().toString(),
						latitude,
						longitude);
			}
			else
			{
				dbHelper.update(restaurantId,
						name.getText().toString(),
						address.getText().toString(),
						type,
						notes.getText().toString(),
						feed.getText().toString());
				dbHelper.updateLocation(restaurantId, latitude, longitude);
			}
			finish();
		}
		else
		{
			Toast.makeText(this, R.string.name_required, Toast.LENGTH_LONG).show();
		}
	}
	
	private void tryToOpenFeed()
	{
		if(isNetworkAvailable())
		{
			Intent i = new Intent(this, FeedActivity.class);
			i.putExtra(FeedActivity.FEED_URL_KEY, feed.getText().toString());
			startActivity(i);
		}
		else
		{
			Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
		}
	}
	
	LocationListener onLocationChange = new LocationListener()
	{
		public void onLocationChanged(Location fix)
		{			
			latitude = fix.getLatitude();
			longitude = fix.getLongitude();
			location.setText(latitude + ", " + longitude);
			locManager.removeUpdates(onLocationChange);
			
			Toast.makeText(DetailForm.this, R.string.loc_success, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{			
		}

		@Override
		public void onProviderEnabled(String provider)
		{			
		}

		@Override
		public void onProviderDisabled(String provider)
		{			
		}
	};
	
	private void setDataMembers()
	{		
		name 		= (EditText) findViewById(R.id.name);
		address 	= (EditText) findViewById(R.id.addr);
		typeGroup 	= (RadioGroup) findViewById(R.id.typeGroup);
		location	= (TextView) findViewById(R.id.locCoords);
		notes 		= (EditText) findViewById(R.id.notes);
		feed		= (EditText) findViewById(R.id.feed);
		dbHelper	= new RestaurantHelper(this);
		restaurantId = getIntent().getStringExtra(LunchList.RESTAURANT_ID_KEY);
		locManager	= (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(restaurantId != null)
		{
			load();
		}
	}
	
	private void load()
	{
		Cursor c = dbHelper.getById(restaurantId);
		c.moveToFirst();
		
		name.setText(dbHelper.getName(c));
		address.setText(dbHelper.getAddress(c));
		if (dbHelper.getLatitude(c) != 0.0 && dbHelper.getLongitude(c) != 0.0)
		{
			latitude = dbHelper.getLatitude(c);
			longitude = dbHelper.getLongitude(c);
			location.setText("(" + latitude + ", " + longitude + ")");
		}
		notes.setText(dbHelper.getNotes(c));
		feed.setText(dbHelper.getFeed(c));
		
		if(dbHelper.getType(c).equals(Restaurant.Type.SIT_DOWN))
		{
			typeGroup.check(R.id.sitdownRadio);
		}
		else if(dbHelper.getType(c).equals(Restaurant.Type.TAKE_OUT))
		{
			typeGroup.check(R.id.takeoutRadio);
		}
		else
		{
			typeGroup.check(R.id.deliveryRadio);
		}
		
		c.close();
	}
	
	private boolean isNetworkAvailable()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo connection = cm.getActiveNetworkInfo();
		return (connection != null);
	}
}
