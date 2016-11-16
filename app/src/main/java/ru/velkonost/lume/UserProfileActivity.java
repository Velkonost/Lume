package ru.velkonost.lume;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BIRTHDAY;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.GET_DATA;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.PNG;
import static ru.velkonost.lume.Constants.SEARCH;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.STUDY;
import static ru.velkonost.lume.Constants.SURNAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_DATA_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Constants.WORK;
import static ru.velkonost.lume.Constants.WORK_EMAIL;
import static ru.velkonost.lume.ImageManager.fetchImage;
import static ru.velkonost.lume.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.PhoneDataStorage.loadText;
import static ru.velkonost.lume.PhoneDataStorage.saveText;

public class UserProfileActivity extends Activity{
    private static final int LAYOUT = R.layout.activity_myprofile;

    private Intent nextIntent;

    private int screenH;
    private int screenW;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private ImageView userAvatar;
    private TextView userName;

    private GetData mGetData;
    private Map<String, String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenH = displayMetrics.heightPixels;
        screenW = displayMetrics.widthPixels;

        userData = new HashMap<>();
        mGetData = new GetData();

        initToolbar();
        initNavigationView();

        userData.put(ID, loadText(UserProfileActivity.this, ID));

        final LinearLayout linLayout = (LinearLayout) findViewById(R.id.profileContainer);
        final LayoutInflater ltInflater = getLayoutInflater();
        final View viewAvatar = ltInflater.inflate(R.layout.item_profile_photo, linLayout, false);

        userAvatar = (ImageView) viewAvatar.findViewById(R.id.imageAvatar);
        userName = (TextView) viewAvatar.findViewById(R.id.userName);

        mGetData.execute();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(screenW / 2,
                        screenH / 2);
                param.gravity = Gravity.CENTER;
                param.setMargins(0, 0, 0, 0);
                userAvatar.setLayoutParams(param);


                userName.setText(userData.get(NAME) + " " + userData.get(SURNAME));

                viewAvatar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)); //width, height


                linLayout.addView(viewAvatar);

                initRequiredInfo(linLayout, ltInflater);
            }
        }, 2000);
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_myprofile);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigationProfile:
                        nextIntent = new Intent(UserProfileActivity.this, ProfileActivity.class);
                        break;
                    case R.id.navigationContacts:
                        break;
                    case R.id.navigationReminder:
                        break;
                    case R.id.navigationMessages:
                        break;
                    case R.id.navigationBoards:
                        break;
                    case R.id.navigationSettings:
                        break;
                    case R.id.navigationLogout:
                        deleteText(UserProfileActivity.this, ID);
                        nextIntent = new Intent(UserProfileActivity.this, WelcomeActivity.class);
                        break;
                }
                startActivity(nextIntent);
                return true;
            }
        });

//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.navigationHeader);

//        Button btnSearch = (Button) linearLayout.findViewById(R.id.startSearch);
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EditText search = (EditText) findViewById(R.id.textSearch);
//                String toSearch = search.getText().toString();
//                saveText(ProfileActivity.this, SEARCH, toSearch);
//                Log.i("SEARCH", toSearch);
//                nextIntent = new Intent(ProfileActivity.this, SearchActivity.class);
//            }
//        });

    }

    public void goToSearch(View view) {
        switch (view.getId()) {
            case R.id.btnStartSearch:
                EditText search = (EditText) findViewById(R.id.textSearch);
                String toSearch = search.getText().toString();
                saveText(UserProfileActivity.this, SEARCH, toSearch);

                nextIntent = new Intent(this, SearchActivity.class);
                break;
        }
        startActivity(nextIntent);
        finish();
    }

    private void initRequiredInfo(LinearLayout linLayout, LayoutInflater ltInflater) {

        View ReqInfo = ltInflater.inflate(R.layout.item_one_line_block, linLayout, false); //required info
        TextView ReqInfoName = (TextView) ReqInfo.findViewById(R.id.titleCardProfile);
        ReqInfoName.setText(userData.get(NAME) + "  " + userData.get(SURNAME));

        ReqInfo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)); //width, height

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.CENTER;
        ReqInfoName.setLayoutParams(params);

        linLayout.addView(ReqInfo);

    }


    private class GetData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_GET_DATA_METHOD;

            @SuppressWarnings("WrongThread") String params = ID + EQUALS + userData.get(ID);

            byte[] data;
            InputStream is;
            BufferedReader reader;
            String resultJson = "";


            try {
                URL url = new URL(dataURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestProperty("Content-Length", ""
                        + Integer.toString(params.getBytes().length));
                OutputStream os = httpURLConnection.getOutputStream();
                data = params.getBytes("UTF-8");
                os.write(data);

                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                Log.i("Data", String.valueOf(responseCode));

                is = httpURLConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
                Log.i("RESULT", resultJson);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);



            int resultCode;
            JSONObject dataJsonObj;

            try {
                dataJsonObj = new JSONObject(strJson);
                resultCode = Integer.parseInt(dataJsonObj.getString(GET_DATA));

                switch (resultCode){
                    case 300:
                        userData.put(LOGIN, dataJsonObj.getString(LOGIN));
                        userData.put(NAME, dataJsonObj.getString(NAME));
                        userData.put(SURNAME, dataJsonObj.getString(SURNAME));
                        userData.put(WORK_EMAIL, dataJsonObj.getString(WORK_EMAIL));
                        userData.put(Constants.COUNTRY, dataJsonObj.getString(Constants.COUNTRY));
                        userData.put(Constants.CITY, dataJsonObj.getString(Constants.CITY));
                        userData.put(AVATAR, dataJsonObj.getString(AVATAR));
                        userData.put(BIRTHDAY, dataJsonObj.getString(BIRTHDAY));
                        userData.put(STUDY, dataJsonObj.getString(STUDY));
                        userData.put(WORK, dataJsonObj.getString(WORK));

//                        mDownloadImageTask.execute();

                        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                                + SERVER_AVATAR + SLASH + userData.get(AVATAR) + SLASH + userData.get(ID) + PNG;

                        fetchImage(avatarURL, userAvatar);
                        break;
//                    case 301:
//                        inititializeAlertDialog(ProfileActivity.this,
//                                getResources().getString(R.string.authorization_error),
//                                getResources().getString(R.string.incorrectly_introduce_login_or_password),
//                                getResources().getString(R.string.btn_ok));
//                        break;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

