package com.gcu.zoltantompa.geocoral;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

import java.util.ArrayList;


/*
    code-base for the map used from the official site
    https://developers.google.com/maps/documentation/android-api/start
    and customised by myself
 */


public class MapView extends AppCompatActivity  implements OnMapReadyCallback, OnInfoWindowClickListener{

    FragmentManager fmAboutDialogue;

    Intent map_Screen;
    Intent list_Screen;
    Intent settings_Screen;
    Intent codeList_Screen;
    Intent details_Screen;


    private GoogleMap mMap;

    //private ArrayList<EarthQ> EQList;

    private pcHttpJSONAsync service;


    Toast toast;


    // URL to get JSON
    private static String url = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=100&minmagnitude=1&orderby=time";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        //Starting a new Intents
        map_Screen = new Intent(getApplicationContext(), MapView.class);
        list_Screen = new Intent(getApplicationContext(), ListView.class);
        settings_Screen = new Intent(getApplicationContext(), Settings.class);
        codeList_Screen = new Intent(getApplicationContext(), CodeIndex.class);
        details_Screen = new Intent(getApplicationContext(), Details.class);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);








        service = new pcHttpJSONAsync(url,this) {
            @Override
            public void onResponseReceived(Object resultMap, ArrayList<EarthQ> resultObjList) {


               // EQList = resultObjList;

                toast = Toast.makeText(getApplicationContext(), "List size!" + resultObjList.size(), Toast.LENGTH_SHORT);
                toast.show();

                buildMarkers(resultObjList);

                /*
                // Bind onclick event handler
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //toast = Toast.makeText(getApplicationContext(), "pos:"+ position +" id="+id, Toast.LENGTH_SHORT);
                        //toast.show();


                        //send the selected object to the new intent
                        details_Screen.putExtra("selEQ",EQList.get(position));
                        startActivity(details_Screen);

                    }
                });
*/
            }
        };


    }

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
                            this, R.raw.daytimemap
                            //this, R.raw.nighttimemap
                    ));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }




        //once the map is loaded, execute the async service
        service.execute();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(47.468637, 19.067642)));

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        EarthQ eq = (EarthQ) marker.getTag();

        Toast.makeText(this, "Info window clicked" + eq.getPlace(),
                Toast.LENGTH_SHORT).show();

        details_Screen.putExtra("selEQ",eq);
        details_Screen.putExtra("callerIntent",MapView.class.getSimpleName());
        startActivity(details_Screen);
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
                System.out.println("List option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "List option Clicked!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(list_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_codeindex:
                System.out.println("CodeList option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "CodeList option Clicked!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(codeList_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_settings:
                System.out.println("Settings option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "Settings option Clicked!", Toast.LENGTH_SHORT);
                toast.show();
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


/*
this is temp. here, copied from the official android site;
https://developers.google.com/maps/documentation/android-api/start
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

*/