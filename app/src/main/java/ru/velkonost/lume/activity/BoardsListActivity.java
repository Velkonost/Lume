package ru.velkonost.lume.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

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

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BOARD_IDS;
import static ru.velkonost.lume.Constants.DESCRIPTION;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_BOARD_METHOD;
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

    private TimerCheckBoardsState timer;

    private FloatingActionButton addNewBoard;

    private String boardName;
    private String boardDescription;

    /**
     * Свойство - опинсание view-элемента, служащего для обновления страницы.
     **/
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        setTheme(R.style.AppTheme_Cursor);

        mGetBoards = new GetBoards();
        bids = new ArrayList<>();
        mBoards = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_boards);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(BoardsListActivity.this, toolbar, R.string.menu_item_boards); /** Инициализация */
        initNavigationView(); /** Инициализация */

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(BoardsListActivity.this, ID);


        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        /**
         *  Установка цветной палитры,
         *  цвета которой будут заменять друг друга в зависимости от прогресса.
         * */
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorMessageBackground,
                R.color.colorPrimary);

        /** Ставит обработчик событий */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override

            public void onRefresh() {
                /** Выполнение происходит с задержкой в 2.5 секунды */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link Initializations#changeActivityCompat(Activity)}
                         * */
                        changeActivityCompat(BoardsListActivity.this);
                    }
                }, 2500);
            }
        });

        mGetBoards.execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timer = new TimerCheckBoardsState(100000000, 10000);
                timer.start();

            }
        }, 10000);

    }

    public void createBoardOnClick(View view) {

        LinearLayout layout = new LinearLayout(BoardsListActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams  params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp2px(5), dp2px(20), dp2px(5), dp2px(20));

        AlertDialog.Builder builder = new AlertDialog.Builder(BoardsListActivity.this);
        builder.setTitle(getResources().getString(R.string.create_board));

        final EditText inputName
                = (EditText) getLayoutInflater().inflate(R.layout.item_edittext_style, null);
        inputName.setTextColor(ContextCompat.getColor(BoardsListActivity.this, R.color.colorBlack));
        inputName.setLayoutParams(params);

        inputName.setHint(getResources().getString(R.string.enter_board_name));
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(inputName);

        final EditText inputDesc
                = (EditText) getLayoutInflater().inflate(R.layout.item_edittext_style, null);
        inputDesc.setLayoutParams(params);

        inputDesc.setTextColor(ContextCompat.getColor(BoardsListActivity.this, R.color.colorBlack));
        inputDesc.setHint(getResources().getString(R.string.enter_board_description));
        layout.addView(inputDesc);


        builder.setView(layout)
                .setPositiveButton(getResources().getString(R.string.create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boardName = inputName.getText().toString();
                        boardDescription = inputDesc.getText().toString();

                        if (boardName.length() != 0) {

                            AddBoard addBoard = new AddBoard();
                            addBoard.execute();

                            changeActivityCompat(BoardsListActivity.this);

                        } else dialog.cancel();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                BoardsListActivity.this.getResources().getDisplayMetrics());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) timer.cancel();
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
        navigationView.getMenu().getItem(4).setChecked(true);

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
                        nextIntent = new Intent(BoardsListActivity.this, BoardsListActivity.class);
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


    public class TimerCheckBoardsState extends CountDownTimer {

        TimerCheckBoardsState(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            RefreshBoards mRefreshBoards = new RefreshBoards();
            mRefreshBoards.execute();
        }

        @Override
        public void onFinish() {
        }
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
                    String boardName = dataJsonObj.getString(bids.get(i));
                    mBoards.add(new Board(
                            Integer.parseInt(bids.get(i)), boardName
                    ));
                }

                /**
                 * Добавляем фрагмент на экран.
                 * {@link BoardsFragment}
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
    private class RefreshBoards extends AsyncTask<Object, Object, String> {
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
                    if (!bids.contains(idsJSON.getString(i)))
                        bids.add(idsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < bids.size(); i++) {
                    boolean exist = false;

                    /**
                     * Получение JSON-объекта с информацией о конкретном сообщении по его идентификатору.
                     */
                    String boardName = dataJsonObj.getString(bids.get(i));

                    for (int j = 0; j < mBoards.size(); j++){
                        if (mBoards.get(j).getId() == Integer.parseInt(bids.get(i))) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist)
                        mBoards.add(new Board(
                                Integer.parseInt(bids.get(i)), boardName
                        ));
                }

                /**
                 * Добавляем фрагмент на экран.
                 * {@link BoardsFragment}
                 */
                if(!isFinishing()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    mBoardsFragment.refreshBoards(mBoards);
                    ft.replace(R.id.llboards, mBoardsFragment);
                    ft.commitAllowingStateLoss();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class AddBoard extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_ADD_BOARD_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = NAME + EQUALS + boardName
                    + AMPERSAND + DESCRIPTION + EQUALS + boardDescription
                    + AMPERSAND + ID + EQUALS + userId;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяетс я с сервером, отправляет данные, получает ответ.
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
        }
    }


}
