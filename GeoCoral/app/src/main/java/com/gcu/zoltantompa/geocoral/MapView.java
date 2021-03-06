package com.gcu.zoltantompa.geocoral;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.location.Location;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;


/**
 *  code-base for the map used from the official site
 *  https://developers.google.com/maps/documentation/android-api/start
 *  and customised by myself
 *  Class that drives the Map-View option
**/


public class MapView extends AppCompatActivity  implements OnMapReadyCallback, OnInfoWindowClickListener,
        ConnectionCallbacks, OnConnectionFailedListener{

    boolean debugEnabled = false;

    private String TAG = Details.class.getSimpleName();
    FragmentManager fmAboutDialogue;

    Intent map_Screen;
    Intent list_Screen;
    Intent settings_Screen;
    Intent codeList_Screen;
    Intent details_Screen;
    Intent chart_Screen;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;


    private pcHttpJSONAsync service;
    pcQueryUrlBuilder urlBuilder;

    Toast toast;


    //this is now dynamically generated
    // URL to get JSON
    //private static String url = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=100&minmagnitude=1&orderby=time";


    //start Google API for location detection
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    //stop Google API
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        //setting up a new Intents
        map_Screen = new Intent(getApplicationContext(), MapView.class);
        list_Screen = new Intent(getApplicationContext(), ListView.class);
        settings_Screen = new Intent(getApplicationContext(), Settings.class);
        codeList_Screen = new Intent(getApplicationContext(), CodeIndex.class);
        details_Screen = new Intent(getApplicationContext(), Details.class);
        chart_Screen = new Intent(getApplicationContext(), ChartView.class);


        //handle the map object
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get and instance of the urlBuilder class and generate the url
        urlBuilder = new pcQueryUrlBuilder(this);

        //async task to read the data-feed
        service = new pcHttpJSONAsync(urlBuilder.getFinalURL(),this) {
            @Override
            public void onResponseReceived(Object resultMap, ArrayList<EarthQ> resultObjList) {
                buildMarkers(resultObjList);
                showNearest(resultObjList);
            }
        };

        buildGoogleApiClient();

    }




    //initialise the Google API for the location service
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //script to set up the API and get the current location of the device
    @Override
    public void onConnected(Bundle connectionHint) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    2);
        } else {
            // Location permission has been granted, continue as usual.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            }
    }

    //script to calculate the distance between two points
    //Returns the approximate distance in meters between this location and the given location.
    //Distance is defined using the WGS84 ellipsoid.
    private float calcDistance(Location locFrom, Location locTo)
    {
        float distKm = (locFrom.distanceTo(locTo))/1000;
        return distKm;
    }

    //script to draw a line on the map between two points
    private void drawLine(Location from, Location to){
        // Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions polypoints = new PolylineOptions()
                .add(new LatLng(from.getLatitude(),from.getLongitude()))
                .add(new LatLng(to.getLatitude(),to.getLongitude()))
                .width(15)
                .color(Color.BLUE)
                .geodesic(false);

        // Get back the mutable Polyline
        Polyline polyline = mMap.addPolyline(polypoints);

    }

    //script to determine device location if possible,
    //then find the nearest EartQuake to that location
    //draw a line between them, calculate the distance and disply it in an info box
    private void showNearest(ArrayList<EarthQ> resultObjList) {

        if (mLastLocation == null) {
            Toast.makeText(this, "no location detected", Toast.LENGTH_LONG).show();

        } else {

            //add marker for current location
            Marker tmpMarker = mMap.addMarker( new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .title("Your Current Location")
                    .snippet(mLastLocation.getLatitude() + "/" + mLastLocation.getLongitude())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            mMap.setOnInfoWindowClickListener(this);



            float nearest[] = findNearestEQ(resultObjList);

            //draw the line from current location to nearest EQ
            Location nearestLoc = new Location("test");
            nearestLoc.setLatitude(nearest[0]);
            nearestLoc.setLongitude(nearest[1]);
            drawLine(mLastLocation,nearestLoc);

            LatLng midpoint = new LatLng(
                    (nearest[0]+mLastLocation.getLatitude())/2,
                    (nearest[1]+mLastLocation.getLongitude())/2
            );

            //use a transparent 1px & 1px box as your marker
            BitmapDescriptor transparent = BitmapDescriptorFactory.fromResource(R.drawable.transparent);
            MarkerOptions lineMarker = new MarkerOptions()
                    .position(midpoint)
                    .title("nearest EarthQuake to your location")
                    .snippet("distance: "+ String.format("%.2f", nearest[2])+" Km")
                    .icon(transparent)
                    .anchor((float) 0.5, (float) 0.5); //puts the info window on the polyline

            Marker marker = mMap.addMarker(lineMarker);

            //open the marker's info window
            marker.showInfoWindow();

    }
    }

    //script to find the nearest EarthQuake to the device's current location
    private float[] findNearestEQ(ArrayList<EarthQ> resultObjList){

        if (debugEnabled) {
            toast = Toast.makeText(getApplicationContext(), "EQList size: " + resultObjList.size(), Toast.LENGTH_SHORT);
            toast.show();
        }
        EarthQ nearest = resultObjList.get(0);
        float distance = Float.MAX_VALUE;

        float[] returnVales = new float[3];

        //loop thought the events, and find the nearest
        for (int i=0;i<resultObjList.size();i++){
                Location tmpLoc = new Location("tmp");
                tmpLoc.setLatitude(resultObjList.get(i).getLatitude());
                tmpLoc.setLongitude(resultObjList.get(i).getLongitude());
            float dist = calcDistance(mLastLocation,tmpLoc);
            if(dist < distance){
                distance = dist;
                nearest = resultObjList.get(i);
            };
        }
        //post back the coordinates plus the distance
        returnVales[0] = nearest.getLatitude().floatValue();
        returnVales[1] = nearest.getLongitude().floatValue();
        returnVales[2] = distance;

        if (debugEnabled) {
            toast = Toast.makeText(getApplicationContext(), "nerast: " + nearest.getPlace() + " " + distance, Toast.LENGTH_SHORT);
            toast.show();
        }
        return returnVales;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    //script to build a list of markers for the map
    private void buildMarkers(ArrayList<EarthQ> EQList) {

        ArrayList<MarkerOptions> markerList = new ArrayList<>();
        Marker tmpMarker;

        //iterate through the result list, and build and assign them to the map
        for (int i = 0; i < EQList.size(); i++) {
            EarthQ eq = EQList.get(i);

            tmpMarker = mMap.addMarker( new MarkerOptions()
                    .position(new LatLng(eq.getLatitude(), eq.getLongitude()))
                    .title("mag:"+(Float.toString(eq.getMag())) + " - " +eq.getPlace())
                    .snippet(eq.getTimeString())
                    .icon(BitmapDescriptorFactory.defaultMarker(calcMarkerColor(eq.getSig())))); //set color to match significance
            tmpMarker.setTag(eq);


            mMap.setOnInfoWindowClickListener(this);
        }

    }

    //script to calculate the event marker color from green to red
    private Float calcMarkerColor(int input)
    {
        //input: 0 - 1000, 0 is low 1000 is high
        //output: 1 is red 100 is green, lineal in between

        //map it to the range
        float f = (input/10);

        //map it to match color (reverse it)
        f = 100-f;

        return f;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //set custom map style
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, selectMap()
                    ));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }


        //once the map is loaded, execute the async service
        service.execute();

        //position the map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(47.468637, 19.067642)));
    }

    //script to determine which map-stye to use, depending on the settings and the time of the day
    private int selectMap()
    {
        int day = R.raw.daytimemap;
        int night = R.raw.nighttimemap;

        if(urlBuilder.isDayNightModeOn)
        {
            //get the time on the device
            Calendar cal = Calendar.getInstance();

            if (debugEnabled) {
                Toast.makeText(this, "current hour: " + cal.get(Calendar.HOUR_OF_DAY),
                        Toast.LENGTH_SHORT).show();
            }
            //if between 7pm-7am switch to night
            if (cal.get(Calendar.HOUR_OF_DAY)>=19 || cal.get(Calendar.HOUR_OF_DAY)<7 ){
                return night;
            }
            //else day
            else
                return day;

        }
        else
        {
            return day;
        }
    }


    //event-listener for info-window click to take to details screen
    @Override
    public void onInfoWindowClick(Marker marker) {

        if (marker.getTag() != null) {
            //get the attached EQ object from the marker
            EarthQ eq = (EarthQ) marker.getTag();

            if (debugEnabled) {
                Toast.makeText(this, "Info window clicked" + eq.getPlace(),
                        Toast.LENGTH_SHORT).show();
            }
            //serialise and send the selected EQ to the details activity
            details_Screen.putExtra("selEQ", eq);
            details_Screen.putExtra("callerIntent", MapView.class.getSimpleName());
            startActivity(details_Screen);
        }
    }


    ///inflating the menu on this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.main_menu, menu);
        return true;
    }

    ///handling the item selection from the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_map:
                //this is the current option, so ignore
                break;

            case R.id.menu_list:
                if(debugEnabled) {
                    System.out.println("List option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "List option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(list_Screen);
                this.  finish(); //ending .this activity
                return true;

            case R.id.menu_chart:
                if(debugEnabled) {
                    System.out.println("Chart option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "Chart option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(chart_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_codeindex:
                if(debugEnabled) {
                    System.out.println("CodeList option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "CodeList option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(codeList_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_settings:
                if(debugEnabled) {
                    System.out.println("Settings option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "Settings option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(settings_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_about:
                // About Dialogue;
                DialogFragment AboutDlg = new pcAboutDlg();
                AboutDlg.show(getFragmentManager(), "menu");
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }
}