package csci498.thevoiceless.lunchlist;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpPage extends Activity
{
	private final static String HELP_URL = "file:///android_asset/help.html";
	private WebView browser;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_help);
		
		setDataMembers();
		browser.loadUrl(HELP_URL);
	}
	
	private void setDataMembers()
	{
		browser = (WebView) findViewById(R.id.webview);
	}

}
