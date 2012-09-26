package csci498.thevoiceless.lunchlist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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

public class LunchList extends ListActivity
{
	// Cursor for restaurants in the database, and its associated adapter
	Cursor restaurants;
	RestaurantAdapter restaurantsAdapter = null;
	// Data members from the view
	RestaurantHelper dbHelper = null;
	public final static String ID_EXTRA = "csci498.thevoiceless.lunchlist_ID";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		
		setDataMembers();
		setAdapters();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Not sure why we got rid of getMenuInflater()...
		new MenuInflater(this).inflate(R.menu.option, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.add)
		{
			startActivity(new Intent(LunchList.this, DetailForm.class));
			return true;
		}
		else if(item.getItemId() == R.id.prefs)
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
		Intent i = new Intent(LunchList.this, DetailForm.class);
		i.putExtra(ID_EXTRA, String.valueOf(id));
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
		dbHelper	= new RestaurantHelper(this);
		restaurants = dbHelper.getAll();
		startManagingCursor(restaurants);
	}
	
	private void setAdapters()
	{
		restaurantsAdapter = new RestaurantAdapter(restaurants);
		setListAdapter(restaurantsAdapter);
	}
}
