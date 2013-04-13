package upenn.edu.cis542.stepcalculator.localDB;
import java.util.ArrayList;

import upenn.edu.cis542.stepcalculator.structure.session;
import upenn.edu.cis542.stepcalculator.structure.sessionSample;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class LocalDBManager {
	public static final String tag = "remoteDB";
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumnsSE = { MySQLiteHelper.SE_C_ID, MySQLiteHelper.SE_C_START_TIME,  
									MySQLiteHelper.SE_C_END_TIME, MySQLiteHelper.SE_C_CALORY, 
									MySQLiteHelper.SE_C_DISTANCE, MySQLiteHelper.SE_C_DESCRIPTION,
									MySQLiteHelper.SE_C_STEPS};
	
	private String[] allColumnsSS = { MySQLiteHelper.SS_C_ID, MySQLiteHelper.SS_C_LONGITUDE,  
									MySQLiteHelper.SS_C_LATITUDE, MySQLiteHelper.SS_C_SPEED, 
									MySQLiteHelper.SS_C_TIME};
	
	private String[] allColumnsSR = { MySQLiteHelper.SR_C_ID, MySQLiteHelper.SR_C_SEID,  
									MySQLiteHelper.SR_C_SSID};
	
	public LocalDBManager(Context context) 
	{
	  dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
	  database = dbHelper.getWritableDatabase();
	}

	public void close() {
	  dbHelper.close();
	}
	
	////////////////////////////////////
	//TABLE SESSIONS
	//create the session before endTime, calory, distance, description are set
	//we need an update after the session completes
	public void createSession(session newSession) {
		  ContentValues values = new ContentValues();
		  values.put(MySQLiteHelper.SE_C_START_TIME, newSession.startTime);
		  values.put(MySQLiteHelper.SE_C_END_TIME, newSession.endTime);
		  values.put(MySQLiteHelper.SE_C_CALORY, newSession.calory);
		  values.put(MySQLiteHelper.SE_C_DISTANCE, newSession.distance);
		  values.put(MySQLiteHelper.SE_C_DESCRIPTION, newSession.description);
		  values.put(MySQLiteHelper.SE_C_STEPS, newSession.steps);
		  long id = database.insert(MySQLiteHelper.TABLE_SESSIONS, null, values);
		  newSession.SEID = (int)id;
		  
	}
	

	
//  users are not allowed ot delete anything	
//	//come back later
//	public void deleteSession(session session) {
//		//find all samples related to this session, delete samples
//		//delete session relation
//		//delete session
//		//send to remoteDBManager deleteSession
//	  int SEID = session.SEID;
//	  Log.d(tag, "Session deleted with id: " + SEID);
//	  database.delete(MySQLiteHelper.TABLE_SESSIONS, MySQLiteHelper.SE_C_ID
//	      + " = " + SEID, null);
//	}
//	
//	public void deleteSession(int SEID) {
//		//find all samples related to this session, delete samples
//		//delete session relation
//		//delete session
//		//send to remoteDBManager deleteSession
//	  Log.d(tag, "Session deleted with id: " + SEID);
//	  database.delete(MySQLiteHelper.TABLE_SESSIONS, MySQLiteHelper.SE_C_ID
//	      + " = " + SEID, null);
//	}

	private session cursorToSession(Cursor cursor) {
	  session session = new session();
	  session.SEID = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.SE_C_ID));
	  session.endTime = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.SE_C_END_TIME));
	  session.startTime = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.SE_C_START_TIME));
	  session.distance = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.SE_C_DISTANCE));
	  session.calory = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.SE_C_CALORY));
	  session.description = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.SE_C_DESCRIPTION));
	  session.steps = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.SE_C_STEPS));
	  
	  //topScore.setComment(cursor.getString(1));
	  return session;
	}
	
	public void updateSession(session newSession)
	{
		//find the top score
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.SE_C_START_TIME, newSession.startTime);
		values.put(MySQLiteHelper.SE_C_END_TIME, newSession.endTime);
		values.put(MySQLiteHelper.SE_C_CALORY, newSession.calory);
		values.put(MySQLiteHelper.SE_C_DISTANCE, newSession.distance);
		values.put(MySQLiteHelper.SE_C_DESCRIPTION, newSession.description);
		values.put(MySQLiteHelper.SE_C_STEPS, newSession.steps);
		int SEID = newSession.SEID;
		
		database.update(MySQLiteHelper.TABLE_SESSIONS, values, MySQLiteHelper.SE_C_ID + " = ?", new String[]{Integer.toString(SEID)});		
	}
	
	public void deleteSession(int SEID)
	{
		
		//delete all samples
		ArrayList<sessionSample> samples = getAllSamples(SEID);
		for(sessionSample ss:samples)
		{
			deleteSessionSample(ss.SSID);
		}
		
		//delete all relations
		deleteSessionRelations(SEID);
		
		//delete session
		database.delete(MySQLiteHelper.TABLE_SESSIONS, MySQLiteHelper.SE_C_ID
			      + " = " + SEID, null);
	}
	
	////////////////////////////////
	//session samples
	public void createSessionSample(sessionSample newSessionSample) {
		  ContentValues values = new ContentValues();
		  values.put(MySQLiteHelper.SS_C_LATITUDE, newSessionSample.latitude);
		  values.put(MySQLiteHelper.SS_C_LONGITUDE, newSessionSample.longitude);
		  values.put(MySQLiteHelper.SS_C_SPEED, newSessionSample.speed);
		  values.put(MySQLiteHelper.SS_C_TIME, newSessionSample.time);
		  //values.put(MySQLiteHelper.SE_C_DESCRIPTION, newSession.description);
		  long id = database.insert(MySQLiteHelper.TABLE_SESSION_SAMPLES, null, values);
		  newSessionSample.SSID = (int)id;
	}
	
	private sessionSample cursorToSS(Cursor cursor) {
		  sessionSample sessionSample = new sessionSample();
		  sessionSample.SSID = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.SS_C_ID));
		  sessionSample.latitude = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.SS_C_LATITUDE));
		  sessionSample.longitude = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.SS_C_LONGITUDE));
		  sessionSample.speed = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.SS_C_SPEED));
		  sessionSample.time = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.SS_C_TIME));
		  
		  //topScore.setComment(cursor.getString(1));
		  return sessionSample;
		}
	
	//get all samples for a session
	public ArrayList<sessionSample> getAllSamples(session session)
	{
		  ArrayList<sessionSample> samples = new ArrayList<sessionSample>();

		  Cursor cursorSR = database.query(MySQLiteHelper.TABLE_SESSION_RELATIONS,
		      allColumnsSR, MySQLiteHelper.SR_C_SEID + " = " + session.SEID, null, null, null, null);

		  cursorSR.moveToFirst();
		  while (cursorSR.moveToNext()) 
		  {
			  int SSID = cursorSR.getInt(cursorSR.getColumnIndex(MySQLiteHelper.SR_C_SSID));
			  Cursor cursor = database.query(MySQLiteHelper.TABLE_SESSION_SAMPLES,
				      allColumnsSS, MySQLiteHelper.SS_C_ID + " = " + SSID, null, null, null, null);
			  cursor.moveToFirst();
			  sessionSample sample = cursorToSS(cursor);
			  samples.add(sample);
		  }
		  // Make sure to close the cursor
		  cursorSR.close();
		  return samples;		
	}
	
	//get all samples for a session
	public ArrayList<sessionSample> getAllSamples(int SEID)
	{
		  ArrayList<sessionSample> samples = new ArrayList<sessionSample>();

		  Cursor cursorSR = database.query(MySQLiteHelper.TABLE_SESSION_RELATIONS,
		      allColumnsSR, MySQLiteHelper.SR_C_SEID + " = " + SEID, null, null, null, null);

		  //cursorSR.moveToFirst();
		  while (cursorSR.moveToNext()) 
		  {
			  int SSID = cursorSR.getInt(cursorSR.getColumnIndex(MySQLiteHelper.SR_C_SSID));
			  Cursor cursor = database.query(MySQLiteHelper.TABLE_SESSION_SAMPLES,
				      allColumnsSS, MySQLiteHelper.SS_C_ID + " = " + SSID, null, null, null, null);
			  cursor.moveToFirst();
			  sessionSample sample = cursorToSS(cursor);
			  samples.add(sample);
		  }
		  // Make sure to close the cursor
		  cursorSR.close();
		  return samples;		
	}
	
	public void deleteSessionSample(int SSID)
	{
		  database.delete(MySQLiteHelper.TABLE_SESSION_SAMPLES, MySQLiteHelper.SS_C_ID
	      + " = " + SSID, null);
	}
	
	//////////////////////////////////////
	//session relation
	public void createSessionRelation(session session, sessionSample sample) {
		  ContentValues values = new ContentValues();
		  values.put(MySQLiteHelper.SR_C_SEID, session.SEID);
		  values.put(MySQLiteHelper.SR_C_SSID, sample.SSID);
		  //values.put(MySQLiteHelper.SE_C_DESCRIPTION, newSession.description);
		  database.insert(MySQLiteHelper.TABLE_SESSION_RELATIONS, null, values);
	}
	
	public void createSessionRelation(int SEID, int SSID) {
		  ContentValues values = new ContentValues();
		  values.put(MySQLiteHelper.SR_C_SEID, SEID);
		  values.put(MySQLiteHelper.SR_C_SSID, SSID);
		  //values.put(MySQLiteHelper.SE_C_DESCRIPTION, newSession.description);
		  database.insert(MySQLiteHelper.TABLE_SESSION_RELATIONS, null, values);
	}
	
	public void deleteSessionRelations(int SEID)
	{
		  database.delete(MySQLiteHelper.TABLE_SESSION_RELATIONS, MySQLiteHelper.SR_C_SEID
	      + " = " + SEID, null);
	}
}
