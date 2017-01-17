package ru.velkonost.lume.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.Contact;
import ru.velkonost.lume.fragments.BoardColumnsTabsFragmentAdapter;

import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;

public class BoardColumnsActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_board_columns;

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


    private int boardId;

    /**
     * Идентификаторы досок, к которым принадлежит авторизованный пользователь.
     **/
    private ArrayList<String> ids;

    /**
     * Свойство - экзмепляр класса {@link BoardParticipantsActivity.GetData}
     */
//    protected BoardParticipantsActivity.GetData mGetData;


    /**
     * Свойство - список контактов.
     * {@link ru.velkonost.lume.descriptions.BoardParticipant}
     */
    private List<Contact> mBoardParticipants;

    /**
     * Контакты авторизованного пользователя.
     *
     * Ключ - идентификатор пользователя.
     * Значение - его полное имя или логин.
     **/
    private Map<String, String> contacts;

//    private BoardsFragment mBoardsFragment;

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 10;

    private ViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);


//        mGetData = new BoardParticipantsActivity.GetData();
        mBoardParticipants = new ArrayList<>();
        ids = new ArrayList<>();
        contacts = new HashMap<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_board_columns);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(BoardColumnsActivity.this, toolbar, R.string.menu_item_participants); /** Инициализация */

        initTabs();

        initNavigationView(); /** Инициализация */

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(BoardColumnsActivity.this, ID);

        Intent intent = getIntent();
        boardId = intent.getIntExtra(BOARD_ID, 0);

//        mGetData.execute();

    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPagerColumns);
        BoardColumnsTabsFragmentAdapter adapter
                = new BoardColumnsTabsFragmentAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_columns);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                /** Инициализируем намерение на следующую активность */
                switch (menuItem.getItemId()) {

                    /** Переход на профиль данного пользователя */
                    case R.id.navigationProfile:
                        nextIntent = new Intent(BoardColumnsActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(BoardColumnsActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(BoardColumnsActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        nextIntent = new Intent(BoardColumnsActivity.this, BoardsListActivity.class);
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(BoardColumnsActivity.this, SettingsActivity.class);
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(BoardColumnsActivity.this, ID);
                        nextIntent = new Intent(BoardColumnsActivity.this, WelcomeActivity.class);
                        break;
                }

                /**
                 * Переход на следующую активность.
                 * {@link Initializations#changeActivityCompat(Activity, Intent)}
                 * */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link Initializations#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardColumnsActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardColumnsActivity.this, ID).equals("")) finishAffinity();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_participant);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

//    private class GetData extends AsyncTask<Object, Object, String> {
//        @Override
//        protected String doInBackground(Object... strings) {
//
//            /**
//             * Формирование адреса, по которому необходимо обратиться.
//             **/
//            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
//                    + SERVER_GET_BOARD_PARTICIPANTS_METHOD;
//
//            /**
//             * Формирование отправных данных.
//             */
//            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId;
//
//            /** Свойство - код ответа, полученных от сервера */
//            String resultJson = "";
//
//            /**
//             * Соединяется с сервером, отправляет данные, получает ответ.
//             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
//             **/
//            try {
//                resultJson = getJSON(dataURL, params);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return resultJson;
//        }
//        protected void onPostExecute(String strJson) {
//            super.onPostExecute(strJson);
//
//            /** Свойство - полученный JSON–объект*/
//            JSONObject dataJsonObj;
//
//            try {
//
//                /**
//                 * Получение JSON-объекта по строке.
//                 */
//                dataJsonObj = new JSONObject(strJson);
//
//                /**
//                 * Получение идентификаторов найденных пользователей.
//                 */
//                JSONArray idsJSON = dataJsonObj.getJSONArray(IDS);
//
//                for (int i = 0; i < idsJSON.length(); i++){
//                    ids.add(idsJSON.getString(i));
//                }
//
//                /**
//                 * Заполнение Map{@link contacts} для последующей сортировки контактов.
//                 *
//                 * По умолчанию идентификатору контакта соответствует его полное имя.
//                 *
//                 * Если такогого не имеется, то устанавливает взамен логин.
//                 **/
//                for (int i = 0; i < ids.size(); i++){
//                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));
//
//                    contacts.put(
//                            ids.get(i),
//                            userInfo.getString(NAME).length() != 0
//                                    ? userInfo.getString(SURNAME).length() != 0
//                                    ? userInfo.getString(NAME) + " " + userInfo.getString(SURNAME)
//                                    : userInfo.getString(LOGIN) : userInfo.getString(LOGIN)
//                    );
//                }
//
//                /** Создание и инициализация Comparator{@link ValueComparator} */
//                Comparator<String> comparator = new ValueComparator<>((HashMap<String, String>) contacts);
//
//                /** Помещает отсортированную Map */
//                TreeMap<String, String> sortedContacts = new TreeMap<>(comparator);
//                sortedContacts.putAll(contacts);
//
//                /** "Обнуляет" хранилище идентификаторов */
//                ids = new ArrayList<>();
//
//                /** Заполняет хранилище идентификаторов */
//                for (String key : sortedContacts.keySet()) {
//                    ids.add(key);
//                }
//
//                /** "Поворачивает" хранилище идентификаторов */
//                Collections.reverse(ids);
//
//                /**
//                 * Составление view-элементов с краткой информацией о пользователях
//                 */
//                for (int i = 0; i < ids.size(); i++) {
//
//                    /**
//                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
//                     */
//                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));
//
//                    mBoardParticipants.add(new Contact(userInfo.getString(ID), userInfo.getString(NAME),
//                            userInfo.getString(SURNAME), userInfo.getString(LOGIN),
//                            Integer.parseInt(userInfo.getString(AVATAR))));
//                }
//
//                /**
//                 * Добавляем фрагмент на экран.
//                 * {@link ContactsFragment}
//                 */
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                BoardAllParticipantsFragment boardAllParticipantsFragment
//                        = BoardAllParticipantsFragment.getInstance(BoardParticipantsActivity.this, mBoardParticipants);
//                ft.add(R.id.llparticipants, boardAllParticipantsFragment);
//                ft.commit();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
