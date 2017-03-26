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
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.R;
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
     * Свойство - описание {@link MessageActivity#LAYOUT}
     */
    @BindView(R.id.activity_messages)
    DrawerLayout drawerLayout;

    /**
     * Свойство - поле для редактирования нового сообщения
     */
    @BindView(R.id.editMessage)
    EditText editMessage;

    /**
     * Свойство - отправка нового сообщения по нажатию
     */
    @BindView(R.id.imageArrowSend)
    ImageView imageArrowSend;

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
     * Свойство - идентификатор пользователя, авторизованного на данном устройстве
     */
    private String userId;

    /**
     * Свойство - следующая активность
     */
    private Intent nextIntent;

    /**
     * Свойство - идентификатор получателя
     */
    private int addresseeId;

    /**
     * Свойство - идентификатор диалога
     */
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
     * Свойство - список сообщений
     * {@link Message}
     */
    private List<Message> mMessages;

    private MessagesFragment mMessagesFragment;
    private TimerCheckMessagesState timer;

    /**
     * Свойство - имя (или логин) получателя
     */
    private String collocutorName;

    /**
     * Свойство - содержание сообщения
     */
    private String textMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBase();
        getData();
        initialization();
        setListeners();

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
     * Получение данных (отсутствует получение с интернета)
     */
    private void getData() {
        getFromFile();
        getExtras();
    }

    /**
     * Получение данных из специального файла приложения
     */
    private void getFromFile() {
        /**
         * Получение id пользователя.
         * {@link PhoneDataStorageManager#loadText(Context, String)}
         **/
        userId = loadText(MessageActivity.this, ID);
    }

    /**
     * Получение данных из предыдущей активности
     */
    private void getExtras() {
        Intent intent = getIntent();
        dialogId = intent.getIntExtra(DIALOG_ID, 0);
        addresseeId = intent.getIntExtra(ID, 0);
        collocutorName = intent.getStringExtra(NAME);
    }

    /**
     * Инитиализация основных элементов
     */
    private void initialization() {

        mGetMessages = new GetMessages();
        mids = new ArrayList<>();
        mMessages = new ArrayList<>();


        /** {@link InitializationsManager#initToolbar(Toolbar, int)}  */
        initToolbar(MessageActivity.this, toolbar, collocutorName); /** Инициализация */
        initNavigationView(); /** Инициализация */

    }

    /**
     * Вызов процессов, происходящих в параллельных потоках
     */
    private void executeTasks() {
        mGetMessages.execute();
    }

    /**
     * Установка слушателей
     */
    private void setListeners() {
        setToolbarListener();
        setEditMessageListener();
    }

    /**
     * Установка слушателя на {@link MessageActivity#toolbar}
     */
    private void setToolbarListener() {

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

    }

    /**
     * Установка слушателя на {@link MessageActivity#editMessage}
     */
    private void setEditMessageListener() {

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

    /**
     * Запуск таймера для проверки состояния списка сообщений
     */
    private void startTimer() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timer = new TimerCheckMessagesState(100000000, 5000);
                timer.start();

            }
        }, 5000);

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
     * Отправка нового сообщения
     * @param view
     */
    public void sendMessage(View view) {
        if (editMessage.getText().toString().length() == 0) return;

        textMessage = editMessage.getText().toString();
        new SendMessage().execute();

        editMessage.setText("");
        new RefreshMessages().execute();
    }

    /**
     * Слушатель, запускающий обновление при нажатии на поле редактирования нового сообщения
     * Необходим для приведения внешнего вида в порядок после открытия клавиатуры
     *
     * @param view
     */
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
                        changeActivityCompat(MessageActivity.this,
                                new Intent(MessageActivity.this, FAQBotActivity.class));
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
        navHeaderLogin.setText(loadText(MessageActivity.this, LOGIN));

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

                        if (loadText(MessageActivity.this, ID).equals("")) {
                            deleteText(MessageActivity.this, USER_PLACE_LIVING);
                            deleteText(MessageActivity.this, USER_PLACE_STUDY);
                            deleteText(MessageActivity.this, USER_PLACE_WORK);
                            deleteText(MessageActivity.this, USER_WORKING_EMAIL);
                        }

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(MessageActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(MessageActivity.this, ID).equals("")) finishAffinity();

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
     * Таймер для обновления состояния списка сообщений
     */
    private class TimerCheckMessagesState extends CountDownTimer {

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

    /**
     * Получения информации о сообщениях данного диалога
     */
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

    /**
     * Обновление состояния списка сообщений
     */
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

    /**
     * Отправка сообщения
     */
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
