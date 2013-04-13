package upenn.edu.cis542.stepcalculator;


import upenn.edu.cis542.stepcalculator.remoteDB.RemoteDBManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class UserSigninActivity extends Activity {

	private static final String tag = "stepCalculator";	
	
	private RemoteDBManager mDBManager;
	
	//edit text
	EditText et_email;
	EditText et_password;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signin);
        
        mDBManager = ((globalApplication)getApplication()).mRemoteDBManager;
        
        //edit text
        et_email = (EditText)findViewById(R.id.et_email);
        et_password = (EditText)findViewById(R.id.et_password);
    }
     
    public void signIn(View view)
    {
    	Log.d(tag, "sign in");
    	
    	//mDBManager.startLoadAllUsers();
    	//get user detail by email, if email exists, check 
    	String email = et_email.getText().toString();
    	String password = et_password.getText().toString();
    	
    	if(email.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no email", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(password.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no password", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	//create new user
    	mDBManager.StartUserLogin(email, password);
    }

    public void signUp(View view)
    {
    	startActivity(new Intent(getApplicationContext(), UserSignupActivity.class));
    	//startActivity(new Intent(this, CalculationMain.class));
    }
}
