package csci498.thevoiceless.lunchlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LunchList extends ListActivity
{
	public final static String RESTAURANT_ID_KEY = "csci498.thevoiceless.RESTAURANT_ID";
	private final static int LONG_PRESS_ACTIONS = 1;
	// Cursor for restaurants in the database, and its associated adapter
	private Cursor restaurants;
	private RestaurantAdapter restaurantsAdapter = null;
	// Data members from the view
	private SharedPreferences prefs = null;
	private RestaurantHelper dbHelper = null;
	private ListView list = null;
	private long longPressedRestaurant;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		
		setDataMembers();
		setListeners();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		new MenuInflater(this).inflate(R.menu.option, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.add)
		{
			startActivity(new Intent(LunchList.this, DetailForm.class));
			return true;
		}
		else if (item.getItemId() == R.id.prefs)
		{
			startActivity(new Intent(this, EditPreferences.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		dbHelper.close();
	}
	
	@Override
	public void onListItemClick(ListView list, View view, int position, long id)
	{
		showDetailForm(id);
	}
	
	private void showDetailForm(long id)
	{
		Intent i = new Intent(LunchList.this, DetailForm.class);
		i.putExtra(RESTAURANT_ID_KEY, String.valueOf(id));
		startActivity(i);
	}
	
	// CursorAdapter uses bindView() and newView() instead of getView()
	class RestaurantAdapter extends CursorAdapter
	{
		RestaurantAdapter(Cursor c)
		{
			super(LunchList.this, c);
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
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			RestaurantHolder holder = new RestaurantHolder(row);
			row.setTag(holder);
			return row;
		}
	}
	
	static class RestaurantHolder
	{
		private TextView rName = null;
		private TextView rAddress = null;
		private ImageView rIcon = null;
		
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
	
	private void setDataMembers()
	{
		prefs		= PreferenceManager.getDefaultSharedPreferences(this);
		dbHelper	= new RestaurantHelper(this);
		list		= getListView();
		
		initList();
		prefs.registerOnSharedPreferenceChangeListener(prefListener);
	}
	
	private void initList()
	{
		if (restaurants != null)
		{
			stopManagingCursor(restaurants);
			restaurants.close();
		}
		
		restaurants = dbHelper.getAll(prefs.getString("sort_order", "name"));
		startManagingCursor(restaurants);
		setAdapters();
	}
	
	private void setAdapters()
	{
		restaurantsAdapter = new RestaurantAdapter(restaurants);
		setListAdapter(restaurantsAdapter);
	}
	
	private void setListeners()
	{
		// Display dialog after long-pressing on a list item
		list.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
			{
				longPressedRestaurant = id;
				// showDialog automatically calls onCreateDialog
				showDialog(LONG_PRESS_ACTIONS);
				return true;
			}
		});
	}
	
	@Override
	public Dialog onCreateDialog(int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(LunchList.this);
		
		if (id == LONG_PRESS_ACTIONS)
		{
			builder.setItems(R.array.longpress_actions,
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int whichIndexWasPressed)
						{
							switch (whichIndexWasPressed)
							{
								// Edit
								case 0:
									showDetailForm(longPressedRestaurant);
									break;
								// Delete
								case 1:
									if (dbHelper.delete(String.valueOf(longPressedRestaurant)))
									{
										Toast.makeText(LunchList.this, R.string.delete_success, Toast.LENGTH_LONG).show();
										restaurants.requery();
									}
									else
									{
										Toast.makeText(LunchList.this, R.string.delete_failure, Toast.LENGTH_LONG).show();
									}
									break;
							}
						}
					});
		}
		
		return builder.create();
	}
}
