package com.example.shopqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ConfirmSend extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedIstanceState){
        MainActivity parent = (MainActivity) getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setTitle("Вы действительно хотите отправить?").setNegativeButton("Нет",null).setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parent.SendMail();
            }
        }).create();
    }
}
