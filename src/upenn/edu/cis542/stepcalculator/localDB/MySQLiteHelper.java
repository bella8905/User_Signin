package upenn.edu.cis542.stepcalculator.localDB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String tag = "remoteDB";
	
	//table sessions
	public static final String TABLE_SESSIONS = "sessions";
	public static final String SE_C_ID = "pid"; 
	public static final String SE_C_START_TIME = "startTime";
	public static final String SE_C_END_TIME = "endTime";
	public static final String SE_C_CALORY = "calory";
	public static final String SE_C_DISTANCE = "distance";
	public static final String SE_C_DESCRIPTION = "description";
	public static final String SE_C_STEPS = "steps";
	//public static final String SE_C_SEID = "sessionID";
	
	//table sessionSamples
	public static final String TABLE_SESSION_SAMPLES = "sessionSamples";
	public static final String SS_C_ID = "pid"; 
	public static final String SS_C_LONGITUDE = "longitude";
	public static final String SS_C_LATITUDE = "latitude";
	public static final String SS_C_TIME = "time";
	public static final String SS_C_SPEED = "speed";
	//public static final String SS_C_SEID = "sessionID";
	
	//table session relations
	public static final String TABLE_SESSION_RELATIONS = "sessionRelations";
	public static final String SR_C_ID = "pid"; 
	public static final String SR_C_SEID = "SEID";
	public static final String SR_C_SSID = "SSID";

	private static final String DATABASE_NAME = "hWalkerLocal.db";
	private static final int DATABASE_VERSION = 1;
	
	public MySQLiteHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Database creation sql statement
	private static final String DATABASE_CREATE_SES = "create table if not exists " + TABLE_SESSIONS + " ( " 
		+ SE_C_ID + " integer primary key autoincrement, " 
		+ SE_C_START_TIME + " varchar, "
		+ SE_C_END_TIME + " varchar, "
		+ SE_C_CALORY + " float, "
		+ SE_C_DISTANCE + " float, "
		+ SE_C_DESCRIPTION + " text," 
		+ SE_C_STEPS + " int);";
	
	private static final String DATABASE_CREATE_SSS = "create table if not exists " + TABLE_SESSION_SAMPLES + " ( " 
			+ SS_C_ID + " integer primary key autoincrement, " 
			+ SS_C_LONGITUDE + " float not null, "
			+ SS_C_LATITUDE + " float not null, "
			+ SS_C_TIME + " varchar, "
			+ SS_C_SPEED + " float);";
	
	private static final String DATABASE_CREATE_SRS = "create table if not exists " + TABLE_SESSION_RELATIONS + " ( " 
			+ SR_C_ID + " integer primary key autoincrement, " 
			+ SR_C_SEID + " int not null, "
			+ SR_C_SSID + " int not null);";


	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(tag, "SQLiteDatabase onCreate");
	  database.execSQL(DATABASE_CREATE_SES);
	  database.execSQL(DATABASE_CREATE_SSS);
	  database.execSQL(DATABASE_CREATE_SRS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	  Log.w(MySQLiteHelper.class.getName(),
	      "Upgrading database from version " + oldVersion + " to "
	          + newVersion + ", which will destroy all old data");
	  db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
	  db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_SAMPLES);
	  db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_RELATIONS);
	  onCreate(db);
	}

}