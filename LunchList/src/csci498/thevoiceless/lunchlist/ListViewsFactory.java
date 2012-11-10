package csci498.thevoiceless.lunchlist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ListViewsFactory implements RemoteViewsService.RemoteViewsFactory 
{
	private static final String SELECT_RESTAURANT_ID_NAME = "SELECT _ID, name FROM restaurants";
	private Context context;
	private RestaurantHelper dbHelper;
	private Cursor restaurantsCursor;
	
	public ListViewsFactory(Context context, Intent intent)
	{
		this.context = context;
	}
	
	@Override
	public void onCreate()
	{
		dbHelper = new RestaurantHelper(context);
		restaurantsCursor = dbHelper.getReadableDatabase().rawQuery(SELECT_RESTAURANT_ID_NAME, null);

	}

	@Override
	public void onDataSetChanged()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onDestroy()
	{
		restaurantsCursor.close();
		dbHelper.close();
	}

	@Override
	public int getCount()
	{
		return restaurantsCursor.getCount();
	}

	@Override
	public RemoteViews getViewAt(int position)
	{
		RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_row);
		restaurantsCursor.moveToPosition(position);
		row.setTextViewText(android.R.id.text1, restaurantsCursor.getString(1));
		
		Intent i = new Intent();
		Bundle extras = new Bundle();
		extras.putString(LunchList.RESTAURANT_ID_KEY, String.valueOf(restaurantsCursor.getInt(0)));
		i.putExtras(extras);
		row.setOnClickFillInIntent(android.R.id.text1, i);
		
		return row;
	}

	@Override
	public RemoteViews getLoadingView()
	{
		return null;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	@Override
	public long getItemId(int position)
	{
		restaurantsCursor.moveToPosition(position);
		return restaurantsCursor.getInt(0);
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}
}
