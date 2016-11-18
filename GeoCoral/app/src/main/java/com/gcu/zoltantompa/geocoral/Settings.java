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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class Settings extends AppCompatActivity implements View.OnClickListener {

    FragmentManager fmAboutDialogue;

    Intent map_Screen;
    Intent list_Screen;
    Intent settings_Screen;
    Intent codeList_Screen;

    Toast toast;

    pcSaveSettings saveSettings; // Lab 3 Shared Preferences
    SharedPreferences mySavedSettings; // Lab 3 Shared Preferences

    //UI variables
    Switch isAudioOn;
    Switch isDayNightModeOn;
    Switch isLimitPeriodOn;
    TextView LimitPeriodFrom;
    TextView LimitPeriodTill;
    Switch isRadiusFilterOn;
    TextView RadiusFilterValue;

    TextView debuggerText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Starting new Intents
        map_Screen = new Intent(getApplicationContext(), MapView.class);
        list_Screen = new Intent(getApplicationContext(), ListView.class);
        settings_Screen = new Intent(getApplicationContext(), Settings.class);
        codeList_Screen = new Intent(getApplicationContext(), CodeIndex.class);

        //getting back saved data
        mySavedSettings = PreferenceManager.getDefaultSharedPreferences(this);
        saveSettings = new pcSaveSettings(mySavedSettings);
        //saveSettings.setDefaultSettings();

        //bind UI
        isAudioOn = (Switch) findViewById(R.id.enableAudioSw);
        isDayNightModeOn = (Switch) findViewById(R.id.DayNightSw);
        isLimitPeriodOn = (Switch)  findViewById(R.id.LimitPeriodSw);
        LimitPeriodFrom = (TextView) findViewById(R.id.LimitPeriodFrom);
        LimitPeriodTill = (TextView) findViewById(R.id.LimitPeriodTo);
        isRadiusFilterOn = (Switch)  findViewById(R.id.RadiusFilterSw);
        RadiusFilterValue = (TextView) findViewById(R.id.RadiusFilterValue);


        debuggerText = (TextView) findViewById(R.id.debugText);

        // Load any saved preferences
        mySavedSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loadSavedSettings();



        //SWITCHES; set eventlisteners and save settings change
        isAudioOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings.saveSettings("SisSAudioEnabled", isAudioOn.isChecked());
                updateDebug();
            }
        });

        isDayNightModeOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings.saveSettings("SisSDayNightEnabled", isDayNightModeOn.isChecked());
                updateDebug();
            }
        });

        isLimitPeriodOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings.saveSettings("SisSDatePeriodEnabled", isLimitPeriodOn.isChecked());
                updateDebug();
            }
        });

        isRadiusFilterOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings.saveSettings("SisSRadiusEnabled", isRadiusFilterOn.isChecked());
                updateDebug();
            }
        });



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
                updateDebug();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



/*
            RadiusFilterValue.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveSettings.saveSettings("SRadiusKm", Integer.getInteger(RadiusFilterValue.getText().toString()));
                    updateDebug();
                    return true;
                }
                return false;
            }
        });
*/

    }


    private void updateDebug()
    {
        toast = Toast.makeText(getApplicationContext(), "switch Clicked!", Toast.LENGTH_SHORT);
        toast.show();

        debuggerText.setText(mySavedSettings.getAll().toString());
    }

    private void loadSavedSettings() {
        isAudioOn.setChecked(mySavedSettings.getBoolean("SisSAudioEnabled",false)); // key, def. value (if no value set)
        isDayNightModeOn.setChecked(mySavedSettings.getBoolean("SisSDayNightEnabled",false));
        isLimitPeriodOn.setChecked(mySavedSettings.getBoolean("SisSDatePeriodEnabled",false));
        isRadiusFilterOn.setChecked(mySavedSettings.getBoolean("SisSRadiusEnabled",false));

        RadiusFilterValue.setText(Integer.toString(mySavedSettings.getInt("SRadiusKm", 100)));

        debuggerText.setText(mySavedSettings.getAll().toString());

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

            case R.id.menu_codeindex:
                System.out.println("CodeList option Clicked!");
                toast = Toast.makeText(getApplicationContext(), "CodeList option Clicked!", Toast.LENGTH_SHORT);
                toast.show();
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





    @Override
    public void onClick(View view) {

    }
}
