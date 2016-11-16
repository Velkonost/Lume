package ru.velkonost.lume;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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

    public static void initToolbar(Toolbar toolbar) {

        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }

    public static void initNavigationView(final Activity activity, final NavigationView navigationView,
                                   final DrawerLayout drawerLayout, Toolbar toolbar) {


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar,
                R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigationBoards:
                        showNotificationTab();
                }
                return true;
            }
        });
    }

    private static void showNotificationTab() {
        viewPager.setCurrentItem(Constants.TAB_TWO);
    }
}
