package csci498.thevoiceless.lunchlist;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class EditPreferences extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		addPreferencesFromResource(R.xml.preferences);
	}
}
