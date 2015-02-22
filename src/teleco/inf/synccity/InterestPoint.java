package teleco.inf.synccity;


/* Interest Point
 *****************/
public class InterestPoint {
	private String name;
	private String description;
	private double latitude;
	private double longitude;
	private int position;
	
	public InterestPoint(String _name, String _descrition, 
			double _latitude, double _longitude, int _position){
		name = _name;
		description = _descrition;
		latitude = _latitude;
		longitude = _longitude;
		position = _position;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public int getPosition(){
		return position;
	}
}
