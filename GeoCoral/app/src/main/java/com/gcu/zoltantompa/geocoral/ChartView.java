package com.gcu.zoltantompa.geocoral;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.id.content;

/**
 * Created by TomZoy on 2016-12-03.
 */

public class ChartView extends AppCompatActivity{

    FragmentManager fmAboutDialogue;

    Intent map_Screen;
    Intent list_Screen;
    Intent settings_Screen;
    Intent codeList_Screen;




    // URL to get JSON
    private static String url = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=100&minmagnitude=1&orderby=time";


    private String TAG = ListView.class.getSimpleName();

    private Chart content;



    Toast toast;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chart);

        content = new Chart(this);

        setContentView(content);

        //setting up new Intents
        map_Screen = new Intent(getApplicationContext(), MapView.class);
        list_Screen = new Intent(getApplicationContext(), ListView.class);
        settings_Screen = new Intent(getApplicationContext(), Settings.class);
        codeList_Screen = new Intent(getApplicationContext(), CodeIndex.class);

        pcHttpJSONAsync service = new pcHttpJSONAsync(url, this) {
            @Override
            public void onResponseReceived(Object resultMap, ArrayList<EarthQ> resultObjList) {

                // EQList = resultObjList;
                content.debug("This is a test");



                content.points.add(resultObjList.get(resultObjList.size()-1).getSig());

                for (int i=resultObjList.size()-2; i > 1 ; i--){
                    content.points.add(resultObjList.get(i).getSig());
                    content.points.add(resultObjList.get(i).getSig());
                }
                content.points.add(resultObjList.get(0).getSig());



                content.redraw("asyn test");

                //setCont();

            }
        };
        service.execute();



    }



private  class Chart extends View {


    public void debug(String s){
        System.out.println("debug: " + s);
    }


    // CONSTRUCTOR
    public Chart(Context context) {
        super(context);
        setFocusable(true);




    }
    public String myText ="a";
    ArrayList<Integer> points = new ArrayList<>();

    public void debug2(){
        System.out.println("debug2 : " + points.size());
    }

    public void redraw(String s)
    {

       // this.getRootView().invalidate();
        myText = s;

        System.out.println("myText : " + myText);

        invalidate();
    }



    public float[] pts = new float[8];



    @Override
    protected void onDraw(Canvas canvas) {

        //canvas.drawColor(Color.YELLOW);

        Paint p = new Paint();

        System.out.println("height: " + canvas.getHeight());
        System.out.println("width: "+canvas.getWidth());

        int b = 8;

        //float: Array of points to draw [x0 y0 x1 y1 x2 y2 ...]
        //float[] pts = new float[b];





        // smooths
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeWidth(4.5f);
        p.setTextSize(240);

        // opacity
        p.setAlpha(0x80);

        canvas.drawLines (pts, p);

        canvas.drawText("test",400,400,p);
        canvas.drawText(myText,400,800,p);

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
                System.out.println("Msp option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "Map option Clicked!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(map_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_list:
                System.out.println("List option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "List option Clicked!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(list_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_chart:
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





}
