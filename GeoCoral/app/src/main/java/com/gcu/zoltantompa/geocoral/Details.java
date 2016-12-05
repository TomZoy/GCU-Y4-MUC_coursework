package com.gcu.zoltantompa.geocoral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class displays the details of a selected event.
 * It's accessed from the Map- or List-View
 */

public class Details extends AppCompatActivity{

    boolean debugEnabled = false;

    private String TAG = Details.class.getSimpleName();
    private EarthQ selectedEQ;
    private android.widget.ListView lv;
    private ArrayList<HashMap<String, String>>  detailedEQList = new ArrayList<>();
    SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss");

    private String caller; //variable to store if the caller was List or Map view, so back button works properly

    private static Map<String,Integer> iconSet; //a dictionary to hold the icons
    private static String[] labels = new String[]{
            "",
            "Significance level",
            "Alert level",
            "Felt by",
            "Mag. source",
            "Depth",
            "Magnitude",
            "Longitude / Latitude",
            "Last update"
    };

    Toast toast;

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //bind UI
        backButton = (Button) findViewById(R.id.detailsView_backBTN);
        lv = (android.widget.ListView)findViewById(R.id.detailsViewList);

        selectedEQ = (EarthQ) this.getIntent().getSerializableExtra("selEQ");
        caller = (String) this.getIntent().getSerializableExtra("callerIntent");

        initIconset();
        buildListViewArray();

    }

    //get the list of icons from the resources
    private void initIconset(){
        iconSet = new HashMap<String,Integer>();

        iconSet.put("event",R.drawable.event);
        iconSet.put("alert", R.drawable.alert);
        iconSet.put("sig", R.drawable.sign);
        iconSet.put("depth", R.drawable.depth);
        iconSet.put("feltBy", R.drawable.feltby);
        iconSet.put("location", R.drawable.location);
        iconSet.put("mag", R.drawable.mag);
        iconSet.put("magSource", R.drawable.magsource);
        iconSet.put("updated", R.drawable.updated);
    }


    //build up the ListView (icons+labels+values)
    private void buildListViewArray()
    {
        if(selectedEQ != null)
        {
            labels[0] = selectedEQ.getPlace();

            for (int i=0; i<9 ; i++) {

                // tmp hash map for single event
                HashMap<String, String> tmpMap = new HashMap<>();

                // adding elements to HashMap key => value
                tmpMap.put("firstL", labels[i]+":");

                String s = "";
                Integer icon;

                switch (i){
                    case 0:

                        tmpMap.put("firstL",tmpMap.get("firstL").replace(":",""));

                        //convert the date
                        Date tmpDate1 = new Date(Long.parseLong(giveStringOrNA(selectedEQ.getTime())));
                        //convert the timezone offset
                        float tz1 = (Float.parseFloat(giveStringOrNA(selectedEQ.getTz()))/60);
                        String GMT1 = (tz1 > 0) ? "+"+tz1 : Float.toString(tz1);

                        s = ft.format(tmpDate1) + " (GMT"+GMT1+")";
                        icon = iconSet.get("event");

                        break;

                    case 1:
                        s = giveStringOrNA(selectedEQ.getSig());
                        icon = iconSet.get("sig");
                        break;
                    case 2:
                        s = giveStringOrNA(selectedEQ.getAlert());
                        icon = iconSet.get("alert");
                        break;
                    case 3:
                        s = giveStringOrNA(selectedEQ.getFeltBy());
                        icon = iconSet.get("feltBy");
                        break;
                    case 4:
                        s = giveStringOrNA(selectedEQ.getMagSources());
                        icon = iconSet.get("magSource");
                        break;
                    case 5:
                        s = giveStringOrNA(selectedEQ.getDepth())+" Km";
                        icon = iconSet.get("depth");
                        break;
                    case 6:
                        s = giveStringOrNA(selectedEQ.getMag());
                        icon = iconSet.get("mag");
                        break;
                    case 7:
                        s = giveStringOrNA(selectedEQ.getLongitude()) + "° / " + giveStringOrNA(selectedEQ.getLatitude())+"°";
                        icon = iconSet.get("location");
                        break;
                    case 8:
                        //convert the date
                        Date tmpDate = new Date(Long.parseLong(giveStringOrNA(selectedEQ.getUpdated())));
                        //convert the timezone offset
                        float tz = (Float.parseFloat(giveStringOrNA(selectedEQ.getTz()))/60);
                        String GMT = (tz > 0) ? "+"+tz : Float.toString(tz);

                        s = ft.format(tmpDate) + " (GMT"+GMT+")";

                        icon = iconSet.get("updated");
                        break;
                    default:

                        s= "something is wrong";
                        icon = null;
                        break;

                }


                tmpMap.put("secL", s);
                tmpMap.put("icon",Integer.toString(icon));

                detailedEQList.add(tmpMap);

            }


            //update UI
            ListAdapter adapter = new SimpleAdapter(
                    Details.this, detailedEQList,
                    R.layout.details_view_item, new String[]{"icon", "firstL","secL"},
                    new int[]{R.id.details_view_icon,R.id.detailsViewFirstL, R.id.detailsViewSecL});



            lv.setAdapter(adapter);

        }

        else{
            Log.e(TAG, "The EQ object WAS NULL! ");
        }

    }

    //accounting for type errors
    private String giveStringOrNA(Object o){
        if (o == null)
        {
            return "n/a";
        }
        else if( o.toString() == "")
        {
            return "n/a";
        }
        else
        {
            String s = o.toString();

            if (s.startsWith(","))
                s = s.substring(1);

            return s;
        }

    }


    //back button functionality
    public void goBack(View view)
    {
        if(debugEnabled) {
            toast = Toast.makeText(getApplicationContext(), "Back Button Clicked!", Toast.LENGTH_SHORT);
            toast.show();

            Log.e(TAG, " caller -> "+ caller);
        }
        Intent list_Screen = new Intent(getApplicationContext(), ListView.class);
        Intent map_Screen = new Intent(getApplicationContext(), MapView.class);


        //determine what the caller class was, and go back to that
        switch (caller){
            case "ListView":
                startActivity(list_Screen);
                finish(); //ending .this activity
                break;
            case "MapView":
                startActivity(map_Screen);
                finish(); //ending .this activity
                break;

            default:
                break;

        }

    }
}
