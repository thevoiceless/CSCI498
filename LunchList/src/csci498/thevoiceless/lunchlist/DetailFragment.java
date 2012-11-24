package csci498.thevoiceless.lunchlist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DetailFragment extends Fragment
{
	EditText name, address, notes, feed;
	RadioGroup typeGroup;
	TextView location;
	RestaurantHelper dbHelper;
	String restaurantId;
	LocationManager locManager;
	double latitude, longitude;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		setDataMembers();
		if (savedInstanceState != null)
		{
			name.setText(savedInstanceState.getString("name"));
			address.setText(savedInstanceState.getString("address"));
			typeGroup.check(savedInstanceState.getInt("type"));
			notes.setText(savedInstanceState.getString("notes"));
			feed.setText(savedInstanceState.getString("feed"));
		}
	}
	
	@Override
	public void onPause()
	{
		saveRestaurant();
		locManager.removeUpdates(onLocationChange);
		dbHelper.close();
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setDataMembers();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.layout_detail_form, container, false);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		Log.v("menu", "lat/long: " + latitude + "/" + longitude);
		if (latitude == 0 && longitude == 0)
		{
			menu.findItem(R.id.menu_map).setEnabled(false);
		}
		else
		{
			menu.findItem(R.id.menu_map).setEnabled(true);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.details_option, menu);
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
			Toast.makeText(getActivity(), R.string.loc_attempting, Toast.LENGTH_LONG).show();
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
			return true;
		}
		else if (item.getItemId() == R.id.menu_map)
		{
			Intent i = new Intent(getActivity(), RestaurantMap.class);
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
	
	private void saveRestaurant()
	{
		if (name.getText().toString().length() > 0)
		{
			Restaurant.Type type = null;
			switch (typeGroup.getCheckedRadioButtonId())
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
			
			if (restaurantId == null)
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
			getActivity().finish();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.name_required, Toast.LENGTH_LONG).show();
		}
	}
	
	private void tryToOpenFeed()
	{
		if (isNetworkAvailable())
		{
			Intent i = new Intent(getActivity(), FeedActivity.class);
			i.putExtra(FeedActivity.FEED_URL_KEY, feed.getText().toString());
			startActivity(i);
		}
		else
		{
			Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_LONG).show();
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
			
			Toast.makeText(getActivity(), R.string.loc_success, Toast.LENGTH_LONG).show();
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
		name 		= (EditText) getView().findViewById(R.id.name);
		address 	= (EditText) getView().findViewById(R.id.addr);
		typeGroup 	= (RadioGroup) getView().findViewById(R.id.typeGroup);
		location	= (TextView) getView().findViewById(R.id.locCoords);
		notes 		= (EditText) getView().findViewById(R.id.notes);
		feed		= (EditText) getView().findViewById(R.id.feed);
		dbHelper	= new RestaurantHelper(getActivity());
		restaurantId = getActivity().getIntent().getStringExtra(LunchList.RESTAURANT_ID_KEY);
		locManager	= (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		if (restaurantId != null)
		{
			loadRestaurant();
		}
	}
	
	private void loadRestaurant()
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
		
		if (dbHelper.getType(c).equals(Restaurant.Type.SIT_DOWN))
		{
			typeGroup.check(R.id.sitdownRadio);
		}
		else if (dbHelper.getType(c).equals(Restaurant.Type.TAKE_OUT))
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
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo connection = cm.getActiveNetworkInfo();
		return (connection != null);
	}
}
