package ru.velkonost.lume.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Depository;
import ru.velkonost.lume.Managers.InitializationsManager;
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.BoardColumn;
import ru.velkonost.lume.fragments.BoardColumnsTabsFragmentAdapter;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.BOARD_NAME;
import static ru.velkonost.lume.Constants.COLUMN_IDS;
import static ru.velkonost.lume.Constants.COLUMN_ORDER;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.MAX_COLUMNS_IN_FIXED_MODE;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.POSITION;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_COLUMN_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_CHANGE_COLUMN_SETTINGS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_BOARD_INFO_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER_IDS;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;
import static ru.velkonost.lume.fragments.BoardColumnsTabsFragmentAdapter.last;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class BoardColumnsActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_board_columns;

    /**
     * Свойство - описание верхней панели инструментов приложения.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Свойство - описание {@link SearchActivity#LAYOUT}
     */
    @BindView(R.id.activity_board_columns)
    DrawerLayout drawerLayout;

    @BindView(R.id.btnAddCard)
    FloatingActionButton addCardButton;

    @BindView(R.id.viewPagerColumns)
    ViewPager viewPager;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.navigation)
    NavigationView navigationView;

    private int columnOrder;

    /**
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

    private String boardId;
    private String columnName;
    private String currentColumnName;

    private int currentColumnPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBase();
        getExtras();
        initialize();

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setBase() {

        setContentView(LAYOUT);
        ButterKnife.bind(this);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

    }

    private void getExtras() {

        Intent intent = getIntent();
        columnOrder = intent.getExtras().getInt(COLUMN_ORDER);
        boardId = intent.getExtras().getString(BOARD_ID);

    }

    private void initialize() {

        /** {@link InitializationsManager#initToolbar(Toolbar, int)}  */
        initToolbar(BoardColumnsActivity.this, toolbar,
                loadText(BoardColumnsActivity.this, BOARD_NAME)); /** Инициализация */
        initTabs();
        initNavigationView(); /** Инициализация */

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_column, menu);
        return true;
    }

    private void goWelcomeActivity() {
        Intent intent = new Intent(BoardColumnsActivity.this,
                BoardWelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BOARD_ID, Integer.parseInt(boardId));
        BoardColumnsActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.activity_right_in,
                R.anim.activity_diagonaltranslate);

        finish();
        overridePendingTransition(R.anim.activity_right_in,
                R.anim.activity_diagonaltranslate);
    }

    private void setInputNameSettings(EditText inputName, LinearLayout.LayoutParams  params) {
        inputName.setTextColor(ContextCompat.getColor(BoardColumnsActivity.this, R.color.colorBlack));
        inputName.setText(currentColumnName);
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputName.setLayoutParams(params);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_change:

                currentColumnName = tabLayout.getTabAt(viewPager.getCurrentItem()).getText().toString();
                currentColumnPosition = viewPager.getCurrentItem() + 1;

                LinearLayout layout = new LinearLayout(BoardColumnsActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder builder = new AlertDialog.Builder(BoardColumnsActivity.this);
                builder.setTitle(getResources().getString(R.string.change_column_name));

                LinearLayout.LayoutParams  params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(dp2px(5), dp2px(20), dp2px(5), dp2px(20));

                final EditText inputName
                        = (EditText) getLayoutInflater().inflate(R.layout.item_edittext_style, null);

                setInputNameSettings(inputName, params);

                layout.addView(inputName);

                builder.setView(layout)
                        .setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentColumnName = inputName.getText().toString();
                                if (currentColumnName.length() != 0) {
                                    new ChangeColumnSettings().execute();
                                    goWelcomeActivity();
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

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                BoardColumnsActivity.this.getResources().getDisplayMetrics());
    }

    private void initTabs() {
        final BoardColumnsTabsFragmentAdapter adapter
                = new BoardColumnsTabsFragmentAdapter(this, getSupportFragmentManager(), boardId);

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(columnOrder);

        tabLayout.setupWithViewPager(viewPager);

        final boolean[] dialogOpen = {false};

        final LinearLayout[] tabStrip = {((LinearLayout) tabLayout.getChildAt(0))};

            tabStrip[0].getChildAt(last + 1).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    if (!dialogOpen[0]) {

                        AlertDialog.Builder builder
                                = new AlertDialog.Builder(BoardColumnsActivity.this);
                        builder.setTitle(getResources().getString(R.string.create_column));

                        final EditText input = new EditText(BoardColumnsActivity.this);
                        input.setHint(getResources().getString(R.string.enter_column_name));
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input)
                                .setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        columnName = input.getText().toString();

                                        if (columnName.length() != 0) new AddColumn().execute();
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
                        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                dialogOpen[0] = false;
                            }
                        });
                        dialogOpen[0] = true;
                    }

                    return true;
                }
            });

        setViewPagerListener();

        if (adapter.getCount() < MAX_COLUMNS_IN_FIXED_MODE)
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

    private void setViewPagerListener() {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                addCardButton.show();
            }

            @Override
            public void onPageSelected(int position) {
                addCardButton.show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                addCardButton.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

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
                        changeActivityCompat(BoardColumnsActivity.this,
                                new Intent(BoardColumnsActivity.this, FAQBotActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    private void initializeNavHeaderLogin(View header) {

        TextView navHeaderLogin = ButterKnife.findById(header, R.id.userNameHeader);
        navHeaderLogin.setText(loadText(BoardColumnsActivity.this, LOGIN));

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
                        changeActivityCompat(BoardColumnsActivity.this,
                                new Intent(BoardColumnsActivity.this, ProfileActivity.class));
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
                        nextIntent = new Intent(BoardColumnsActivity.this, ContactsActivity.class);
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
                 * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                 * */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardColumnsActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardColumnsActivity.this, ID).equals("")) finishAffinity();

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

                ArrayList<String> cids = new ArrayList<>();
                List<BoardColumn> boardColumns = new ArrayList<>();


                for (int i = 0; i < cidsJSON.length(); i++) {
                    cids.add(cidsJSON.getString(i));
                }

                for (int i = 0; i < cids.size(); i++) {
                    JSONObject columnInfo = dataJsonObj.getJSONObject(cids.get(i));

                    boardColumns.add(new BoardColumn(
                            Integer.parseInt(columnInfo.getString(ID)),
                            columnInfo.getString(NAME),  i)
                    );
                }

                Depository.setBoardColumns(boardColumns);

                Intent intent = new Intent(BoardColumnsActivity.this,
                        BoardColumnsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(BOARD_ID, boardId);
                intent.putExtra(COLUMN_ORDER, last + 1);
                BoardColumnsActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.activity_right_in,
                        R.anim.activity_diagonaltranslate);

                finish();
                overridePendingTransition(R.anim.activity_right_in,
                        R.anim.activity_diagonaltranslate);

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
            new GetBoardInfo().execute();

        }
    }
    private class ChangeColumnSettings extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CHANGE_COLUMN_SETTINGS_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId
                    + AMPERSAND + NAME + EQUALS + currentColumnName
                    + AMPERSAND + POSITION + EQUALS + currentColumnPosition;

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

}
