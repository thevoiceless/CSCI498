package csci498.thevoiceless.lunchlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LunchFragment extends ListFragment
{
	public final static String RESTAURANT_ID_KEY = "csci498.thevoiceless.RESTAURANT_ID";
	private final static int LONG_PRESS_ACTIONS = 1;
	// Cursor for restaurants in the database, and its associated adapter
	private Cursor restaurants;
	private RestaurantAdapter restaurantsAdapter;
	// Data members from the view
	private SharedPreferences prefs;
	private RestaurantHelper dbHelper;
	private ListView list;
	private long longPressedRestaurant;
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
		
		setListeners();
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
	
	private void showDetailForm(long id)
	{
		Intent i = new Intent(getActivity(), DetailForm.class);
		i.putExtra(RESTAURANT_ID_KEY, String.valueOf(id));
		startActivity(i);
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
		prefs		= PreferenceManager.getDefaultSharedPreferences(getActivity());
		dbHelper	= new RestaurantHelper(getActivity());
		list		= getListView();
		
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
	
	private void setListeners()
	{
		/*
		// Display dialog after long-pressing on a list item
		list.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
			{
				longPressedRestaurant = id;
				// showDialog automatically calls onCreateDialog for an Activity
				showDialog(LONG_PRESS_ACTIONS);
				return true;
			}
		});
		*/
	}
	
	public void setOnRestaurantListener(onRestaurantListener listener)
	{
		this.listener = listener;
	}
	
	/*
	@Override
	public Dialog onCreateDialog(int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
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
										Toast.makeText(getActivity(), R.string.delete_success, Toast.LENGTH_LONG).show();
										restaurants.requery();
									}
									else
									{
										Toast.makeText(getActivity(), R.string.delete_failure, Toast.LENGTH_LONG).show();
									}
									break;
							}
						}
					});
		}		
		return builder.create();
	}
	*/
}
