package csci498.thevoiceless.lunchlist;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Log;

public class FeedActivity extends ListActivity
{
	private static class FeedTask extends AsyncTask<String, Void, RSSFeed>
	{
		private RSSReader reader = new RSSReader();
		private Exception e = null;
		private FeedActivity activity = null;
		
		FeedTask(FeedActivity activity)
		{
			attach(activity);
		}
		
		void attach(FeedActivity activity)
		{
			this.activity = activity;
		}
		
		void detach()
		{
			this.activity = null;
		}
		
		@Override
		public RSSFeed doInBackground(String... urls)
		{
			RSSFeed result = null;
			
			try
			{
				result = reader.load(urls[0]);
			}
			catch(Exception e)
			{
				this.e = e;
			}
			return result;
		}
		
		@Override
		public void onPostExecute(RSSFeed feed)
		{
			if(e == null)
			{
				activity.setFeed(feed);
			}
			else
			{
				Log.e("LunchList", "Exception while parsing feed", e);
				activity.showException(e);
			}
		}
	}
	
	private void showException(Throwable t)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder
			.setTitle(R.string.exception)
			.setMessage(t.toString())
			.setPositiveButton(R.string.ok, null)
			.show();
	}
}
