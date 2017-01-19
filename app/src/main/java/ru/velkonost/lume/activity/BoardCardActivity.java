package ru.velkonost.lume.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.BoardParticipant;
import ru.velkonost.lume.descriptions.CardComment;
import ru.velkonost.lume.fragments.BoardDescriptionFragment;
import ru.velkonost.lume.fragments.CardCommentsFragment;
import ru.velkonost.lume.fragments.CardParticipantsFragment;
import ru.velkonost.lume.fragments.MessagesFragment;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BOARD_DESCRIPTION;
import static ru.velkonost.lume.Constants.BOARD_LAST_CONTRIBUTED_USER;
import static ru.velkonost.lume.Constants.CARD_DESCRIPTION;
import static ru.velkonost.lume.Constants.CARD_ID;
import static ru.velkonost.lume.Constants.CARD_NAME;
import static ru.velkonost.lume.Constants.COMMENT;
import static ru.velkonost.lume.Constants.COMMENT_IDS;
import static ru.velkonost.lume.Constants.DATE;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.TEXT;
import static ru.velkonost.lume.Constants.URL.SERVER_CARD_ADD_COMMENT_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_CARD_INFO_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER;
import static ru.velkonost.lume.Constants.USER_IDS;
import static ru.velkonost.lume.Managers.DateConverter.formatDate;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.saveText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class BoardCardActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_board_card;

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

    private int cardId;

    private List<BoardParticipant> mCardParticipants;

    private List<CardComment> mCardComments;

    private GetCardData mGetCardData;

    private ArrayList<String> commentIds;

    private CardCommentsFragment mCommentsFragment;

    private TimerCheckComments mTimerCheckComments;

    private EditText mEditTextComment;

    private String userId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        mCardParticipants = new ArrayList<>();
        mCardComments = new ArrayList<>();
        mGetCardData = new GetCardData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_board_card);

        mEditTextComment = (EditText) findViewById(R.id.editComment);

        Intent intent = getIntent();
        String cardName = intent.getExtras().getString(CARD_NAME);
        cardId = intent.getExtras().getInt(CARD_ID);

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(BoardCardActivity.this, ID);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(BoardCardActivity.this, toolbar, cardName); /** Инициализация */
        initNavigationView(); /** Инициализация */

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mGetCardData.execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTimerCheckComments = new TimerCheckComments(100000000, 5000);
                mTimerCheckComments.start();

            }
        }, 5000);

    }



    @Override
    protected void onStop() {
        super.onStop();
        if (mTimerCheckComments != null)
            mTimerCheckComments.cancel();
    }

    public void addComment(View view) {
        if (mEditTextComment.getText().toString().length() == 0) return;

        AddComment addComment = new AddComment();
        addComment.execute();

        mEditTextComment.setText("");

        RefreshComments mRefreshComments = new RefreshComments();
        mRefreshComments.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_card);
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

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                /** Инициализируем намерение на следующую активность */
                switch (menuItem.getItemId()) {

                    /** Переход на профиль данного пользователя */
                    case R.id.navigationProfile:
                        nextIntent = new Intent(BoardCardActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(BoardCardActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(BoardCardActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        nextIntent = new Intent(BoardCardActivity.this, BoardsListActivity.class);
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(BoardCardActivity.this, SettingsActivity.class);
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(BoardCardActivity.this, ID);
                        nextIntent = new Intent(BoardCardActivity.this, WelcomeActivity.class);
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
                        changeActivityCompat(BoardCardActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardCardActivity.this, ID).equals("")) finishAffinity();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_card);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    public class TimerCheckComments extends CountDownTimer {

        TimerCheckComments(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            RefreshComments mRefreshComments = new RefreshComments();
            mRefreshComments.execute();
        }

        @Override
        public void onFinish() {
        }
    }

    private class GetCardData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_CARD_INFO_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId;

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
                JSONArray cidsJSON = dataJsonObj.getJSONArray(COMMENT_IDS);

                String cardDescription = dataJsonObj.getString(CARD_DESCRIPTION);


                ArrayList<String> uids = new ArrayList<>();
                commentIds = new ArrayList<>();

                for (int i = 0; i < idsJSON.length(); i++) {
                    uids.add(idsJSON.getString(i));
                }

                for (int i = 0; i < cidsJSON.length(); i++) {
                    commentIds.add(cidsJSON.getString(i));
                }


                for (int i = 0; i < uids.size(); i++) {
                    String participantId = uids.get(i);

                    JSONObject userInfo = dataJsonObj.getJSONObject(participantId);

                    mCardParticipants.add(new BoardParticipant(
                            Integer.parseInt(participantId),
                            Integer.parseInt(userInfo.getString(AVATAR)),
                            userInfo.getString(LOGIN),
                            BOARD_LAST_CONTRIBUTED_USER == i + 1, uids.size() - i, cardId
                    ));

                    if (BOARD_LAST_CONTRIBUTED_USER == i) break;

                }

                for (int i = 0; i < commentIds.size(); i++) {

                    String commentId = commentIds.get(i) + COMMENT;

                    JSONObject commentInfo = dataJsonObj.getJSONObject(commentId);

                    String formattedCommentDate = formatDate(commentInfo.getString(DATE).substring(0, 10));

                    mCardComments.add(new CardComment(
                            Integer.parseInt(commentId.substring(0, commentId.length() - 7)),
                            commentInfo.getString(USER), cardId,
                            commentInfo.getString(TEXT),
                            formattedCommentDate.equals(new SimpleDateFormat("dd-MM-yyyy")
                                    .format(Calendar.getInstance().getTime()))
                                    ? commentInfo.getString(DATE)
                                    .substring(11, commentInfo.getString(DATE).length())
                                    : new SimpleDateFormat("dd MMM yyyy")
                                    .format(new SimpleDateFormat("dd-MM-yyyy")
                                            .parse(formattedCommentDate))
                    ));

                }

                Collections.reverse(mCardComments);

                saveText(BoardCardActivity.this, BOARD_DESCRIPTION, cardDescription);

                BoardDescriptionFragment descriptionFragment = new BoardDescriptionFragment();
                CardParticipantsFragment cardParticipantsFragment
                        = CardParticipantsFragment.getInstance(BoardCardActivity.this, mCardParticipants);
                mCommentsFragment
                        = CardCommentsFragment.getInstance(BoardCardActivity.this, mCardComments);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.add(R.id.descriptionContainer, descriptionFragment);
                transaction.add(R.id.participantsContainer, cardParticipantsFragment);
                transaction.add(R.id.commentsContainer, mCommentsFragment);

                transaction.commit();

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private class RefreshComments extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_CARD_INFO_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId;

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

                Collections.reverse(mCardComments);

                /**
                 * Получение идентификаторов найденных пользователей.
                 */
                JSONArray idsJSON = dataJsonObj.getJSONArray(COMMENT_IDS);

                for (int i = 0; i < idsJSON.length(); i++){
                    if (!commentIds.contains(idsJSON.getString(i)))
                        commentIds.add(0, idsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < commentIds.size(); i++) {
                    boolean exist = false;

                    String commentId = commentIds.get(i) + COMMENT;

                    /**
                     * Получение JSON-объекта с информацией о конкретном сообщении по его идентификатору.
                     */
                    JSONObject commentInfo = dataJsonObj.getJSONObject(commentId);

                    for (int j = 0; j < mCardComments.size(); j++){
                        if (mCardComments.get(j).getId() == commentInfo.getInt(ID)) {
                            exist = true;
                            break;
                        }
                    }

                    String formattedCommentDate = formatDate(commentInfo.getString(DATE).substring(0, 10));

                    if (!exist){
                        mCardComments.add(new CardComment(
                                Integer.parseInt(commentId.substring(0, commentId.length() - 7)),
                                commentInfo.getString(USER), cardId,
                                commentInfo.getString(TEXT),
                                formattedCommentDate.equals(new SimpleDateFormat("dd-MM-yyyy")
                                        .format(Calendar.getInstance().getTime()))
                                        ? commentInfo.getString(DATE)
                                        .substring(11, commentInfo.getString(DATE).length())
                                        : new SimpleDateFormat("dd MMM yyyy")
                                        .format(new SimpleDateFormat("dd-MM-yyyy")
                                                .parse(formattedCommentDate))
                        ));
                    }
                }

                Collections.reverse(mCardComments);

                /**
                 * Добавляем фрагмент на экран.
                 * {@link MessagesFragment}
                 */
                if(!isFinishing()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    mCommentsFragment.refreshComments(mCardComments);
                    ft.replace(R.id.commentsContainer, mCommentsFragment);
                    ft.commitAllowingStateLoss();
                }

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private class AddComment extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CARD_ADD_COMMENT_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + ID + EQUALS + userId
                    + AMPERSAND + TEXT + EQUALS + mEditTextComment.getText().toString();

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
