package ru.velkonost.lume;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class Initializations {

    private static ViewPager viewPager;

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

    public static void initToolbar(Toolbar toolbar, int title) {

        toolbar.setTitle(title);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }

    public static final void changeActivityCompat(final Activity a) {
        final Intent intent = a.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        a.finish();
        a.overridePendingTransition(0, 0);

        a.startActivity(intent);
        a.overridePendingTransition(0, 0);
    }


    public static final void changeActivityCompat(final Activity a, Intent nextIntent) {
        final Intent currentIntent = a.getIntent();
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


        if (currentIntent.filterEquals(nextIntent)) {
            a.finish();
            Log.i("CUR", String.valueOf(currentIntent));
        }
        a.overridePendingTransition(0, 0);

        a.startActivity(nextIntent);
        a.overridePendingTransition(0, 0);
    }
}
