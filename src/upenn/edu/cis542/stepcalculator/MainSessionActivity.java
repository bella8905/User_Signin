package upenn.edu.cis542.stepcalculator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import upenn.edu.cis542.stepcalculator.localDB.LocalDBManager;
import upenn.edu.cis542.stepcalculator.structure.session;
import upenn.edu.cis542.stepcalculator.structure.sessionSample;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainSessionActivity extends FragmentActivity{
	//for debug
	String tag = "stepCalculator";

	//bluetooth
	//ListView listView;
	private BluetoothAdapter BTAdapter; 
	protected int BT_ENABLE_RETURN = 4;

	ArrayAdapter<String> listAdapter;
	Set<BluetoothDevice> devicesArray;
	ArrayList<String> pairedDevices;
	ArrayList<BluetoothDevice> devices;

	IntentFilter filter;
	BroadcastReceiver receiver;

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	protected static final int CONNECTION_LOST = 2;
	protected static final int CONNECT_FAIL = 3;

	ConnectedThread connectedThread;
	ConnectThread connect;

	boolean isConnected = false;
	boolean isAppFinish = false;

	//test bluetooth
	TextView tv_contentTime;

	AlertDialog bTListDialog;

    //set time parameters
	TimeThread timeThread;
    private static final int msgKey1 = 4;
    long sysTime,iniTime;
    
    //set arduino data parameters
    private String stepStr;
    private int steps;
    private String speedStr;
    private float speed;
    private float distance;
    private float preDist; //for database refresh
    private boolean isFirstMsg = true;
    private String latStr, lonStr;
    private float latitude, longitude;
    private String xStr;
    private String yStr;
    private String zStr;
    private float calory;
    private int preSteps = 0;
    TextView contentStep,contentSpeed, contentLat, contentLon, contentCal,contentDist, contentAccel;
	Button btn_end;
	
    
    //session
    session mSession;
    
    //local database
    LocalDBManager mLocalDBManager;

    Handler mHandler = new Handler()
    {
    	public void handleMessage(Message message)
    	{
    		Log.d(tag, "handle message");
    		super.handleMessage(message);
    		switch(message.what)
    		{
    		case SUCCESS_CONNECT:
    			Log.d(tag, "HANDLE, start connect");
    			connectedThread = new ConnectedThread((BluetoothSocket)message.obj);
    			String s = "successfully connected";
    			connectedThread.write(s.getBytes());
    			//dismiss the search list dialog
    			//Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_SHORT).show();
    			connectedThread.start();
    	        if(!isConnected)	isConnected = true;
    			bTListDialog.dismiss();
    			//start timer
    	        //init time
    			if(iniTime<1)
    			{
            	    iniTime = System.currentTimeMillis();
        	        timeThread = new TimeThread();
        	        timeThread.start();
    			}
    			//disable the button
    			if(mSession == null)
    				startNewSession();

    			break;
    		case MESSAGE_READ:
    			//read in data
    			String str = (String)message.obj;
    			//Toast.makeText(getApplicationContext(), string, 0).show();
    			Log.d(tag, "read message: "+str);
    			int start = str.indexOf(' ');
    			int end = str.indexOf(' ', start+1);
    			stepStr = str.substring(start+1, end);
    			
    			//int steps;
    			try
    			{
    				 steps = Integer.parseInt(stepStr);
    			}
    			catch (NumberFormatException e) {
					// TODO: handle exception
    				Log.d(tag, "steps wrong");
    				break;
				}
    			
    			Log.d(tag, "steps " + steps + "; " + preSteps);
    			if(steps<preSteps||(steps-preSteps)>20)
    				break;
    			preSteps = steps;
    			
    			contentStep.setText(stepStr);  
    			
     			
    			//calculate calory and distance
    			calory = 0.05f * steps;
    			contentCal.setText(String.format("%.2f", calory));
    			preDist = distance;
    			distance = 1.0f/2000.0f * steps;
    			contentDist.setText(String.format("%.5f", distance));
    			
    			start = end;
    			end = str.indexOf( ' ', end+1);
    			if(start<end)
    				xStr = str.substring(start+1, end);
    			else {
					break;
				}
    			
    			start = end;
    			end = str.indexOf( ' ', end+1);
    			if(start<end)
    				yStr = str.substring(start+1, end);
    			else break;
    			
    			start = end;
    			end = str.indexOf( ' ', end+1);
    			if(start<end)
    				zStr = str.substring(start+1, end);
    			else break;
    			
    			contentAccel.setText(xStr + " " + yStr + " " + zStr);
    			
    			start = end;
    			end = str.indexOf( ' ', end+1);
    			if(start<end)
    				latStr = str.substring(start+1, end);
    			else break;
    			
    			try
    			{
    				 latitude = Float.parseFloat(latStr);
    			}
    			catch (NumberFormatException e) {
					// TODO: handle exception
    				Log.d(tag, "lat wrong");
    				break;
				}
    			
    			contentLat.setText(latStr);
    			
    			start = end;
    			end = str.indexOf( ' ', end+1);
    			if(start<end)
    				lonStr = str.substring(start+1, end);
    			else break;
    			
    			try
    			{
    				 longitude = Float.parseFloat(lonStr);
    			}
    			catch (NumberFormatException e) {
					// TODO: handle exception
    				Log.d(tag, "lon wrong");
    				break;
				}
    			
    			contentLon.setText(lonStr);
    			    			
    			start = end;
    			if(start<end)
    				speedStr = str.substring(start+1);
    			else break;
    			
    			try
    			{
    				 speed = Float.parseFloat(speedStr);
    			}
    			catch (NumberFormatException e) {
					// TODO: handle exception
    				Log.d(tag, "speed wrong");
    				break;
				}
    			
    			contentSpeed.setText(speedStr);
   
    			
    			//data base refresh
    			if(isFirstMsg)
    			{
    				//first sample
    				sessionSample ss = new sessionSample();
    				ss.latitude = latitude;
    				ss.longitude = longitude;
    				ss.speed = speed;
    				ss.time = getCurDate();
    				mLocalDBManager.createSessionSample(ss);
    				mLocalDBManager.createSessionRelation(mSession, ss);
    				
    				isFirstMsg = false;
    			}
    			else
    			{
        			if(distance - preDist > 100)
        			{
        				sessionSample ss = new sessionSample();
        				ss.latitude = latitude;
        				ss.longitude = longitude;
        				ss.speed = speed;
        				ss.time = getCurDate();
        				mLocalDBManager.createSessionSample(ss);
        				mLocalDBManager.createSessionRelation(mSession, ss);
        			}   				
    			}
    			
    			Log.d(tag, "HANDLE, read");

    			break;
    		case CONNECTION_LOST: 
    			Log.d(tag, "HANDLE, connection lost");
    			isConnected = false;
    			showAlertDialog("Lost connection. Do you want to reconnect?");  
    			break;
    		case CONNECT_FAIL: 
    			Log.d(tag, "HANDLE, connec fail");
    			Toast.makeText(getApplicationContext(), "connect fail, check if the port is open", Toast.LENGTH_SHORT).show();
    			break;
			case msgKey1:
				Log.d(tag, "HANDLE, timer");
	             sysTime = System.currentTimeMillis() - iniTime;

	             sysTime/=1000;
	             int hour = (int) (sysTime/3600);
	             int min = (int)(sysTime - hour*3600)/60;
	             int sec = (int)(sysTime%3600)%60;

	             tv_contentTime.setText(hour+"'"+min+"''"+sec);
	             break;

			default:
               break;
    			
    		}
    	}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_session);
        
        Log.d(tag, "Calculation create");
        tv_contentTime = (TextView)findViewById(R.id.content_time);
        
        initBluetoth();  
        
        //init android-arduino parameters
        contentSpeed = (TextView)findViewById(R.id.content_speed);
        contentCal = (TextView)findViewById(R.id.content_calory);
        //contentAccel = (TextView)findViewById(R.id.content_accel);
        contentLat = (TextView)findViewById(R.id.content_latitute);
        contentLon = (TextView)findViewById(R.id.content_lontitute);
        contentStep = (TextView)findViewById(R.id.content_step);
        contentDist = (TextView)findViewById(R.id.content_distance);

        mLocalDBManager = ((globalApplication)getApplication()).mLocalDBManager;
        
		btn_end = (Button)findViewById(R.id.btn_end);
		btn_end.setEnabled(false);
    }

    private String getCurDate()
    {
    	Calendar c = Calendar.getInstance();
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    
    private void startNewSession()
    {
    	Log.d(tag, "start a new session");

    	//create a new session
    	mSession = new session();
    	mSession.startTime = getCurDate();
    	mLocalDBManager.createSession(mSession);
    	
    	//enable end button
    	btn_end.setEnabled(true);
    }
    
    private void startBTSearch() {
		// TODO Auto-generated method stub
		//clear data first
    	
    	if(!isConnected)
    	{
    		pairedDevices.clear();
    		listAdapter.clear();
    		devices.clear();
    		getPairedBluetooth();
    		startDiscovery();
    		
    		showBTListDialog();
    		
    		return;
    	}
 
		showAlertDialog("Bluetooth is connected to one device, do you want to disconnet it and reconnect?");
	}

	private void initBluetoth() {
	// TODO Auto-generated method stub
    	//check bluetooth
    	//listView = (ListView)findViewById(R.id.listView_bluetooth);
    	//listView.setOnItemClickListener(this);
    	//listView.setAdapter(listAdapter);
    	listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
    	
    	BTAdapter = BluetoothAdapter.getDefaultAdapter();
    	pairedDevices = new ArrayList<String>();
    	filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	
    	devices = new ArrayList<BluetoothDevice>();
    	
    	receiver = new BroadcastReceiver() {
    		public void onReceive(Context context, Intent intent) {
    		    String action = intent.getAction();
    		    // When discovery finds a device
    		    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
    		    	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    		    	devices.add(device);
    		    	String s = "";
  
	    			for(int j = 0; j < pairedDevices.size(); ++j)
	    			{
	    				if(device.getAddress().equals(pairedDevices.get(j)))
	    				{
	    					s = "(paired)";
	    					//Log.i(tag, "insert");	    					
	    					break;
	    				}	    				
	    			}
    		    	listAdapter.add(device.getName() + " " + s + " " + "\n" + device.getAddress()); 
		        }
    		    
    		    else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
    		    {
    		    	
    		    }

    		    else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
    		    {

    		    }
    		    
    		    else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
    		    {
//    		    	//when you run the app, someone turn off the bt
//    		    	if(BTAdapter.getState() == BluetoothAdapter.STATE_OFF)
//    		    	{
//    		    		//Log.d(tag, "bluetooth turned off");
//    		    		isConnected = false;
//    		    		turnOnBT();
//    		    	}   		    		
    		    }
		    }
		};

		registerReceiver(receiver, filter);
		 filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver, filter);
		 filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, filter);
		 filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter);
    }

	@Override
	protected void onDestroy()
	{
		//unregister the receiver
		super.onDestroy();
		unregisterReceiver(receiver);
		//shup down the connection
		if(connectedThread != null)
		{
			isAppFinish = true;
			connectedThread.cancel();
		}

		if(timeThread!=null)
		{
			timeThread.setContinue(false);
		}

	}
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calculation_main, menu);
        return true;
    }
    
    //bluetooth
    public void startBluetooth(View view)
    {
    	//if the device does not support Bluetooth, simply return.
    	if (BTAdapter == null) 
    	{
    		showBTErrorDialog("No bluetooth");
    	}
    	else 
    	{    		
    		if (!BTAdapter.isEnabled()) 
    		{ 
    			turnOnBT();
    			//Log.d("bluetoothTest", "3");
    		}
    		else
    			startBTSearch();
		}
    }
    
        
    private void startDiscovery() {
		// TODO Auto-generated method stub
		BTAdapter.cancelDiscovery();
		BTAdapter.startDiscovery();
	}

	private void turnOnBT() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
		startActivityForResult(intent, 1);
	}

	private void getPairedBluetooth() {
		// TODO Auto-generated method stub
    	devicesArray = BTAdapter.getBondedDevices();
		// If there are paired devices
		if (devicesArray.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : devicesArray) {
		        // Add the name and address to an array adapter to show in a ListView
		        pairedDevices.add(device.getAddress());
		    }
		}
	}
    
    //after trying to open bluetooth
    //onActivityResult will be called before onResume, so we cannot show the dialog here
    //????should come back here later???
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
    	super.onActivityResult(requestCode, resultCode, data);
    	//Log.d(tag, "" + resultCode);
    	if(resultCode == RESULT_CANCELED)
    	{
            showBTErrorDialog("Bluetooth Canceled!");
            //finish();
    	}
    	else if(resultCode == RESULT_OK)
    	{
    		startBTSearch();
    	}
    	
    }

	public static class BTErrorDialogFragment extends DialogFragment {
	    String m_message;

	    static BTErrorDialogFragment newInstance(String message) {
	        BTErrorDialogFragment f = new BTErrorDialogFragment();

	        // Supply num input as an argument.
	        Bundle args = new Bundle();
	        args.putString("message", message);
	        f.setArguments(args);

	        return f;
	    }

		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	m_message = getArguments().getString("message");
			//Log.d("bluetoothTest", m_message);
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(m_message)
	               .setNegativeButton(R.string.btn_OK, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                   }
	               });
	        // Create the AlertDialog object and return it
	        Dialog d = builder.create();
	        //d.setCanceledOnTouchOutside(false);
	        return d;
		}		 
	}
          

	void showBTErrorDialog(String message) {

	    // DialogFragment.show() will take care of adding the fragment
	    // in a transaction.  We also want to remove any currently showing
	    // dialog, so make our own transaction and take care of that here.
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    BTErrorDialogFragment newFragment = BTErrorDialogFragment.newInstance(message);
	    ft.add(newFragment, "dialog");
		ft.commitAllowingStateLoss();
//	    newFragment.show(ft, "dialog");
	}

	void showBTListDialog()
	{
		//list view dialog
		bTListDialog = new AlertDialog.Builder(this).setTitle("Bluetooth devices")
		.setIcon( android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(listAdapter, 0, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) 
			{
				if(BTAdapter.isDiscovering())
				{
					BTAdapter.cancelDiscovery();
				}

				//Log.d(tag, "paired device is selected");
				BluetoothDevice selectedDevice = devices.get(which);
				Toast.makeText(getApplicationContext(), selectedDevice.getName() + " is selected" , Toast.LENGTH_SHORT).show();
				connect = new ConnectThread(selectedDevice);
				connect.start();

				//dialog.dismiss();
				}
			})
		.setNegativeButton("Cancel", null).show();
	}

	//show an alertdialog suggesting if reconnect
	void showAlertDialog(String message)
	{
		new AlertDialog.Builder(this)
		.setIcon( android.R.drawable.ic_dialog_alert)
		.setMessage(message)
		.setNegativeButton("Cancel", null)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		{
            public void onClick(DialogInterface dialog, int id) 
            {
            	//cancel the connection and research
            	if(isConnected)
            	{
            		connectedThread.cancel();
            	}
            	else 
            	{
					turnOnBT();
				}
            }
		}).show();
	}

	//Bluetooth

    private void connectionLost() {
		// TODO Auto-generated method stub
    	 mHandler.obtainMessage(CONNECTION_LOST).sendToTarget();
	}
    
    private void connectFail() {
		// TODO Auto-generated method stub
    	mHandler.obtainMessage(CONNECT_FAIL).sendToTarget();
	}

	private class ConnectThread extends Thread {

		private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;

	    public ConnectThread(BluetoothDevice device) {
	    	Log.d(tag, "build connect");
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;

	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) 
	        { 
	        	Log.d(tag, "socket created failed"); 
	        }
	        mmSocket = tmp;
	    }

		public void run() {
	    	Log.d(tag, "run connect");
	        // Cancel discovery because it will slow down the connection
	        BTAdapter.cancelDiscovery();

	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();

	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	        	Log.d(tag, "socket connect failed");
	        	connectFail();
	            try {	                
	                mmSocket.close();

	            } catch (IOException closeException) 
	            { 
	            	Log.d(tag, "socket close failed"); 
	            	connectFail();
	            }
	            return;
	        }

	        // Do work to manage the connection (in a separate thread)
	        mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
	    }


		/** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}



	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;

	    private String readMessage = "";

	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;

	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { Log.d(tag, "tmp socket not connected"); }

	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }

	    public void run() {
	    	byte[] buffer = new byte[256];  // buffer store for the stream
	        int bytes; // bytes returned from read()
	        Log.d(tag, "run connection"); 

	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            try {	            	
	            	Log.d(tag, "skfjksjfksjglajfgak");
	                // Read from the InputStream
	            	//buffer = new byte[1024];
	                bytes = mmInStream.read(buffer);
	                // Send the obtained bytes to the UI activity
	                Log.d(tag, "new string "+new String(buffer));
	                readMessage += new String(buffer);
	                int start, end;
	                if(((start = (readMessage.indexOf('$')))!=-1))
	                {
	                	Log.d(tag, "start" + start);
	                	if((end = (readMessage.indexOf('@', start+1)))!=-1)
	                	{
	                		Log.d(tag, "end" + end);
	                		String str = readMessage.substring(start+1, end);
	                		if(readMessage.length() == end+1)
	                			readMessage = "";
	                		else {
		                		readMessage = readMessage.substring(end+1);
							}

	    	                Log.d(tag, str);
	    	                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, str)
	    	                        .sendToTarget();
	                	}
	                }
	            } catch (IOException e) { 
	            	Log.d(tag, "connection start failed"); 
	            	if(!isAppFinish&&isConnected)
	            		connectionLost();
	            	else if(!isConnected)
	            		connectFail();
	                break;
	            } 

	        }
	    }


		/* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }

	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	    	Log.d(tag, "asdfadfa");
	        try {
	            mmSocket.close();
	            //
	            isConnected = false;
	        } catch (IOException e) { }
	    }
	}

	//set time thread
	 public class TimeThread extends Thread 
	 {
		 private boolean shouldContinue = true;
	    @Override
	    public void run () 
	    {
	    	do 
	    	{
	    		try 
			    {
			        Thread.sleep(1000);
			        Message msg = new Message();
			        msg.what = msgKey1;
			        mHandler.sendMessage(msg);
			    }
			    catch (InterruptedException e) 
			    {
			        e.printStackTrace();
			    }
	        } while(shouldContinue);
	     }

		 public void setContinue(boolean t_shouldContinue) { shouldContinue = t_shouldContinue; }
	 }

	 
	 ///////!!!!!!!!!!!change back later
	 public void shareIt(View view){
//	    	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);  
//	    	sharingIntent.setType("text/plain"); 
//	    	String shareBody = "Here is the share content body";  
//	    	sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");  
//	    	sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);  
//	    	startActivity(Intent.createChooser(sharingIntent,"Share via"));
		 
		 
		 //test add sample
		 	
//		sessionSample ss = new sessionSample();
//		ss.latitude = latitude;
//		ss.longitude = longitude;
//		ss.speed = speed;
//		ss.time = getCurDate();
//		mLocalDBManager.createSessionSample(ss);
//		mLocalDBManager.createSessionRelation(mSession, ss);
		 
		 //test delete session
		// mLocalDBManager.deleteSession(2);
	}
	 
	 //end session
	 public void endSession(View view)
	 {
		 Log.d(tag, "endSession");
		 //update the session
		 //for test
		 steps = 100;
		 calory = 200;
		 distance = 20.9f;
		 
		 mSession.endTime = getCurDate();
		 mSession.calory = calory;
		 mSession.steps = steps;
		 mSession.distance = distance;
		 mLocalDBManager.updateSession(mSession);
		 //start session review
		 startActivity(new Intent(getApplicationContext(), SessionReviewActivity.class));
		 
		 finish();
	 }
	 
}