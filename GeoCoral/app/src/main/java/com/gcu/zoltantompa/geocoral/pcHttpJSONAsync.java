package com.gcu.zoltantompa.geocoral;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * This class implements asynctask and is responsible to read the data-feed
 * and build an object list from the results
 */


public abstract class pcHttpJSONAsync extends AsyncTask<Void, Void, Void> implements pcHttpJSONAsyncInterface {

    boolean debugEnabled = false;


    SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss");

    private String TAG = ListView.class.getSimpleName();

    private String URL;
    private ProgressDialog pDialog;
    private Context CT; //variable to hold the caller context

    private ArrayList<HashMap<String, String>> hashMapEQList = new ArrayList<>();
    public ArrayList<EarthQ> EQList= new ArrayList<>();


    //constructor
    public pcHttpJSONAsync(String url, Context ct) {

        hashMapEQList = new ArrayList<>();

        CT = ct;
        URL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(CT);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        pcHttpJSONHandler sh = new pcHttpJSONHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(URL);

        if (debugEnabled) {
            Log.e(TAG, " ZZZZ - Response from url: " + jsonStr);
        }

        //if response is not null
        if (jsonStr != null) {

            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray results = jsonObj.getJSONArray("features");

                // looping through All instances and reading in tha data
                for (int i = 0; i < results.length(); i++) {

                    JSONObject JsonNode = results.getJSONObject(i);
                    JSONObject JsonNProperties = JsonNode.getJSONObject("properties");
                    JSONObject JsonNGeometry = JsonNode.getJSONObject("geometry");

                    String tmpId = JsonNode.getString("id");

                    String tmpFirstL = JsonNProperties.getString("place");

                    String tmpMag = String.format(java.util.Locale.UK,"%.2f", Float.parseFloat(JsonNProperties.getString("mag"))); //= cc.getString("mag");

                    Date tmpDate = new Date(Long.parseLong(JsonNProperties.getString("time")));

                    Float tz;
                    try {
                        tz = Float.parseFloat(JsonNProperties.getString("tz"))/60;
                    }
                    catch(NumberFormatException ex) {
                        tz = 0f; // default ??
                    }

                    String GMT = (tz > 0) ? "+"+tz : Float.toString(tz);

                    String secL = ft.format(tmpDate)+" (GMT"+GMT+")";




                    //building object-based list for details view to use
                    EarthQ resultObj = new EarthQ(JsonNode.getString("id"));
                    resultObj.setMag(Float.parseFloat(JsonNProperties.getString("mag")));
                    resultObj.setPlace(JsonNProperties.getString("place"));
                    resultObj.setTime(Long.parseLong(JsonNProperties.getString("time")));
                    resultObj.setTz(Integer.parseInt(JsonNProperties.getString("tz")));

                    if(!JsonNProperties.isNull("sig"))
                        resultObj.setSig(Integer.parseInt(JsonNProperties.getString("sig")));
                    if(!JsonNProperties.isNull("updated"))
                        resultObj.setUpdated(JsonNProperties.getLong("updated"));
                    if(!JsonNProperties.isNull("alert"))
                        resultObj.setAlert(JsonNProperties.getString("alert"));
                    if(!JsonNProperties.isNull("felt"))
                        resultObj.setFeltBy(JsonNProperties.getInt("felt"));
                    if(!JsonNProperties.isNull("sources"))
                        resultObj.setMagSources(JsonNProperties.getString("sources"));

                    resultObj.setLongitude(JsonNGeometry.getJSONArray("coordinates").getDouble(0));
                    resultObj.setLatitude(JsonNGeometry.getJSONArray("coordinates").getDouble(1));
                    resultObj.setDepth(JsonNGeometry.getJSONArray("coordinates").getDouble(2));


                    EQList.add(resultObj);


                    // tmp hash map for single event
                    HashMap<String, String> tmpRes = new HashMap<>();

                    // adding each child node to HashMap key => value
                    tmpRes.put("id", tmpId);
                    tmpRes.put("mag", tmpMag);
                    tmpRes.put("firstL", tmpFirstL);
                    tmpRes.put("secL", secL);

                    // add temp results to the list
                    hashMapEQList.add(tmpRes);

                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();

        // handling the results, whatever the caller class wants
        onResponseReceived(hashMapEQList,EQList);

    }

    //declaring interface method
    public abstract void onResponseReceived(Object resultMap, ArrayList<EarthQ> resultObj);

}
