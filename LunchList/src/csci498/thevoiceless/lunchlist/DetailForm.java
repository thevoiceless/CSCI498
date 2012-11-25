package csci498.thevoiceless.lunchlist;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DetailForm extends FragmentActivity
{	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_detail_form);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		String restaurantId = getIntent().getStringExtra(LunchList.RESTAURANT_ID_KEY);
		if (restaurantId != null)
		{
			DetailFragment details = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detailsPane);
			
			if (details != null)
			{
				details.loadRestaurantById(restaurantId);
			}
		}
	}
}
