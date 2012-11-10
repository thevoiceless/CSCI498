package csci498.thevoiceless.lunchlist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

public class AppWidget extends AppWidgetProvider
{
	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			onUpdateV11(context, manager, appWidgetIds);
		}
		else
		{
			context.startService(new Intent(context, WidgetService.class));
		}
	}
	
	public void onUpdateV11(Context context, AppWidgetManager manager, int[] appWidgetIds)
	{
		for (int i = 0; i < appWidgetIds.length; i++)
		{
			Intent serviceIntent = new Intent(context, ListWidgetService.class);
			serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
			
			RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget);
			widget.setRemoteAdapter(appWidgetIds[i], R.id.widgetRestaurantsList, serviceIntent);
			
			Intent clickIntent = new Intent(context, DetailForm.class);
			PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 
				0, 
				clickIntent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
			
			widget.setPendingIntentTemplate(R.id.widgetRestaurantsList, clickPendingIntent);
			manager.updateAppWidget(appWidgetIds[i], widget);
		}
		super.onUpdate(context, manager, appWidgetIds);
	}
}
