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

import java.util.List;

import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.BoardParticipant;

import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;

public class BoardParticipantsActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_board_participant;

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
     * Свойство - экзмепляр класса {@link BoardWelcomeActivity.GetBoardInfo}
     */
//    protected BoardWelcomeActivity.GetBoardInfo mGetBoardInfo;


    /**
     * Свойство - список контактов.
     * {@link ru.velkonost.lume.descriptions.BoardParticipant}
     */
    private List<BoardParticipant> mBoardParticipants;

//    private BoardsFragment mBoardsFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

//        mGetBoardInfo = new BoardWelcomeActivity.GetBoardInfo();
//        mBoardParticipants = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_board_participant);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(BoardParticipantsActivity.this, toolbar, R.string.menu_item_participants); /** Инициализация */
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
        userId = loadText(BoardParticipantsActivity.this, ID);

        Intent intent = getIntent();
        boardId = intent.getIntExtra(BOARD_ID, 0);

//        mGetBoardInfo.execute();


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
                        nextIntent = new Intent(BoardParticipantsActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(BoardParticipantsActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(BoardParticipantsActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        nextIntent = new Intent(BoardParticipantsActivity.this, BoardsListActivity.class);
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(BoardParticipantsActivity.this, SettingsActivity.class);
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(BoardParticipantsActivity.this, ID);
                        nextIntent = new Intent(BoardParticipantsActivity.this, WelcomeActivity.class);
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
                        changeActivityCompat(BoardParticipantsActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardParticipantsActivity.this, ID).equals("")) finishAffinity();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_participant);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }
}
