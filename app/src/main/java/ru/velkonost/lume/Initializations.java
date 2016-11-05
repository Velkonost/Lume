package ru.velkonost.lume;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Initializations {
    public static void inititializeAlertDialog(Context context, String header,
                                               String description, String btnName){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(header)
                .setMessage(description)
//                .setIcon(R.drawable.ic_android_cat) МОЖНО ДОБАВИТЬ ИКОНКУ!
                .setCancelable(false)
                .setNegativeButton(btnName,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
