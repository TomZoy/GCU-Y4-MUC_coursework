package com.gcu.zoltantompa.geocoral;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.text.InputType;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.appyvet.rangebar.RangeBar;

/**
 * This class drives the Settings screen
 */

public class Settings extends AppCompatActivity implements OnClickListener {

    boolean debugEnabled = false;


    FragmentManager fmAboutDialogue;

    Intent map_Screen;
    Intent list_Screen;
    Intent settings_Screen;
    Intent codeList_Screen;
    Intent chart_Screen;

    Toast toast;

    pcSaveSettings saveSettings;
    SharedPreferences mySavedSettings;

    //UI variables
    //Switch isAudioOn;
    Switch isDayNightModeOn;
    Switch isLimitPeriodOn;
    TextView LimitPeriodFrom;
    TextView LimitPeriodTill;
    Switch isRadiusFilterOn;
    TextView RadiusFilterValue;

    RangeBar magRangeBar;
    RangeBar sigRangeBar;
    RangeBar limitResultsBar;
    TextView minMaxMagValueDisp;
    TextView minMaxSigValueDisp;
    TextView LimitResValueDisp;


    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    //DB
    CodeInstance database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Starting new Intents
        map_Screen = new Intent(getApplicationContext(), MapView.class);
        list_Screen = new Intent(getApplicationContext(), ListView.class);
        settings_Screen = new Intent(getApplicationContext(), Settings.class);
        codeList_Screen = new Intent(getApplicationContext(), CodeIndex.class);
        chart_Screen = new Intent(getApplicationContext(), ChartView.class);

        //getting back saved data
        mySavedSettings = PreferenceManager.getDefaultSharedPreferences(this);
        saveSettings = new pcSaveSettings(mySavedSettings);

        //bind UI
        isDayNightModeOn = (Switch) findViewById(R.id.DayNightSw);
        isLimitPeriodOn = (Switch)  findViewById(R.id.LimitPeriodSw);
        LimitPeriodFrom = (EditText) findViewById(R.id.LimitPeriodFrom);
        LimitPeriodFrom.setInputType(InputType.TYPE_NULL);
        LimitPeriodTill = (EditText) findViewById(R.id.LimitPeriodTo);
        LimitPeriodTill.setInputType(InputType.TYPE_NULL);
        isRadiusFilterOn = (Switch)  findViewById(R.id.RadiusFilterSw);
        RadiusFilterValue = (TextView) findViewById(R.id.RadiusFilterValue);

        magRangeBar = (RangeBar) findViewById(R.id.magRangebar);
        sigRangeBar = (RangeBar) findViewById(R.id.SigRangeBar);
        limitResultsBar = (RangeBar) findViewById(R.id.limitResultsBar);
        minMaxMagValueDisp = (TextView) findViewById(R.id.minMaxMagValueDisp);
        minMaxSigValueDisp = (TextView) findViewById(R.id.minMaxSigValueDisp);
        LimitResValueDisp = (TextView) findViewById(R.id.LimitResValueDisp);


        //set up the date-formatter
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);



        // Load any saved preferences
        mySavedSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loadSavedSettings();


        //RangeBars; set event-listeners and save settings on change + update displays
        magRangeBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //tap event ended
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (debugEnabled) {
                        toast = Toast.makeText(getApplicationContext(), "released"
                                + magRangeBar.getLeftPinValue()
                                + "/"
                                + magRangeBar.getRightPinValue(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    //save values
                    saveSettings.saveSettings("SminMag", Integer.parseInt(magRangeBar.getLeftPinValue()));
                    saveSettings.saveSettings("SmaxMag", Integer.parseInt(magRangeBar.getRightPinValue()));
                    //update displays
                    minMaxMagValueDisp.setText(magRangeBar.getLeftPinValue()
                            + "/"
                            + magRangeBar.getRightPinValue());
                }
                return false; //make sure the event is not captured, and propagates further!
            }
        });
        sigRangeBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //tap event ended
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (debugEnabled) {
                        toast = Toast.makeText(getApplicationContext(), "released" + sigRangeBar.getLeftPinValue() + "/" + sigRangeBar.getRightPinValue(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    //save values
                    saveSettings.saveSettings("SminSig", Integer.parseInt(sigRangeBar.getLeftPinValue()));
                    saveSettings.saveSettings("SmaxSig", Integer.parseInt(sigRangeBar.getRightPinValue()));
                    //update displays
                    minMaxSigValueDisp.setText(sigRangeBar.getLeftPinValue()
                            + "/"
                            + sigRangeBar.getRightPinValue());
                }
                return false; //make sure the event is not captured, and propagates further!
            }
        });

        limitResultsBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //tap event ended
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (debugEnabled) {
                        toast = Toast.makeText(getApplicationContext(), "released" + limitResultsBar.getLeftPinValue() + "/" + limitResultsBar.getRightPinValue(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    //save values
                    saveSettings.saveSettings("SmaxResults", Integer.parseInt(limitResultsBar.getRightPinValue()));
                    //update displays
                    LimitResValueDisp.setText(limitResultsBar.getRightPinValue());
                }

                return false; //make sure the event is not captured, and propagates further!
            }
        });



        //SWITCHES; set event-listeners and save settings change
        isDayNightModeOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings.saveSettings("SisSDayNightEnabled", isDayNightModeOn.isChecked());
            }
        });

        isLimitPeriodOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings.saveSettings("SisSDatePeriodEnabled", isLimitPeriodOn.isChecked());
            }
        });

        isRadiusFilterOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings.saveSettings("SisSRadiusEnabled", isRadiusFilterOn.isChecked());
            }
        });


        //add event-listener to text field
        RadiusFilterValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                int defVal = 0;

                try{
                    defVal = Integer.parseInt(RadiusFilterValue.getText().toString());
                }
                catch(NumberFormatException e){
                    //parsing error, just use default
                }

                saveSettings.saveSettings("SRadiusKm",defVal);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        setDateTimeField();

    }



    //initialising the date-pickers and setting up listeners
    private void setDateTimeField() {
        LimitPeriodFrom.setOnClickListener(this);
        LimitPeriodTill.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                LimitPeriodFrom.setText(dateFormatter.format(newDate.getTime()));
                saveSettings.saveSettings("SPeriodFrom", dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), (newCalendar.get((Calendar.DAY_OF_MONTH ))-7));

        toDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                LimitPeriodTill.setText(dateFormatter.format(newDate.getTime()));
                saveSettings.saveSettings("SPeriodTill", dateFormatter.format(newDate.getTime()));

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    //method to get the saved user settings
    private void loadSavedSettings() {
        isDayNightModeOn.setChecked(mySavedSettings.getBoolean("SisSDayNightEnabled",false));
        isLimitPeriodOn.setChecked(mySavedSettings.getBoolean("SisSDatePeriodEnabled",false));
        isRadiusFilterOn.setChecked(mySavedSettings.getBoolean("SisSRadiusEnabled",false));

        RadiusFilterValue.setText(Integer.toString(mySavedSettings.getInt("SRadiusKm", 100)));

        LimitPeriodFrom.setText(mySavedSettings.getString("SPeriodFrom",null));
        LimitPeriodTill.setText(mySavedSettings.getString("SPeriodTill",null));

        //set the rangebar min-max values
        magRangeBar.setRangePinsByValue(mySavedSettings.getInt("SminMag",1),mySavedSettings.getInt("SmaxMag",10));
        sigRangeBar.setRangePinsByValue(mySavedSettings.getInt("SminSig",1),mySavedSettings.getInt("SmaxSig",1000));
        limitResultsBar.setRangePinsByValue(1,mySavedSettings.getInt("SmaxResults",100));
        //also populate the display-fields
        minMaxMagValueDisp.setText(Integer.toString(mySavedSettings.getInt("SminMag",1)) + "/" + Integer.toString(mySavedSettings.getInt("SmaxMag",10)));
        minMaxSigValueDisp.setText(Integer.toString(mySavedSettings.getInt("SminSig",1)) + "/" + Integer.toString(mySavedSettings.getInt("SmaxSig",1000)));
        LimitResValueDisp.setText(Integer.toString(mySavedSettings.getInt("SmaxResults",100)));

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
                if (debugEnabled) {
                    System.out.println("Msp option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "Map option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(map_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_list:
                if (debugEnabled) {
                    System.out.println("List option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "List option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(list_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_chart:
                if (debugEnabled) {
                    System.out.println("Chart option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "Chart option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(chart_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_codeindex:
                if (debugEnabled) {
                    System.out.println("CodeList option Clicked!");
                    toast = Toast.makeText(getApplicationContext(), "CodeList option Clicked!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                startActivity(codeList_Screen);
                finish(); //ending .this activity
                return true;

            case R.id.menu_settings:
                //this is the current option, so ignore
                break;

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


    //displaying the date-picker dialogues when the values are clicked/tapped
    @Override
    public void onClick(View view) {
        if(view == LimitPeriodFrom) {
            fromDatePickerDialog.show();
        } else if(view == LimitPeriodTill) {
            toDatePickerDialog.show();
        }
    }
}