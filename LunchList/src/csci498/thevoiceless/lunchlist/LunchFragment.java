package csci498.thevoiceless.lunchlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LunchFragment extends ListFragment
{
	public final static String RESTAURANT_ID_KEY = "csci498.thevoiceless.RESTAURANT_ID";
	// Cursor for restaurants in the database, and its associated adapter
	private Cursor restaurants;
	private RestaurantAdapter restaurantsAdapter;
	// Data members from the view
	private SharedPreferences prefs;
	private RestaurantHelper dbHelper;
	private onRestaurantListener listener;
	
	public interface onRestaurantListener
	{
		void onRestaurantSelected(long id);
	}
	
	static class RestaurantHolder
	{
		private TextView rName;
		private TextView rAddress;
		private ImageView rIcon;
		
		RestaurantHolder(View row)
		{
			rName = (TextView) row.findViewById(R.id.title);
			rAddress = (TextView) row.findViewById(R.id.address);
			rIcon = (ImageView) row.findViewById(R.id.icon);
		}
		
		void populateFrom(Cursor cursor, RestaurantHelper helper)
		{
			rName.setText(helper.getName(cursor));
			rAddress.setText(helper.getAddress(cursor));
			
			if (helper.getType(cursor).equals(Restaurant.Type.SIT_DOWN))
			{
				rIcon.setImageResource(R.drawable.green_circle);
				rName.setTextColor(Color.rgb(0, 153, 0));
			}
			else if (helper.getType(cursor).equals(Restaurant.Type.TAKE_OUT))
			{
				rIcon.setImageResource(R.drawable.blue_circle);
				rName.setTextColor(Color.BLUE);
			}
			else
			{
				rIcon.setImageResource(R.drawable.lightblue_circle);
				rName.setTextColor(Color.rgb(56, 178, 206));
			}
		}
	}
	
	class RestaurantAdapter extends CursorAdapter
	{
		RestaurantAdapter(Cursor c)
		{
			super(getActivity(), c);
		}
		
		@Override
		public void bindView(View row, Context context, Cursor cursor)
		{
			RestaurantHolder holder = (RestaurantHolder) row.getTag();
			holder.populateFrom(cursor, dbHelper);
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent)
		{
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			RestaurantHolder holder = new RestaurantHolder(row);
			row.setTag(holder);
			return row;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		setDataMembers();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.option, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.add)
		{
			startActivity(new Intent(getActivity(), DetailForm.class));
			return true;
		}
		else if (item.getItemId() == R.id.prefs)
		{
			startActivity(new Intent(getActivity(), EditPreferences.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause()
	{
		dbHelper.close();
		super.onPause();
	}
	
	@Override
	public void onListItemClick(ListView list, View view, int position, long id)
	{
		if (listener != null)
		{
			listener.onRestaurantSelected(id);
		}
	}
	
	private SharedPreferences.OnSharedPreferenceChangeListener prefListener =
			new SharedPreferences.OnSharedPreferenceChangeListener()
			{
				@Override
				public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key)
				{
					if (key.equals("sort_order"))
					{
						initList();
					}
				}
			};
			
	public void setOnRestaurantListener(onRestaurantListener listener)
	{
		this.listener = listener;
	}
	
	public RestaurantHelper getDatabaseHelper()
	{
		return dbHelper;
	}
	
	public Cursor getCursor()
	{
		return restaurants;
	}
	
	private void setDataMembers()
	{
		prefs		= PreferenceManager.getDefaultSharedPreferences(getActivity());
		dbHelper	= new RestaurantHelper(getActivity());
		
		initList();
		prefs.registerOnSharedPreferenceChangeListener(prefListener);
	}
	
	private void initList()
	{
		if (restaurants != null)
		{
			restaurants.close();
		}
		
		restaurants = dbHelper.getAll(prefs.getString("sort_order", "name"));
		setAdapters();
	}
	
	private void setAdapters()
	{
		restaurantsAdapter = new RestaurantAdapter(restaurants);
		setListAdapter(restaurantsAdapter);
	}
}
