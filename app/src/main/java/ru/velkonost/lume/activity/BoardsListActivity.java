package ru.velkonost.lume.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.Board;
import ru.velkonost.lume.fragments.BoardsFragment;
import ru.velkonost.lume.fragments.MessagesFragment;

import static ru.velkonost.lume.Constants.BOARD_IDS;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_SHOW_BOARDS_METHOD;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class BoardsListActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_boards;

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
     * Идентификаторы досок, к которым принадлежит авторизованный пользователь.
     **/
    private ArrayList<String> bids;

    /**
     * Свойство - экзмепляр класса {@link GetBoards}
     */
    protected GetBoards mGetBoards;

    /**
     * Свойство - список контактов.
     * {@link Board}
     */
    private List<Board> mBoards;

    private BoardsFragment mBoardsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_boards);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(BoardsListActivity.this, toolbar, R.string.menu_item_messages); /** Инициализация */
        initNavigationView(); /** Инициализация */

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(BoardsListActivity.this, ID);

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
        navigationView.getMenu().getItem(3).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                /** Инициализируем намерение на следующую активность */
                switch (menuItem.getItemId()) {

                    /** Переход на профиль данного пользователя */
                    case R.id.navigationProfile:
                        nextIntent = new Intent(BoardsListActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(BoardsListActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(BoardsListActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(BoardsListActivity.this, SettingsActivity.class);
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(BoardsListActivity.this, ID);
                        nextIntent = new Intent(BoardsListActivity.this, WelcomeActivity.class);
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
                        changeActivityCompat(BoardsListActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardsListActivity.this, ID).equals("")) finishAffinity();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_boards);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    private class GetBoards extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_SHOW_BOARDS_METHOD;

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
                JSONArray idsJSON = dataJsonObj.getJSONArray(BOARD_IDS);

                for (int i = 0; i < idsJSON.length(); i++){
                    bids.add(idsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < bids.size(); i++) {

                    /**
                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
                     */
                    JSONObject boardInfo = dataJsonObj.getJSONObject(bids.get(i));

                    mBoards.add(new Board(
                            Integer.parseInt(bids.get(i)), boardInfo.getString(NAME)
                    ));
                }

                /**
                 * Добавляем фрагмент на экран.
                 * {@link MessagesFragment}
                 */
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mBoardsFragment
                        = BoardsFragment.getInstance(BoardsListActivity.this, mBoards);
                ft.add(R.id.llboards, mBoardsFragment);
                ft.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
