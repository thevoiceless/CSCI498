package csci498.thevoiceless.lunchlist;

import android.annotation.TargetApi;
import android.content.Intent;
import android.widget.RemoteViewsService;

@TargetApi(11)
public class ListWidgetService extends RemoteViewsService
{
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent)
	{
		return new ListViewsFactory(this.getApplicationContext(), intent);
	}
	
}
