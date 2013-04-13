package upenn.edu.cis542.stepcalculator.structure;

public class sessionSample {
	public float longitude = 0;
	public float latitude = 0;
	public float speed = 0;
	public String time;
	public int SSID = -1;  //get it from database
	//String description = "";
	
	public sessionSample(float t_lon, float t_lat, float t_speed)
	{
		longitude = t_lon;
		latitude = t_lat;
		speed = t_speed;
	}
	
	public sessionSample() {}
}
