package com.example.currentplacedetailsonmap;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.heatmaps.HeatmapTileProvider;


/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivityCurrentPlace extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // The entry point to the Places API.
    private PlacesClient mPlacesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private List[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    ////////////////////////////////////////////////////////////////////////////////
    //////////// Important Values for your localization and bluetooth //////////////
    ////////////////////////////////////////////////////////////////////////////////
    // Selected current place
    private LatLng markerLatLng;
    private String markerSnippet;
    private String markerPlaceName;

    // New Bluetooth Devices Number
    private int btDevicesCount;
    ////////////////////////////////////////////////////////////////////////////////
    private String mac_address;
    private FusedLocationProviderClient fusedLocationClient;
    public static String id1 = "test_channel_01";


    public static LatLng currLocation;

    final String[] result = {"","",""};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getLocations("last_day");






        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mLastKnownLocation = location;
                            mDefaultLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            System.out.println(mDefaultLocation.latitude);
                            System.out.println(mDefaultLocation.longitude);
                        }
                    }
                });
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);


        Button lastDay =  findViewById(R.id.button1);
        lastDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocations("last_day");
                System.out.println(result[0]);
                AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivityCurrentPlace.this);
                alert.setTitle("Your travel score is ...");



                TextView myMsg = new TextView(MapsActivityCurrentPlace.this);
                myMsg.setText(result[0]);
                myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                myMsg.setTypeface(null, Typeface.BOLD);
                myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);

                alert.setView(myMsg);

                alert.setPositiveButton("Got it!",null);
                alert.show();



                AlertDialog.Builder alert2 = new AlertDialog.Builder(MapsActivityCurrentPlace.this);
                alert2.setTitle("Your interaction score is ...");



                TextView myMsg2 = new TextView(MapsActivityCurrentPlace.this);
                myMsg2.setText(result[1]);
                myMsg2.setGravity(Gravity.CENTER_HORIZONTAL);
                myMsg2.setTypeface(null, Typeface.BOLD);
                myMsg2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);

                alert2.setView(myMsg2);

                alert2.setPositiveButton("Got it!",null);
                alert2.show();

            }
        });


        Button allTime =  findViewById(R.id.button2);
        allTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocations("all");
                System.out.println(result[0]);
                AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivityCurrentPlace.this);
                alert.setTitle("Your travel score is ...");



                TextView myMsg = new TextView(MapsActivityCurrentPlace.this);
                myMsg.setText(result[0]);
                myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                myMsg.setTypeface(null, Typeface.BOLD);
                myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);

                alert.setView(myMsg);

                alert.setPositiveButton("Got it!",null);
                alert.show();



                AlertDialog.Builder alert2 = new AlertDialog.Builder(MapsActivityCurrentPlace.this);
                alert2.setTitle("Your interaction score is ...");



                TextView myMsg2 = new TextView(MapsActivityCurrentPlace.this);
                myMsg2.setText(result[1]);
                myMsg2.setGravity(Gravity.CENTER_HORIZONTAL);
                myMsg2.setTypeface(null, Typeface.BOLD);
                myMsg2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);

                alert2.setView(myMsg2);

                alert2.setPositiveButton("Got it!",null);
                alert2.show();

            }
        });




        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        mPlacesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mac_address = getMacAddr();


//        addLocation("default");
//        getLocations("last_day");
        createchannel();
        Intent number5 = new Intent(getBaseContext(), DataSender.class);
        number5.putExtra("times", 5);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(number5);
        } else {
            //lower then Oreo, just start the service.
            startService(number5);
        }





        //getHeatMapData();
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    private void addLocation(String type) {//throws UnsupportedEncodingException {
        String BaseUrl = new Secret().getUrl();
        String server_url_insert=BaseUrl + "/store-location";


        if (type.equals("default")){
            try{

                String url=server_url_insert;
                HttpsTrustManager.allowAllSSL();


                Map<String, String> params = new HashMap();

                if (mac_address.equals("")){
                    mac_address = "no_mac";
                }

                String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                System.out.println("Timestamp: ");
                System.out.println(timeStamp);

                params.put("User", mac_address);
                params.put("Latitude", Double.toString(mDefaultLocation.latitude));
                params.put("Longitude", Double.toString(mDefaultLocation.longitude));
                params.put("Timestamp", timeStamp);

                JSONObject parameters = new JSONObject(params);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        url, parameters,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSONPost", response.toString());
                                Toast.makeText(MapsActivityCurrentPlace.this,response.toString(),Toast.LENGTH_LONG).show();
                                System.out.println(response);
                                System.out.println("SUCCESS");
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("JSONPost", "Error: " + error.getMessage());
                        Toast.makeText(MapsActivityCurrentPlace.this,"e"+error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error.toString());
                        System.out.println("FAILURE");
                    }
                });


                Volley.newRequestQueue(this).add(jsonObjReq);

            } catch(Exception e){

            }


        }

    }

    private String getLocations(String type){
        String BaseUrl = new Secret().getUrl();


        if(type.equals("last")){
            String server_url_insert=BaseUrl + "/get-last-locations/" + mac_address;
            try{

                String url=server_url_insert;
                HttpsTrustManager.allowAllSSL();


                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                System.out.println("Response is: "+ response);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("That didn't work!");
                    }
                });


                Volley.newRequestQueue(this).add(stringRequest);

            } catch(Exception e){

            }
        }

        if(type.equals("last_day")){
            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            String server_url_insert=BaseUrl + "/get-last-locations-day/" + mac_address + "/" + timeStamp;
            try{

                String url=server_url_insert;
                HttpsTrustManager.allowAllSSL();


                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                //System.out.println("Response is: "+ response);
                                boolean isFound = response.indexOf("score1") !=-1? true: false;
                               // System.out.println(isFound);

                                if (isFound){
                                    String score1 = response.substring(response.indexOf("score1")+9, response.indexOf("score1") + 10);
                                    String score2 = response.substring(response.indexOf("score2")+9, response.indexOf("score2") + 10);

                                    int scorenum1 = Integer.parseInt(score1);

                                    int scorenum2 = Integer.parseInt(score2);


                                    scorenum1++;
                                    scorenum2++;

                                    result[0] =  String.valueOf(scorenum1);
                                    result[1] =  String.valueOf(scorenum2);

                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("That didn't work!");
                    }
                });


                Volley.newRequestQueue(this).add(stringRequest);



            } catch(Exception e){

            }
        }


        if (type.equals("all")){
            String server_url_insert=BaseUrl + "/get-all-locations/" + mac_address;


            try{

                String url=server_url_insert;
                HttpsTrustManager.allowAllSSL();


                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                boolean isFound = response.indexOf("score1") !=-1? true: false;
                                // System.out.println(isFound);

                                if (isFound){
                                    String score1 = response.substring(response.indexOf("score1")+9, response.indexOf("score1") + 10);
                                    String score2 = response.substring(response.indexOf("score2")+9, response.indexOf("score2") + 10);

                                    int scorenum1 = Integer.parseInt(score1);

                                    int scorenum2 = Integer.parseInt(score2);


                                    scorenum1++;
                                    scorenum2++;

                                    result[0] =  String.valueOf(scorenum1);
                                    result[1] =  String.valueOf(scorenum2);

                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("That didn't work!");
                    }
                });


                Volley.newRequestQueue(this).add(stringRequest);

            } catch(Exception e){

            }
        }



        return "No type specified";
    }



    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.option_get_place) {
//            showCurrentPlace();
//        } else if (item.getItemId() == R.id.nearby_devices) {
//            // Launch the DeviceListActivity to see devices and do scan
//            Intent serverIntent = new Intent(this, DeviceListActivity.class);
//            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    ////////////////////////////////////////////////////////////////////////////
                    //////////////////////   WRITE YOUR CODE HERE ! ////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////
                    // Example:
                    btDevicesCount = data.getExtras()
                            .getInt(DeviceListActivity.EXTRA_DEVICE_COUNT);
                    Log.d(TAG, "Device number:" + btDevicesCount);
                    // You can change the address to the number of certain type of devices, or
                    // other variables you want to use. Remember to change the corresponding
                    // name at DeviceListActivity.java.
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    ////////////////////////////////////////////////////////////////////////////
                    //////////////////////   WRITE YOUR CODE HERE ! ////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////
                    // Example:
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                }
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;





        // TEST
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });



        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


        getHeatMapData();


    }

    final List<LatLng> list = new ArrayList<LatLng>();

    private void getHeatMapData() {
        String BaseUrl = new Secret().getUrl();


        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        String server_url_insert=BaseUrl + "/popular-places-day/" + timeStamp;

        try{

            String url=server_url_insert;
            HttpsTrustManager.allowAllSSL();


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            String s = response.substring(response.indexOf("result")+10,response.length() - 4);

                            result[2] = s;
                            System.out.println(result[2]);
                            String[] latlons = result[2].split(",");


                            boolean isLat = true;
                            Double lat = 0.0;
                            for (String dec : latlons){
                                if (isLat){
                                    lat = Double.valueOf(dec);
                                    isLat = false;
                                } else {
                                    Double lon = Double.valueOf(dec);
                                    list.add(new LatLng(lat, lon));
                                    isLat = true;
                                }
                            }

                            System.out.println("HERE Points " + list.size());
                            addHeatMap();



                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("That didn't work!");
                }
            });


            Volley.newRequestQueue(this).add(stringRequest);



        } catch(Exception e){

        }




    }

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;


    private void addHeatMap(){

        System.out.println("Points " + list.size());
        // Create the tile provider.
        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }



    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<FindCurrentPlaceResponse> placeResult =
                    mPlacesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = task.getResult();

                        // Set the count, handling cases where less than 5 entries are returned.
                        int count;
                        if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getPlaceLikelihoods().size();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        mLikelyPlaceNames = new String[count];
                        mLikelyPlaceAddresses = new String[count];
                        mLikelyPlaceAttributions = new List[count];
                        mLikelyPlaceLatLngs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            // Build a list of likely places to show the user.
                            mLikelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            mLikelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                            mLikelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                    .getAttributions();
                            mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                        }

                        // Show a dialog offering the user the list of likely places, and add a
                        // marker at the selected place.
                        MapsActivityCurrentPlace.this.openPlacesDialog();
                    } else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                markerLatLng = mLikelyPlaceLatLngs[which];
                markerSnippet = mLikelyPlaceAddresses[which];
                markerPlaceName = mLikelyPlaceNames[which];

                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(markerPlaceName)
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));


            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void createchannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel mChannel = new NotificationChannel(id1,
                    getString(R.string.channel_name),  //name of the channel
                    NotificationManager.IMPORTANCE_LOW);   //importance level
            //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
            // Configure the notification channel.
            mChannel.setDescription(getString(R.string.channel_description));
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setShowBadge(true);
            nm.createNotificationChannel(mChannel);
        }
    }





}
class HttpsTrustManager implements X509TrustManager {

    private static TrustManager[] trustManagers;
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};

    @Override
    public void checkClientTrusted(
            java.security.cert.X509Certificate[] x509Certificates, String s)
            throws java.security.cert.CertificateException {

    }

    @Override
    public void checkServerTrusted(
            java.security.cert.X509Certificate[] x509Certificates, String s)
            throws java.security.cert.CertificateException {

    }

    public boolean isClientTrusted(X509Certificate[] chain) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
        return true;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return _AcceptedIssuers;
    }

    public static void allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }

        });

        SSLContext context = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{new HttpsTrustManager()};
        }

        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(context
                .getSocketFactory());
    }

}



// powered by bee