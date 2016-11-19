package ru.velkonost.lume;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.PNG;
import static ru.velkonost.lume.Constants.SEARCH;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.SURNAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_CONTACTS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Constants.USER_ID;
import static ru.velkonost.lume.ImageManager.fetchImage;
import static ru.velkonost.lume.ImageManager.getCircleMaskedBitmap;
import static ru.velkonost.lume.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Initializations.initToolbar;
import static ru.velkonost.lume.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.PhoneDataStorage.loadText;
import static ru.velkonost.lume.PhoneDataStorage.saveText;

/**
 * @author Velkonost
 *
 * Класс, состояние страницы контактов авторизованного пользователя.
 *
 */
public class ContactsActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_contact;

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
     * Свойство - идентификатор пользователя, авторизованного на данном устройстве.
     */
    private String userId;

    /**
     * Идентификаторы пользователей, некоторые данные которых соответствуют искомой информации.
     **/
    private ArrayList<String> ids;

    /**
     * Контакты авторизованного пользователя.
     *
     * Ключ - идентификатор пользователя.
     * Значение - его полное имя или логин.
     **/
    private Map <String, String> contacts;

    /**
     * Условный контейнер, в который помещаются все view-элементы, созданные программно.
     **/
    private LinearLayout linLayout;
    private LayoutInflater ltInflater;

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

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(ContactsActivity.this, ID);

        linLayout = (LinearLayout) findViewById(R.id.contactsContainer);
        ltInflater = getLayoutInflater();

        mGetData.execute();
    }

    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_contact);

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
                        nextIntent = new Intent(ContactsActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(ContactsActivity.this, ContactsActivity.class);
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
                        deleteText(ContactsActivity.this, ID);
                        nextIntent = new Intent(ContactsActivity.this, WelcomeActivity.class);
                        break;
                }

                /**
                 * Удаляет информацию о владельце открытого профиля.
                 * {@link PhoneDataStorage#deleteText(Context, String)}
                 **/
                deleteText(ContactsActivity.this, USER_ID);

                /**
                 * Переход на следующую активность.
                 * {@link Initializations#changeActivityCompat(Activity, Intent)}
                 * */
                changeActivityCompat(ContactsActivity.this, nextIntent);

                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(ContactsActivity.this, ID).equals(""))
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
                saveText(ContactsActivity.this, SEARCH, toSearch);

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
        changeActivityCompat(ContactsActivity.this, nextIntent);
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
        saveText(ContactsActivity.this, USER_ID, String.valueOf(view.getId()));

        /**
         * Переход в профиль выбранного пользователя.
         * {@link Initializations#changeActivityCompat(Activity, Intent)}
         * */
        changeActivityCompat(ContactsActivity.this, new Intent(this, ProfileActivity.class));
    }






    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue(Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    // a comparator using generic type
    class ValueComparator<K, V extends Comparable<V>> implements Comparator<K>{

        HashMap<K, V> map = new HashMap<K, V>();

        public ValueComparator(HashMap<K, V> map){
            this.map.putAll(map);
        }

        @Override
        public int compare(K s1, K s2) {
            return -map.get(s1).compareTo(map.get(s2));//descending order
        }
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
                    + SERVER_GET_CONTACTS_METHOD;

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
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

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

                for (int i = 0; i < idsJSON.length(); i++){
                    ids.add(idsJSON.getString(i));
                }



                contacts = new HashMap<>();

                for (int i = 0; i < ids.size(); i++){
                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));
                    contacts.put(
                            ids.get(i),
                            userInfo.getString(NAME).length() != 0
                                    ? userInfo.getString(SURNAME).length() != 0
                                    ? userInfo.getString(NAME) + " " + userInfo.getString(SURNAME)
                                    : userInfo.getString(LOGIN) : userInfo.getString(LOGIN)
                    );
                }

                Comparator<String> comparator = new ValueComparator<>((HashMap<String, String>) contacts);
                TreeMap<String, String> sortedContacts = new TreeMap<>(comparator);
                sortedContacts.putAll(contacts);

                ids = new ArrayList<>();

                for (String key : sortedContacts.keySet()) {
                    ids.add(key);
                }
                Collections.reverse(ids);

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < ids.size(); i++) {

                    /**
                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
                     */
                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));


                    View userView = ltInflater.inflate(R.layout.item_contact_block, linLayout, false);
                    View rl = userView.findViewById(R.id.relativeLayoutContact);

                    /**
                     * Установление идентификатора пользователя,
                     * чтобы при нажатии на элемент проще было понять, профиль какого пользователя необходимо открыть.
                     */
                    rl.setId(Integer.parseInt(userInfo.getString(ID)));

                    ImageView userAvatar = (ImageView) userView.findViewById(R.id.userAvatar); /** Аватар пользователя */

                    /** Иконка, указывающая, что пользователь еще не указал свое польное имя */
                    ImageView userWithoutName = (ImageView) userView.findViewById(R.id.userWithoutName);

                    /** Полное имя пользователя, иначе его логин */
                    TextView userName = (TextView) userView.findViewById(R.id.userName);
                    TextView userLogin = (TextView) userView.findViewById(R.id.userLogin);

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

                    if (sUserName.equals(userInfo.getString(LOGIN)))
                        userWithoutName.setImageResource(R.drawable.withoutname);
                    else
                        userLogin.setText(userInfo.getString(LOGIN));

                    userName.setText(sUserName);

                    /** Формирование адреса, по которому лежит аватар пользователя */
                    String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                            + SERVER_AVATAR + SLASH + userInfo.getString(AVATAR)
                            + SLASH + userInfo.getString(ID) + PNG;

                    /**
                     *  Загрузка и установка аватара.
                     *  {@link ImageManager#fetchImage(String, ImageView)}
                     * */
                    fetchImage(avatarURL, userAvatar);
                    Bitmap bitmap = ((BitmapDrawable)userAvatar.getDrawable()).getBitmap();
                    userAvatar.setImageBitmap(getCircleMaskedBitmap(bitmap, 25));

                    /** Добавление элемента в контейнер {@link SearchActivity#linLayout} */
                    linLayout.addView(userView);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
