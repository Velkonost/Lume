package ru.velkonost.lume.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.Depository;
import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.BoardColumn;
import ru.velkonost.lume.descriptions.BoardParticipant;
import ru.velkonost.lume.fragments.BoardDescriptionFragment;
import ru.velkonost.lume.fragments.BoardParticipantsFragment;
import ru.velkonost.lume.fragments.BoardWelcomeColumnFragment;

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BOARD_DESCRIPTION;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.BOARD_LAST_CONTRIBUTED_USER;
import static ru.velkonost.lume.Constants.BOARD_NAME;
import static ru.velkonost.lume.Constants.COLUMN_IDS;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_BOARD_INFO_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER_IDS;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.saveText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class BoardWelcomeActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_board_welcome;

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
//    private ArrayList<String> bids;

    /**
     * Свойство - экзмепляр класса {@link GetBoardInfo}
     */
    protected GetBoardInfo mGetBoardInfo;


    /**
     * Свойство - список контактов.
     * {@link ru.velkonost.lume.descriptions.BoardParticipant}
     */
    private List<BoardParticipant> mBoardParticipants;

    private List<BoardColumn> mBoardColumns;

//    private BoardsFragment mBoardsFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        mGetBoardInfo = new GetBoardInfo();
        mBoardParticipants = new ArrayList<>();
        mBoardColumns = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_board_welcome);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(BoardWelcomeActivity.this, toolbar, R.string.menu_item_boards); /** Инициализация */
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
        userId = loadText(BoardWelcomeActivity.this, ID);

        Intent intent = getIntent();
        boardId = intent.getIntExtra(BOARD_ID, 0);

        mGetBoardInfo.execute();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_welcome);
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
                        nextIntent = new Intent(BoardWelcomeActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(BoardWelcomeActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(BoardWelcomeActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        nextIntent = new Intent(BoardWelcomeActivity.this, BoardsListActivity.class);
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(BoardWelcomeActivity.this, SettingsActivity.class);
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(BoardWelcomeActivity.this, ID);
                        nextIntent = new Intent(BoardWelcomeActivity.this, WelcomeActivity.class);
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
                        changeActivityCompat(BoardWelcomeActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardWelcomeActivity.this, ID).equals("")) finishAffinity();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_welcome);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    private class GetBoardInfo extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_BOARD_INFO_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId;

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
                JSONArray idsJSON = dataJsonObj.getJSONArray(USER_IDS);
                JSONArray cidsJSON = dataJsonObj.getJSONArray(COLUMN_IDS);

                String boardName = dataJsonObj.getString(BOARD_NAME);
                String boardDescription = dataJsonObj.getString(BOARD_DESCRIPTION);

                toolbar.setTitle(boardName);
                saveText(BoardWelcomeActivity.this, BOARD_NAME, boardName);

                ArrayList<String> uids = new ArrayList<>();
                ArrayList<String> cids = new ArrayList<>();

                for (int i = 0; i < idsJSON.length(); i++) {
                    uids.add(idsJSON.getString(i));
                }

                for (int i = 0; i < cidsJSON.length(); i++) {
                    cids.add(cidsJSON.getString(i));
                }


                for (int i = 0; i < uids.size(); i++) {
                    String participantId = uids.get(i);

                    JSONObject userInfo = dataJsonObj.getJSONObject(participantId);

                    mBoardParticipants.add(new BoardParticipant(
                            Integer.parseInt(participantId.substring(0, uids.get(i).length() - 4)),
                            Integer.parseInt(userInfo.getString(AVATAR)),
                            userInfo.getString(LOGIN),
                            BOARD_LAST_CONTRIBUTED_USER == i + 1, uids.size() - i, boardId
                    ));

                    if (BOARD_LAST_CONTRIBUTED_USER == i) break;

                }

                for (int i = 0; i < cids.size(); i++) {
                    JSONObject columnInfo = dataJsonObj.getJSONObject(cids.get(i));

                    mBoardColumns.add(new BoardColumn(
                            Integer.parseInt(columnInfo.getString(ID)), columnInfo.getString(NAME))
                    );

                }

                Depository.setBoardColumns(mBoardColumns);

                saveText(BoardWelcomeActivity.this, BOARD_DESCRIPTION, boardDescription);

                BoardDescriptionFragment descriptionFragment = new BoardDescriptionFragment();
                BoardParticipantsFragment boardParticipantsFragment
                        = BoardParticipantsFragment.getInstance(BoardWelcomeActivity.this, mBoardParticipants);
                BoardWelcomeColumnFragment boardWelcomeColumnFragment
                        = BoardWelcomeColumnFragment.getInstance(BoardWelcomeActivity.this, mBoardColumns);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.add(R.id.descriptionContainer, descriptionFragment);
                transaction.add(R.id.participantsContainer, boardParticipantsFragment);
                transaction.add(R.id.columnsContainer, boardWelcomeColumnFragment);
                transaction.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
