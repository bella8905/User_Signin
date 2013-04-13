package upenn.edu.cis542.stepcalculator;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends Activity {
	
	private Button button; 
    //number of history items
    private int length = 3;
    
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setTitle("History");
        setContentView(R.layout.activity_history);
        ListView lv=(ListView)findViewById(R.id.historyList);
		
		ArrayList<HashMap<String, Object>> items = getItems();

        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.history_item, 
	                new String[] {"date","mile","startTime","endTime"}, 
	                new int[] {R.id.item_date, R.id.item_mile, R.id.item_startTime,R.id.item_endTime});

	    lv.setAdapter(adapter);
	    lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
              
                Intent intent=new Intent(); 
                
                //从Activity IntentTest跳转到Activity IntentTest01 
                intent.setClass(HistoryActivity.this,GoogleMapTest.class); 
                //启动intent的Activity 
                HistoryActivity.this.startActivity(intent); 
            }
    });
	    
    } 

	 private ArrayList<HashMap<String, Object>> getItems() {
	        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
	        for(int i = 0; i < length; i++) {
	         
	             HashMap<String, Object> map = new HashMap<String, Object>();
	             map.put("date", "today");
	        	 map.put("mile", "3.1");
	        	 map.put("startTime", "9:00:00");
	        	 map.put("endTime", "12:00:00");
	       	
	            items.add(map);
	        }
	        return items;
	    }
	 
		public void goBack(View view){
			 Intent intent = new Intent();  
			 intent.setClass(this, MainSessionActivity.class );
		     startActivity(intent);
		}
	

}
