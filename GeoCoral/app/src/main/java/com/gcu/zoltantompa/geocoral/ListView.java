package com.gcu.zoltantompa.geocoral;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * code-fragments taken from http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
 * and modified by me
 */

public class ListView extends AppCompatActivity implements Serializable {

    private String TAG = ListView.class.getSimpleName();

    private android.widget.ListView lv;

    //this is now dynamically generated
    // URL to get JSON
    //private static String url = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=100&minmagnitude=1&orderby=time";

    //the list of EarthQuake objects
    public ArrayList<EarthQ> EQList;

    FragmentManager fmAboutDialogue;

    Intent map_Screen;
    Intent list_Screen;
    Intent settings_Screen;
    Intent codeList_Screen;
    Intent details_Screen;
    Intent chart_Screen;

    Toast toast;

    pcQueryUrlBuilder urlBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        //setting up a new Intents
        map_Screen = new Intent(getApplicationContext(), MapView.class);
        list_Screen = new Intent(getApplicationContext(), ListView.class);
        settings_Screen = new Intent(getApplicationContext(), Settings.class);
        codeList_Screen = new Intent(getApplicationContext(), CodeIndex.class);
        details_Screen = new Intent(getApplicationContext(), Details.class);
        chart_Screen = new Intent(getApplicationContext(), ChartView.class);


        //bind the listView
        lv = (android.widget.ListView)findViewById(R.id.listViewList);
        lv.setTextFilterEnabled(true);

        urlBuilder = new pcQueryUrlBuilder(this);

        pcHttpJSONAsync service = new pcHttpJSONAsync(urlBuilder.getFinalURL(), this) {
            @Override
            public void onResponseReceived(Object resultMap, ArrayList<EarthQ> resultObjList) {
                // Updating parsed JSON data into ListView
                ListAdapter adapter = new SimpleAdapter(
                        ListView.this, (ArrayList<HashMap<String, String>>) resultMap,
                        R.layout.list_view_item, new String[]{"mag", "firstL","secL"},
                        new int[]{R.id.ListtextView_mag,R.id.ListtextViewFirstL, R.id.ListtextViewSecL});

                lv.setAdapter(adapter);


                EQList = resultObjList;

                // Bind onclick event handler
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //toast = Toast.makeText(getApplicationContext(), "pos:"+ position +" id="+id, Toast.LENGTH_SHORT);
                        //toast.show();


                        //send the selected object to the new intent
                        details_Screen.putExtra("selEQ",EQList.get(position));
                        details_Screen.putExtra("callerIntent",ListView.class.getSimpleName());
                        startActivity(details_Screen);

                    }
                });

            }
        };
        service.execute();


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
                System.out.println("Msp option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "Map option Clicked!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(map_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_list:
                //this is the current option, so ignore
                break;

            case R.id.menu_codeindex:
                System.out.println("CodeList option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "CodeList option Clicked!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(codeList_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_chart:
                System.out.println("Chart option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "Chart option Clicked!", Toast.LENGTH_SHORT);
                toast.show();

                startActivity(chart_Screen);
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

