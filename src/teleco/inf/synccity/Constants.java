package teleco.inf.synccity;

import java.util.HashMap;

public class Constants {
	public static final String get_bmix_url = "http://synccity.mybluemix.net/api_user/route?";
	public static final String date_to_url = "date=2015-02-22";
	public static final String directions_url = "https://maps.googleapis.com/maps/api/directions/";
	public static final HashMap<String, String> url_codes = new HashMap<String,String>(){{
		put("All Day","all_day");
		put("Morning","morning");
		put("Evening","evening");
		put("Barcelona","barcelona");
		put("Merlin","merlin");
		put("John","john");
	}
	};
}
