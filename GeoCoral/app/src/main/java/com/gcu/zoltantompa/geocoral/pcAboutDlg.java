package com.gcu.zoltantompa.geocoral;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * code taken and modified from lab7
 * This class sets up the About Dialogue
 */

public class pcAboutDlg extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder pcAboutDlg = new AlertDialog.Builder(getActivity());
        pcAboutDlg.setMessage(R.string.dialog_About)
                .setPositiveButton(R.string.dialog_About_OK_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        pcAboutDlg.setTitle("About");
        //pcAboutDlg.setIcon(R);

        // Create the AlertDialog object and return it
        return pcAboutDlg.create();
    }
}
