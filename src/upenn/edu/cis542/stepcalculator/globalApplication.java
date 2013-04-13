package upenn.edu.cis542.stepcalculator;

import upenn.edu.cis542.stepcalculator.localDB.LocalDBManager;
import upenn.edu.cis542.stepcalculator.remoteDB.RemoteDBManager;
import upenn.edu.cis542.stepcalculator.structure.user;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class globalApplication extends Application {
	
	public static user curUser;  //system should remember the curUser
	
	private static final String tag = "stepCalculator";
	
	public RemoteDBManager mRemoteDBManager;
	public LocalDBManager mLocalDBManager;
	
	// Message types sent from the database management Handler
	public static final int CONNECT_SUCCESS = 1;
	public static final int CONNECT_FAIL = 2;
	public static final int LOAD_USERS_COMPLETE = 3;
	public static final int LOAD_USERS_ADDR_ERROR = 4;
	public static final int LOAD_USERS_DATA_ERROR = 5;
	public static final int CREATE_USER_COMPLETE = 6;
	public static final int CREATE_USER_ADDR_ERROR = 7;
	public static final int CREATE_USER_DATA_ERROR = 8;
	public static final int USER_LOGIN_COMPLETE = 9;
	public static final int USER_LOGIN_ADDR_ERROR = 10;
	public static final int USER_LOGIN_DATA_ERROR = 11;

	public static final int CREATE_SESSION_COMPLETE = 12;
	public static final int CREATE_SESSION_ADDR_ERROR = 13;
	public static final int CREATE_SESSION_DATA_ERROR = 14;
	
	//public static final int CHECK_NET_CONNECTION = 30;
	    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(tag, "handle message");
            switch (msg.what) {
            //remote database connect
            case CONNECT_FAIL:
            	Log.d(tag, "CONNECT_FAIL");
            	Toast.makeText(getApplicationContext(), "Unable to connect to remote database, please check database config", Toast.LENGTH_SHORT).show();
                break;
            case CONNECT_SUCCESS:
            	Log.d(tag, "CONNECT_SUCCESS");
            	Toast.makeText(getApplicationContext(), "connect to remote database successfully", Toast.LENGTH_SHORT).show();
                break;
            //////////////////////////
            //load_all_users
            case LOAD_USERS_ADDR_ERROR:
            	Log.d(tag, "LOAD_USERS_ADDR_ERROR");
            	Toast.makeText(getApplicationContext(), "Unable to get data from database, please check http address in LoadAllUsers class", Toast.LENGTH_SHORT).show();
                break;
            case LOAD_USERS_COMPLETE:
            	Log.d(tag, "LOAD_USERS_COMPLETE");
            	Toast.makeText(getApplicationContext(), "load products complete", Toast.LENGTH_SHORT).show();
                break;
            case LOAD_USERS_DATA_ERROR:
            	Log.d(tag, "LOAD_USERS_DATA_ERROR");
            	Toast.makeText(getApplicationContext(), "Get user data unsuccessfully, check the get_all_users php file", Toast.LENGTH_SHORT).show();
                break;
            //////////////////////////  
            //create user
            case CREATE_USER_ADDR_ERROR:
            	Log.d(tag, "CREATE_USER_ADDR_ERROR");
            	Toast.makeText(getApplicationContext(), "Unable to get data from database, please check http address in CreateNewUser class", Toast.LENGTH_SHORT).show();
                break;
            case CREATE_USER_COMPLETE:
            	Log.d(tag, "CREATE_USER_COMPLETE");
            	Toast.makeText(getApplicationContext(), "create new user complete", Toast.LENGTH_SHORT).show();
            	Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
            	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	startActivity(i);
                break;
            case CREATE_USER_DATA_ERROR:
            	Log.d(tag, "CREATE_USER_DATA_ERROR");
            	if(msg.arg1 == 1)
            	{
            		//user exists
            		Toast.makeText(getApplicationContext(), "User exists!", Toast.LENGTH_SHORT).show();
            	}
            	else
            	{
            		Toast.makeText(getApplicationContext(), "Get user data unsuccessfully, check the create_user php file", Toast.LENGTH_SHORT).show();
            	}
                break;
                //////////////////////////  
                //user login
                case USER_LOGIN_ADDR_ERROR:
                	Log.d(tag, "USER_LOGIN_ADDR_ERROR");
                	Toast.makeText(getApplicationContext(), "Unable to get data from database, please check http address in UserLogIn class", Toast.LENGTH_SHORT).show();
                    break;
                case USER_LOGIN_COMPLETE:
                	Log.d(tag, "USER_LOGIN_COMPLETE");
                	Toast.makeText(getApplicationContext(), "user login complete", Toast.LENGTH_SHORT).show();
                	Intent i1 = new Intent(getApplicationContext(), MainMenuActivity.class);
                	i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                	startActivity(i1);
                    break;
                case USER_LOGIN_DATA_ERROR:
                	Log.d(tag, "USER_LOGIN_DATA_ERROR");
                	if(msg.arg1 == 1)
                	{
                		//wroing user
                		Toast.makeText(getApplicationContext(), "User not exists!", Toast.LENGTH_SHORT).show();
                	}
                	else if(msg.arg1 == 2)
                	{
                		Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                	}
                	else
                	{
                		Toast.makeText(getApplicationContext(), "Get user data unsuccessfully, check the create_user php file", Toast.LENGTH_SHORT).show();
                	}
                    break;
                    
                    //////////////////////////  
                    //create session
                    case CREATE_SESSION_ADDR_ERROR:
                    	Log.d(tag, "CREATE_SESSION_ADDR_ERROR");
                    	Toast.makeText(getApplicationContext(), "Unable to get data from database, please check http address in CreateNewSession class", Toast.LENGTH_SHORT).show();
                        break;
                    case CREATE_SESSION_COMPLETE:
                    	Log.d(tag, "CREATE_SESSION_COMPLETE");
                    	//Toast.makeText(getApplicationContext(), "create new session complete", Toast.LENGTH_SHORT).show();
                        break;
                    case CREATE_SESSION_DATA_ERROR:
                    	Log.d(tag, "CREATE_SESSION_DATA_ERROR");
                    	Toast.makeText(getApplicationContext(), "Get user data unsuccessfully, check the create_session php file", Toast.LENGTH_SHORT).show();
                    	break;
            }
        }
    };
	
	public void onCreate ()
	{
		Log.d(tag, "new Application");
		//initiate mDBManager
		mRemoteDBManager = new RemoteDBManager(this, mHandler);
		mLocalDBManager = new LocalDBManager(this);
		mLocalDBManager.open();
		//mConMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	//sync local data to server
	//happens when user trys to delete a session, will delete a session on server and local
	//or press the sync button, push local data onto server
	public void deleteSession(int SEID)
	{
		//delete session in remote database,
	    //if not succeed, return
		
		//if succeed, delete session in local database
		
	}
	
	public void syncDB()
	{
		
	}
}