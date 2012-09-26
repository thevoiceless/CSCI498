package csci498.thevoiceless.lunchlist;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class DetailForm extends Activity
{
	EditText name = null;
	EditText address = null;
	RadioGroup typeGroup = null;
	EditText notes = null;
	Button save = null;
	RestaurantHelper dbHelper = null;
	String restaurantId = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_form);
		
		setDataMembers();
		setListeners();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		dbHelper.close();
	}
	
	private View.OnClickListener onSave = new View.OnClickListener()
	{
		public void onClick(View v)
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
						notes.getText().toString());
			}
			else
			{
				dbHelper.update(restaurantId,
						name.getText().toString(),
						address.getText().toString(),
						type,
						notes.getText().toString());
			}
			
			finish();
		}
	};
	
	private void setDataMembers()
	{		
		name 		= (EditText) findViewById(R.id.name);
		address 	= (EditText) findViewById(R.id.addr);
		typeGroup 	= (RadioGroup) findViewById(R.id.typeGroup);
		notes 		= (EditText) findViewById(R.id.notes);
		save 		= (Button) findViewById(R.id.save);
		dbHelper	= new RestaurantHelper(this);
		restaurantId = getIntent().getStringExtra(LunchList.ID_EXTRA);
		
		if(restaurantId != null)
		{
			load();
		}
	}
	
	private void setListeners()
	{
		save.setOnClickListener(onSave);
	}
	
	private void load()
	{
		Cursor c = dbHelper.getById(restaurantId);
		
		c.moveToFirst();
		name.setText(dbHelper.getName(c));
		address.setText(dbHelper.getAddress(c));
		notes.setText(dbHelper.getNotes(c));
		
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
}
