package csci498.thevoiceless.lunchlist;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OnBootReceiver extends BroadcastReceiver
{
	public static void setAlarm(Context context)
	{
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String time = prefs.getString(context.getString(R.string.key_alarm_time), 
				context.getString(R.string.pref_default_alarm_time));
		
		setCalendar(calendar, time);
		
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, 
				calendar.getTimeInMillis(), 
				AlarmManager.INTERVAL_DAY, 
				getPendingIntent(context));
	}
	
	public static void cancelAlarm(Context context)
	{
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.cancel(getPendingIntent(context));
	}
	
	private static void setCalendar(Calendar calendar, String time)
	{
		calendar.set(Calendar.HOUR_OF_DAY, TimePreference.getHour(time));
		calendar.set(Calendar.MINUTE, TimePreference.getMinute(time));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		// If it is before the alarm time today, we want the next alarm to be today's
		// If it is after today's alarm should have gone off, we want the next alarm to be tomorrow's
		if (calendar.getTimeInMillis() < System.currentTimeMillis())
		{
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
	
	private static PendingIntent getPendingIntent(Context context)
	{
		Intent i = new Intent(context, OnAlarmReceiver.class);
		return PendingIntent.getBroadcast(context, 0, i	, 0);
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		setAlarm(context);
	}
}
