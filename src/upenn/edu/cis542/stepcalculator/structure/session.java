package upenn.edu.cis542.stepcalculator.structure;

public class session {
	public String startTime = "";
	public String endTime = "";
	public float calory = 0;
	public float distance = 0;
	public String description = "";
	public int steps = 0;
	public int SEID = -1;   //get it from database
	
	public session(String t_st, String t_et, float t_calory, float t_dist, int t_steps, String t_des)
	{
		startTime = t_st;
		endTime = t_et;
		calory = t_calory;
		distance = t_dist;
		description = t_des;
		steps = t_steps;
	}
	
	public session() {}
}
