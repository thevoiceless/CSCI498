package csci498.thevoiceless.lunchlist;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

public class LunchList extends TabActivity
{
	private static final int LIST_TAB_ID = 0;
	private static final int DETAILS_TAB_ID = 1;
	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	RestaurantAdapter restaurantsAdapter = null;
	EditText name = null;
	EditText address = null;
	RadioGroup typeGroup = null;
	ListView list = null;
	Button save = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		
		setDataMembers();
		//setFonts();
		//addMoreRadioButtons();
		setListeners();
		setAdapters();
		createTabs();
	}
	
	private View.OnClickListener onSave = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			// TODO: Put this in a try/catch and show errors in a toast
			Restaurant r = new Restaurant();
			r.setName(name.getText().toString());
			r.setAddress(address.getText().toString());
			setRestaurantType(r);
			restaurantsAdapter.add(r);
		}
	};
	
	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Restaurant r = restaurants.get(position);
			
			name.setText(r.getName());
			address.setText(r.getAddress());
			
			if(r.getType().equals(Restaurant.Type.SIT_DOWN))
			{
				typeGroup.check(R.id.sitdownRadio);
			}
			else if(r.getType().equals(Restaurant.Type.TAKE_OUT))
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
	
	class RestaurantAdapter extends ArrayAdapter<Restaurant>
	{
		// http://stackoverflow.com/questions/10270143/why-do-recipes-promote-overriding-getitemviewtype-and-getviewtypecount-when-it-d
		// Not really sure what I'm doing here...
		private static final int ROW_TYPE_SIT_DOWN = 0;
		private static final int ROW_TYPE_TAKE_OUT = 1;
		private static final int ROW_TYPE_DELIVERY = 2;

		RestaurantAdapter()
		{
			super(LunchList.this, android.R.layout.simple_list_item_1, restaurants);
		}
		
		// 3 different kinds of views
		public int getViewTypeCount()
		{
			return 3;
		}
		
		// Get the type of the view at the given position
		public int getItemViewType(int position)
		{
			if(restaurants.get(position).getType().equals(Restaurant.Type.SIT_DOWN))
			{
				return ROW_TYPE_SIT_DOWN;
			}
			else if(restaurants.get(position).getType().equals(Restaurant.Type.TAKE_OUT))
			{
				return ROW_TYPE_TAKE_OUT;
			}
			else
			{
				return ROW_TYPE_DELIVERY;
			}
		}
		
		// I don't really know what's going on here
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			RestaurantHolder holder = null;
			
			if(row == null)
			{
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row, null);
				
				holder = new RestaurantHolder(row);
				row.setTag(holder);
			}
			else
			{
				holder = (RestaurantHolder) row.getTag();
			}
			
			holder.populateForm(restaurants.get(position));
			
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
		
		void populateForm(Restaurant r)
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
		name = (EditText) findViewById(R.id.name);
		address = (EditText) findViewById(R.id.addr);
		typeGroup = (RadioGroup) findViewById(R.id.typeGroup);
		list = (ListView) findViewById(R.id.restaurantsList);
		save = (Button) findViewById(R.id.save);
	}
	
	private void setListeners()
	{
		save.setOnClickListener(onSave);
		list.setOnItemClickListener(onListClick);
	}
	
	private void setFonts()
	{
		// TODO: See if it's possible to do all of this via iteration
		
		TextView nameLabel = (TextView) findViewById(R.id.nameLabel);
		nameLabel.setTypeface(Typeface.createFromAsset(getAssets(), "Clemente-Bold.ttf"));
		TextView addrLabel = (TextView) findViewById(R.id.addrLabel);
		addrLabel.setTypeface(Typeface.createFromAsset(getAssets(), "Clemente-Bold.ttf"));
		
		name.setTypeface(Typeface.createFromAsset(getAssets(), "CaviarDreams.ttf"));
		address.setTypeface(Typeface.createFromAsset(getAssets(), "CaviarDreams.ttf"));
		
		TextView typeLabel = (TextView) findViewById(R.id.typeLabel);
		typeLabel.setTypeface(Typeface.createFromAsset(getAssets(), "Clemente-Bold.ttf"));
		
		// TODO: Figure out how to set the activity title font
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
	
	/*
	 * Add 10 more radio buttons to the "Type" button group
	 * This was to practice adding radio buttons via code instead of XML
	 */
	private void addMoreRadioButtons()
	{
		for (int i = 0; i < 10; ++i)
		{
			RadioButton r = new RadioButton(this);
			r.setText("Test" + i);
			typeGroup.addView(r);
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_lunch_list, menu);
		return true;
	}
}
