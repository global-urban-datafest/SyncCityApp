package teleco.inf.synccity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpRequest {
	private static final String TAG = "HTTP REQUEST";
	public String getRequest(String directionsURL){
	//public String readUrl(String mapsApiDirectionsUrl) throws IOException {
		String response = "";
		HttpURLConnection httpRequest = null;
		try {
			httpRequest = (HttpURLConnection) (new URL(directionsURL)).openConnection();
			httpRequest.connect();
			if(httpRequest.getResponseCode() != 200) throw new IOException("Response not OK!");
			httpRequest.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(httpRequest.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			response = sb.toString();
			br.close();
		} catch (IOException e) {
			Log.e(TAG, "IOException in HTTP Request!");
			//return null to show error
			response = null;
			e.printStackTrace();
		} finally {
			httpRequest.disconnect();
		}
		return response;
	}
}