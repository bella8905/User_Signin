package upenn.edu.cis542.stepcalculator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainMenuActivity extends Activity {

	TextView tv_hello;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		//ui
		tv_hello = (TextView)findViewById(R.id.tv_hello);
		
		tv_hello.setText("Hello " + globalApplication.curUser.username);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	public void startSession(View view)
	{
		startActivity(new Intent(getApplicationContext(), MainSessionActivity.class));
		//kill the activity
		finish();
	}
	
	
	public void goHistory(View view)
	{
		startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
		
	}
	
	public void goSettings(View view)
	{
		startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
		
	}

}
