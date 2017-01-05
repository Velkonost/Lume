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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.Constants;
import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.DialogContact;
import ru.velkonost.lume.descriptions.Message;
import ru.velkonost.lume.fragments.DialogsFragment;
import ru.velkonost.lume.fragments.MessagesFragment;

import static ru.velkonost.lume.Constants.ADDRESSEE_ID;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.DATE;
import static ru.velkonost.lume.Constants.DIALOG_ID;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.MESSAGE_IDS;
import static ru.velkonost.lume.Constants.STATUS;
import static ru.velkonost.lume.Constants.TEXT;
import static ru.velkonost.lume.Constants.URL.SERVER_DIALOG_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_SEND_MESSAGE_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_SHOW_MESSAGES_METHOD;
import static ru.velkonost.lume.Constants.USER;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

/**
 * @author Velkonost
 *
 * Класс, описывающий активность открытого диалога.
 *
 */
public class MessageActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_message;

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
    private int addresseeId;
    private int dialogId;

    /**
     * Идентификаторы сообщений данного диалога.
     **/
    private ArrayList<String> mids;

    /**
     * Свойство - экзмепляр класса {@link GetMessages}
     */
    protected GetMessages mGetMessages;

    /**
     * Свойство - список контактов.
     * {@link DialogContact}
     */
    private List<Message> mMessages;

    private MessagesFragment mMessagesFragment;

    private EditText editMessage;

    private TimerCheckMessagesState timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        mGetMessages = new GetMessages();
        mids = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_messages);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(MessageActivity.this, toolbar, R.string.menu_item_messages); /** Инициализация */
        initNavigationView(); /** Инициализация */

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(MessageActivity.this, ID);

        editMessage = (EditText) findViewById(R.id.editMessage);

        mMessages = new ArrayList<>();

        Intent intent = getIntent();
        dialogId = intent.getIntExtra(DIALOG_ID, 0);
        addresseeId = intent.getIntExtra(ID, 0);

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

        mGetMessages.execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timer = new TimerCheckMessagesState(100000000, 5000);
                timer.start();

            }
        }, 5000);

        editMessage.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        mMessagesFragment.refreshRecyclerView(mMessages);
                        ft.replace(R.id.llmessage, mMessagesFragment);
                        ft.commit();
                    }
                }, 500);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_messages);
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
                        nextIntent = new Intent(MessageActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(MessageActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(MessageActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(MessageActivity.this, SettingsActivity.class);
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(MessageActivity.this, ID);
                        nextIntent = new Intent(MessageActivity.this, WelcomeActivity.class);
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
                        changeActivityCompat(MessageActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(MessageActivity.this, ID).equals(""))
                    finishAffinity();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_messages);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    public void sendMessage(View view) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.execute();

        editMessage.setText("");

        RefreshMessages mRefreshMessages = new RefreshMessages();
        mRefreshMessages.execute();
    }

    public void clickOnEditText(View view) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mMessagesFragment.refreshRecyclerView(mMessages);
                ft.replace(R.id.llmessage, mMessagesFragment);
                ft.commit();
            }
        }, 500);

    }


    public class TimerCheckMessagesState extends CountDownTimer {

        TimerCheckMessagesState(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            RefreshMessages mRefreshMessages = new RefreshMessages();
            mRefreshMessages.execute();
        }

        @Override
        public void onFinish() {
        }
    }

    private class GetMessages extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_DIALOG_SCRIPT
                    + SERVER_SHOW_MESSAGES_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = DIALOG_ID + EQUALS + dialogId
                    + AMPERSAND + ADDRESSEE_ID + EQUALS + addresseeId;

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
                JSONArray idsJSON = dataJsonObj.getJSONArray(MESSAGE_IDS);

                for (int i = 0; i < idsJSON.length(); i++){
                    mids.add(idsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < mids.size(); i++) {

                    /**
                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
                     */
                    JSONObject messageInfo = dataJsonObj.getJSONObject(mids.get(i));

                    mMessages.add(new Message(
                            messageInfo.getInt(USER) == Integer.parseInt(userId),
                            messageInfo.getInt(ID), messageInfo.getInt(USER),
                            dialogId, messageInfo.getInt(STATUS),
                            messageInfo.getString(Constants.TEXT),
                            messageInfo.getString(DATE)
                    ));
                }

                /**
                 * Добавляем фрагмент на экран.
                 * {@link MessagesFragment}
                 */
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mMessagesFragment
                        = MessagesFragment.getInstance(MessageActivity.this, mMessages);
                ft.add(R.id.llmessage, mMessagesFragment);
                ft.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class RefreshMessages extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_DIALOG_SCRIPT
                    + SERVER_SHOW_MESSAGES_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = DIALOG_ID + EQUALS + dialogId
                    + AMPERSAND + ADDRESSEE_ID + EQUALS + addresseeId;

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
                JSONArray idsJSON = dataJsonObj.getJSONArray(MESSAGE_IDS);

                for (int i = 0; i < idsJSON.length(); i++){
                    if (!mids.contains(idsJSON.getString(i)))
                        mids.add(idsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < mids.size(); i++) {
                    boolean exist = false;

                    /**
                     * Получение JSON-объекта с информацией о конкретном сообщении по его идентификатору.
                     */
                    JSONObject messageInfo = dataJsonObj.getJSONObject(mids.get(i));

                    for (int j = 0; j < mMessages.size(); j++){
                        if (mMessages.get(j).getId() == messageInfo.getInt(ID)) {

                            mMessages.get(j).setStatus(Integer.parseInt(messageInfo
                                    .getString(STATUS)));
                            mMessages.get(j).setExist(true);

                            exist = true;
                            break;
                        }
                    }

                    if (!exist){
                        mMessages.add(new Message(
                                messageInfo.getInt(USER) == Integer.parseInt(userId),
                                messageInfo.getInt(ID), messageInfo.getInt(USER),
                                dialogId, messageInfo.getInt(STATUS),
                                messageInfo.getString(Constants.TEXT),
                                messageInfo.getString(DATE)
                        ));
                    }
                }

                /**
                 * Добавляем фрагмент на экран.
                 * {@link DialogsFragment}
                 */
                if(!isFinishing()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    mMessagesFragment.refreshMessages(mMessages);
                    ft.replace(R.id.llmessage, mMessagesFragment);
                    ft.commitAllowingStateLoss();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class SendMessage extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_DIALOG_SCRIPT
                    + SERVER_SEND_MESSAGE_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = DIALOG_ID + EQUALS + dialogId
                    + AMPERSAND + ID + EQUALS + userId
                    + AMPERSAND + TEXT + EQUALS + editMessage.getText().toString();

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
