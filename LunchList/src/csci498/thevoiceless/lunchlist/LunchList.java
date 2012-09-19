package csci498.thevoiceless.lunchlist;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class LunchList extends TabActivity
{
	// Tab ID values
	private static final int LIST_TAB_ID = 0;
	private static final int DETAILS_TAB_ID = 1;
	// Cursor for restaurants in the database, and its associated adapter
	Cursor restaurants;
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
			dbHelper.insert(current.getName(), 
					current.getAddress(),
					current.getType(),
					current.getNotes());
			restaurants.requery();
		}
	};
	
	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			restaurants.moveToPosition(position);
			name.setText(dbHelper.getName(restaurants));
			address.setText(dbHelper.getAddress(restaurants));
			notes.setText(dbHelper.getNotes(restaurants));
			
			if(dbHelper.getType(restaurants).equals(Restaurant.Type.SIT_DOWN))
			{
				typeGroup.check(R.id.sitdownRadio);
			}
			else if(dbHelper.getType(restaurants).equals(Restaurant.Type.TAKE_OUT))
			{
				typeGroup.check(R.id.takeoutRadio);
			}
			else
			{
				typeGroup.check(R.id.deliveryRadio);
			}
			getTabHost().setCurrentTab(DETAILS_TAB_ID);
		}
	};
	
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
			
			if(helper.getType(cursor).equals(Restaurant.Type.SIT_DOWN))
			{
				rIcon.setImageResource(R.drawable.green_circle);
				rName.setTextColor(Color.rgb(0, 153, 0));
			}
			else if(helper.getType(cursor).equals(Restaurant.Type.TAKE_OUT))
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
		
		restaurants = dbHelper.getAll();
		startManagingCursor(restaurants);
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
		restaurantsAdapter = new RestaurantAdapter(restaurants);
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
