package ru.velkonost.lume.Activities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.Managers.ValueComparator;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.SearchContact;
import ru.velkonost.lume.fragments.SearchFragment;

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.CITY;
import static ru.velkonost.lume.Constants.COUNTRY;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.IDS;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.SEARCH;
import static ru.velkonost.lume.Constants.STUDY;
import static ru.velkonost.lume.Constants.SURNAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_SEARCH_METHOD;
import static ru.velkonost.lume.Constants.USER_ID;
import static ru.velkonost.lume.Constants.WORK;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initSearch;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.saveText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;


/**
 * @author Velkonost
 *
 * Класс, описывающий процесс поиска пользователей.
 *
 */
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
     * Свойство - информация, по которой собирается искать пользователь.
     **/
    private String whatSearch;

    /**
     * Идентификаторы пользователей, некоторые данные которых соответствуют искомой информации.
     **/
    private ArrayList<String> ids;

    /**
     * Найденные контакты.
     *
     * Ключ - идентификатор пользователя.
     * Значение - его полное имя или логин.
     **/
    private Map<String, String> searchContacts;

    /**
     * Свойство - экзмепляр класса {@link GetData}
     */
    protected GetData mGetData;

    /**
     * Свойство - строка поиска.
     * {@link MaterialSearchView}
     */
    private MaterialSearchView searchView;

    private List<SearchContact> mSearchContacts;
    SearchFragment mSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Установка темы по умолчанию */
        setTheme(R.style.AppDefault);

        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        mGetData = new GetData();
        ids = new ArrayList<>();
        searchContacts = new HashMap<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(SearchActivity.this, toolbar, R.string.app_name); /** Инициализация */
        initNavigationView(); /** Инициализация */

        /**
         * Инициализируем строку поиска.
         * {@link MaterialSearchView}
         * {@link Initializations#initSearch(Activity, MaterialSearchView)}
         **/
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        initSearch(this, searchView);

        /**
         * Получение данных, которые вводил пользователь.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        whatSearch = loadText(SearchActivity.this, SEARCH);

//        TextView textView = (TextView) findViewById(R.id.toSearch);
//        textView.setText(whatSearch);
        mSearchContacts = new ArrayList<>();

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
                JSONArray idsJSON = dataJsonObj.getJSONArray(IDS);

                /**
                 * Исключение возможности пользователя найти свой же профиль.
                 */
                for (int i = 0; i < idsJSON.length(); i++){
                    if (!idsJSON.getString(i).equals(loadText(SearchActivity.this, ID)))
                        ids.add(idsJSON.getString(i));
                }

                /**
                 * Заполнение Map{@link searchContacts} для последующей сортировки контактов.
                 *
                 * По умолчанию идентификатору контакта соответствует его полное имя.
                 *
                 * Если такогого не имеется, то устанавливает взамен логин.
                 **/
                for (int i = 0; i < ids.size(); i++){
                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));

                    searchContacts.put(
                            ids.get(i),
                            userInfo.getString(NAME).length() != 0
                                    ? userInfo.getString(SURNAME).length() != 0
                                    ? userInfo.getString(NAME) + " " + userInfo.getString(SURNAME)
                                    : userInfo.getString(LOGIN) : userInfo.getString(LOGIN)
                    );
                }

                /** Создание и инициализация Comparator{@link ValueComparator} */
                Comparator<String> comparator = new ValueComparator<>((HashMap<String, String>) searchContacts);

                /** Помещает отсортированную Map */
                TreeMap<String, String> sortedContacts = new TreeMap<>(comparator);
                sortedContacts.putAll(searchContacts);

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

                    mSearchContacts.add(new SearchContact(userInfo.getString(ID), userInfo.getString(NAME),
                            userInfo.getString(SURNAME), userInfo.getString(LOGIN),
                            userInfo.getString(CITY), userInfo.getString(COUNTRY),
                            userInfo.getString(STUDY), userInfo.getString(WORK),
                            Integer.parseInt(userInfo.getString(AVATAR))));
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mSearchFragment =  SearchFragment.getInstance(SearchActivity.this, mSearchContacts);
                ft.add(R.id.llsearch, mSearchFragment);
                ft.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}