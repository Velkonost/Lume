package ru.velkonost.lume;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.velkonost.lume.fragments.ContactsFragment;

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.SURNAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_CONTACTS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER_ID;
import static ru.velkonost.lume.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Initializations.initSearch;
import static ru.velkonost.lume.Initializations.initToolbar;
import static ru.velkonost.lume.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.PhoneDataStorage.loadText;
import static ru.velkonost.lume.PhoneDataStorage.saveText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

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

    /**
     * Свойство - строка поиска.
     * {@link MaterialSearchView}
     */
    private MaterialSearchView searchView;
    private List<Contact> mContacts;
    private ContactsFragment mContactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Установка темы по умолчанию */
        setTheme(R.style.AppDefault);

        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        mGetData = new GetData();
        ids = new ArrayList<>();
        contacts = new HashMap<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(ContactsActivity.this, toolbar, R.string.app_name); /** Инициализация */
        initNavigationView(); /** Инициализация */

        /**
         * Инициализируем строку поиска.
         * {@link MaterialSearchView}
         * {@link Initializations#initSearch(Activity, MaterialSearchView)}
         **/
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        initSearch(this, searchView);

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(ContactsActivity.this, ID);

//        linLayout = (LinearLayout) findViewById(R.id.contactsContainer);
//        ltInflater = getLayoutInflater();

        mContacts = new ArrayList<>();

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
                    finishAffinity();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        /**
         * Устанавливает меню для строки поиска.
         */
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        /**
         * Вешает слушателя для открытия строки по нажатию.
         */
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown () {
                searchView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onSearchViewClosed() {
            }
        });
        return true;
    }

    /**
     * При нажатии на кнопку "Назад" поиск закрывется.
     */
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen())
            searchView.closeSearch();
        else
            super.onBackPressed();
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


    /**
     * Сортирует Map по значению.
     *
     * @param <K> Тип ключа.
     * @param <V> Тип значения.
     */
    class ValueComparator<K, V extends Comparable<V>> implements Comparator<K>{

        HashMap<K, V> map = new HashMap<>();

        /** Конструктор */
        public ValueComparator(HashMap<K, V> map){
            this.map.putAll(map);
        }

        /** Сортировка по значению сверху вниз */
        @Override
        public int compare(K s1, K s2) {
            return -map.get(s1).compareTo(map.get(s2));
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

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
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

                /**
                 * Заполнение Map{@link contacts} для последующей сортировки контактов.
                 *
                 * По умолчанию, идентификатору контакта соответствует его полное имя.
                 *
                 * Если такогого не имеется, то устанавливает взамен логин.
                 **/
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

                /** Создание и инициализация Comparator{@link ValueComparator} */
                Comparator<String> comparator = new ValueComparator<>((HashMap<String, String>) contacts);

                /** Помещает отсортированную Map */
                TreeMap<String, String> sortedContacts = new TreeMap<>(comparator);
                sortedContacts.putAll(contacts);

                /** "Обнуляет" хранилище идентификаторов */
                ids = new ArrayList<>();

                /** Заполняет хранилище идентификаторов */
                for (String key : sortedContacts.keySet()) {
                    ids.add(key);
                }

                /** "Поворачивает" хранилище идентификаторов */
                Collections.reverse(ids);

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < ids.size(); i++) {

                    /**
                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
                     */
                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));

                    mContacts.add(new Contact(userInfo.getString(ID), userInfo.getString(NAME),
                            userInfo.getString(SURNAME), userInfo.getString(LOGIN),
                            Integer.parseInt(userInfo.getString(AVATAR))));
//
//                    View userView = ltInflater.inflate(R.layout.item_contact_block, linLayout, false);
//                    View rl = userView.findViewById(R.id.relativeLayoutContact);
//
//                    /**
//                     * Установление идентификатора пользователя,
//                     * чтобы при нажатии на элемент проще было понять, профиль какого пользователя необходимо открыть.
//                     */
//                    rl.setId(Integer.parseInt(userInfo.getString(ID)));
//
//                    ImageView userAvatar = (ImageView) userView.findViewById(R.id.userAvatar); /** Аватар пользователя */
//
//                    /** Иконка, указывающая, что пользователь еще не указал свое польное имя */
//                    ImageView userWithoutName = (ImageView) userView.findViewById(R.id.userWithoutName);
//
//                    /** Полное имя пользователя, иначе его логин */
//                    TextView userName = (TextView) userView.findViewById(R.id.userName);
//                    TextView userLogin = (TextView) userView.findViewById(R.id.userLogin);
//
//                    /**
//                     * Установка имени владельца открытого профиля.
//                     *
//                     * Если имя и фамилия не найдены,
//                     * то устанавливается логин + показывается иконка {@link userWithoutName}
//                     **/
//                    String sUserName = userInfo.getString(NAME).length() == 0
//                            ? userInfo.getString(LOGIN)
//                            : userInfo.getString(SURNAME).length() == 0
//                            ? userInfo.getString(LOGIN)
//                            : userInfo.getString(NAME) + " " +  userInfo.getString(SURNAME);
//
//                    if (sUserName.equals(userInfo.getString(LOGIN)))
//                        userWithoutName.setImageResource(R.drawable.withoutname);
//                    else
//                        userLogin.setText(userInfo.getString(LOGIN));
//
//                    userName.setText(sUserName);

//                    /** Формирование адреса, по которому лежит аватар пользователя */
//                    String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
//                            + SERVER_AVATAR + SLASH + userInfo.getString(AVATAR)
//                            + SLASH + userInfo.getString(ID) + PNG;
//
//                    /**
//                     *  Загрузка и установка аватара.
//                     *  {@link ImageManager#fetchImage(String, ImageView)}
//                     * */
//                    fetchImage(avatarURL, userAvatar);
//                    Bitmap bitmap = ((BitmapDrawable)userAvatar.getDrawable()).getBitmap();
//                    userAvatar.setImageBitmap(getCircleMaskedBitmap(bitmap, 25));
//
//                    /** Добавление элемента в контейнер {@link SearchActivity#linLayout} */
//                    linLayout.addView(userView);
                }
//                mContactsFragment.setContacts(mContacts);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mContactsFragment =  ContactsFragment.getInstance(ContactsActivity.this, mContacts);
                ft.add(R.id.llcontact, mContactsFragment);
                ft.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
