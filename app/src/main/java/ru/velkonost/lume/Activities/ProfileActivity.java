package ru.velkonost.lume.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.EachExceptionsHandler;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

import ru.velkonost.lume.Managers.ImageManager;
import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.R;

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
import static ru.velkonost.lume.Constants.JPG;
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
import static ru.velkonost.lume.Constants.URL.SERVER_UPLOAD_IMAGE_METHOD;
import static ru.velkonost.lume.Constants.USER_ID;
import static ru.velkonost.lume.Constants.WORK;
import static ru.velkonost.lume.Constants.WORK_EMAIL;
import static ru.velkonost.lume.Managers.ImageManager.fetchImage;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.Initializations.inititializeAlertDialog;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

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
    private Serializable profileId;

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

    CollapsingToolbarLayout collapsingToolbar;



    private CameraPhoto cameraPhoto;
    private GalleryPhoto galleryPhoto;
    final int GALLERY_REQUEST = 22131;
    final int CAMERA_REQUEST = 13323;
    private String selectedPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /** Установка темы по умолчанию */
        setTheme(R.style.AppDefault);

        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        /** Инициализация экземпляров классов */
        mGetData = new GetData();
        mAddContact = new AddContact();

        galleryPhoto = new GalleryPhoto(this);
        cameraPhoto = new CameraPhoto(this);


        linLayout = (LinearLayout) findViewById(R.id.profileContainer);
        ltInflater = getLayoutInflater();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(ProfileActivity.this, toolbar, R.string.app_name); /** Инициализация */
        initNavigationView(); /** Инициализация */

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(ProfileActivity.this, ID);

        Intent intent = getIntent();

        /**
         * Проверка:
         * Принадлежит открытый профиль пользователю,
         *      авторизованному на данном устройстве или нет?
         * */
        profileId = intent.getIntExtra(ID, 0) != 0
                ? intent.getIntExtra(ID, 0)
                : userId;

        Log.i("PID", String.valueOf(profileId));
        /**
         *  Установка цветной палитры,
         *  цвета которой будут заменять друг друга в зависимости от прогресса.
         * */
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorBlue, R.color.colorGreen,
//                R.color.colorYellow, R.color.colorRed);

//        /** Ставит обработчик событий */
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//
//            public void onRefresh() {
//                /** Выполнение происходит с задержкой в 2.5 секунды */
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        /**
//                         * Обновляет страницу.
//                         * {@link Initializations#changeActivityCompat(Activity, Intent)}
//                         * */
//                        changeActivityCompat(ProfileActivity.this);
//                    }
//                }, 2500);
//            }
//        });

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.noavatar);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = getResources().getColor(R.color.colorPrimary);
                collapsingToolbar.setContentScrimColor(mutedColor);
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
//                        nextIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
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
                + HYPHEN + month
                + HYPHEN + year;
    }

    /**
     * При полном закрытии активности удаляем информацию о владельце открытого профиля.
     **/
    @Override
    protected void onStop() {
        super.onStop();
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

            /** Свойство - код ответа, полученный от сервера */
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
            final JSONObject dataJsonObj;

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
                                + SLASH + profileId + JPG;

                        userAvatar = (ImageView) findViewById(R.id.imageAvatar);

                        /**
                         * Установка имени владельца открытого профиля.
                         *
                         * Если имя и фамилия не найдены,
                         * то устанавливается логин + показывается иконка {@link userWithoutName}
                         **/
                        final String sUserName = dataJsonObj.getString(NAME).length() == 0
                                ? dataJsonObj.getString(LOGIN)
                                : dataJsonObj.getString(SURNAME).length() == 0
                                ? dataJsonObj.getString(LOGIN)
                                : dataJsonObj.getString(NAME) + " " + dataJsonObj.getString(SURNAME);

                        collapsingToolbar.setTitle(sUserName);

                        /**
                         * Загрузка аватара пользователя
                         * {@link ImageManager#fetchImage(String, ImageView)}
                         **/
                        fetchImage(avatarURL, userAvatar);

                        userAvatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Bitmap bitmap = ((BitmapDrawable) userAvatar.getDrawable()).getBitmap();

                                if (profileId != userId) {
                                    Intent fullScreenIntent = new Intent(ProfileActivity.this, FullScreenPhotoActivity.class);

                                    fullScreenIntent.putExtra(NAME, sUserName);
                                    fullScreenIntent.putExtra(ID, profileId);
                                    try {
                                        fullScreenIntent.putExtra(AVATAR, dataJsonObj.getString(AVATAR));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ProfileActivity.this.startActivity(fullScreenIntent);
                                }
                                else {

                                    CharSequence[] data = {
                                            getResources().getString(R.string.dialog_item_open),
                                            getResources().getString(R.string.dialog_item_upload),
                                            getResources().getString(R.string.dialog_item_create)
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                    builder.setTitle(getResources().getString(R.string.dialog_header_photo))
                                            .setItems(data,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case 0:
                                                                    Intent fullScreenIntent = new Intent(ProfileActivity.this,
                                                                            FullScreenPhotoActivity.class);

                                                                    fullScreenIntent.putExtra(NAME, sUserName);
                                                                    fullScreenIntent.putExtra(ID, profileId);
                                                                    try {
                                                                        fullScreenIntent.putExtra(AVATAR, dataJsonObj.getString(AVATAR));
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    ProfileActivity.this.startActivity(fullScreenIntent);
                                                                    break;

                                                                case 1:
                                                                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                                                    //Из галереи
                                                                    startActivityForResult(intent, GALLERY_REQUEST);
                                                                    break;
                                                                case 2:
                                                                    try {
                                                                        //С камеры
                                                                        startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                                                                        cameraPhoto.addToGallery();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                            }
                                                        }
                                                    });

                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }
                        });


                        /**
                         * Если профиль не принадлежит авторизованному пользователю,
                         *      то добавляем модуль взаимодействия.
                         **/
                        if (!profileId.equals(userId)) {

                            /** Кнопка добавления/удаления владельца профиля из контактов авторизованного пользоавателя */
                            FloatingActionButton btnAddIntoContacts = (FloatingActionButton) findViewById(R.id.btnAddIntoContacts);

                            /** Кнопка открытия диалога между авторизованным пользователем и владельцем открытого профиля */
                            FloatingActionButton btnSendMessage = (FloatingActionButton) findViewById(R.id.btnSendMessage);

                            btnAddIntoContacts.setVisibility(View.VISIBLE);
                            btnSendMessage.setVisibility(View.VISIBLE);

                            /**
                             * Проверка, добавил ли {@link ProfileActivity#userId}
                             *         в контакты {@link ProfileActivity#profileId}
                             * */
                            isContact = dataJsonObj.getBoolean(CONTACT);
                            if (isContact)
                                btnAddIntoContacts.setImageResource(R.mipmap.ic_account_multiple_minus);


                            /** Создает обработчик событий */
                            btnAddIntoContacts.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    /** Открытие нового потока */
                                    mAddContact = new AddContact();
                                    mAddContact.execute();

                                }
                            });


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

                            viewUserPlaceWork = ltInflater.inflate(R.layout.item_profile_place_work,
                                    linLayout, false);
                            TextView userPlaceWork = (TextView) viewUserPlaceWork
                                    .findViewById(R.id.descriptionCardPlaceWork);
                            userPlaceWork.setText(sUserPlaceWork);

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserPlaceWork);
                        }

                        /** Если указано только место учебы */
                        if (sUserPlaceWork.equals("") && !sUserPlaceStudy.equals("")) {

                            viewUserPlaceStudy = ltInflater.inflate(R.layout.item_profile_place_study,
                                    linLayout, false);
                            TextView userPlaceStudy = (TextView) viewUserPlaceStudy
                                    .findViewById(R.id.descriptionCardPlaceStudy);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {//Проверка откуда была выбрана загрузка
            if (requestCode == GALLERY_REQUEST) {//Из галереи
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                selectedPhoto = galleryPhoto.getPath(); //Получаем путь

                //Загружаем на сервер
                try {
                    Bitmap bitmap = ImageLoader.init().from(selectedPhoto).getBitmap();
                    String encodedImage = ImageBase64.encode(bitmap);

                    HashMap<String, String> postData = new HashMap<String, String>();

                    postData.put("image", encodedImage);
                    postData.put("id", userId);

                    PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if (s.contains("500")) {
                                Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                changeActivityCompat(ProfileActivity.this);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    task.execute(
                            SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                                    + SERVER_UPLOAD_IMAGE_METHOD
                    );

                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(ProfileActivity.this, "Error with connect", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Toast.makeText(ProfileActivity.this, "Error with url", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Toast.makeText(ProfileActivity.this, "Error with protocol", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Toast.makeText(ProfileActivity.this, "Error with encode", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

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


                FloatingActionButton btnAddIntoContacts = (FloatingActionButton)
                        findViewById(R.id.btnAddIntoContacts);

                switch (resultCode) {

                    /**
                     * Владелец открытого профиля удален из контактов пользователя,
                     *                          авторизованного на данном устройвстве.
                     **/
                    case 401:
                        btnAddIntoContacts.setImageResource(R.mipmap.ic_account_multiple_plus);
                        break;

                    /**
                     * Владелец открытого профиля добавлен в контакты пользователя,
                     *                          авторизованного на данном устройвстве.
                     **/
                    case 400:
                        btnAddIntoContacts.setImageResource(R.mipmap.ic_account_multiple_minus);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}