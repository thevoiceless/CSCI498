package csci498.thevoiceless.lunchlist;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class AppWidget extends AppWidgetProvider
{
	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds)
	{
		context.startService(new Intent(context, WidgetService.class));
	}

}
