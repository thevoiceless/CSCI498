package csci498.thevoiceless.lunchlist;

import android.app.IntentService;
import android.content.Intent;

public class FeedService extends IntentService
{
	private static final String NAME = "FeedService";
	
	// IntentService requires you to implement a no-argument constructor
	// and chain to the superclass, supplying a name for your IntentService
	public FeedService()
	{
		super(NAME);
	}
	
	@Override
	public void onHandleIntent(Intent i)
	{
		// TODO
	}

}
