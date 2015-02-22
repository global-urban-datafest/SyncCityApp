package teleco.inf.synccity;

//Author: Santi Pascual, Sync City Team

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity implements OnClickListener,
LocationListener,OnItemClickListener,OnCheckedChangeListener{

	private FrameLayout mapContainer;
	private Button listButton;
	private boolean btn_up;
	private GoogleMap map;
	private Dialog pick_dialog;
	private LocationManager locationManager;
	private static final String TAG="MainActivity";
	private static final double lat=41.387015,lon=2.170047;
	private AppState appState;
	private ArrayAdapter<String> adapter; 
	private ListView placesList;
	private Menu menu;
	private NetworkRequest netRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//deshabilitem el títol
		getActionBar().setDisplayShowTitleEnabled(false);

		//creem la llista en si i fem que carregui les dades del vector places_s i que escolti tocs
		placesList = (ListView)findViewById(R.id.places_list);
		placesList.setOnItemClickListener(this);
		//obtenim referència del contenidor del mapa per poder modificar la disposició
		mapContainer = (FrameLayout) findViewById(R.id.map_container);
		//botó de llista
		listButton = (Button) findViewById(R.id.list_appear_btn);
		listButton.setOnClickListener(this);
		listButton.setEnabled(false);
		//estat del botó actualment: up or down
		btn_up = true;
		//referenciem el mapa per se
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//amaguem els botons de zoom del mapa, no ens agraden per ara
		hideZoomButtonsOnMap(((MapFragment) getFragmentManager().findFragmentById(R.id.map)));
		//inicialitzem el manager de localització
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,this);
		//creem una instancia singleton on desar les dades d'estat de l'apliació
		appState = AppState.getInstance(this);
	}

	@Override
	protected void onResume(){
		super.onResume();
		updateMapLocation(null);
		printTrace(null);
	}

	/*
	 * Callback que es crida quan s'ha fet un canvi de localització i s'ha d'actualitzar posició
	 */
	@Override
	public void onLocationChanged(Location location){
		updateMapLocation(location);
		locationManager.removeUpdates(this);
		Log.d(TAG, "New location: "+location.getLatitude() + "," + location.getLongitude());
	}

	/* MENU ACTION BAR
	 * *******************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case R.id.action_about:
			showAbout();
			break;
		case R.id.action_part:
			showPickDialog(R.layout.part_pick);
			break;
		case R.id.load_route:
			//Crida HTTP al servidor
			String url = Constants.get_bmix_url+ "&username=" +Constants.url_codes.get(appState.getUsername()) + 
			"&" + Constants.date_to_url + "&time=" + 
			Constants.url_codes.get(appState.getPartOfDay()) + 
			"&city=" + Constants.url_codes.get(appState.getCity());
			Log.d(TAG, "Requesting to : " + url);
			netRequest = new NetworkRequest();
			//especifiquem que demanem al servidor de points, no a directions
			netRequest.execute(url,"points");
			break;
		case R.id.action_john:
			appState.setUsername("John");
			break;
		case R.id.action_merlin:
			appState.setUsername("Merlin");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	/*
	 * *******************************/

	//AMAGA ELS BOTONS RETRO
	private void hideZoomButtonsOnMap(MapFragment mapView){
		View zoomControls = mapView.getView().findViewById(0x1);
		for(int i=0;i<((ViewGroup)zoomControls).getChildCount();i++){
			View child=((ViewGroup)zoomControls).getChildAt(i);
			child.setVisibility(View.GONE);
		}
	}

	//ZOOM
	private void updateMapLocation(Location location){
		LatLng latlng = null;
		if(location == null){
			latlng = new LatLng(lat, lon);
		}else
		{
			latlng = new LatLng(location.getLatitude(), location.getLongitude());
		}
		CameraUpdate cUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 12);
		map.animateCamera(cUpdate);
	}


	//Mostra el Dialeg per escollir part del dia
	private void showPickDialog(int view_identifier){
		pick_dialog = new Dialog(this);
		pick_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pick_dialog.setContentView(view_identifier);
		RadioGroup rGroup = (RadioGroup) pick_dialog.findViewById(R.id.radio_part);
		rGroup.setOnCheckedChangeListener(this);
		Log.d(TAG,"CHECKED: " + appState.getCheckedId());
		rGroup.check(appState.getCheckedId());
		pick_dialog.show();

	}

	//Mostra la descripció d'un lloc de la llista
	public void showDetail(String key){
		//custom dialog
		Log.d(TAG,"Key: " + key);
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.item_description);
		dialog.setTitle(key);
		TextView text = (TextView)dialog.findViewById(R.id.description);
		if(text==null) Log.e(TAG, "NO TEXT");
		String description = appState.getIPoint(key).getDescription();
		text.setText(description);
		dialog.show();
	}

	public void showAbout(){
		final Dialog about_dialog = new Dialog(this);
		about_dialog.setTitle(R.string.action_about);
		about_dialog.setContentView(R.layout.about);
		about_dialog.show();
	}

	//Redimensiona el fragment de mapa quan apretem el boto up&down
	private void resizeMapFragment(){
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mapContainer.getLayoutParams();
		layoutParams.weight = btn_up?1.0f:0.0f;
		mapContainer.setLayoutParams(layoutParams);
		updateMapLocation(null);
		swapButtonDirection();
	}

	//Canvia el sentit del dibuix del boto up&down
	private void swapButtonDirection(){
		listButton.setText(btn_up?"v":"^");
		btn_up = !btn_up;
	}



	//Dibuixem els markers segons les dades retornades pel servidor
	public void printInterestPoints(){
		map.clear();
		List<InterestPoint> points = appState.getListOfPoints();
		MarkerOptions marker = new MarkerOptions();
		List<LatLng> latslons = new ArrayList<LatLng>();
		PolylineOptions polyLineOptions = new PolylineOptions();
		for(InterestPoint iP : points){
			marker.title(iP.getName());
			LatLng ltlg = new LatLng(iP.getLatitude(),iP.getLongitude());
			marker.position(ltlg);
			map.addMarker(marker);
			latslons.add(ltlg);
		}
		polyLineOptions.addAll(latslons);
		polyLineOptions.width(8);
		polyLineOptions.color(Color.BLUE);
		map.addPolyline(polyLineOptions);

	}

	//Omplim la llista amb les localitzacions enviades pel servidor
	public void fillInterestList(){
		List<String> keys = new ArrayList<String>(appState.getIPoints().keySet());
		//creem l'adaptador de llocs
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keys);
		placesList.setAdapter(adapter);
	}

	//TODO: future release
	public String buildDirectionsUrl(){
		StringBuilder sb = new StringBuilder();
		List<InterestPoint> points = appState.getListOfPoints();
		sb.append(Constants.directions_url);
		sb.append("json?");
		sb.append("origin=" + points.get(0).getLongitude()+","+points.get(0).getLatitude());
		sb.append("&destination=" + points.get(points.size()-1).getLongitude()+","+points.get(points.size()-1).getLatitude());
		sb.append("&waypoints=optimize:true");
		for(InterestPoint ip : points){
			sb.append("|");
			sb.append(ip.getLatitude());
			sb.append(",");
			sb.append(ip.getLongitude());
		}
		sb.append("&sensor=false");
		return sb.toString();
	}
	//TODO: future release
	public void printRoutes(){
		ArrayList<LatLng> points = null;
		PolylineOptions polyLineOptions = null;
		List<List<HashMap<String,String>>> routes = appState.getRoutes();
		//all latlng stored for every route
		points = new ArrayList<LatLng>();
		polyLineOptions = new PolylineOptions();
		List<HashMap<String, String>> path = routes.get(0);
		for(int j=0;j<path.size();j++){
			HashMap<String, String> point = path.get(j);
			double latitude = Double.parseDouble(point.get("lat"));
			double longitude = Double.parseDouble(point.get("lng"));
			LatLng pos = new LatLng(latitude, longitude);
			//			points.add(pos);
		}
		/*
		polyLineOptions.addAll(points);
		polyLineOptions.width(2);
		polyLineOptions.color(Color.RED);
		map.addPolyline(polyLineOptions);
		 */

	}

	//Set the trace. Dibuixa el traçat sobre el mapa a partir d'un array de punts 
	public void printTrace(List<LatLng> points){
	}

	// --------------------------------------------------------------------------------------------
	/* USER ACTION CALLBACKS
	 ************************/
	@Override
	public void onClick(View v) {
		Integer id = v.getId();
		switch(id){
		case R.id.list_appear_btn:
			resizeMapFragment();
			break;
		default:
			break;
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//show the description
		showDetail((String)parent.getItemAtPosition(position));
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		String part_of_day = ((RadioButton)group.findViewById(checkedId)).getText().toString();
		appState.setPartOfDay(part_of_day);
		//aquesta linia ens permetrà deixar marcada la opció 
		appState.setCheckedId(checkedId);
		menu.getItem(0).setTitle(part_of_day);
		Log.d(TAG, "Selected part of day: " + part_of_day);
		//tanquem el dialeg
		pick_dialog.dismiss();

	}

	// --------------------------------------------------------------------------------------------
	/* NETWORKING
	 ************************/
	private class NetworkRequest extends AsyncTask<String, Void, String>{
		private ProgressDialog pDialog;
		private static final String NET_TAG = "Net Request";
		//TODO: future release
		//private boolean points;
		@Override
		protected void onPreExecute(){
			//esborra la memoria anterior
			//TODO: future release
			//points = false;
			appState.clearPoints();
			//mostra dialeg de càrrega
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Making route...");
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String response;
			Log.i(NET_TAG, "Input size in net request: " + params.length);
			String url = params[0];
			String tag = params[1];
			HttpRequest get = new HttpRequest();
			response = get.getRequest(url);
			if(response == null){
				//ERROR
				Log.e(NET_TAG, "Error a la petició HTTP, no s'ha obtingut res");
				return null;
			}try {
				if(tag.equalsIgnoreCase("points")){
					Log.d(NET_TAG, "S'han demanat punts!");
					Log.d(NET_TAG, "Resposta: " + response);
					//parseja la info
					ResponseParser.parseInterestPoints(response);
					//TODO: future release
					//points = true;
				}else{
					List<List<HashMap<String,String>>> routes = ResponseParser.parseRoute(response);
					if(routes == null){
						Log.e(TAG, "ERROR, NULL");
					}
					appState.setRoutes(routes);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result){
			pDialog.dismiss();
			if(result == null){
				Toast.makeText(MainActivity.this, "Error: Could not connect", Toast.LENGTH_LONG).show();
				return;
			}
			//			if(points){
			//pintem els punts al mapa
			printInterestPoints();
			//posem la llista amb les descripcions
			fillInterestList();
			//enable
			listButton.setEnabled(true);
			//TODO: future release
			String url = buildDirectionsUrl();
			//Log.e(NET_TAG, "URL: " + url);
			//new NetworkRequest().execute(url,"routes");
			/*		}else{
				pDialog.dismiss();
				//printem les linies de ruta
				printRoutes();

			}*/

		}

	}


}
