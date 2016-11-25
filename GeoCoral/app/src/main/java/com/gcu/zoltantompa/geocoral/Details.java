package com.gcu.zoltantompa.geocoral;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Details extends AppCompatActivity {

    private String TAG = Details.class.getSimpleName();
    private EarthQ selectedEQ;
    private android.widget.ListView lv;
    private ArrayList<HashMap<String, String>> detailedEQList = new ArrayList<>();

    Toast toast;

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //bind UI
        backButton = (Button) findViewById(R.id.detailsView_backBTN);
        lv = (android.widget.ListView)findViewById(R.id.listViewList);
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
        toast = Toast.makeText(getApplicationContext(), "location is:" +selectedEQ.getPlace(), Toast.LENGTH_LONG);
        toast.show();

        buildListViewArray();

    }


    private void buildListViewArray()
    {
        if(selectedEQ != null)
        {





            
            //update UI
            ListAdapter adapter = new SimpleAdapter(
                    Details.this, detailedEQList,
                    R.layout.details_view_item, new String[]{"icon", "firstL","secL"},
                    new int[]{R.id.ListtextView_mag,R.id.detailsViewFirstL, R.id.ListtextViewSecL});

            lv.setAdapter(adapter);

        }

        else{
            Log.e(TAG, "The EQ object WAS NULL! ");
        }

    }

    public void setEQList(ArrayList<EarthQ> EQList) {
        this.selectedEQ = selectedEQ;
    }





}
