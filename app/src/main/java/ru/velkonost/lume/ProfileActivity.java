package ru.velkonost.lume;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static ru.velkonost.lume.Constants.ADD_CONTACT;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BIRTHDAY;
import static ru.velkonost.lume.Constants.CITY;
import static ru.velkonost.lume.Constants.CONTACT;
import static ru.velkonost.lume.Constants.COUNTRY;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.GET_DATA;
import static ru.velkonost.lume.Constants.GET_ID;
import static ru.velkonost.lume.Constants.HYPHEN;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.PNG;
import static ru.velkonost.lume.Constants.SEARCH;
import static ru.velkonost.lume.Constants.SEND_ID;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.STUDY;
import static ru.velkonost.lume.Constants.SURNAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_CONTACT_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_DATA_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Constants.USER_ID;
import static ru.velkonost.lume.Constants.WORK;
import static ru.velkonost.lume.Constants.WORK_EMAIL;
import static ru.velkonost.lume.ImageManager.fetchImage;
import static ru.velkonost.lume.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Initializations.initToolbar;
import static ru.velkonost.lume.Initializations.inititializeAlertDialog;
import static ru.velkonost.lume.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.PhoneDataStorage.loadText;
import static ru.velkonost.lume.PhoneDataStorage.saveText;

/**
 * @author Velkonost
 *
 * Класс, описывающий профиль <b>данного</b> пользователя.
 *
 */
public class ProfileActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_profile;

    /**
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

    /**
     * Свойство - высота экрана устройства.
     */
    private int screenH;
    /**
     * Свойство - ширина экрана устройства.
     */
    private int screenW;

    /**
    * Свойство - опинсание view-элемента, служащего для обновления страницы.
    **/
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Свойство - описание верхней панели инструментов приложения.
     */
    private Toolbar toolbar;

    /**
     * Свойство - описание {@link ProfileActivity#LAYOUT}.
     */
    private DrawerLayout drawerLayout;

    /**
     * Свойство - view-элемент для размещения аватара пользователя.
     */
    protected ImageView userAvatar;
    /**
     * Свойство - view-элемент для размещения полного имени пользователя.
     */
    protected TextView userName;

    /**
     * Свойство - экзмепляр класса {@link GetData}
     */
    protected GetData mGetData;

    /**
     * Свойство - экземпляр класса {@link AddContact}
     **/
    private AddContact mAddContact;

    /**
     * Свойство - идентификатор пользователя, авторизованного на данном устройстве.
     */
    private String userId;

    /**
     * Свойство - идентификатор пользователя, которому принадлежит открытый профиль.
     * */
    private String profileId;

    /**
     * Свойство - состояние между
     * {@link ProfileActivity#userId} и {@link ProfileActivity#profileId}
     *
     * Положительный ответ означает, что {@link ProfileActivity#userId} уже добавлял в контакты
     * {@link ProfileActivity#profileId}
     * */
    protected boolean isContact;

    /**
     * Условный контейнер, в который помещаются все view-элементы, созданные программно.
     **/
    private LinearLayout linLayout;
    private LayoutInflater ltInflater;

    /**
     * Свойства - view-элементы отдельных частей страницы профиля,
     *      которые в дальнейшем могут быть добавлены в условный контейнер.
     * {@link ProfileActivity#linLayout}
     **/
    protected View viewAvatar; /** Свойство - аватар пользователя */
    protected View viewUserPlaceLiving; /** Свойство - место проживания пользователя */
    protected View viewUserBirthday; /** Свойство - дата рождения пользователя */
    private View viewUserPlaceStudy; /** Свойство - место обучения пользователя */
    private View viewUserPlaceWork; /** Свойство - текущее место работы пользователя */
    /**
     * Свойство - email, который пользователь данного профиля готов
     *          предоставить другим пользователя для связи с ним.
     **/
    protected View viewUserWorkingEmail;
    /**
     * Свойство - места обучения и работы пользователя.
     * {@link ProfileActivity#viewUserPlaceStudy} и {@link ProfileActivity#viewUserPlaceWork},
     *      "склеенные" вместе.
     **/
    protected View viewUserPlaceStudyAndWork;
    /**
     * Свойство - модуль для взаимодействия пользователя,
     *      авторизованного на данном устройстве
     *      и пользователя открытого на текущий момент аккаунта.
     **/
    private View viewUserInteraction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /** Установка темы по умолчанию */
        setTheme(R.style.AppDefault);

        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        /** Вычисляет размеры экрана устройства */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenH = displayMetrics.heightPixels;
        screenW = displayMetrics.widthPixels;

        /** Инициализация экземпляров классов */
        mGetData = new GetData();
        mAddContact = new AddContact();

        linLayout = (LinearLayout) findViewById(R.id.profileContainer);
        ltInflater = getLayoutInflater();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(toolbar, R.string.app_name); /** Инициализация */
        initNavigationView(); /** Инициализация */

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(ProfileActivity.this, ID);

        /**
         * Проверка:
         * Принадлежит открытый профиль пользователю,
         *      авторизованному на данном устройстве или нет?
         * */
        profileId = loadText(ProfileActivity.this, USER_ID).length() != 0
                ? loadText(ProfileActivity.this, USER_ID)
                : userId;

        /**
         *  Установка цветной палитры,
         *  цвета которой будут заменять друг друга в зависимости от прогресса.
         * */
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorBlue, R.color.colorGreen,
                R.color.colorYellow, R.color.colorRed);

        /** Ставит обработчик событий */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override

            public void onRefresh() {
                /** Выполнение происходит с задержкой в 2.5 секунды */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link Initializations#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(ProfileActivity.this);
                    }
                }, 2500);
            }
        });

        /** Обращаемся к серверу */
        mGetData.execute();
    }


    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_profile);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /**
         * Обработчки событий для меню бокового меню навигации.
         **/
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
                        nextIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(ProfileActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(ProfileActivity.this, ID);
                        nextIntent = new Intent(ProfileActivity.this, WelcomeActivity.class);
                        break;
                }

                /**
                 * Удаляет информацию о владельце открытого профиля.
                 * {@link PhoneDataStorage#deleteText(Context, String)}
                 **/
                deleteText(ProfileActivity.this, USER_ID);

                /**
                 * Переход на следующую активность.
                 * {@link Initializations#changeActivityCompat(Activity, Intent)}
                 * */
                changeActivityCompat(ProfileActivity.this, nextIntent);

                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(ProfileActivity.this, ID).equals(""))
                    finishAffinity();

                return true;
            }
        });
    }

    /**
     * Обработчик событий для кнопки поиска.
     */
    public void goToSearch(View view) {
        switch (view.getId()) {
            case R.id.btnStartSearch:

                /** Получение данных, по которым пользователь хочет найти информацию */
                EditText search = (EditText) findViewById(R.id.textSearch);
                String toSearch = search.getText().toString();

                /** Сохранение этих данных в файл на данном устройстве */
                saveText(ProfileActivity.this, SEARCH, toSearch);

                /**
                 * Переход на страницу поиска, где выоводится результат.
                 * {@link SearchActivity}
                 **/
                nextIntent = new Intent(this, SearchActivity.class);
                break;
        }

        /**
         * Переход на следующую активность.
         * {@link Initializations#changeActivityCompat(Activity, Intent)}
         **/
        changeActivityCompat(ProfileActivity.this, nextIntent);
    }

    /**
     * Форматирование даты из вида, полученного с сервер - YYYY-MM-DD
     *                в вид, необходимый для отображения - DD-MM-YYYY
     **/
    public String formatDate(String dateInStr) {

        String day, month, year;

        /** Разделяем строку на три ключевый строки */
        day = String.valueOf(dateInStr.charAt(dateInStr.length() - 2)) +
                dateInStr.charAt(dateInStr.length() - 1);

        month = String.valueOf(dateInStr.charAt(dateInStr.length() - 5)) +
                dateInStr.charAt(dateInStr.length() - 4);

        year = String.valueOf(dateInStr.charAt(dateInStr.length() - 10)) +
                dateInStr.charAt(dateInStr.length() - 9) +
                dateInStr.charAt(dateInStr.length() - 8) +
                dateInStr.charAt(dateInStr.length() - 7);

        /** Соединяем все воедино */
        return day
                +
                HYPHEN + month
                + HYPHEN + year;
    }


    @Override
    protected void onStart() {
        super.onStart();
        deleteText(ProfileActivity.this, USER_ID);
    }

    /**
     * Класс для получения данных о пользователе с сервера.
     **/
    private class GetData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_GET_DATA_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = ID + EQUALS + userId
                    + AMPERSAND + USER_ID + EQUALS + profileId;

            byte[] data;
            InputStream is;
            BufferedReader reader;

            /** Свойство - код ответа, полученный от сервера */
            String resultJson = "";

            try {

                /**
                 * Устанавливает соединение.
                 */
                URL url = new URL(dataURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                /**
                 * Выставляет необходимые параметры.
                 */
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                /**
                 * Формирует тело запроса.
                 */
                httpURLConnection.setRequestProperty("Content-Length", ""
                        + Integer.toString(params.getBytes().length));
                OutputStream os = httpURLConnection.getOutputStream();
                data = params.getBytes("UTF-8");
                os.write(data);

                /** Соединяемся */
                httpURLConnection.connect();

                /**
                 * Получение кода состояния.
                 */
                int responseCode = httpURLConnection.getResponseCode();
                Log.i("Data", String.valueOf(responseCode));

                /**
                 * Получение данных из потока в виде JSON-объекта.
                 */
                is = httpURLConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJson;
        }

        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            /**
             * Свойство - код ответа от методов сервера.
             *
             * ВНИМАНИЕ!
             *
             * Этот параметр не имеет никакого отношения к кодам состояния.
             * Он формируется на сервере в зависимости от результата проведения обработки данных.
             *
             **/
            int resultCode;

            /** Свойство - полученный JSON–объект*/
            JSONObject dataJsonObj;

            try {

                /**
                 * Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);
                resultCode = Integer.parseInt(dataJsonObj.getString(GET_DATA));

                /**
                 * Обработка полученного кода ответа.
                 */
                switch (resultCode) {
                    /** В случае успешного выполнения */
                    case 300:

                        /** Формирование адреса, по которому хранится аватар владельца открытого профиля */
                        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                                + SERVER_AVATAR + SLASH + dataJsonObj.getString(AVATAR)
                                + SLASH + profileId + PNG;

                        viewAvatar = ltInflater.inflate(R.layout.item_profile_photo, linLayout, false);

                        userAvatar = (ImageView) viewAvatar.findViewById(R.id.imageAvatar);
                        userName = (TextView) viewAvatar.findViewById(R.id.userName);

                        /** Картинка, обозначающая, что пользователь не указал свое имя и фамилию */
                        ImageView userWithoutName = (ImageView) viewAvatar.findViewById(R.id.userWithoutName);

                        /** Задает параметры для аватара пользователя */
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(screenW / 2,
                                screenH / 2);
                        param.gravity = Gravity.CENTER;
                        param.setMargins(0, 0, 0, 0);
                        userAvatar.setLayoutParams(param);

                        /**
                         * Установка имени владельца открытого профиля.
                         *
                         * Если имя и фамилия не найдены,
                         * то устанавливается логин + показывается иконка {@link userWithoutName}
                         **/
                        String sUserName = dataJsonObj.getString(NAME).length() == 0
                                ? dataJsonObj.getString(LOGIN)
                                : dataJsonObj.getString(SURNAME).length() == 0
                                ? dataJsonObj.getString(LOGIN)
                                : dataJsonObj.getString(NAME) + " " + dataJsonObj.getString(SURNAME);
                        userName.setText(sUserName);

                        if (sUserName.equals(dataJsonObj.getString(LOGIN)))
                            userWithoutName.setImageResource(R.drawable.withoutname);

                        /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                        linLayout.addView(viewAvatar);

                        /**
                         * Загрузка аватара пользователя
                         * {@link ImageManager#fetchImage(String, ImageView)}
                         **/
                        fetchImage(avatarURL, userAvatar);


                        /**
                         * Если профиль не принадлежит авторизованному пользователю,
                         *      то добавляем модуль взаимодействия.
                         **/
                        if (!profileId.equals(userId)) {
                            viewUserInteraction = ltInflater
                                    .inflate(R.layout.item_profile_interaction, linLayout, false);

                            /** Кнопка добавления/удаления владельца профиля из контактов авторизованного пользоавателя */
                            Button btnAddIntoContacts = (Button) viewUserInteraction
                                    .findViewById(R.id.btnAddToContacts);

                            /** Кнопка открытия диалога между авторизованным пользователем и владельцем открытого профиля */
                            Button btnSendMessages = (Button) viewUserInteraction
                                    .findViewById(R.id.btnSendMessage);

                            /**
                             * Проверка, добавил ли {@link ProfileActivity#userId}
                             *         в контакты {@link ProfileActivity#profileId}
                             * */
                            isContact = dataJsonObj.getBoolean(CONTACT);
                            if (isContact)
                                btnAddIntoContacts.setText(R.string.user_remove_from_contacts);


                            /** Создает обработчик событий */
                            btnAddIntoContacts.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    /** Открытие нового потока */
                                    mAddContact = new AddContact();
                                    mAddContact.execute();

                                }
                            });

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserInteraction);


                        }

                        /**
                         * Формирование места жительства владельца открытого профиля.
                         **/
                        viewUserPlaceLiving = ltInflater.inflate(R.layout.item_profile_place_living, linLayout, false);
                        TextView userPlaceLiving = (TextView) viewUserPlaceLiving.findViewById(R.id.descriptionCardPlaceLiving);

                        /**
                         * Формируется место проживания из имеющихся данных.
                         **/
                        String sUserPlaceLiving = dataJsonObj.getString(CITY).length() != 0
                                ? dataJsonObj.getString(COUNTRY).length() != 0
                                ? dataJsonObj.getString(CITY) + ", " + dataJsonObj.getString(COUNTRY)
                                : dataJsonObj.getString(CITY)
                                : "";

                        userPlaceLiving.setText(sUserPlaceLiving);

                        /**
                         * Если данные введены, то добавляем элемент в контейнер.
                         **/
                        if (!sUserPlaceLiving.equals(""))
                            linLayout.addView(viewUserPlaceLiving);



                        /** Формирование даты рождения владельца открытого профиля */
                        viewUserBirthday = ltInflater.inflate(R.layout.item_profile_birthday, linLayout, false);
                        TextView userBirthday = (TextView) viewUserBirthday.findViewById(R.id.descriptionCardBirthday);

                        String sUserBirthday = dataJsonObj.getString(BIRTHDAY).length() != 0
                                ? dataJsonObj.getString(BIRTHDAY)
                                : "";

                        /**
                         * Форматирование даты.
                         * {@link ProfileActivity#formatDate(String)}
                         **/
                        String formattedUserBirthday = formatDate(sUserBirthday);
                        userBirthday.setText(formattedUserBirthday);

                        /** Если владелец открытого профиля указывал дату своего рождения */
                        if (!formattedUserBirthday.equals("00-00-0000"))
                            linLayout.addView(viewUserBirthday);



                        /** Формирование места учебы пользователя */
                        String sUserPlaceStudy = dataJsonObj.getString(STUDY).length() != 0
                                ? dataJsonObj.getString(STUDY)
                                : "";

                        /** Формирование текущего места работы пользователя */
                        String sUserPlaceWork = dataJsonObj.getString(WORK).length() != 0
                                ? dataJsonObj.getString(WORK)
                                : "";

                        /** Если указано только место работы */
                        if (sUserPlaceStudy.equals("") && !sUserPlaceWork.equals("")) {

                            viewUserPlaceWork = ltInflater.inflate(R.layout.item_profile_place_work, linLayout, false);
                            TextView userPlaceWork = (TextView) viewUserPlaceWork.findViewById(R.id.descriptionCardPlaceWork);
                            userPlaceWork.setText(sUserPlaceWork);

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserPlaceWork);
                        }

                        /** Если указано только место учебы */
                        if (sUserPlaceWork.equals("") && !sUserPlaceStudy.equals("")) {

                            viewUserPlaceStudy = ltInflater.inflate(R.layout.item_profile_place_study, linLayout, false);
                            TextView userPlaceStudy = (TextView) viewUserPlaceStudy.findViewById(R.id.descriptionCardPlaceStudy);
                            userPlaceStudy.setText(sUserPlaceStudy);

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserPlaceStudy);
                        }

                        /** Если указаны место работы и место учебы */
                        if (!sUserPlaceStudy.equals("") && !sUserPlaceWork.equals("")) {

                            viewUserPlaceStudyAndWork = ltInflater
                                    .inflate(R.layout.item_profile_place_study_and_work,
                                            linLayout, false);

                            TextView userPlaceStudy = (TextView) viewUserPlaceStudyAndWork.
                                    findViewById(R.id.descriptionCardPlaceStudy);
                            userPlaceStudy.setText(sUserPlaceStudy);

                            TextView userPlaceWork = (TextView) viewUserPlaceStudyAndWork.
                                    findViewById(R.id.descriptionCardPlaceWork);
                            userPlaceWork.setText(sUserPlaceWork);

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserPlaceStudyAndWork);
                        }

                        /** Формирование рабочего email пользователя */
                        String sUserWorkingEmail = dataJsonObj.getString(WORK_EMAIL).length() != 0
                                ? dataJsonObj.getString(WORK_EMAIL)
                                : "";

                        /** Если владелец открытого профиля указал рабочий email */
                        if (!sUserWorkingEmail.equals("")) {

                            viewUserWorkingEmail = ltInflater
                                    .inflate(R.layout.item_profile_working_email, linLayout, false);

                            final TextView userWorkingEmail = (TextView) viewUserWorkingEmail
                                    .findViewById(R.id.descriptionCardWorkingEmail);
                            userWorkingEmail.setText(sUserWorkingEmail);

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserWorkingEmail);

                        }

                        break;
                    /**
                     * Произошла неожиданная ошибка.
                     **/
                    case 301:
                        /**
                         * Формирование уведомления об ошибке.
                         */
                        inititializeAlertDialog(ProfileActivity.this,
                                getResources().getString(R.string.server_error),
                                getResources().getString(R.string.relogin),
                                getResources().getString(R.string.btn_ok));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Класс для изменения списка контактов пользователя, авторизованного на данном устройстве.
     **/
    private class AddContact extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object... strings) {
            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_ADD_CONTACT_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = SEND_ID + EQUALS + userId
                    + AMPERSAND + GET_ID + EQUALS + profileId;

            byte[] data;
            InputStream is;
            BufferedReader reader;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            try {

                /**
                 * Устанавливает соединение.
                 */
                URL url = new URL(dataURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                /**
                 * Выставляет необходимые параметры.
                 */
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                /**
                 * Формирует тело запроса.
                 */
                httpURLConnection.setRequestProperty("Content-Length", ""
                        + Integer.toString(params.getBytes().length));
                OutputStream os = httpURLConnection.getOutputStream();
                data = params.getBytes("UTF-8");
                os.write(data);

                /** Соединяемся */
                httpURLConnection.connect();

                /**
                 * Получение кода состояния.
                 */
                int responseCode = httpURLConnection.getResponseCode();
                Log.i("Data", String.valueOf(responseCode));

                /**
                 * Получение данных из потока в виде JSON-объекта.
                 */
                is = httpURLConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            /**
             * Свойство - код ответа от методов сервера.
             *
             * ВНИМАНИЕ!
             *
             * Этот параметр не имеет никакого отношения к кодам состояния.
             * Он формируется на сервере в зависимости от результата проведения обработки данных.
             *
             **/
            int resultCode;

            /** Свойство - полученный JSON–объект*/
            JSONObject dataJsonObj;

            try {
                /**
                 * Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);
                resultCode = Integer.parseInt(dataJsonObj.getString(ADD_CONTACT));

                Button btnAddIntoContacts = (Button) viewUserInteraction
                        .findViewById(R.id.btnAddToContacts);

                switch (resultCode) {

                    /**
                     * Владелец открытого профиля удален из контактов пользователя,
                     *                          авторизованного на данном устройвстве.
                     **/
                    case 401:
                        btnAddIntoContacts.setText(R.string.user_add_into_contacts);
                        break;

                    /**
                     * Владелец открытого профиля добавлен в контакты пользователя,
                     *                          авторизованного на данном устройвстве.
                     **/
                    case 400:
                        btnAddIntoContacts.setText(R.string.user_remove_from_contacts);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}