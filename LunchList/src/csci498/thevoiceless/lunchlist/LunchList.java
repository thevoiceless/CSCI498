package csci498.thevoiceless.lunchlist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.TabActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class LunchList extends TabActivity
{
	// Tab ID values
	private static final int LIST_TAB_ID = 0;
	private static final int DETAILS_TAB_ID = 1;
	// ArrayList of restaurants and its associated adapter
	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	RestaurantAdapter restaurantsAdapter = null;
	// Data members from the view
	EditText name = null;
	EditText address = null;
	RadioGroup typeGroup = null;
	EditText notes = null;
	Button save = null;
	ListView list = null;
	Restaurant current = null;
	RestaurantHelper dbHelper = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		
		setDataMembers();
		setListeners();
		setAdapters();
		createTabs();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.option, menu);
		return true;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		dbHelper.close();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.toast)
		{
			String message = "No restaurant selected";
			if(current != null)
			{
				if(current.getNotes().length() == 0)
				{
					message = "No notes for the current restaurant";
				}
				else
				{
					message = current.getNotes();
				}
			}
			
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			
			return true;
		}		
		return super.onOptionsItemSelected(item);
	}
	
	private View.OnClickListener onSave = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			// TODO: Put this in a try/catch and show errors in a toast
			current = new Restaurant();
			current.setName(name.getText().toString());
			current.setAddress(address.getText().toString());
			setRestaurantType(current);
			current.setNotes(notes.getText().toString());
			//restaurantsAdapter.add(current);
			dbHelper.insert(current.getName(), 
					current.getAddress(),
					current.getType(),
					current.getNotes());
		}
	};
	
	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			current = restaurants.get(position);
			
			name.setText(current.getName());
			address.setText(current.getAddress());
			
			if(current.getType().equals(Restaurant.Type.SIT_DOWN))
			{
				typeGroup.check(R.id.sitdownRadio);
			}
			else if(current.getType().equals(Restaurant.Type.TAKE_OUT))
			{
				typeGroup.check(R.id.takeoutRadio);
			}
			else
			{
				typeGroup.check(R.id.deliveryRadio);
			}
			
			notes.setText(current.getNotes());
			
			getTabHost().setCurrentTab(DETAILS_TAB_ID);
		}
	};
	
	// If we implement our own adapter, we need to override getView to display stuff
	class RestaurantAdapter extends ArrayAdapter<Restaurant>
	{
		RestaurantAdapter()
		{
			super(LunchList.this, android.R.layout.simple_list_item_1, restaurants);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			RestaurantHolder holder = null;
			
			if(row == null)
			{
				// Create a new View object and assign it to row
				// "Inflate" the XML into a View object
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row, null);
				
				holder = new RestaurantHolder(row);
				row.setTag(holder);
			}
			else
			{
				holder = (RestaurantHolder) row.getTag();
			}
			
			holder.populateFrom(restaurants.get(position));
			
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
		
		void populateFrom(Restaurant r)
		{
			rName.setText(r.getName());
			rAddress.setText(r.getAddress());
			
			if(r.getType().equals(Restaurant.Type.SIT_DOWN))
			{
				rIcon.setImageResource(R.drawable.green_circle);
				rName.setTextColor(Color.rgb(0, 153, 0));
			}
			else if(r.getType().equals(Restaurant.Type.TAKE_OUT))
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
	
	private void setDataMembers()
	{
		name 		= (EditText) findViewById(R.id.name);
		address 	= (EditText) findViewById(R.id.addr);
		typeGroup 	= (RadioGroup) findViewById(R.id.typeGroup);
		notes 		= (EditText) findViewById(R.id.notes);
		save 		= (Button) findViewById(R.id.save);
		list 		= (ListView) findViewById(R.id.restaurantsList);
		dbHelper	= new RestaurantHelper(this);
	}
	
	private void setListeners()
	{
		save.setOnClickListener(onSave);
		list.setOnItemClickListener(onListClick);
	}
	
	private void setRestaurantType(Restaurant r)
	{
		switch (typeGroup.getCheckedRadioButtonId())
		{
			case R.id.sitdownRadio:
				r.setType(Restaurant.Type.SIT_DOWN);
				break;
			case R.id.takeoutRadio:
				r.setType(Restaurant.Type.TAKE_OUT);
				break;
			case R.id.deliveryRadio:
				r.setType(Restaurant.Type.DELIVERY);
				break;
		}
	}
	
	private void setAdapters()
	{
		restaurantsAdapter = new RestaurantAdapter();
		list.setAdapter(restaurantsAdapter);
	}
	
	private void createTabs()
	{
		// https://developer.android.com/reference/android/widget/TabHost.TabSpec.html
		// getTabHost() returns the TabHost that the activity is using to host its tabs
		// TabHost holds two children: a set of tab labels that the user clicks to select a specific tab, and a FrameLayout object that displays the contents of that page
		TabHost.TabSpec tab = getTabHost().newTabSpec("tag1");
		
		tab.setContent(R.id.restaurantsList);
		tab.setIndicator("List", getResources().getDrawable(R.drawable.list));
		getTabHost().addTab(tab);
		
		tab = getTabHost().newTabSpec("tag2");
		tab.setContent(R.id.details);
		tab.setIndicator("Details", getResources().getDrawable(R.drawable.restaurant));
		getTabHost().addTab(tab);
		
		getTabHost().setCurrentTab(LIST_TAB_ID);
		
		// TODO: Figure out how to hide the keyboard when switching to the "list" tab
	}
}
