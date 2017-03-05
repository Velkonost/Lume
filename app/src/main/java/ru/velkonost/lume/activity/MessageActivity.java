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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import ru.velkonost.lume.Constants;
import ru.velkonost.lume.Managers.InitializationsManager;
import ru.velkonost.lume.Managers.PhoneDataStorageManager;
import ru.velkonost.lume.R;
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.descriptions.Message;
import ru.velkonost.lume.fragments.MessagesFragment;

import static ru.velkonost.lume.Constants.ADDRESSEE_ID;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.DATE;
import static ru.velkonost.lume.Constants.DIALOG_ID;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.MESSAGE_IDS;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.STATUS;
import static ru.velkonost.lume.Constants.TEXT;
import static ru.velkonost.lume.Constants.URL.SERVER_DIALOG_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_SEND_MESSAGE_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_SHOW_MESSAGES_METHOD;
import static ru.velkonost.lume.Constants.USER;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;
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
     * Свойство - описание верхней панели инструментов приложения.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Свойство - описание {@link SearchActivity#LAYOUT}
     */
    @BindView(R.id.activity_messages)
    DrawerLayout drawerLayout;

    @BindView(R.id.editMessage)
    EditText editMessage;

    @BindView(R.id.imageArrowSend)
    ImageView imageArrowSend;

    @BindView(R.id.navigation)
    NavigationView navigationView;

    @BindView(R.id.loadingDots)
    LoadingDots loadingDots;

    /**
     * Свойство - идентификатор пользователя, авторизованного на данном устройстве.
     */
    private String userId;

    /**
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

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
     * {@link Message}
     */
    private List<Message> mMessages;

    private MessagesFragment mMessagesFragment;

    private TimerCheckMessagesState timer;

    private String collocutorName;

    private String textMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        ButterKnife.bind(this);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        mGetMessages = new GetMessages();
        mids = new ArrayList<>();
        mMessages = new ArrayList<>();

        Intent intent = getIntent();
        dialogId = intent.getIntExtra(DIALOG_ID, 0);
        addresseeId = intent.getIntExtra(ID, 0);
        collocutorName = intent.getStringExtra(NAME);

        /** {@link InitializationsManager#initToolbar(Toolbar, int)}  */
        initToolbar(MessageActivity.this, toolbar, collocutorName); /** Инициализация */
        initNavigationView(); /** Инициализация */

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ID, addresseeId);
                MessageActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.activity_right_in,
                        R.anim.activity_diagonaltranslate);
            }
        });

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorageManager#loadText(Context, String)}
         **/
        userId = loadText(MessageActivity.this, ID);

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

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imageArrowSend.setColorFilter(ContextCompat
                            .getColor(MessageActivity.this, R.color.colorMessageBackground));
                } else {
                    imageArrowSend.setColorFilter(ContextCompat
                            .getColor(MessageActivity.this, R.color.colorPrimary));

                }

            }
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getCurrentFocus().clearFocus();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getCurrentFocus().clearFocus();
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View header = navigationView.getHeaderView(0);
        TextView navHeaderLogin = ButterKnife.findById(header, R.id.userNameHeader);
        ImageView askQuestion = ButterKnife.findById(header, R.id.askQuestion);

        navHeaderLogin.setText(loadText(MessageActivity.this, LOGIN));


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
                        changeActivityCompat(MessageActivity.this,
                                new Intent(MessageActivity.this, FAQBotActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);
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
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(MessageActivity.this,
                                new Intent(MessageActivity.this, ProfileActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);

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
                        nextIntent = new Intent(MessageActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(MessageActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        nextIntent = new Intent(MessageActivity.this, BoardsListActivity.class);
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
                 * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                 * */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(MessageActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(MessageActivity.this, ID).equals(""))
                    finishAffinity();

                drawerLayout.closeDrawer(GravityCompat.START);

                return false;
            }
        });
    }

    public void sendMessage(View view) {
        if (editMessage.getText().toString().length() == 0) return;

        textMessage = editMessage.getText().toString();

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

                loadingDots.setVisibility(View.INVISIBLE);

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
                 * {@link MessagesFragment}
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

            String textToSend = textMessage;

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
                    + AMPERSAND + TEXT + EQUALS + textToSend;

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
