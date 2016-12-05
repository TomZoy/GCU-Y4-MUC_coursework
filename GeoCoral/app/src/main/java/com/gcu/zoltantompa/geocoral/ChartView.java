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
 * This class displays a chart of significance values, using real-time values
 */

public class ChartView extends AppCompatActivity{

    boolean debugEnabled = false;

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
        content = new Chart(this);
        setContentView(content);

        //setting up new Intents
        map_Screen = new Intent(getApplicationContext(), MapView.class);
        list_Screen = new Intent(getApplicationContext(), ListView.class);
        settings_Screen = new Intent(getApplicationContext(), Settings.class);
        codeList_Screen = new Intent(getApplicationContext(), CodeIndex.class);

        //async task to read the data-feed
        pcHttpJSONAsync service = new pcHttpJSONAsync(url, this) {

            //call back method from interface
            @Override
            public void onResponseReceived(Object resultMap, ArrayList<EarthQ> resultObjList) {

                //adding points to plot
                //except the first and last points, points added twice, for drawing lines
                content.points.add(resultObjList.get(resultObjList.size()-1).getSig());

                for (int i=resultObjList.size()-2; i > 1 ; i--){
                    content.points.add(resultObjList.get(i).getSig());
                    content.points.add(resultObjList.get(i).getSig());
                }
                content.points.add(resultObjList.get(0).getSig());

                //redraw the canvas with the points supplied above
                content.redraw();
            }
        };

        //run the async task
        service.execute();



    }


//define a class that extends View, to use have a canvas to draw to
private  class Chart extends View {

    ArrayList<Integer> points = new ArrayList<>(); //array that holds the Y values
    public float[] pts; //array to hold all the XY values

    int canvasWidth;    //hold the width of the device

    final int verticalOffset = 200; //offset the chart vertically

    private Boolean drawPoints = false; //switch to draw the plot

    // CONSTRUCTOR
    public Chart(Context context) {
        super(context);
        setFocusable(true);
    }

    //method called from the async task, to redraw the canvas
    public void redraw()
    {

        //determine scale
        float dx = (canvasWidth - 100) / (points.size()/2); //((points.size()-2)/2)+2;

        pts = new float[(points.size()*2)]; //instantiate the array to fit all elements


        //build up all the XY point pairs for the plotting
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

        drawPoints = true; //enable a switch in the onDraw method
        invalidate(); //redraw the canvas
    }







    @Override
    protected void onDraw(Canvas canvas) {


        Paint pPlot = new Paint();
        Paint pText = new Paint();
        Paint pAxis = new Paint();

        canvasWidth = canvas.getWidth();

        if(debugEnabled)
        {
            System.out.println("height: " + canvas.getHeight());
            System.out.println("width: " + canvas.getWidth());
        }

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
            if(debugEnabled) {
                System.out.println("drawing stuff");
            }
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
                if(debugEnabled) {
                    System.out.println("Msp option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "Map option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(map_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_list:
                if(debugEnabled) {
                    System.out.println("List option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "List option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(list_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_chart:
                //this is the current option, so ignore
                break;

            case R.id.menu_codeindex:
                if(debugEnabled) {
                    System.out.println("CodeList option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "CodeList option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(codeList_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_settings:
                if(debugEnabled) {
                    System.out.println("Settings option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "Settings option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
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