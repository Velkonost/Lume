package ru.velkonost.lume;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BIRTHDAY;
import static ru.velkonost.lume.Constants.CITY;
import static ru.velkonost.lume.Constants.COUNTRY;
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
import static ru.velkonost.lume.Constants.USER_ID;
import static ru.velkonost.lume.Constants.WORK;
import static ru.velkonost.lume.Constants.WORK_EMAIL;
import static ru.velkonost.lume.ImageManager.fetchImage;
import static ru.velkonost.lume.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Initializations.initToolbar;
import static ru.velkonost.lume.Initializations.inititializeAlertDialog;
import static ru.velkonost.lume.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.PhoneDataStorage.loadText;
import static ru.velkonost.lume.PhoneDataStorage.saveText;

/**
 * @author Velkonost
 *
 * Класс, описывающий профиль <b>данного</b> пользователя.
 *
 */
public class ProfileActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_myprofile;

    /**
     * Свойство - следующая активность
     */
    private Intent nextIntent;

    /**
     * Свойство - высота экрана устройства
     */
    private int screenH;
    /**
     * Свойство - ширина экрана устройства
     */
    private int screenW;

    /**
     * Свойство - описание верхней панели инструментов приложения
     */
    private Toolbar toolbar;
    /**
     * Свойство - описание боковой панели навигации
     */
    private DrawerLayout drawerLayout;

    /**
     * Свойство - view-элемент для размещения аватара пользователя
     */
    private ImageView userAvatar;
    /**
     * Свойство - view-элемент для размещения полного имени пользователя
     */
    private TextView userName;

    /**
     * Свойство - экзмепляр класса {@link GetData}
     */
    private GetData mGetData;
    /**
     * Свойство - идентификатор данного пользователя
     */
    private String userId;

    private View viewAvatar;
    private View viewUserPlaceLiving;
    private View viewUserBirthday;
    private View viewUserPlaceStudy;
    private View viewUserPlaceWork;
    private View viewUserWorkingEmail;
    private View viewUserPlaceStudyAndWork;
    private View viewUserInteraction;

    private SwipeRefreshLayout mSwipeRefreshLayout;


    private LinearLayout linLayout;
    private LayoutInflater ltInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Установка темы по умолчанию */
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        /** Вычисляет размеры экрана устройства */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenH = displayMetrics.heightPixels;
        screenW = displayMetrics.widthPixels;

        mGetData = new GetData();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(toolbar, R.string.app_name); /** Инициализация */
        initNavigationView(); /** Инициализация */

        /**
         * Получение id пользователя, помещение в хранилище.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId =  loadText(ProfileActivity.this, USER_ID).length() == 0
                ? loadText(ProfileActivity.this, ID)
                : loadText(ProfileActivity.this, USER_ID);


        /**
         * Условный контейнер, в который помещаются все view-элементы, созданные программно.
         **/
        linLayout = (LinearLayout) findViewById(R.id.profileContainer);
        ltInflater = getLayoutInflater();



        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorBlue, R.color.colorGreen,
                R.color.colorYellow, R.color.colorRed);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeActivityCompat(ProfileActivity.this);
                    }
                }, 2500);
            }
        });

        /** Обращаемся к серверу */
        mGetData.execute();
    }


    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_myprofile);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /**
         * Обработчки событий для меню бокового меню навигации.
         **/
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    /** Переход на профиль данного пользователя */
                    case R.id.navigationProfile:
                        nextIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
//                        nextIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        finish();
                        break;
                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        break;
                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;
                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        break;
                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        break;
                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        break;
                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(ProfileActivity.this, ID);
                        nextIntent = new Intent(ProfileActivity.this, WelcomeActivity.class);
                        break;
                }
                deleteText(ProfileActivity.this, USER_ID);
                /**
                 * Переход на следующую активность.
                 * {@link Initializations#changeActivityCompat(Activity, Intent)}
                 * */
                changeActivityCompat(ProfileActivity.this, nextIntent);
                if (loadText(ProfileActivity.this, ID).equals(""))
                    finish();
                return true;
            }
        });
    }

    /**
     * Обработчки событий для кнопки поиска
     */
    public void goToSearch(View view) {
        switch (view.getId()) {
            case R.id.btnStartSearch:
                /** Получение данных, по которым пользователь хочет найти информацию */
                EditText search = (EditText) findViewById(R.id.textSearch);
                String toSearch = search.getText().toString();
                /** Сохранение этих данных в файл на данном устройстве */
                saveText(ProfileActivity.this, SEARCH, toSearch);
                /**
                 * Переход на страницу поиска, где выоводится результат.
                 * {@link SearchActivity}
                 **/
                nextIntent = new Intent(this, SearchActivity.class);
                break;
        }
        /**
         * Переход на следующую активность.
         * {@link Initializations#changeActivityCompat(Activity, Intent)}
         * */
        changeActivityCompat(ProfileActivity.this, nextIntent);
    }


    public String formatDate(String dateInStr) {
        String day, month, year;
        day = new StringBuilder()
                .append(dateInStr.charAt(dateInStr.length() - 2))
                .append(dateInStr.charAt(dateInStr.length() - 1))
                .toString();
        month = new StringBuilder()
                .append(dateInStr.charAt(dateInStr.length() - 5))
                .append(dateInStr.charAt(dateInStr.length() - 4))
                .toString();
        year = new StringBuilder()
                .append(dateInStr.charAt(dateInStr.length() - 10))
                .append(dateInStr.charAt(dateInStr.length() - 9))
                .append(dateInStr.charAt(dateInStr.length() - 8))
                .append(dateInStr.charAt(dateInStr.length() - 7))
                .toString();

        return new StringBuilder(day).append("-").append(month).append("-").append(year).toString();
    }


    /**
     * Класс для получения данных о пользователе с сервера.
     **/
    private class GetData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {
            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_GET_DATA_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = ID + EQUALS + userId;

            byte[] data;
            InputStream is;
            BufferedReader reader;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";


            try {
                /**
                 * Устанавливает соединение.
                 */
                URL url = new URL(dataURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                /**
                 * Выставляет необходимые параметры.
                 */
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                /**
                 * Формирует тело запроса.
                 */
                httpURLConnection.setRequestProperty("Content-Length", ""
                        + Integer.toString(params.getBytes().length));
                OutputStream os = httpURLConnection.getOutputStream();
                data = params.getBytes("UTF-8");
                os.write(data);

                /** Соединяемся */
                httpURLConnection.connect();

                /**
                 * Получение кода состояния.
                 */
                int responseCode = httpURLConnection.getResponseCode();
                Log.i("Data", String.valueOf(responseCode));

                /**
                 * Получение данных из потока в виде JSON-объекта.
                 */
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

            /**
             * Свойство - код ответа от методов сервера.
             *
             * ВНИМАНИЕ!
             *
             * Этот параметр не имеет никакого отношения к кодам состояния.
             * Он формируется на сервере в зависимости от результата проведения обработки данных.
             *
             **/
            int resultCode;

            /** Свойство - полученный JSON–объект*/
            JSONObject dataJsonObj;

            try {
                /**
                 * Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);
                resultCode = Integer.parseInt(dataJsonObj.getString(GET_DATA));

                /**
                 * Обработка полученного кода ответа.
                 */
                switch (resultCode) {
                    case 300:
                        /**
                         * Получение данных пользователя.
                         * Добавление в хранилище.
                         * {@link ProfileActivity#userData}
                         */
//                        userData.put(LOGIN, dataJsonObj.getString(LOGIN));
//                        userData.put(NAME, dataJsonObj.getString(NAME));
//                        userData.put(SURNAME, dataJsonObj.getString(SURNAME));
//                        userData.put(WORK_EMAIL, dataJsonObj.getString(WORK_EMAIL));
//                        userData.put(COUNTRY, dataJsonObj.getString(COUNTRY));
//                        userData.put(Constants.CITY, dataJsonObj.getString(Constants.CITY));
//                        userData.put(AVATAR, dataJsonObj.getString(AVATAR));
//                        userData.put(BIRTHDAY, dataJsonObj.getString(BIRTHDAY));
//                        userData.put(STUDY, dataJsonObj.getString(STUDY));
//                        userData.put(WORK, dataJsonObj.getString(WORK));

                        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                                + SERVER_AVATAR + SLASH + dataJsonObj.getString(AVATAR)
                                + SLASH + userId + PNG;

                        viewAvatar = ltInflater.inflate(R.layout.item_profile_photo, linLayout, false);

                        userAvatar = (ImageView) viewAvatar.findViewById(R.id.imageAvatar);
                        userName = (TextView) viewAvatar.findViewById(R.id.userName);

                        ImageView userWithoutName = (ImageView) viewAvatar.findViewById(R.id.userWithoutName);

                        /** Задает параметры для аватара пользователя */
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(screenW / 2,
                                screenH / 2);
                        param.gravity = Gravity.CENTER;
                        param.setMargins(0, 0, 0, 0);
                        userAvatar.setLayoutParams(param);

                        /** Устанавливает полученные значения */
                        String sUserName = dataJsonObj.getString(NAME).length() == 0
                                ? dataJsonObj.getString(LOGIN)
                                : dataJsonObj.getString(SURNAME).length() == 0
                                ? dataJsonObj.getString(LOGIN)
                                : dataJsonObj.getString(NAME) + " " + dataJsonObj.getString(SURNAME);
                        userName.setText(sUserName);

                        if (sUserName.equals(dataJsonObj.getString(LOGIN)))
                            userWithoutName.setImageResource(R.drawable.withoutname);


                        linLayout.addView(viewAvatar);

                        fetchImage(avatarURL, userAvatar);

                        /////////////////////////////


                        if (!loadText(ProfileActivity.this, USER_ID).equals("")) {
                            viewUserInteraction = ltInflater
                                    .inflate(R.layout.item_profile_interaction, linLayout, false);
                            Button btnAddIntoContacts = (Button) viewUserInteraction
                                    .findViewById(R.id.btnAddToContacts);
                            Button btnSendMessages = (Button) viewUserInteraction
                                    .findViewById(R.id.btnSendMessage);
                            linLayout.addView(viewUserInteraction);
                        }

                        viewUserPlaceLiving = ltInflater.inflate(R.layout.item_profile_place_living, linLayout, false);
                        TextView userPlaceLiving = (TextView) viewUserPlaceLiving.findViewById(R.id.descriptionCardPlaceLiving);


                        String sUserPlaceLiving = dataJsonObj.getString(CITY).length() != 0
                                ? dataJsonObj.getString(COUNTRY).length() != 0
                                ? dataJsonObj.getString(CITY) + ", " + dataJsonObj.getString(COUNTRY)
                                : dataJsonObj.getString(CITY)
                                : "";

                        userPlaceLiving.setText(sUserPlaceLiving);

                        if (!sUserPlaceLiving.equals(""))
                            linLayout.addView(viewUserPlaceLiving);


                        viewUserBirthday = ltInflater.inflate(R.layout.item_profile_birthday, linLayout, false);
                        TextView userBirthday = (TextView) viewUserBirthday.findViewById(R.id.descriptionCardBirthday);

                        String sUserBirthday = dataJsonObj.getString(BIRTHDAY).length() != 0
                                ? dataJsonObj.getString(BIRTHDAY)
                                : "";
                        String formattedUserBirthday = formatDate(sUserBirthday);
                        userBirthday.setText(formattedUserBirthday);
                        if (!formattedUserBirthday.equals("00-00-0000"))
                            linLayout.addView(viewUserBirthday);


                        String sUserPlaceStudy = dataJsonObj.getString(STUDY).length() != 0
                                ? dataJsonObj.getString(STUDY)
                                : "";
                        String sUserPlaceWork = dataJsonObj.getString(WORK).length() != 0
                                ? dataJsonObj.getString(WORK)
                                : "";


                        if (sUserPlaceStudy.equals("") && !sUserPlaceWork.equals("")) {
                            viewUserPlaceWork = ltInflater.inflate(R.layout.item_profile_place_work, linLayout, false);
                            TextView userPlaceWork = (TextView) viewUserPlaceWork.findViewById(R.id.descriptionCardPlaceWork);
                            userPlaceWork.setText(sUserPlaceWork);

                            linLayout.addView(viewUserPlaceWork);
                        }
                        if (sUserPlaceWork.equals("") && !sUserPlaceStudy.equals("")) {
                            viewUserPlaceStudy = ltInflater.inflate(R.layout.item_profile_place_study, linLayout, false);
                            TextView userPlaceStudy = (TextView) viewUserPlaceStudy.findViewById(R.id.descriptionCardPlaceStudy);
                            userPlaceStudy.setText(sUserPlaceStudy);

                            linLayout.addView(viewUserPlaceStudy);
                        }
                        if (!sUserPlaceStudy.equals("") && !sUserPlaceWork.equals("")) {
                            viewUserPlaceStudyAndWork = ltInflater
                                    .inflate(R.layout.item_profile_place_study_and_work,
                                            linLayout, false);

                            TextView userPlaceStudy = (TextView) viewUserPlaceStudyAndWork.
                                    findViewById(R.id.descriptionCardPlaceStudy);
                            userPlaceStudy.setText(sUserPlaceStudy);

                            TextView userPlaceWork = (TextView) viewUserPlaceStudyAndWork.
                                    findViewById(R.id.descriptionCardPlaceWork);
                            userPlaceWork.setText(sUserPlaceWork);

                            linLayout.addView(viewUserPlaceStudyAndWork);
                        }


                        String sUserWorkingEmail = dataJsonObj.getString(WORK_EMAIL).length() != 0
                                ? dataJsonObj.getString(WORK_EMAIL)
                                : "";


                        if (!sUserWorkingEmail.equals("")) {

                            viewUserWorkingEmail = ltInflater
                                    .inflate(R.layout.item_profile_working_email, linLayout, false);

                            final TextView userWorkingEmail = (TextView) viewUserWorkingEmail
                                    .findViewById(R.id.descriptionCardWorkingEmail);
                            userWorkingEmail.setText(sUserWorkingEmail);

                            linLayout.addView(viewUserWorkingEmail);

                        }



                        break;
                    case 301:
                        /**
                         * Формирование уведомления об ошибке.
                         */
                        inititializeAlertDialog(ProfileActivity.this,
                                getResources().getString(R.string.server_error),
                                getResources().getString(R.string.relogin),
                                getResources().getString(R.string.btn_ok));
                        break;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}