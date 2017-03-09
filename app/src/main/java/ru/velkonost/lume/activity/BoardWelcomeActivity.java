package ru.velkonost.lume.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eyalbira.loadingdots.LoadingDots;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Depository;
import ru.velkonost.lume.Managers.InitializationsManager;
import ru.velkonost.lume.Managers.PhoneDataStorageManager;
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.Managers.ValueComparatorManager;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.BoardInviteListAdapter;
import ru.velkonost.lume.descriptions.BoardColumn;
import ru.velkonost.lume.descriptions.BoardParticipant;
import ru.velkonost.lume.descriptions.Contact;
import ru.velkonost.lume.fragments.BoardDescriptionFragment;
import ru.velkonost.lume.fragments.BoardParticipantsFragment;
import ru.velkonost.lume.fragments.BoardWelcomeColumnFragment;
import ru.velkonost.lume.fragments.BoardsFragment;

import static android.widget.ListPopupWindow.WRAP_CONTENT;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BOARD_DESCRIPTION;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.BOARD_LAST_CONTRIBUTED_USER;
import static ru.velkonost.lume.Constants.BOARD_NAME;
import static ru.velkonost.lume.Constants.COLUMN_IDS;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.IDS;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.SURNAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_COLUMN_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_CHANGE_BOARD_SETTINGS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_BOARD_INFO_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_CONTACTS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_LEAVE_BOARD_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER_IDS;
import static ru.velkonost.lume.Managers.HtmlConverterManager.fromHtml;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.saveText;
import static ru.velkonost.lume.R.layout.popup_board_invite_list;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class BoardWelcomeActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_board_welcome;

    /**
     * Свойство - описание верхней панели инструментов приложения.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Свойство - описание {@link SearchActivity#LAYOUT}
     */
    @BindView(R.id.activity_board_welcome)
    DrawerLayout drawerLayout;

    @BindView(R.id.editBoardName)
    EditText editBoardName;

    @BindView(R.id.navigation)
    NavigationView navigationView;

    @BindView(R.id.loadingDots)
    LoadingDots loadingDots;


    private RecyclerView recyclerView;
    private View popupView;
    public static PopupWindow popupWindowBoardInvite;

    /**
     * Свойство - идентификатор пользователя, авторизованного на данном устройстве.
     */
    private String userId;


    /**
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

    private int boardId;

    /**
     * Свойство - экзмепляр класса {@link GetBoardInfo}
     */
    protected GetBoardInfo mGetBoardInfo;


    private GetContacts mGetContacts;

    /**
     * Свойство - список контактов.
     * {@link ru.velkonost.lume.descriptions.BoardParticipant}
     */
    private List<BoardParticipant> mBoardParticipants;

    private List<BoardColumn> mBoardColumns;

    private List<Contact> mContacts;

    /**
     * Идентификаторы пользователей, некоторые данные которых соответствуют искомой информации.
     **/
    private ArrayList<String> ids;

    /**
     * Контакты авторизованного пользователя.
     *
     * @Ключ - идентификатор пользователя.
     * @Значение - его полное имя или логин.
     **/
    private Map<String, String> contacts;
    private ArrayList<String> cids;

    private ArrayList<String> uids;

    private boolean invitePerson;

    private String boardName;
    private String columnName;
    private String boardDescription;

    private BoardDescriptionFragment descriptionFragment;
    private BoardWelcomeColumnFragment boardWelcomeColumnFragment;
    private BoardParticipantsFragment boardParticipantsFragment;

    private Menu menu;

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

    }

    private void setBase() {

        setContentView(LAYOUT);
        ButterKnife.bind(this);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

    }

    private void getData() {
        getFromFile();
        getExtras();
    }

    private void getFromFile() {
        /**
         * Получение id пользователя.
         * {@link PhoneDataStorageManager#loadText(Context, String)}
         **/
        userId = loadText(BoardWelcomeActivity.this, ID);
    }

    private void getExtras() {
        Intent intent = getIntent();
        boardId = intent.getIntExtra(BOARD_ID, 0);
    }

    private void initialize() {

        mGetBoardInfo = new GetBoardInfo();
        mGetContacts = new GetContacts();

        mBoardParticipants = new ArrayList<>();
        mBoardColumns = new ArrayList<>();
        mContacts = new ArrayList<>();
        ids = new ArrayList<>();
        contacts = new HashMap<>();
        cids = new ArrayList<>();
        uids = new ArrayList<>();

        /** {@link InitializationsManager#initToolbar(Toolbar, int)}  */
        initToolbar(BoardWelcomeActivity.this, toolbar, R.string.menu_item_boards); /** Инициализация */
        initNavigationView(); /** Инициализация */
        initializePopups();

    }

    private void initializePopups() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        initializePopupInvite(layoutInflater, height);

    }

    private void initializePopupInvite(LayoutInflater layoutInflater, int height) {

        popupView = layoutInflater.inflate(popup_board_invite_list, null);

        popupWindowBoardInvite = new PopupWindow(popupView,
                WRAP_CONTENT, height - dp2px(120));

        recyclerView = ButterKnife.findById(popupView, R.id.recyclerViewBoardInvite);

    }

    private void executeTasks() {
        mGetBoardInfo.execute();
        mGetContacts.execute();
    }

    private void setInputParams(EditText input, LinearLayout.LayoutParams  params) {

        input.setTextColor(ContextCompat.getColor(BoardWelcomeActivity.this, R.color.colorBlack));
        input.setLayoutParams(params);

        input.setHint(getResources().getString(R.string.enter_board_name));
        input.setInputType(InputType.TYPE_CLASS_TEXT);

    }

    public void addColumnOnClick(View view) {

        LinearLayout layout = new LinearLayout(BoardWelcomeActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams  params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp2px(5), dp2px(20), dp2px(5), dp2px(20));

        android.support.v7.app.AlertDialog.Builder builder
                = new android.support.v7.app.AlertDialog.Builder(BoardWelcomeActivity.this);
        builder.setTitle(getResources().getString(R.string.create_board));

        final EditText input
                = (EditText) getLayoutInflater().inflate(R.layout.item_edittext_style, null);

        setInputParams(input, params);
        layout.addView(input);

        builder.setView(layout)
                .setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        columnName = input.getText().toString();

                        if (columnName.length() != 0) {
                            invitePerson = false;
                            new AddColumn().execute();

                        } else dialog.cancel();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();


    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                BoardWelcomeActivity.this.getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            changeActivityCompat(BoardWelcomeActivity.this,
                    new Intent(BoardWelcomeActivity.this, BoardsListActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_welcome, menu);

        this.menu = menu;
        return true;
    }

    private void showAgreeMenu() {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_invite).setVisible(false);
        menu.findItem(R.id.action_leave).setVisible(false);
        menu.findItem(R.id.action_agree).setVisible(true);
    }

    private void hideAgreeMenu() {

        menu.findItem(R.id.action_settings).setVisible(true);
        menu.findItem(R.id.action_invite).setVisible(true);
        menu.findItem(R.id.action_leave).setVisible(true);
        menu.findItem(R.id.action_agree).setVisible(false);

    }

    private void hideKeyBoard() {

        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        getCurrentFocus().clearFocus();

    }

    private void setAgreeMenuListener() {
        menu.findItem(R.id.action_agree).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                boardName = editBoardName.getText().toString();
                boardDescription = descriptionFragment.getText();

                toolbar.setTitle(boardName);
                descriptionFragment.changeText();
                descriptionFragment.showNext();

                editBoardName.setVisibility(View.INVISIBLE);

                hideAgreeMenu();
                new ChangeBoardSettings().execute();

                hideKeyBoard();

                return false;
            }
        });
    }

    private void showPopupInvite() {

        popupWindowBoardInvite.setTouchable(true);
        popupWindowBoardInvite.setFocusable(true);
        popupWindowBoardInvite.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(android.R.color.transparent)));
        popupWindowBoardInvite.setOutsideTouchable(true);

        popupWindowBoardInvite.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

                toolbar.setTitle("");
                editBoardName.setVisibility(View.VISIBLE);
                editBoardName.setText(boardName);

                descriptionFragment.showNext();

                showAgreeMenu();
                setAgreeMenuListener();

                break;
            case R.id.action_invite:

                popupWindowBoardInvite.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        invitePerson = true;
                        new RefreshBoardInfo().execute();
                    }
                });

                showPopupInvite();

                break;
            case R.id.action_leave:

                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.leave_board))
                        .setMessage(getResources().getString(R.string.ask_confirmation))
                        .setCancelable(true)
                        .setNegativeButton(getResources().getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton(getResources().getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new LeaveBoard().execute();

                                        changeActivityCompat(BoardWelcomeActivity.this,
                                                new Intent(BoardWelcomeActivity.this,
                                                        BoardsListActivity.class));
                                        finishAffinity();
                                    }
                                })
                        .create().show();

                break;
        }

        return super.onOptionsItemSelected(item);
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

    private void initializeNavHeader() {
        View header = navigationView.getHeaderView(0);
        initializeNavHeaderLogin(header);
        initializeNavHeaderAskQuestion(header);
    }

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
                        changeActivityCompat(BoardWelcomeActivity.this,
                                new Intent(BoardWelcomeActivity.this, FAQBotActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    private void initializeNavHeaderLogin(View header) {

        TextView navHeaderLogin = ButterKnife.findById(header, R.id.userNameHeader);
        navHeaderLogin.setText(loadText(BoardWelcomeActivity.this, LOGIN));

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
                        changeActivityCompat(BoardWelcomeActivity.this,
                                new Intent(BoardWelcomeActivity.this, ProfileActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);

            }
        });

    }

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
                        nextIntent = new Intent(BoardWelcomeActivity.this, ContactsActivity.class);
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
                 * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                 * */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardWelcomeActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardWelcomeActivity.this, ID).equals("")) finishAffinity();

                drawerLayout.closeDrawer(GravityCompat.START);

                return false;
            }
        });
    }

    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {

        ActionBarDrawerToggle toggle = initializeToggle();
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        initializeNavHeader();
        setNavigationViewListener();
    }

    private class ChangeBoardSettings extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CHANGE_BOARD_SETTINGS_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId
                    + AMPERSAND + BOARD_NAME + EQUALS + boardName
                    + AMPERSAND + BOARD_DESCRIPTION + EQUALS + boardDescription;

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
        }
    }
    private class LeaveBoard extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_LEAVE_BOARD_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId
                    + AMPERSAND + ID + EQUALS + userId;

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
        }
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

                boardName = dataJsonObj.getString(BOARD_NAME);
                boardDescription = dataJsonObj.getString(BOARD_DESCRIPTION);

                toolbar.setTitle(fromHtml(boardName));
                saveText(BoardWelcomeActivity.this, BOARD_NAME, boardName);


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
                }

                for (int i = 0; i < cids.size(); i++) {
                    JSONObject columnInfo = dataJsonObj.getJSONObject(cids.get(i));

                    mBoardColumns.add(new BoardColumn(
                            Integer.parseInt(columnInfo.getString(ID)),
                            columnInfo.getString(NAME),  i)
                    );
                }

                Depository.setBoardColumns(mBoardColumns);
                Depository.setBoardId(String.valueOf(boardId));

                saveText(BoardWelcomeActivity.this, BOARD_DESCRIPTION, boardDescription);

                descriptionFragment = new BoardDescriptionFragment();
                boardParticipantsFragment
                        = BoardParticipantsFragment.getInstance(BoardWelcomeActivity.this,
                        mBoardParticipants);
                boardWelcomeColumnFragment
                        = BoardWelcomeColumnFragment.getInstance(BoardWelcomeActivity.this,
                        mBoardColumns, String.valueOf(boardId));

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.add(R.id.descriptionContainer, descriptionFragment);
                transaction.add(R.id.participantsContainer, boardParticipantsFragment);
                transaction.add(R.id.columnsContainer, boardWelcomeColumnFragment);
                transaction.commit();

                loadingDots.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RefreshBoardInfo extends AsyncTask<Object, Object, String> {
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


                for (int i = 0; i < idsJSON.length(); i++){
                    if (!uids.contains(idsJSON.getString(i)))
                        uids.add(idsJSON.getString(i));
                }

                for (int i = 0; i < cidsJSON.length(); i++){
                    if (!cids.contains(cidsJSON.getString(i)))
                        cids.add(cidsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < uids.size(); i++) {
                    boolean exist = false;


                    /**
                     * Получение JSON-объекта с информацией о конкретном сообщении по его идентификатору.
                     */
                    String participantId = uids.get(i);
                    JSONObject userInfo = dataJsonObj.getJSONObject(participantId);


                    for (int j = 0; j < mBoardParticipants.size(); j++){
                        if (mBoardParticipants.get(j).getId()
                                == Integer.parseInt(participantId
                                .substring(0, uids.get(i).length() - 4))) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist)
                        mBoardParticipants.add(new BoardParticipant(
                                Integer.parseInt(participantId.substring(0, uids.get(i).length() - 4)),
                                Integer.parseInt(userInfo.getString(AVATAR)),
                                userInfo.getString(LOGIN),
                                BOARD_LAST_CONTRIBUTED_USER == i + 1, uids.size() - i, boardId
                        ));
                }


                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < cids.size(); i++) {
                    boolean exist = false;

                    /**
                     * Получение JSON-объекта с информацией о конкретном сообщении по его идентификатору.
                     */
                    JSONObject columnInfo = dataJsonObj.getJSONObject(cids.get(i));


                    for (int j = 0; j < mBoardColumns.size(); j++){
                        if (mBoardColumns.get(j).getId() == Integer.parseInt(cids.get(i))) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist)
                        mBoardColumns.add(new BoardColumn(
                                Integer.parseInt(columnInfo.getString(ID)),
                                columnInfo.getString(NAME),  i)
                        );
                }

                /**
                 * Добавляем фрагмент на экран.
                 * {@link BoardsFragment}
                 */
                if(!isFinishing()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    if (!invitePerson)
                        boardWelcomeColumnFragment.refreshColumns(mBoardColumns);
                    else
                        boardParticipantsFragment.refreshParticipants(mBoardParticipants);

                    ft.replace(R.id.participantsContainer, boardParticipantsFragment);
                    ft.replace(R.id.columnsContainer, boardWelcomeColumnFragment);
                    ft.commitAllowingStateLoss();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class GetContacts extends AsyncTask<Object, Object, String> {
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
                JSONArray idsJSON = dataJsonObj.getJSONArray(IDS);

                for (int i = 0; i < idsJSON.length(); i++){
                    ids.add(idsJSON.getString(i));
                }

                /**
                 * Заполнение Map{@link contacts} для последующей сортировки контактов.
                 *
                 * По умолчанию идентификатору контакта соответствует его полное имя.
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

                /** Создание и инициализация Comparator{@link ValueComparatorManager} */
                Comparator<String> comparator = new ValueComparatorManager<>((HashMap<String, String>) contacts);

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
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(BoardWelcomeActivity.this));
                recyclerView.setAdapter(new BoardInviteListAdapter(BoardWelcomeActivity.this, mContacts, boardId));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class AddColumn extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_ADD_COLUMN_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId
                    + AMPERSAND + NAME + EQUALS + columnName;

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
            new RefreshBoardInfo().execute();
        }
    }

}
