package csci498.thevoiceless.sherlocktest;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

// Extend SherlockActivity
public class SherlockTesting extends SherlockActivity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sherlock_testing);
	}

	// Note: These are actionbarsherlock.view.Menu objects that
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem prefs = menu.add("Preferences");
		prefs.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		prefs.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				Intent prefIntent = new Intent(SherlockTesting.this,Preferences.class);
				SherlockTesting.this.startActivity(prefIntent);
				return true;
			}
		});

		// Empty menu items to test display changes in different orientations
		menu.add("Other Stuff").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Great Stuff").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Probably Hidden").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}
}
