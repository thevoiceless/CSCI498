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
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
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
						feed.getText().toString());
			}
			else
			{
				dbHelper.update(restaurantId,
						name.getText().toString(),
						address.getText().toString(),
						type,
						notes.getText().toString(),
						feed.getText().toString());
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
			i.putExtra(FeedActivity.FEED_URL, feed.getText().toString());
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
			dbHelper.updateLocation(restaurantId, fix.getLatitude(), fix.getLongitude());
			location.setText(String.valueOf(fix.getLatitude()) + ", " + String.valueOf(fix.getLongitude()));
			locManager.removeUpdates(onLocationChange);
			
			Toast.makeText(DetailForm.this, R.string.loc_saved, Toast.LENGTH_LONG).show();
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
		restaurantId = getIntent().getStringExtra(LunchList.ID_EXTRA);
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
		location.setText(String.valueOf(dbHelper.getLatitude(c)) + ", " + String.valueOf(dbHelper.getLongitude(c)));
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
