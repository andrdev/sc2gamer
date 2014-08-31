package com.github.andrdev.sc2gamer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.github.andrdev.sc2gamer.R;

public class AlarmDialog extends SherlockDialogFragment implements DialogInterface.OnClickListener {
    View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, this)
                .setNeutralButton(R.string.button_default, this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d("DreeBut", "" + which);
    }
}
