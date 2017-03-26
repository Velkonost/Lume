package ru.velkonost.lume.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Managers.InitializationsManager;
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.USER_PLACE_LIVING;
import static ru.velkonost.lume.Constants.USER_PLACE_STUDY;
import static ru.velkonost.lume.Constants.USER_PLACE_WORK;
import static ru.velkonost.lume.Constants.USER_WORKING_EMAIL;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;

/**
 * @author Velkonost
 *
 * Класс, описывающий диалог пользователя с автоответчиком
 *
 */
public class FAQBotActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_bot;

    /**
     * Свойство - описание верхней панели инструментов приложения.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Свойство - описание {@link SearchActivity#LAYOUT}
     */
    @BindView(R.id.activity_bot)
    DrawerLayout drawerLayout;

    /**
     * Свойство - боковая панель
     */
    @BindView(R.id.navigation)
    NavigationView navigationView;

    /**
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBase();
        initialization();

        /**
         * Кнопка возврата на предыдущую активность.
         */
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
    private void initialization() {
        /** {@link InitializationsManager#initToolbar(Toolbar, int)}  */
        initToolbar(FAQBotActivity.this, toolbar, "Задать вопрос"); /** Инициализация */
        initNavigationView(); /** Инициализация */

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                        changeActivityCompat(FAQBotActivity.this,
                                new Intent(FAQBotActivity.this, FAQBotActivity.class));
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
        navHeaderLogin.setText(loadText(FAQBotActivity.this, LOGIN));

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
                        changeActivityCompat(FAQBotActivity.this,
                                new Intent(FAQBotActivity.this, ProfileActivity.class));
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
                        nextIntent = new Intent(FAQBotActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(FAQBotActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        nextIntent = new Intent(FAQBotActivity.this, BoardsListActivity.class);
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(FAQBotActivity.this, SettingsActivity.class);
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(FAQBotActivity.this, ID);
                        nextIntent = new Intent(FAQBotActivity.this, WelcomeActivity.class);
                        break;
                }

                /**
                 * Переход на следующую активность.
                 * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                 * */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (loadText(FAQBotActivity.this, ID).equals("")) {
                            deleteText(FAQBotActivity.this, USER_PLACE_LIVING);
                            deleteText(FAQBotActivity.this, USER_PLACE_STUDY);
                            deleteText(FAQBotActivity.this, USER_PLACE_WORK);
                            deleteText(FAQBotActivity.this, USER_WORKING_EMAIL);
                        }

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(FAQBotActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(FAQBotActivity.this, ID).equals("")) finishAffinity();

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
}
