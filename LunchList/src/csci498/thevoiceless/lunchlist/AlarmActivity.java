package csci498.thevoiceless.lunchlist;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmActivity extends Activity implements MediaPlayer.OnPreparedListener
{
	MediaPlayer player = new MediaPlayer();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_alarm);
		
		playSoundIfEnabled();
	}
	
	@Override
	public void onPause()
	{
		if (player.isPlaying())
		{
			player.stop();
		}
		super.onPause();
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		player.start();
	}
	
	private void playSoundIfEnabled()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String useSound = prefs.getString(getString(R.string.key_alarm_ringtone), null);
		
		if (useSound != null)
		{
			player.setAudioStreamType(AudioManager.STREAM_ALARM);
			try
			{
				player.setDataSource(useSound);
				player.setOnPreparedListener(this);
				player.prepareAsync();
			}
			catch (Exception e)
			{
				Log.e(getString(R.string.app_name), "Exception while attempting to play ringtone", e);
			}
			
		}
	}
}
