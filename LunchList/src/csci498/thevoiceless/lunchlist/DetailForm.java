package csci498.thevoiceless.lunchlist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DetailForm extends Activity
{
	EditText name = null;
	EditText address = null;
	RadioGroup typeGroup = null;
	EditText notes = null;
	EditText feed = null;
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		new MenuInflater(this).inflate(R.menu.details_option, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.feed)
		{
			if(isNetworkAvailable())
			{
				Intent i = new Intent(this, FeedActivity.class);
				i.putExtra(FeedActivity.FEED_URL, feed.getText().toString());
				startActivity(i);
			}
			else
			{
				Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
			}
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
	
	@Override
	public void onRestoreInstanceState(Bundle state)
	{
		super.onRestoreInstanceState(state);
		
		name.setText(state.getString("name"));
		address.setText(state.getString("address"));
		typeGroup.check(state.getInt("type"));
		notes.setText(state.getString("notes"));
		feed.setText(state.getString("feed"));
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
						notes.getText().toString(),
						feed.getText().toString());
			}
			else
			{
				dbHelper.update(restaurantId,
						name.getText().toString(),
						address.getText().toString(),
						type,
						notes.getText().toString(),
						feed.getText().toString());
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
		feed		= (EditText) findViewById(R.id.feed);
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
		feed.setText(dbHelper.getFeed(c));
		
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
	
	private boolean isNetworkAvailable()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo connection = cm.getActiveNetworkInfo();
		return (connection != null);
	}
}
