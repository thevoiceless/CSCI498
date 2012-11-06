package csci498.thevoiceless.lunchlist;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

public class WidgetService extends IntentService
{
	private static final String SERVICE_NAME = "WidgetService";
	private static final String SELECT_COUNT_FROM_RESTAURANTS = "SELECT COUNT(*) FROM restaurants";
	private static final String SELECT_ID_NAME_LIMIT_1 = "SELECT _ID, name FROM restaurants LIMIT 1 OFFSET ?";
	private static final int COUNT_RESULT_COL = 0;
	private static final int ID_RESULT_COL = 0;
	private static final int NAME_RESULT_COL = 1;
	
	public WidgetService()
	{
		super(SERVICE_NAME);
	}
	
	@Override
	public void onHandleIntent(Intent intent)
	{
		ComponentName comp = new ComponentName(this, AppWidget.class);
		RemoteViews widgetViews = new RemoteViews(this.getString(R.string.package_name), R.layout.widget);
		RestaurantHelper dbHelper = new RestaurantHelper(this);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		
		try
		{
			Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_COUNT_FROM_RESTAURANTS, null);
			c.moveToFirst();
			int count = c.getInt(COUNT_RESULT_COL);
			c.close();
			
			if (count > 0)
			{
				int offset = (int) (count * Math.random());
				String args[] = {String.valueOf(offset)};
				c = dbHelper.getReadableDatabase().rawQuery(SELECT_ID_NAME_LIMIT_1, args);
				c.moveToFirst();
				
				widgetViews.setTextViewText(R.id.widgetName, c.getString(NAME_RESULT_COL));
				Intent i = new Intent(this, DetailForm.class);
				i.putExtra(LunchList.RESTAURANT_ID_KEY, c.getString(ID_RESULT_COL));
				PendingIntent p = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				widgetViews.setOnClickPendingIntent(R.id.widgetName, p);
				
				c.close();
			}
			else
			{
				widgetViews.setTextViewText(R.id.widgetName, this.getString(R.string.widget_no_restaurants));
			}
		}
		finally
		{
			dbHelper.close();
		}
		
		Intent i = new Intent(this, WidgetService.class);
		PendingIntent p = PendingIntent.getService(this, 0, i, 0);
		widgetViews.setOnClickPendingIntent(R.id.widgetButtonNext, p);
		manager.updateAppWidget(comp, widgetViews);
	}

}
