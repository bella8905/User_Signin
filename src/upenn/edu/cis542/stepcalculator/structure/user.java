package upenn.edu.cis542.stepcalculator.structure;

public class user
{
	public String username = "";
	public String email = "";
	public int gender;  //0 for female, 1 for male, -1 not specified
	public String height;
	public String weight;
	public int UID;
	
	public user(String t_username, String t_email, int t_gender, String t_height, String t_weight)
	{
		username = t_username;
		email = t_email;
		gender = t_gender;
		height = t_height;
		weight = t_weight;		
	}
	
	public user() {}
}