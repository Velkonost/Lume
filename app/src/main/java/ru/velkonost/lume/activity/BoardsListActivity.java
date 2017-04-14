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
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Managers.InitializationsManager;
import ru.velkonost.lume.Managers.PhoneDataStorageManager;
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.R;
import ru.velkonost.lume.model.Board;
import ru.velkonost.lume.fragments.BoardsFragment;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BOARD_IDS;
import static ru.velkonost.lume.Constants.DESCRIPTION;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_BOARD_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_SHOW_BOARDS_METHOD;
import static ru.velkonost.lume.Constants.USER_PLACE_LIVING;
import static ru.velkonost.lume.Constants.USER_PLACE_STUDY;
import static ru.velkonost.lume.Constants.USER_PLACE_WORK;
import static ru.velkonost.lume.Constants.USER_WORKING_EMAIL;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

/**
 * @author Velkonost
 *
 * Класс, описывающий список досок, в которых состоит пользователь
 *
 */
public class BoardsListActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_boards;

    /**
     * Свойство - описание верхней панели инструментов приложения.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Свойство - описание {@link SearchActivity#LAYOUT}
     */
    @BindView(R.id.activity_boards)
    DrawerLayout drawerLayout;

    /**
     * Свойство - боковая панель навигации
     */
    @BindView(R.id.navigation)
    NavigationView navigationView;

    /**
     * Свойство - элемент, символизирующий загрузку данных
     */
    @BindView(R.id.loadingDots)
    LoadingDots loadingDots;

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
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

    /**
     * Свойство - список досок.
     * {@link Board}
     */
    private List<Board> mBoards;

    private BoardsFragment mBoardsFragment;

    private TimerCheckBoardsState timer;

    /**
     * Свойство - название доски
     */
    private String boardName;

    /**
     * Свойство - описание доски
     */
    private String boardDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBase();
        getData();
        initialize();

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        executeTasks();
        startTimer();
    }

    /**
     * Установка первоначальных настроек активности
     */
    private void setBase() {
        setContentView(LAYOUT);
        ButterKnife.bind(this);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");
    }

    /**
     * Инитиализация основных элементов
     */
    private void initialize() {
        mGetBoards = new GetBoards();
        bids = new ArrayList<>();
        mBoards = new ArrayList<>();

        /** {@link InitializationsManager#initToolbar(Toolbar, int)}  */
        initToolbar(BoardsListActivity.this, toolbar, R.string.menu_item_boards); /** Инициализация */
        initNavigationView(); /** Инициализация */
    }

    /**
     * Получение данных (отсутствует получение с интернета)
     */
    private void getData() {
        getFromFile();
    }

    /**
     * Вызов процессов, происходящих в параллельных потоках
     */
    private void executeTasks() {
        mGetBoards.execute();
    }

    /**
     * Получение данных из специального файла приложения
     */
    private void getFromFile() {

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorageManager#loadText(Context, String)}
         **/
        userId = loadText(BoardsListActivity.this, ID);

    }

    /**
     * Запуск таймера
     */
    private void startTimer() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timer = new TimerCheckBoardsState(100000000, 10000);
                timer.start();

            }
        }, 10000);

    }

    /**
     * Настройка поля для редактирования названия доски
     * @param inputName - поле для редактирования названия доски
     * @param params - layout-параметры для установки
     */
    private void setInputNameParams(EditText inputName, LinearLayout.LayoutParams  params) {

        inputName.setTextColor(ContextCompat.getColor(BoardsListActivity.this, R.color.colorBlack));
        inputName.setLayoutParams(params);
        inputName.setHint(getResources().getString(R.string.enter_board_name));
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);

    }

    /**
     * Настройка поля для редактирования описания доски
     * @param inputDesc - поле для редактирования описания доски
     * @param params - layout-параметры для установки
     */
    private void setInputDescParams(EditText inputDesc, LinearLayout.LayoutParams  params) {

        inputDesc.setLayoutParams(params);

        inputDesc.setTextColor(ContextCompat.getColor(BoardsListActivity.this, R.color.colorBlack));
        inputDesc.setHint(getResources().getString(R.string.enter_board_description));

    }

    /**
     * Слушатель для открытия диалогового окна, из которого создается новая доска
     * @param view
     */
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

        final EditText inputDesc
                = (EditText) getLayoutInflater().inflate(R.layout.item_edittext_style, null);

        setInputNameParams(inputName, params);
        setInputDescParams(inputDesc, params);

        layout.addView(inputName);
        layout.addView(inputDesc);

        builder.setView(layout)
                .setPositiveButton(getResources().getString(R.string.create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boardName = inputName.getText().toString();
                        boardDescription = inputDesc.getText().toString();

                        if (boardName.length() != 0) new AddBoard().execute();
                        else dialog.cancel();

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

    /**
     * Конвертер из dp в px
     *
     * @param dp - значения в dp
     * @return - значение в px
     */
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
     * Скрытие клавиатуры
     */
    private void hideKeyBoard() {

        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        getCurrentFocus().clearFocus();

    }

    private ActionBarDrawerToggle initializeToggle() {
        return new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.view_navigation_open, R.string.view_navigation_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyBoard();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideKeyBoard();
            }
        };
    }

    /**
     * Инициализация заголовка боковой панели
     */
    private void initializeNavHeader() {
        View header = navigationView.getHeaderView(0);
        initializeNavHeaderLogin(header);
        initializeNavHeaderAskQuestion(header);
    }

    /**
     * Инициализация элемента в заголовке боковой панели
     * @param header - заголовок боковой панели
     */
    private void initializeNavHeaderAskQuestion(View header) {

        ImageView askQuestion = ButterKnife.findById(header, R.id.askQuestion);

        askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardsListActivity.this,
                                new Intent(BoardsListActivity.this, FAQBotActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    /**
     * Инициализация элемента в заголовке боковой панели
     * @param header - заголовок боковой панели
     */
    private void initializeNavHeaderLogin(View header) {

        TextView navHeaderLogin = ButterKnife.findById(header, R.id.userNameHeader);
        navHeaderLogin.setText(loadText(BoardsListActivity.this, LOGIN));

        navHeaderLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardsListActivity.this,
                                new Intent(BoardsListActivity.this, ProfileActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);

            }
        });

    }

    /**
     * Установка слушателя на боковую панель
     */
    private void setNavigationViewListener() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                /** Инициализируем намерение на следующую активность */
                switch (menuItem.getItemId()) {

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(BoardsListActivity.this, ContactsActivity.class);
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
                 * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                 * */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (loadText(BoardsListActivity.this, ID).equals("")) {
                            deleteText(BoardsListActivity.this, USER_PLACE_LIVING);
                            deleteText(BoardsListActivity.this, USER_PLACE_STUDY);
                            deleteText(BoardsListActivity.this, USER_PLACE_WORK);
                            deleteText(BoardsListActivity.this, USER_WORKING_EMAIL);
                        }

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardsListActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardsListActivity.this, ID).equals("")) finishAffinity();

                drawerLayout.closeDrawer(GravityCompat.START);

                return false;
            }
        });
    }

    /**
     * Инициализация боковой панели навигации.
     **/
    private void initNavigationView() {

        ActionBarDrawerToggle toggle = initializeToggle();
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        initializeNavHeader();
        setNavigationViewListener();
    }

    /**
     * Таймер для обновления состояния списка досок
     */
    private class TimerCheckBoardsState extends CountDownTimer {

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

    /**
     * Получение информации о досках
     */
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

                loadingDots.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Обновление состояния списка досок
     */
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

    /**
     * Добавление новой доски
     */
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
            new RefreshBoards().execute();
        }
    }


}
