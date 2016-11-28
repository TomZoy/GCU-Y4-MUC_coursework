package com.gcu.zoltantompa.geocoral;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public abstract class pcHttpJSONAsync extends AsyncTask<Void, Void, Void> implements pcHttpJSONAsyncInterface {

    SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss");

    private String TAG = ListView.class.getSimpleName();

    private String URL;
    private ProgressDialog pDialog;
    ListView targetListView;


    private ArrayList<HashMap<String, String>> hashMapEQList = new ArrayList<>();
    public ArrayList<EarthQ> EQList= new ArrayList<>();


    public pcHttpJSONAsync(String url, ListView li) {

        targetListView = li;
        hashMapEQList = new ArrayList<>();

        URL = url;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(targetListView);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        pcHttpJSONHandler sh = new pcHttpJSONHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(URL);

        Log.e(TAG, " ZZZZ - Response from url: " + jsonStr);

        if (jsonStr != null) {

            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray results = jsonObj.getJSONArray("features");

                // looping through All instances
                for (int i = 0; i < results.length(); i++) {

                    JSONObject JsonNode = results.getJSONObject(i);
                    JSONObject JsonNProperties = JsonNode.getJSONObject("properties");
                    JSONObject JsonNGeometry = JsonNode.getJSONObject("geometry");


                    Log.e(TAG, " id - " + JsonNode.getString("id"));
                    String tmpId = JsonNode.getString("id");


                    Log.e(TAG, " place - " + JsonNProperties.getString("place"));
                    String tmpFirstL = JsonNProperties.getString("place");

                    Log.e(TAG, " mag - " + JsonNProperties.getString("mag"));

                    String tmpMag = String.format(java.util.Locale.UK,"%.2f", Float.parseFloat(JsonNProperties.getString("mag"))); //= cc.getString("mag");

                    Log.e(TAG, " time - " + JsonNProperties.getString("time"));
                    Date tmpDate = new Date(Long.parseLong(JsonNProperties.getString("time")));
                    Log.e(TAG, " time2 - " + ft.format(tmpDate));
                    Log.e(TAG, " timeZone - " + JsonNProperties.getString("tz"));
                    Log.e(TAG, " timeZone2 - " + (Float.parseFloat(JsonNProperties.getString("tz"))/60));
                    Float tz = Float.parseFloat(JsonNProperties.getString("tz"))/60;
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
                        resultObj.setAltert(JsonNProperties.getString("alert"));
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
               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
*/

                //build up the refined list from the objects
                //refinedList


            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
*/
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

    public abstract void onResponseReceived(Object resultMap, ArrayList<EarthQ> resultObj);

}
