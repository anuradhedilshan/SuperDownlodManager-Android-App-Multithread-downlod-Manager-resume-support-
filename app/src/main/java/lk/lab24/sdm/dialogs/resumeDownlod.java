package lk.lab24.sdm.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import lk.lab24.sdm.R;

public class resumeDownlod extends DialogFragment implements View.OnClickListener{
    View dialogView;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        this.dialogView = LayoutInflater.from(getContext()).inflate(R.layout.resumedownlod,null);
        TextView pathChoose = (TextView) dialogView.findViewById(R.id.path_Choose);
        pathChoose.setOnClickListener(this);
        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })
                .setNegativeButton("cencel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resumeDownlod.this.getDialog().cancel();

                    }
                });
        return builder.create();
    }


    @Override
    public void onClick(View v) {

    }
}

