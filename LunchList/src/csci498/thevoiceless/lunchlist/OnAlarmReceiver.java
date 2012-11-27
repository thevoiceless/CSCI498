package csci498.thevoiceless.lunchlist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;

public class OnAlarmReceiver extends BroadcastReceiver
{
	private static final int NOTIFY_ME_ID = 1337;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean useNotification = prefs.getBoolean(context.getString(R.string.key_alarm_use_notification), true);
		
		if (useNotification)
		{
			NotificationManager notificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Notification notification = new Notification(R.drawable.stat_notify_chat, 
					context.getString(R.string.notification_lunch_ticker), 
					System.currentTimeMillis());
			
			PendingIntent i = PendingIntent.getActivity(context, 
					0, 
					new Intent(context, AlarmActivity.class), 
					0);
			
			notification.setLatestEventInfo(context, 
					context.getString(R.string.activity_lunchlist), 
					context.getString(R.string.notification_lunch_content), 
					i);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			
			String useSound = prefs.getString(context.getString(R.string.key_alarm_ringtone), null);
			if (useSound != null)
			{
				notification.sound = Uri.parse(useSound);
				notification.audioStreamType = AudioManager.STREAM_ALARM;
			}
			
			notificationMgr.notify(NOTIFY_ME_ID, notification);
		}
		else
		{
			Intent i = new Intent(context, AlarmActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
