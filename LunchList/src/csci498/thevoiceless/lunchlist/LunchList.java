package csci498.thevoiceless.lunchlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.ViewFlipper;

public class LunchList extends Activity
{
	// Tab ID values
	private static final int LIST_TAB_ID = 0;
	private static final int DETAILS_TAB_ID = 1;
	// ArrayList of restaurants and its associated adapter
	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	RestaurantAdapter restaurantsAdapter = null;
	// Data members from the view
	DatePicker visited = null;
	EditText name = null;
	EditText address = null;
	RadioGroup typeGroup = null;
	ListView list = null;
	Button save = null;
	
	private ViewFlipper vf = null;
	private float lastX;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		
		vf = (ViewFlipper) findViewById(R.id.view_flipper);
		
		setDataMembers();
		//setFonts();
		//addMoreRadioButtons();
		setListeners();
		setAdapters();
	}
	
	private View.OnClickListener onSave = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			// TODO: Put this in a try/catch and show errors in a toast
			Restaurant r = new Restaurant();
			r.setName(name.getText().toString());
			r.setAddress(address.getText().toString());
			r.setDateVisited(visited.getMonth(), visited.getDayOfMonth(), visited.getYear());
			setRestaurantType(r);
			restaurantsAdapter.add(r);
		}
	};
	
	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Restaurant r = restaurants.get(position);
			
			visited.updateDate(r.getDateVisited().getYear(), r.getDateVisited().getMonth(), r.getDateVisited().getDate());
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
		}
	};
	
	class RestaurantAdapter extends ArrayAdapter<Restaurant>
	{
		RestaurantAdapter()
		{
			super(LunchList.this, android.R.layout.simple_list_item_1, restaurants);
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
		visited = (DatePicker) findViewById(R.id.visited);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_lunch_list, menu);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent touchevent)
	{
		// TODO: Figure out how to trigger this when swiping on the ListView
		switch (touchevent.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				lastX = touchevent.getX();
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				float currentX = touchevent.getX();
				if (lastX < currentX)
				{
					if (vf.getDisplayedChild() == 0)
						break;
					vf.setInAnimation(this, R.anim.in_from_left);
					vf.setOutAnimation(this, R.anim.out_to_right);
					vf.showNext();
					TextView instr = (TextView) findViewById(R.id.instructions);
					instr.setText("Swipe to the left in this area");
				}
				if (lastX > currentX)
				{
					if (vf.getDisplayedChild() == 1)
						break;
					vf.setInAnimation(this, R.anim.in_from_right);
					vf.setOutAnimation(this, R.anim.out_to_left);
					vf.showPrevious();
					TextView instr = (TextView) findViewById(R.id.instructions);
					instr.setText("Swipe to the right in this area");
				}
				break;
			}
		}
		return false;
	}
}
