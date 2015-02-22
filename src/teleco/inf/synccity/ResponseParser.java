package teleco.inf.synccity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

public class ResponseParser {
	private static final String PARSER_TAG = "Response Parser";
	public static void parseInterestPoints(String json_message) throws JSONException{
		JSONArray points = new JSONObject(json_message).getJSONArray("points");
		AppState appState = AppState.getInstance(null);
		for(int i=0;i<points.length();i++){
			//get every jsonobject and build an interest point, putting it in the app state 
			JSONObject point = points.getJSONObject(i);
			Log.d(PARSER_TAG, "Name: " + point.getString("name") + "\nDescription: " + point.getString("description") +
					"\nLat, Long: " + point.getDouble("latitude") +", "+ point.getDouble("longitude") +
					"\nPosition: " + point.getInt("num"));
			appState.addIPoint(new InterestPoint(point.getString("name"), point.getString("description"),
					point.getDouble("latitude"), point.getDouble("longitude"),point.getInt("num")));
		}
	}

	public static List<List<HashMap<String,String>>> parseRoute(String json_message) throws JSONException{
		JSONObject json_routes = new JSONObject(json_message);
		List<List<HashMap<String,String>>> routes = new ArrayList<List<HashMap<String,String>>>(); 
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;
		try {
			jRoutes = json_routes.getJSONArray("routes");
			//All routes
			for (int i = 0; i < jRoutes.length(); i++) {
				jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
				List<HashMap<String,String>> path = new ArrayList<HashMap<String,String>>();

				//All legs
				for (int j = 0; j < jLegs.length(); j++) {
					jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

					//All steps
					for (int k = 0; k < jSteps.length(); k++) {
						String polyline = "";
						polyline = (String) ((JSONObject) ((JSONObject) jSteps
								.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);

						//All points
						for (int l = 0; l < list.size(); l++) {
							HashMap<String, String> hm = new HashMap<String,String>();
							hm.put("lat",
									Double.toString(((LatLng) list.get(l)).latitude));
							hm.put("lng",
									Double.toString(((LatLng) list.get(l)).longitude));
							path.add(hm);
						}
					}
					routes.add(path);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}
		return routes;
	}


	/**
	 * Method Courtesy :
	 * jeffreysambells.com/2010/05/27
	 * /decoding-polylines-from-google-maps-direction-api-with-java
	 * */
	private static List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}
		return poly;
	}

}