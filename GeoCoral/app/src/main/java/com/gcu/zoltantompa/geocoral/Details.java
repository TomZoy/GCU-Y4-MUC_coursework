package com.gcu.zoltantompa.geocoral;

import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
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

public class Details extends AppCompatActivity {

    private String TAG = Details.class.getSimpleName();
    private EarthQ selectedEQ;
    private android.widget.ListView lv;
    private ArrayList<HashMap<String, String>>  detailedEQList = new ArrayList<>();
    SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss");

    private static Map<String,Drawable> iconSet;
    private static String[] labels = new String[]{
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
        //lv.setTextFilterEnabled(true);

        //set listener
        View.OnClickListener clickListernerHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.detailsView_backBTN:
                        toast = Toast.makeText(getApplicationContext(), "Back Button Clicked!", Toast.LENGTH_SHORT);
                        toast.show();
                        break;

                    default:
                        break;
                }
            }
        };

        selectedEQ = (EarthQ) this.getIntent().getSerializableExtra("selEQ");


        initIconset();
        buildListViewArray();

    }

    private void initIconset(){
        iconSet = new HashMap<String,Drawable>();

        iconSet.put("alert",ResourcesCompat.getDrawable(getResources(), R.drawable.alert, null));
        iconSet.put("sig",ResourcesCompat.getDrawable(getResources(), R.drawable.sign, null));
        iconSet.put("depth",ResourcesCompat.getDrawable(getResources(), R.drawable.depth, null));
        iconSet.put("feltBy",ResourcesCompat.getDrawable(getResources(), R.drawable.feltby, null));
        iconSet.put("location",ResourcesCompat.getDrawable(getResources(), R.drawable.location, null));
        iconSet.put("mag",ResourcesCompat.getDrawable(getResources(), R.drawable.mag, null));
        iconSet.put("magSource",ResourcesCompat.getDrawable(getResources(), R.drawable.magsource, null));
        iconSet.put("updated",ResourcesCompat.getDrawable(getResources(), R.drawable.updated, null));
    }



    private void buildListViewArray()
    {
        if(selectedEQ != null)
        {

            for (int i=0; i<8 ; i++) {

                // tmp hash map for single event
                HashMap<String, String> tmpMap = new HashMap<>();

                // adding elements to HashMap key => value
                tmpMap.put("firstL", labels[i]+":");

                String s = "";
                Drawable d;

                switch (i){
                    case 0:
                        s = giveStringOrNA(selectedEQ.getSig());
                        d = iconSet.get("sig");
                        break;
                    case 1:
                        s = giveStringOrNA(selectedEQ.getAltert());
                        d = iconSet.get("alert");
                        break;
                    case 2:
                        s = giveStringOrNA(selectedEQ.getFeltBy());
                        d = iconSet.get("feltBy");
                        break;
                    case 3:
                        s = giveStringOrNA(selectedEQ.getMagSources());
                        d = iconSet.get("magSource");
                        break;
                    case 4:
                        s = giveStringOrNA(selectedEQ.getDepth())+"Km";
                        d = iconSet.get("depth");
                        break;
                    case 5:
                        s = giveStringOrNA(selectedEQ.getMag());
                        d = iconSet.get("mag");
                        break;
                    case 6:
                        s = giveStringOrNA(selectedEQ.getLongitude()) + "° / " + giveStringOrNA(selectedEQ.getLatitude())+"°";
                        d = iconSet.get("location");
                        break;
                    case 7:
                        //convert the date
                        Date tmpDate = new Date(Long.parseLong(giveStringOrNA(selectedEQ.getUpdated())));
                        //convert the timezone offset
                        float tz = (Float.parseFloat(giveStringOrNA(selectedEQ.getTz()))/60);

                        s = ft.format(tmpDate) + " (GMT"+tz+")";

                        d = iconSet.get("updated");
                        break;
                    default:

                        s= "something is wrong";
                        break;

                }





                tmpMap.put("secL", s);

                detailedEQList.add(tmpMap);

            }


            //update UI
            ListAdapter adapter = new SimpleAdapter(
                    Details.this, detailedEQList,
                    R.layout.details_view_item, new String[]{ "firstL","secL"},
                    new int[]{R.id.detailsViewFirstL, R.id.detailsViewSecL});

            lv.setAdapter(adapter);

        }

        else{
            Log.e(TAG, "The EQ object WAS NULL! ");
        }

    }

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

}
