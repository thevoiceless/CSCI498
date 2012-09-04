package csci498.thevoiceless.lunchlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class LunchList extends Activity
{
	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	ArrayAdapter<Restaurant> restaurantsAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		
		//setFonts();
		setListeners();
		//addMoreRadioButtons();
		setAdapters();
	}
	
	private View.OnClickListener onSave = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			EditText name = (EditText) findViewById(R.id.name);
			//AutoCompleteTextView address = (AutoCompleteTextView) findViewById(R.id.addr);
			EditText address = (EditText) findViewById(R.id.addr);
			
			// TODO: Put this in a try/catch and show errors in a toast
			Restaurant r = new Restaurant();
			r.setName(name.getText().toString());
			r.setAddress(address.getText().toString());
			setRestaurantType(r);
			restaurantsAdapter.add(r);
		}
	};
	
	private void setListeners()
	{
		Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(onSave);
	}
	
	private void setFonts()
	{
		// TODO: See if it's possible to do all of this via iteration
		
		TextView nameLabel = (TextView) findViewById(R.id.nameLabel);
		nameLabel.setTypeface(Typeface.createFromAsset(getAssets(), "Clemente-Bold.ttf"));
		TextView addrLabel = (TextView) findViewById(R.id.addrLabel);
		addrLabel.setTypeface(Typeface.createFromAsset(getAssets(), "Clemente-Bold.ttf"));
		
		EditText name = (EditText) findViewById(R.id.name);
		name.setTypeface(Typeface.createFromAsset(getAssets(), "CaviarDreams.ttf"));
		EditText addr = (EditText) findViewById(R.id.addr);
		addr.setTypeface(Typeface.createFromAsset(getAssets(), "CaviarDreams.ttf"));
		
		TextView typeLabel = (TextView) findViewById(R.id.typeLabel);
		typeLabel.setTypeface(Typeface.createFromAsset(getAssets(), "Clemente-Bold.ttf"));
		
		// TODO: Figure out how to set the activity title font
	}
	
	private void setRestaurantType(Restaurant r)
	{
		RadioGroup typeGroup = (RadioGroup) findViewById(R.id.typeGroup);
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
		RadioGroup typeGroup = (RadioGroup) findViewById(R.id.typeGroup);
		for (int i = 0; i < 10; ++i)
		{
			RadioButton r = new RadioButton(this);
			r.setText("Test" + i);
			typeGroup.addView(r);
		}
	}
	
	private void setAdapters()
	{
		//Spinner list = (Spinner) findViewById(R.id.restaurantsList);
		ListView list = (ListView) findViewById(R.id.restaurantsList);
		restaurantsAdapter = new ArrayAdapter<Restaurant>(this,
				//android.R.layout.simple_spinner_dropdown_item,
				android.R.layout.simple_list_item_1,
				restaurants);
		list.setAdapter(restaurantsAdapter);
		
		//AutoCompleteTextView address = (AutoCompleteTextView) findViewById(R.id.addr);
		//address.setAdapter(restaurantsAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_lunch_list, menu);
		return true;
	}
}
