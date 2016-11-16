package ru.velkonost.lume;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.CITY;
import static ru.velkonost.lume.Constants.COUNTRY;
import static ru.velkonost.lume.Constants.EQUALS;
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
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Constants.URL.SERVER_SEARCH_METHOD;
import static ru.velkonost.lume.Constants.USER_ID;
import static ru.velkonost.lume.Constants.WORK;
import static ru.velkonost.lume.ImageManager.fetchImage;
import static ru.velkonost.lume.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.PhoneDataStorage.loadText;
import static ru.velkonost.lume.PhoneDataStorage.saveText;

public class SearchActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_search;

    private Intent nextIntent;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private String whatSearch;

    private Map <String, Map<String, String> > usersData;
    private Map <String, String> depUserInfo; // depositoryUserInfo
    private ArrayList<String> ids;

    private GetData mGetData;

    LinearLayout linLayout;
    LayoutInflater ltInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        mGetData = new GetData();

        initToolbar();
        initNavigationView();

        linLayout = (LinearLayout) findViewById(R.id.searchContainer);
        ltInflater = getLayoutInflater();

        usersData = new HashMap<>();
        ids = new ArrayList<String>();

        whatSearch = loadText(SearchActivity.this, SEARCH);

        TextView textView = (TextView) findViewById(R.id.toSearch);
        textView.setText(whatSearch);

        mGetData.execute();
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
                        nextIntent = new Intent(SearchActivity.this, ProfileActivity.class);
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
                        deleteText(SearchActivity.this, ID);
                        nextIntent = new Intent(SearchActivity.this, WelcomeActivity.class);
                        break;
                }
                startActivity(nextIntent);
                return true;
            }
        });
    }

    public void goToSearch(View view) {
        switch (view.getId()) {
            case R.id.btnStartSearch:
                EditText search = (EditText) findViewById(R.id.textSearch);
                String toSearch = search.getText().toString();
                saveText(SearchActivity.this, SEARCH, toSearch);

                nextIntent = new Intent(this, SearchActivity.class);
                break;
        }
        startActivity(nextIntent);
        finish();
    }

    public void openUserProfile(View view) {
        saveText(SearchActivity.this, USER_ID, String.valueOf(view.getId()));
        startActivity(new Intent(this, UserProfileActivity.class));
    }


    private class GetData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_SEARCH_METHOD;

            @SuppressWarnings("WrongThread") String params = SEARCH + EQUALS + whatSearch;

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

            JSONObject dataJsonObj;

            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray idsJSON = dataJsonObj.getJSONArray("ids");

                for (int i = 0; i < idsJSON.length(); i++){
                    ids.add(idsJSON.getString(i));
                }

                for (int i = 0; i < idsJSON.length(); i++) {
                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));

                    View userView = ltInflater.inflate(R.layout.item_search_block, linLayout, false);
                    View rl = userView.findViewById(R.id.relativeLayoutSearch);
                    rl.setId(Integer.parseInt(userInfo.getString(ID)));

                    ImageView userAvatar = (ImageView) userView.findViewById(R.id.userAvatar);
                    ImageView userWithoutName = (ImageView) userView.findViewById(R.id.userWithoutName);

                    TextView userName = (TextView) userView.findViewById(R.id.userName);
                    TextView userPlace = (TextView) userView.findViewById(R.id.livingPlace);
                    TextView userWork = (TextView) userView.findViewById(R.id.workingPlace);

                    TextView userId = (TextView) userView.findViewById(R.id.userId);

                    String sUserName = userInfo.getString(NAME).length() == 0
                            ? userInfo.getString(LOGIN)
                            : userInfo.getString(SURNAME).length() == 0
                            ? userInfo.getString(LOGIN)
                            :userInfo.getString(NAME) + " " +  userInfo.getString(SURNAME);

                    String sUserPlace = userInfo.getString(COUNTRY).length() != 0
                            ? userInfo.getString(CITY).length() != 0
                            ? userInfo.getString(COUNTRY) + ", " + userInfo.getString(CITY)
                            : "" : "";

                    String sUserWork = userInfo.getString(WORK).length() != 0
                            ? userInfo.getString(WORK)
                            : userInfo.getString(STUDY).length() != 0
                            ? userInfo.getString(STUDY)
                            : "";

                    if (sUserName.equals(userInfo.getString(LOGIN)))
                        userWithoutName.setImageResource(R.drawable.withoutname);


                    userName.setText(sUserName);
                    userPlace.setText(sUserPlace);
                    userWork.setText(sUserWork);

                    userId.setText(userInfo.getString(ID));

                    String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                            + SERVER_AVATAR + SLASH + userInfo.getString(AVATAR) + SLASH + userInfo.getString(ID) + PNG;

                    fetchImage(avatarURL, userAvatar);

                    linLayout.addView(userView);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}