package csci498.thevoiceless.sherlocktest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnCreateOptionsMenuListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class Preferences extends Activity implements OnCreateOptionsMenuListener
{
	ActionBarSherlock sherlock = ActionBarSherlock.wrap(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Allow placing entries along the bottom of the screen
		sherlock.setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		// Note: ActionBarSherlock instance handles the content layout
		sherlock.setContentView(R.layout.preferences);
	}

	// This onCreateOptionsMenu method is passed a standard android.view.Menu object
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu)
	{
		// Let the ActionBarSherlock object handle the creation of menus
		return sherlock.dispatchCreateOptionsMenu(menu);
	}
	
	// The ActionBarSherlock object, in turn, will have the parent Activity class
	// receive an onCreateOptionsMenu() method, but this time the Menu is an
    // com.actionbarsherlock.view.Menu object
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem example1 = menu.add("Example1");
		example1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		MenuItem dismiss = menu.add("Dismiss");
		dismiss.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		dismiss.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				finish();
				return true;
			}
		});
		
		return true;
	}

}
