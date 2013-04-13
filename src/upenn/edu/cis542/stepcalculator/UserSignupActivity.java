package upenn.edu.cis542.stepcalculator;

import upenn.edu.cis542.stepcalculator.remoteDB.RemoteDBManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class UserSignupActivity extends Activity {
	
	private static final String tag = "stepCalculator";	
	
	private RemoteDBManager mDBManager;
	
	//edit text
	EditText et_email;
	EditText et_username;
	EditText et_password;
	EditText et_passwordAgain;
	EditText et_height;
	EditText et_weight;
	RadioGroup rg_gender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_signup);
		
		mDBManager = ((globalApplication)getApplication()).mRemoteDBManager;
		
        //edit text
        et_email = (EditText)findViewById(R.id.et_email);
        et_username = (EditText)findViewById(R.id.et_username);
        et_password = (EditText)findViewById(R.id.et_password);
        et_passwordAgain = (EditText)findViewById(R.id.et_passwordAgain);
        et_height = (EditText)findViewById(R.id.et_height);
        et_weight = (EditText)findViewById(R.id.et_weight);
        rg_gender = (RadioGroup)findViewById(R.id.rg_gender);
	}

	
	public void signUp(View view)
	{
    	Log.d(tag, "sign up");
    	String email = et_email.getText().toString();
    	String username = et_username.getText().toString();
    	String password = et_password.getText().toString();
    	String passwordAgain = et_passwordAgain.getText().toString();
    	String heightStr = et_height.getText().toString();
    	String weightStr = et_weight.getText().toString();
    	float height;
    	float weight;
    	int gender;
    	
    	int selectedGender = rg_gender.getCheckedRadioButtonId();
    	Log.d(tag, ""+selectedGender);
    	RadioButton radioGenderButton = (RadioButton) findViewById(selectedGender);
    	String genderStr = radioGenderButton.getText().toString();
    	
    	if(email.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no email", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(username.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no username", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(password.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no password", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(passwordAgain.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no password again", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	if(!password.equals(passwordAgain))
    	{
    		Toast.makeText(getApplicationContext(), "password not match", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	if(genderStr.equals("Male"))
    		gender = 1;
    	else 
    		gender = 0;
    	
		if(!heightStr.equals(""))
		{
	    	try
	    	{
	    		
	    		height = Float.parseFloat(heightStr);
	    	}
	    	catch(NumberFormatException e)
	    	{
	    		Toast.makeText(getApplicationContext(), "height not correct!", Toast.LENGTH_SHORT).show();
	    		return;
	    	}
		}
    	
		if(!heightStr.equals(""))
		{
	    	try
	    	{
	    		weight = Float.parseFloat(weightStr);
	    	}
	    	catch(NumberFormatException e)
	    	{
	    		Toast.makeText(getApplicationContext(), "weight not correct!", Toast.LENGTH_SHORT).show();
	    		return;
	    	}
		}
		
    	//create new user
    	mDBManager.StartCreateUser(email, username, password, heightStr, weightStr, gender);		
	}
	
	public void goBack(View view){
//			 Intent signUpIntent = new Intent();  
//			 signUpIntent.setClass(this, User_Signin.class );
//		     startActivity(signUpIntent);
		finish();
	}
}
