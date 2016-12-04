package com.gcu.zoltantompa.geocoral;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

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



                //adding points to plot
                //except the first and last points, points added twice, for drawing lines
                content.points.add(resultObjList.get(resultObjList.size()-1).getSig());

                for (int i=resultObjList.size()-2; i > 1 ; i--){
                    content.points.add(resultObjList.get(i).getSig());
                    content.points.add(resultObjList.get(i).getSig());
                }
                content.points.add(resultObjList.get(0).getSig());



                content.redraw("asyn test");


            }
        };
        service.execute();



    }



private  class Chart extends View {

    public String myText ="a";
    ArrayList<Integer> points = new ArrayList<>();
    public float[] pts;

    int canvasWidth;

    final int verticalOffset = 200;

    private Boolean drawPoints = false;

    // CONSTRUCTOR
    public Chart(Context context) {
        super(context);
        setFocusable(true);
    }

    public void debug(String s){
        System.out.println("debug: " + s);
    }


    public void redraw(String s)
    {

        //determine scale
        float dx = (canvasWidth - 100) / (points.size()/2); //((points.size()-2)/2)+2;

        pts = new float[(points.size()*2)];


        float x = 50;
        int j = 0;

        for(int i = 0; i<(points.size()*2);i=i+2){
            pts[i] = x;
            pts[i+1] = (1000+verticalOffset) - points.get(j); //re-mapping the values

            j++;

            if ( i == 0)
                x = x + dx;

            //if i is bigger than 1 and even, add to x
            if ( (i % 4) == 0) {
                x = x + dx;
            }
        }


        myText = s;
        System.out.println("myText : " + myText);

        drawPoints = true;
        invalidate();
    }







    @Override
    protected void onDraw(Canvas canvas) {


        Paint pPlot = new Paint();
        Paint pText = new Paint();
        Paint pAxis = new Paint();

        canvasWidth = canvas.getWidth();

//        System.out.println("height: " + canvas.getHeight());
//        System.out.println("width: "+canvas.getWidth());

        float[] axisLines = new float[24];
        float[] axisScaleLines = new float[36];

        String description1 = "This chart displays the last 100 earthquakes";
        String description2 = "around the globe and their significance level." ;


        //setting up the Paints

        //Plot
        pPlot.setAntiAlias(true);
        pPlot.setColor(ContextCompat.getColor(this.getContext(), R.color.ZcolorPrimary));
        pPlot.setStrokeWidth(2.5f);
        //pPlot.setAlpha(0x80);

        //Text
        pText.setAntiAlias(true);
        pText.setColor(ContextCompat.getColor(this.getContext(), R.color.ZcolorPrimaryDark));
        pText.setStrokeWidth(4.5f);
        pText.setTextSize(40);

        //Axis
        pAxis.setAntiAlias(true);
        pAxis.setColor(ContextCompat.getColor(this.getContext(), R.color.ZcolorAccent));
        pAxis.setStrokeWidth(4.5f);


        //draw the plot
        if(drawPoints){
            System.out.println("drawing stuff");
            canvas.drawLines (pts, pPlot);
        }

        //set up the axis etc...
            // X axis
            axisLines[0] = 20;
            axisLines[1] = 1000+verticalOffset;
            axisLines[2] = canvasWidth - 20;
            axisLines[3] = 1000+verticalOffset;
                axisLines[4] = canvasWidth - 20;
                axisLines[5] = 1000+verticalOffset;
                axisLines[6] = canvasWidth - 40;
                axisLines[7] = 990+verticalOffset;
                axisLines[8] = canvasWidth - 20;
                axisLines[9] = 1000+verticalOffset;
                axisLines[10] = canvasWidth - 40;
                axisLines[11] = 1010+verticalOffset;

            // Y axis
            axisLines[12] = 50;
            axisLines[13] = 1030+verticalOffset;
            axisLines[14] = 50;
            axisLines[15] = 10+verticalOffset;
                axisLines[16] = 50;
                axisLines[17] = 10+verticalOffset;
                axisLines[18] = 40;
                axisLines[19] = 30+verticalOffset;
                axisLines[20] = 50;
                axisLines[21] = 10+verticalOffset;
                axisLines[22] = 60;
                axisLines[23] = 30+verticalOffset;

        //set up the axis scaleLines etc...
        int y = (1000+verticalOffset)-100;
        for (int i = 0; i < 35; i = i+4)
        {
            //x0
            axisScaleLines[i] = 40;
            //y0
            axisScaleLines[i+1] = y;
            //x1
            axisScaleLines[i+2] = 60;
            //y1
            axisScaleLines[i+3] = y;

            y = y - 100;
        }

        canvas.drawLines (axisLines, pAxis);
        canvas.drawLines (axisScaleLines, pAxis);

        //write labels

        canvas.drawText("900",70,315,pText);
        canvas.drawText("Sig",50,200,pText);
        canvas.drawText("time",(canvasWidth - 140),(1000+verticalOffset+50),pText);

        canvas.drawText(description1,100,(1000+verticalOffset+150),pText);
        canvas.drawText(description2,100,(1000+verticalOffset+200),pText);
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
