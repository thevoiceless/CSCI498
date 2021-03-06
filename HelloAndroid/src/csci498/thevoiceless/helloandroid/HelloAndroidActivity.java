package csci498.thevoiceless.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import java.util.Date;

public class HelloAndroidActivity extends Activity implements View.OnClickListener  {
	Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btn = new Button(this);
        btn.setOnClickListener(this);
        updateTime();
        //setContentView(R.layout.activity_hello_android);
        setContentView(btn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_hello_android, menu);
        return true;
    }
    
    public void onClick(View view)
    {
    	updateTime();
    }
    
    private void updateTime()
    {
    	btn.setText(new Date().toString());
    }
}
