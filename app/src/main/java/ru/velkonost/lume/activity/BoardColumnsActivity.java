package ru.velkonost.lume.activity;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.R;
import ru.velkonost.lume.TypefaceUtil;
import ru.velkonost.lume.fragments.BoardColumnsTabsFragmentAdapter;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.BOARD_NAME;
import static ru.velkonost.lume.Constants.COLUMN_ORDER;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.MAX_COLUMNS_IN_FIXED_MODE;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.POSITION;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_COLUMN_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_CHANGE_COLUMN_SETTINGS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.fragments.BoardColumnsTabsFragmentAdapter.last;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

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

    private ViewPager viewPager;

    private int columnOrder;

    private String boardId;
    private String columnName;

    private TabLayout tabLayout;

    private String currentColumnName;
    private int currentColumnPosition;

    private FloatingActionButton addCardButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_board_columns);

        Intent intent = getIntent();
        columnOrder = intent.getExtras().getInt(COLUMN_ORDER);
        boardId = intent.getExtras().getString(BOARD_ID);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(BoardColumnsActivity.this, toolbar,
                loadText(BoardColumnsActivity.this, BOARD_NAME)); /** Инициализация */
        initTabs();
        initNavigationView(); /** Инициализация */

        addCardButton = (FloatingActionButton) findViewById(R.id.btnAddCard);

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_column, menu);
        return true;
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

                inputName.setTextColor(ContextCompat.getColor(BoardColumnsActivity.this, R.color.colorBlack));
                inputName.setText(currentColumnName);
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);
                inputName.setLayoutParams(params);
                layout.addView(inputName);

                builder.setView(layout)

                        .setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentColumnName = inputName.getText().toString();

                                if (currentColumnName.length() != 0) {

                                    ChangeColumnSettings changeColumnSettings = new ChangeColumnSettings();
                                    changeColumnSettings.execute();

                                    Intent intent = new Intent(BoardColumnsActivity.this,
                                            BoardWelcomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(BOARD_ID, Integer.parseInt(boardId));
                                    BoardColumnsActivity.this.startActivity(intent);
                                    finish();

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
        viewPager = (ViewPager) findViewById(R.id.viewPagerColumns);
        final BoardColumnsTabsFragmentAdapter adapter
                = new BoardColumnsTabsFragmentAdapter(this, getSupportFragmentManager(), boardId);

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(columnOrder);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);

        final boolean[] dialogOpen = {false};

        final LinearLayout[] tabStrip = {((LinearLayout) tabLayout.getChildAt(0))};

            tabStrip[0].getChildAt(last + 1).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    if (!dialogOpen[0]) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(BoardColumnsActivity.this);
                        builder.setTitle(getResources().getString(R.string.create_column));

                        final EditText input = new EditText(BoardColumnsActivity.this);
                        input.setHint(getResources().getString(R.string.enter_column_name));
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input)
                                .setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        columnName = input.getText().toString();

                                        if (columnName.length() != 0) {
                                            AddColumn addColumn = new AddColumn();
                                            addColumn.execute();

                                            Intent intent = new Intent(BoardColumnsActivity.this,
                                                    BoardWelcomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra(BOARD_ID, Integer.parseInt(boardId));
                                            BoardColumnsActivity.this.startActivity(intent);
                                            finish();
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

        if (adapter.getCount() < MAX_COLUMNS_IN_FIXED_MODE)
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_columns);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
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

        View header = navigationView.getHeaderView(0);
        TextView navHeaderLogin = (TextView) header.findViewById(R.id.userNameHeader);
        navHeaderLogin.setText(loadText(BoardColumnsActivity.this, LOGIN));

        ImageView askQuestion = (ImageView) header.findViewById(R.id.askQuestion);

        askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link Initializations#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardColumnsActivity.this,
                                new Intent(BoardColumnsActivity.this, FAQBotActivity.class));
                    }
                }, 350);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_columns);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        navHeaderLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link Initializations#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardColumnsActivity.this,
                                new Intent(BoardColumnsActivity.this, ProfileActivity.class));
                    }
                }, 350);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_columns);
                drawer.closeDrawer(GravityCompat.START);

            }
        });

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

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_columns);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
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
