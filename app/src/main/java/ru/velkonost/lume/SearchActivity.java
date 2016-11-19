package ru.velkonost.lume;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import static ru.velkonost.lume.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Initializations.initToolbar;
import static ru.velkonost.lume.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.PhoneDataStorage.loadText;
import static ru.velkonost.lume.PhoneDataStorage.saveText;

public class SearchActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_search;

    /**
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

    /**
     * Свойство - описание верхней панели инструментов приложения.
     */
    private Toolbar toolbar;

    /**
     * Свойство - описание {@link SearchActivity#LAYOUT}
     */
    private DrawerLayout drawerLayout;

    /**
     * Условный контейнер, в который помещаются все view-элементы, созданные программно.
     **/
    private LinearLayout linLayout;
    private LayoutInflater ltInflater;

    /**
     * Свойство - информация, по которой собирается искать пользователь.
     **/
    private String whatSearch;

    /**
     * Идентификаторы пользователей, некоторые данные которых соответствуют искомой информации.
     **/
    private ArrayList<String> ids;

    /**
     * Свойство - экзмепляр класса {@link GetData}
     */
    protected GetData mGetData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        mGetData = new GetData();
        ids = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(toolbar, R.string.app_name); /** Инициализация */
        initNavigationView(); /** Инициализация */

        linLayout = (LinearLayout) findViewById(R.id.searchContainer);
        ltInflater = getLayoutInflater();

        /**
         * Получение данных, которые вводил пользователь.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        whatSearch = loadText(SearchActivity.this, SEARCH);

        TextView textView = (TextView) findViewById(R.id.toSearch);
        textView.setText(whatSearch);

        mGetData.execute();
    }

    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_search);

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
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();

                /** Инициализируем намерение на следующую активность */
                switch (menuItem.getItemId()) {

                    /** Переход на профиль данного пользователя */
                    case R.id.navigationProfile:
                        nextIntent = new Intent(SearchActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(SearchActivity.this, ContactsActivity.class);
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
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(SearchActivity.this, ID);
                        nextIntent = new Intent(SearchActivity.this, WelcomeActivity.class);
                        break;
                }
                deleteText(SearchActivity.this, USER_ID);
                deleteText(SearchActivity.this, SEARCH);

                /**
                 * Переход на следующую активность.
                 * {@link Initializations#changeActivityCompat(Activity, Intent)}
                 * */
                changeActivityCompat(SearchActivity.this, nextIntent);

                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(SearchActivity.this, ID).equals(""))
                    finish();

                return true;
            }
        });
    }

    /**
     * Обработчик событий для кнопки поиска.
     */
    public void goToSearch(View view) {
        switch (view.getId()) {
            case R.id.btnStartSearch:

                /** Получение данных, по которым пользователь хочет найти информацию */
                EditText search = (EditText) findViewById(R.id.textSearch);
                String toSearch = search.getText().toString();

                /** Сохранение этих данных в файл на данном устройстве */
                saveText(SearchActivity.this, SEARCH, toSearch);

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
        changeActivityCompat(SearchActivity.this, nextIntent);
        finish();
    }

    /**
     * Обработчик нажатий по view-элементам,
     * каждый из которых отображает краткую информацию о конкретном пользователе.
     **/
    public void openUserProfile(View view) {

        /**
         * Сохраняет идентификатор пользователя, чей профиль намеревается открыть.
         **/
        saveText(SearchActivity.this, USER_ID, String.valueOf(view.getId()));

        /**
         * Переход в профиль выбранного пользователя.
         * {@link Initializations#changeActivityCompat(Activity, Intent)}
         * */
        changeActivityCompat(SearchActivity.this, new Intent(this, ProfileActivity.class));
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
                    + SERVER_SEARCH_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = SEARCH + EQUALS + whatSearch;

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
                StringBuilder buffer = new StringBuilder();
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

            /** Свойство - полученный JSON–объект*/
            JSONObject dataJsonObj;

            try {

                /**
                 * Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);

                /**
                 * Получение идентификаторов найденных пользователей.
                 */
                JSONArray idsJSON = dataJsonObj.getJSONArray("ids");

                /**
                 * Исключение возможности пользователя найти свой же профиль.
                 */
                for (int i = 0; i < idsJSON.length(); i++){
                    if (!idsJSON.getString(i).equals(loadText(SearchActivity.this, ID)))
                        ids.add(idsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < ids.size(); i++) {

                    /**
                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
                     */
                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));


                    View userView = ltInflater.inflate(R.layout.item_search_block, linLayout, false);
                    View rl = userView.findViewById(R.id.relativeLayoutSearch);

                    /**
                     * Установление идентификатора пользователя,
                     * чтобы при нажатии на элемент проще было понять, профиль какого пользователя необходимо открыть.
                     */
                    rl.setId(Integer.parseInt(userInfo.getString(ID)));

                    ImageView userAvatar = (ImageView) userView.findViewById(R.id.userAvatar); /** Аватар пользователя */

                    /** Иконка, указывающая, что пользователь еще не указал свое польное имя */
                    ImageView userWithoutName = (ImageView) userView.findViewById(R.id.userWithoutName);

                    TextView userName = (TextView) userView.findViewById(R.id.userName); /** Полное имя пользователя, иначе его логин */
                    TextView userPlace = (TextView) userView.findViewById(R.id.livingPlace); /** Место проживания пользователя */
                    TextView userWork = (TextView) userView.findViewById(R.id.workingPlace); /** Текущее место работы пользователя */

                    /**
                     * Установка имени владельца открытого профиля.
                     *
                     * Если имя и фамилия не найдены,
                     * то устанавливается логин + показывается иконка {@link userWithoutName}
                     **/
                    String sUserName = userInfo.getString(NAME).length() == 0
                            ? userInfo.getString(LOGIN)
                            : userInfo.getString(SURNAME).length() == 0
                            ? userInfo.getString(LOGIN)
                            : userInfo.getString(NAME) + " " +  userInfo.getString(SURNAME);

                    /**
                     * Формируется место проживания из имеющихся данных.
                     **/
                    String sUserPlace = userInfo.getString(COUNTRY).length() != 0
                            ? userInfo.getString(CITY).length() != 0
                            ? userInfo.getString(COUNTRY) + ", " + userInfo.getString(CITY)
                            : "" : "";

                    /** Формирование текущего места работы пользователя */
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

                    /** Формирование адреса, по которому лежит аватар пользователя */
                    String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                            + SERVER_AVATAR + SLASH + userInfo.getString(AVATAR)
                            + SLASH + userInfo.getString(ID) + PNG;

                    /**
                     *  Загрузка и установка аватара.
                     *  {@link ImageManager#fetchImage(String, ImageView)}
                     * */
                    fetchImage(avatarURL, userAvatar);

                    /** Добавление элемента в контейнер {@link SearchActivity#linLayout} */
                    linLayout.addView(userView);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}