package teleco.inf.synccity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class AppState {
	private static AppState APP_STATE = null;
	private String part_of_day;
	private int checked_id;
	private HashMap<String,InterestPoint> points;
	private List<InterestPoint> list_points;
	private String city;
	private String username;
	private List<List<HashMap<String,String>>> routes;
	private static final String TAG = "App State";
	
	
	private AppState(Context context){
		if(context != null){
			part_of_day = context.getResources().getString(R.string.all_day);
			username = context.getResources().getString(R.string.default_username);
		}else{
			part_of_day = "none";
			username = "none";
		}
		checked_id = R.id.all_day;
		city = "Barcelona";
		points = new HashMap<String,InterestPoint>();
		list_points = new ArrayList<InterestPoint>();
		Log.d(TAG, "checked_id: " + checked_id);
	}
	
	public static AppState getInstance(Context context){
		if(APP_STATE == null){
			APP_STATE = new AppState(context);
		}
		return APP_STATE;
	}
	
	public void setPartOfDay(String _part_of_day){
		part_of_day = _part_of_day;
	}
	
	public String getCity(){
		return city;
	}
	
	public void setCheckedId(int _checked_id){
		checked_id = _checked_id;
	}
	
	public int getCheckedId(){
		return checked_id;
	}
	
	public String getPartOfDay(){
		return part_of_day;
	}
	
	public HashMap<String, InterestPoint> getIPoints(){
		return points;
	}
	
	public void addIPoint(InterestPoint point){
		points.put(point.getName(), point);
		list_points.add(point);
	}
	
	public void addIPoint(String key, String description, double lat, double lon, int pos){
		InterestPoint to_put = new InterestPoint(key,description, lat, lon, pos);
		points.put(key, to_put);
		list_points.add(to_put);
	}
	
	public InterestPoint getIPoint(String key){
		return points.get(key);
	}
	
	public List<InterestPoint> getListOfPoints(){
		return list_points;
	}
	
	public int numberOfPoints(){
		return points.size();
	}
	
	public void clearPoints(){
		points.clear();
		list_points.clear();
	}
	
	public void setRoutes(List<List<HashMap<String,String>>> _routes){
		routes = _routes;
	}
	
	public List<List<HashMap<String,String>>> getRoutes(){
		return routes;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setUsername(String _username){
		username = _username;
	}
}
