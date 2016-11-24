package com.gcu.zoltantompa.geocoral;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * code-fragments taken from http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
 * and modified by me
 */

public class ListView extends AppCompatActivity {

    private String TAG = ListView.class.getSimpleName();

    private ProgressDialog pDialog;
    private android.widget.ListView lv;

    // URL to get contacts JSON
    private static String url = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=100&minmagnitude=1&orderby=time";

    private ArrayList<HashMap<String, String>> refinedEQList = new ArrayList<>();

    FragmentManager fmAboutDialogue;

    Intent map_Screen;
    Intent list_Screen;
    Intent settings_Screen;
    Intent codeList_Screen;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        //Starting a new Intents
        map_Screen = new Intent(getApplicationContext(), MapView.class);
        list_Screen = new Intent(getApplicationContext(), ListView.class);
        settings_Screen = new Intent(getApplicationContext(), Settings.class);
        codeList_Screen = new Intent(getApplicationContext(), CodeIndex.class);

        //
        //contactList = new ArrayList<>();
        lv = (android.widget.ListView)findViewById(R.id.listViewList);

        refinedEQList = new ArrayList<>();

        new pcHttpJSONAsync().execute();
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

    private class pcHttpJSONAsync extends AsyncTask<Void, Void, Void> {

        SimpleDateFormat ft =
                new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss");



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ListView.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            pcHttpJSONHandler sh = new pcHttpJSONHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, " ZZZZ - Response from url: " + jsonStr);

            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray results = jsonObj.getJSONArray("features");

                    // looping through All instances
                    for (int i = 0; i < results.length(); i++) {

                        JSONObject c = results.getJSONObject(i);
                        Log.e(TAG, " id - " + c.getString("id"));
                        String tmpId = c.getString("id");

                        JSONObject cc = c.getJSONObject("properties");
                        Log.e(TAG, " place - " + cc.getString("place"));
                        String tmpFirstL = cc.getString("place");

                        Log.e(TAG, " mag - " + cc.getString("mag"));

                        String tmpMag = String.format(java.util.Locale.UK,"%.2f", Float.parseFloat(cc.getString("mag"))); //= cc.getString("mag");

                        Log.e(TAG, " time - " + cc.getString("time"));
                        Date tmpDate = new Date(Long.parseLong(cc.getString("time")));
                        Log.e(TAG, " time2 - " + ft.format(tmpDate));
                        Log.e(TAG, " timeZone - " + cc.getString("tz"));
                        Log.e(TAG, " timeZone2 - " + (Float.parseFloat(cc.getString("tz"))/60));

                        String secL = ft.format(tmpDate)+" (GMT"+ (Float.parseFloat(cc.getString("tz"))/60)+")";


                        //EarthQ resultInst = new EarthQ(c.getString("id"));
                        //resultList.add(resultInst);


                        // tmp hash map for single contact
                        HashMap<String, String> tmpRes = new HashMap<>();

                        // adding each child node to HashMap key => value
                        tmpRes.put("id", tmpId);
                        tmpRes.put("mag", tmpMag);
                        tmpRes.put("firstL", tmpFirstL);
                        tmpRes.put("secL", secL);

                        // add temp results to the list
                        refinedEQList.add(tmpRes);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });


                    //build up the refined list from the objects
                    //refinedList


                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            ListAdapter adapter = new SimpleAdapter(
                    ListView.this, refinedEQList,
                    R.layout.list_view_item, new String[]{"mag", "firstL","secL"},
                    new int[]{R.id.ListtextView_mag,R.id.ListtextViewFirstL, R.id.ListtextViewSecL});

            lv.setAdapter(adapter);

        }

    }




}

