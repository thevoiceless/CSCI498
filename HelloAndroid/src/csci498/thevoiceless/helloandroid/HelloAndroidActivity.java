package csci498.thevoiceless.helloandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HelloAndroidActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_android);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_hello_android, menu);
        return true;
    }
}
