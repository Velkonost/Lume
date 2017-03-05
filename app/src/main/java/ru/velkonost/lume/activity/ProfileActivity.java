package ru.velkonost.lume.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.eyalbira.loadingdots.LoadingDots;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Managers.InitializationsManager;
import ru.velkonost.lume.Managers.PhoneDataStorageManager;
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.R;
import ru.velkonost.lume.patterns.SecretTextView;

import static ru.velkonost.lume.Constants.ADDRESSEE_ID;
import static ru.velkonost.lume.Constants.ADD_CONTACT;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BIRTHDAY;
import static ru.velkonost.lume.Constants.CAMERA_REQUEST;
import static ru.velkonost.lume.Constants.CITY;
import static ru.velkonost.lume.Constants.CONTACT;
import static ru.velkonost.lume.Constants.COUNTRY;
import static ru.velkonost.lume.Constants.DIALOG_ID;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.GALLERY_REQUEST;
import static ru.velkonost.lume.Constants.GET_DATA;
import static ru.velkonost.lume.Constants.GET_EDIT_RESULT;
import static ru.velkonost.lume.Constants.GET_ID;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.IMAGE;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.RESULT.ERROR;
import static ru.velkonost.lume.Constants.RESULT.ERROR_WITH_CONNECTION;
import static ru.velkonost.lume.Constants.RESULT.ERROR_WITH_ENCODING;
import static ru.velkonost.lume.Constants.RESULT.ERROR_WITH_PROTOCOL;
import static ru.velkonost.lume.Constants.RESULT.ERROR_WITH_URL;
import static ru.velkonost.lume.Constants.RESULT.SUCCESS;
import static ru.velkonost.lume.Constants.SENDER_ID;
import static ru.velkonost.lume.Constants.SEND_ID;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.STUDY;
import static ru.velkonost.lume.Constants.SURNAME;
import static ru.velkonost.lume.Constants.UPLOAD_IMAGE_SUCCESS_CODE;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_CONTACT_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_CREATE_DIALOG_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_DIALOG_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_DATA_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Constants.URL.SERVER_SHORT_EDIT_PARAMETERS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_UPLOAD_IMAGE_METHOD;
import static ru.velkonost.lume.Constants.USER_ID;
import static ru.velkonost.lume.Constants.WORK;
import static ru.velkonost.lume.Constants.WORK_EMAIL;
import static ru.velkonost.lume.Managers.DateConverterManager.formatDate;
import static ru.velkonost.lume.Managers.DateConverterManager.formatDateBack;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.Managers.InitializationsManager.inititializeAlertDialog;
import static ru.velkonost.lume.Managers.InitializationsManager.inititializeAlertDialogWithRefresh;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;
import static ru.velkonost.lume.Managers.SetImageManager.fetchImage;
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
     * Свойство - описание верхней панели инструментов приложения.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Свойство - описание {@link ProfileActivity#LAYOUT}.
     */
    @BindView(R.id.activity_profile)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation)
    NavigationView navigationView;

    /**
     * Свойство - view-элемент для размещения аватара пользователя.
     */
    @BindView(R.id.imageAvatar)
    ImageView userAvatar;

    @BindView(R.id.loadingDots)
    LoadingDots loadingDots;

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
    private int profileIdInt;
    private String profileIdString;

    /**
     * Свойство - состояние между
     * {@link ProfileActivity#userId} и {@link ProfileActivity#profileIdString}
     *
     * Положительный ответ означает, что {@link ProfileActivity#userId} уже добавлял в контакты
     * {@link ProfileActivity#profileIdString}
     * */
    protected boolean isContact;

    /**
     * Условный контейнер, в который помещаются все view-элементы, созданные программно.
     **/
    @BindView(R.id.profileContainer)
    LinearLayout linLayout;
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

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;


    /**
     * Свойство - захват фото с камеры.
     */
    private CameraPhoto cameraPhoto;

    /**
     * Свойство - выбор фото из галереи устройства.
     */
    private GalleryPhoto galleryPhoto;

    /** Кнопка добавления/удаления владельца профиля из контактов авторизованного пользоавателя */
    @BindView(R.id.btnAddIntoContacts)
    FloatingActionButton btnAddIntoContacts;

    /** Кнопка открытия диалога между авторизованным пользователем и владельцем открытого профиля */
    @BindView(R.id.btnSendMessage)
    FloatingActionButton btnSendMessage;

    private TextView navHeaderLogin;

    private String sUserName;

    /**
     * Свойство - элемент для выбора даты.
     */
    private DatePickerDialog dateBirdayDatePicker;

    private String sUserPlaceLiving;
    private String sUserPlaceStudy;
    private String sUserPlaceWork;
    private String sUserBirthday;
    private String sUserWorkingEmail;

    private SecretTextView userBirthday;

    private EditText editPlaceLiving;
    private EditText editUserPlaceStudy;
    private EditText editUserPlaceWork;
    private EditText editUserWorkingEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        ButterKnife.bind(this);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");


        /** Инициализация экземпляров классов */
        mGetData = new GetData();
        mAddContact = new AddContact();

        galleryPhoto = new GalleryPhoto(this);
        cameraPhoto = new CameraPhoto(this);

        ltInflater = getLayoutInflater();

        /** {@link InitializationsManager#initToolbar(Toolbar, int)}  */
        initToolbar(ProfileActivity.this, toolbar, ""); /** Инициализация */

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorageManager#loadText(Context, String)}
         **/
        userId = loadText(ProfileActivity.this, ID);

        Intent intent = getIntent();

        /**
         * Проверка:
         * Принадлежит открытый профиль пользователю,
         *      авторизованному на данном устройстве или нет?
         * */
        profileIdInt = intent.getIntExtra(ID, Integer.parseInt(userId));
        profileIdString = String.valueOf(profileIdInt);

        initNavigationView(); /** Инициализация */

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.noavatar);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbar.setContentScrimColor(ContextCompat
                        .getColor(ProfileActivity.this, R.color.colorPrimary));
            }
        });

        /**
         * Кнопка возврата на предыдущую активность, если текущий профиль не принадлежит пользователю,
         *          авторизованному на данном устройстве.
         */
        if (!(profileIdString.equals(userId))) {
            toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        /** Обращаемся к серверу */
        mGetData.execute();
    }


    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View header = navigationView.getHeaderView(0);

        navHeaderLogin = ButterKnife.findById(header, R.id.userNameHeader);
        ImageView askQuestion = ButterKnife.findById(header, R.id.askQuestion);

        navHeaderLogin.setText(loadText(ProfileActivity.this, LOGIN));


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
                        changeActivityCompat(ProfileActivity.this,
                                new Intent(ProfileActivity.this, FAQBotActivity.class));
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

                        if ((profileIdString.equals(userId))) {
                            sUserPlaceLiving = editPlaceLiving.getText().toString();
                            sUserPlaceStudy = editUserPlaceStudy.getText().toString();
                            sUserPlaceWork = editUserPlaceWork.getText().toString();
                            sUserBirthday = userBirthday.getText().toString();
                            sUserWorkingEmail = editUserWorkingEmail.getText().toString();

                            new PostData().execute();

                        }

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(ProfileActivity.this,
                                new Intent(ProfileActivity.this, ProfileActivity.class));
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
                        nextIntent = new Intent(ProfileActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(ProfileActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        nextIntent = new Intent(ProfileActivity.this, BoardsListActivity.class);
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
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
                        changeActivityCompat(ProfileActivity.this, nextIntent);
                    }
                }, 350);

                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(ProfileActivity.this, ID).equals(""))
                    finishAffinity();

                drawerLayout.closeDrawer(GravityCompat.START);

                return false;
            }
        });
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
     * При полном закрытии активности удаляем информацию о владельце открытого профиля.
     **/
    @Override
    protected void onStop() {
        super.onStop();
        deleteText(ProfileActivity.this, USER_ID);

        if ((profileIdString.equals(userId))) {

            sUserPlaceLiving = editPlaceLiving.getText().toString();
            sUserPlaceStudy = editUserPlaceStudy.getText().toString();
            sUserPlaceWork = editUserPlaceWork.getText().toString();
            sUserBirthday = userBirthday.getText().toString();
            sUserWorkingEmail = editUserWorkingEmail.getText().toString();

            new PostData().execute();

        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                ProfileActivity.this.getResources().getDisplayMetrics());
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
                    + AMPERSAND + USER_ID + EQUALS + profileIdString;

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
                                + SLASH + profileIdString + JPG;

                        /**
                         * Установка имени владельца открытого профиля.
                         *
                         * Если имя и фамилия не найдены,
                         * то устанавливается логин + показывается иконка {@link userWithoutName}
                         **/
                        sUserName = dataJsonObj.getString(NAME).length() == 0
                                ? dataJsonObj.getString(LOGIN)
                                : dataJsonObj.getString(SURNAME).length() == 0
                                ? dataJsonObj.getString(LOGIN)
                                : dataJsonObj.getString(NAME) + " " + dataJsonObj.getString(SURNAME);

                        collapsingToolbar.setTitle(sUserName);

                        fetchImage(avatarURL, userAvatar, false, false);

                        /**
                         * Слушатель на аватар открытого профиля.
                         */
                        userAvatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                /**
                                 * Если профиль не принадлежит авторизованному пользователю.
                                 */
                                if (!profileIdString.equals(userId)) {
                                    /**
                                     * То при нажатии сразу открывает аватар на весь экран.
                                     * {@link FullScreenPhotoActivity}
                                     */
                                    Intent fullScreenIntent = new Intent(ProfileActivity.this, FullScreenPhotoActivity.class);

                                    fullScreenIntent.putExtra(NAME, sUserName);
                                    fullScreenIntent.putExtra(ID, profileIdString);

                                    try {
                                        fullScreenIntent.putExtra(AVATAR, dataJsonObj.getString(AVATAR));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    ProfileActivity.this.startActivity(fullScreenIntent);
                                    overridePendingTransition(R.anim.activity_right_in,
                                            R.anim.activity_diagonaltranslate);

                                }
                                else {
                                    /**
                                     * Иначе открывает диалоговое окно.
                                     */

                                    CharSequence[] data = {
                                            getResources().getString(R.string.dialog_item_open),
                                            getResources().getString(R.string.dialog_item_upload),
                                            getResources().getString(R.string.dialog_item_create)
                                    };

                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(ProfileActivity.this);
                                    builder
                                            .setTitle(getResources().getString(R.string.dialog_header_photo))
                                            .setItems(data,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            switch (which) {
                                                                case 0:
                                                                    /**
                                                                     * Открывает аватар на весь экран.
                                                                     * {@link FullScreenPhotoActivity}
                                                                     */
                                                                    Intent fullScreenIntent = new Intent(
                                                                            ProfileActivity.this,
                                                                            FullScreenPhotoActivity.class
                                                                    );

                                                                    fullScreenIntent.putExtra(NAME, sUserName);
                                                                    fullScreenIntent.putExtra(ID, profileIdString);

                                                                    try {
                                                                        fullScreenIntent.putExtra(AVATAR,
                                                                                dataJsonObj.getString(AVATAR));
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    ProfileActivity.this
                                                                            .startActivity(fullScreenIntent);
                                                                    overridePendingTransition(R.anim.activity_right_in,
                                                                            R.anim.activity_diagonaltranslate);

                                                                    break;

                                                                case 1:
                                                                    /**
                                                                     * Открывает галерею для выбора фото.
                                                                     */
                                                                    Intent intent = new Intent(
                                                                            Intent.ACTION_PICK,
                                                                            android.provider.MediaStore
                                                                                    .Images.Media
                                                                                    .EXTERNAL_CONTENT_URI
                                                                    );

                                                                    startActivityForResult(intent,
                                                                            GALLERY_REQUEST);
                                                                    break;
                                                                case 2:
                                                                    /**
                                                                     * Включает камеру для совершения снимка.
                                                                     */
                                                                    try {
                                                                        startActivityForResult(
                                                                                cameraPhoto.takePhotoIntent(),
                                                                                CAMERA_REQUEST)
                                                                        ;
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
                        if (!profileIdString.equals(userId)) {

                            btnAddIntoContacts
                                    .setBackgroundTintList(ColorStateList.valueOf(getResources()
                                            .getColor(R.color.colorPurpleDark)));
                            btnSendMessage
                                    .setBackgroundTintList(ColorStateList.valueOf(getResources()
                                            .getColor(R.color.colorPurpleDark)));

                            btnAddIntoContacts.setVisibility(View.VISIBLE);
                            btnSendMessage.setVisibility(View.VISIBLE);

                            /**
                             * Проверка, добавил ли {@link ProfileActivity#userId}
                             *         в контакты {@link ProfileActivity#profileId}
                             * */
                            isContact = dataJsonObj.getBoolean(CONTACT);
                            if (isContact)
                                btnAddIntoContacts
                                        .setImageResource(R.mipmap.ic_account_multiple_minus);


                            /** Создает обработчик событий */
                            btnAddIntoContacts.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    /** Открытие нового потока */
                                    mAddContact = new AddContact();
                                    mAddContact.execute();
                                }
                            });

                            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    try {
                                        if (dataJsonObj.getInt(DIALOG_ID) != -1){

                                            Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra(DIALOG_ID, dataJsonObj.getInt(DIALOG_ID));
                                            intent.putExtra(ID, Integer.parseInt(userId));
                                            intent.putExtra(NAME, sUserName);

                                            ProfileActivity.this.startActivity(intent);
                                            overridePendingTransition(R.anim.activity_right_in,
                                                    R.anim.activity_diagonaltranslate);

                                        } else {
                                            CreateDialog mCreateDialog = new CreateDialog();
                                            mCreateDialog.execute();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }

                        /**
                         * Формирование места жительства владельца открытого профиля.
                         **/
                        viewUserPlaceLiving = ltInflater
                                .inflate(R.layout.item_profile_place_living, linLayout, false);

                        SecretTextView userPlaceLiving = ButterKnife.findById(viewUserPlaceLiving,
                                R.id.descriptionCardPlaceLiving);

                        SecretTextView titleUserPlaceLiving = ButterKnife.findById(viewUserPlaceLiving,
                                R.id.titleCardPlaceLiving);

                        editPlaceLiving = ButterKnife.findById(viewUserPlaceLiving,
                                R.id.editPlaceLiving);

                        /**
                         * Формируется место проживания из имеющихся данных.
                         **/
                        sUserPlaceLiving = dataJsonObj.getString(CITY).length() != 0
                                ? dataJsonObj.getString(COUNTRY).length() != 0
                                ? dataJsonObj.getString(CITY) + ", " + dataJsonObj.getString(COUNTRY)
                                : dataJsonObj.getString(CITY)
                                : "";

                        userPlaceLiving.setText(sUserPlaceLiving);
                        editPlaceLiving.setText(sUserPlaceLiving);

                        if (profileIdString.equals(userId)) {
                            ViewSwitcher switcher = ButterKnife.findById(viewUserPlaceLiving,
                                    R.id.switcherPlaceLiving);
                            switcher.showNext();
                        }

                        /**
                         * Если данные введены, то добавляем элемент в контейнер.
                         **/
                        if (!sUserPlaceLiving.equals("") || profileIdString.equals(userId))
                            linLayout.addView(viewUserPlaceLiving);

                        titleUserPlaceLiving.setDuration(1000);
                        titleUserPlaceLiving.show();

                        userPlaceLiving.setDuration(1500);
                        userPlaceLiving.show();


                        /** Формирование даты рождения владельца открытого профиля */
                        viewUserBirthday = ltInflater.inflate(R.layout.item_profile_birthday,
                                linLayout, false);

                        userBirthday = ButterKnife.findById(viewUserBirthday,
                                R.id.descriptionCardBirthday);

                        SecretTextView titleUserBirthday = ButterKnife.findById(viewUserBirthday,
                                R.id.titleCardBirthday);

                        sUserBirthday = dataJsonObj.getString(BIRTHDAY).length() != 0
                                ? dataJsonObj.getString(BIRTHDAY)
                                : "";

                        /**
                         * Форматирование даты.
                         * {@link ProfileActivity#formatDate(String)}
                         **/
                        String formattedUserBirthday = formatDate(sUserBirthday);
                        userBirthday.setText(formattedUserBirthday);

                        if (profileIdString.equals(userId)) {
                            LinearLayout.LayoutParams params
                                    = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                            params.setMargins(dp2px(10), 0, 0, 0);

                            userBirthday.setLayoutParams(params);

                            userBirthday.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    /**
                                     * Использует для получения даты.
                                     */
                                    Calendar newCalendar = Calendar.getInstance();

                                    /**
                                     * Требуется для дальнейшего преобразования даты в строку.
                                     */
                                    @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat
                                            = new SimpleDateFormat("dd-MM-yyyy");

                                    /**
                                     * Создает объект и инициализирует обработчиком события выбора даты и данными для даты по умолчанию.
                                     */
                                    dateBirdayDatePicker = new DatePickerDialog(ProfileActivity.this,
                                            new DatePickerDialog.OnDateSetListener() {
                                        // функция onDateSet обрабатывает шаг 2: отображает выбранные нами данные в элементе EditText
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            Calendar newCal = Calendar.getInstance();
                                            newCal.set(year, monthOfYear, dayOfMonth);
                                            userBirthday.setText(dateFormat.format(newCal.getTime()));
                                        }
                                    },
                                            newCalendar.get(Calendar.YEAR),
                                            newCalendar.get(Calendar.MONTH),
                                            newCalendar.get(Calendar.DAY_OF_MONTH));


                                    dateBirdayDatePicker.show();
                                }
                            });
                        }

                        /** Если владелец открытого профиля указывал дату своего рождения */
                        if (!formattedUserBirthday.equals("00-00-0000") || profileIdString.equals(userId))
                            linLayout.addView(viewUserBirthday);

                        titleUserBirthday.setDuration(1500);
                        titleUserBirthday.show();

                        userBirthday.setDuration(1500);
                        userBirthday.show();


                        /** Формирование места учебы пользователя */
                        sUserPlaceStudy = dataJsonObj.getString(STUDY).length() != 0
                                ? dataJsonObj.getString(STUDY)
                                : "";

                        /** Формирование текущего места работы пользователя */
                        sUserPlaceWork = dataJsonObj.getString(WORK).length() != 0
                                ? dataJsonObj.getString(WORK)
                                : "";

                        /** Если указано только место работы */
                        if (sUserPlaceStudy.equals("")
                                && !sUserPlaceWork.equals("")
                                && !profileIdString.equals(userId)) {

                            viewUserPlaceWork = ltInflater.inflate(R.layout.item_profile_place_work,
                                    linLayout, false);

                            SecretTextView userPlaceWork = ButterKnife.findById(viewUserPlaceWork,
                                    R.id.descriptionCardPlaceWork);

                            SecretTextView titleUserPlaceWork = ButterKnife.findById(viewUserPlaceWork,
                                    R.id.titleCardPlaceWork);

                            userPlaceWork.setText(sUserPlaceWork);

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserPlaceWork);

                            titleUserPlaceWork.setDuration(2000);
                            titleUserPlaceWork.show();

                            userPlaceWork.setDuration(2000);
                            userPlaceWork.show();
                        }

                        /** Если указано только место учебы */
                        if (sUserPlaceWork.equals("")
                                && !sUserPlaceStudy.equals("")
                                && !profileIdString.equals(userId)) {

                            viewUserPlaceStudy = ltInflater.inflate(R.layout.item_profile_place_study,
                                    linLayout, false);

                            SecretTextView userPlaceStudy = ButterKnife.findById(viewUserPlaceStudy,
                                    R.id.descriptionCardPlaceStudy);

                            SecretTextView titleUserPlaceStudy = ButterKnife.findById(viewUserPlaceStudy,
                                    R.id.titleCardPlaceStudy);

                            userPlaceStudy.setText(sUserPlaceStudy);

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserPlaceStudy);

                            titleUserPlaceStudy.setDuration(2000);
                            titleUserPlaceStudy.show();

                            userPlaceStudy.setDuration(2000);
                            userPlaceStudy.show();
                        }

                        /** Если указаны место работы и место учебы */
                        if ((!sUserPlaceStudy.equals("") && !sUserPlaceWork.equals(""))
                                || profileIdString.equals(userId)) {

                            viewUserPlaceStudyAndWork = ltInflater
                                    .inflate(R.layout.item_profile_place_study_and_work,
                                            linLayout, false);

                            SecretTextView userPlaceStudy = ButterKnife.findById(viewUserPlaceStudyAndWork,
                                    R.id.descriptionCardPlaceStudy);

                            SecretTextView titleUserPlaceStudy = ButterKnife.findById(viewUserPlaceStudyAndWork,
                                    R.id.titleCardPlaceStudy);

                            editUserPlaceStudy = ButterKnife.findById(viewUserPlaceStudyAndWork,
                                    R.id.editPlaceStudy);

                            userPlaceStudy.setText(sUserPlaceStudy);
                            editUserPlaceStudy.setText(sUserPlaceStudy);

                            SecretTextView userPlaceWork = ButterKnife.findById(viewUserPlaceStudyAndWork,
                                    R.id.descriptionCardPlaceWork);

                            SecretTextView titleUserPlaceWork = ButterKnife.findById(viewUserPlaceStudyAndWork,
                                    R.id.titleCardPlaceWork);

                            editUserPlaceWork = ButterKnife.findById(viewUserPlaceStudyAndWork,
                                    R.id.editPlaceWork);

                            userPlaceWork.setText(sUserPlaceWork);
                            editUserPlaceWork.setText(sUserPlaceWork);

                            if (profileIdString.equals(userId)) {
                                ViewSwitcher switcher1 = ButterKnife.findById(viewUserPlaceStudyAndWork,
                                        R.id.switcherPlaceStudy);

                                ViewSwitcher switcher2 = ButterKnife.findById(viewUserPlaceStudyAndWork,
                                        R.id.switcherPlaceWork);

                                switcher1.showNext();
                                switcher2.showNext();
                            }

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserPlaceStudyAndWork);

                            titleUserPlaceWork.setDuration(2000);
                            titleUserPlaceWork.show();

                            userPlaceWork.setDuration(2000);
                            userPlaceWork.show();

                            titleUserPlaceStudy.setDuration(2000);
                            titleUserPlaceStudy.show();

                            userPlaceStudy.setDuration(2000);
                            userPlaceStudy.show();
                        }

                        /** Формирование рабочего email пользователя */
                        sUserWorkingEmail = dataJsonObj.getString(WORK_EMAIL).length() != 0
                                ? dataJsonObj.getString(WORK_EMAIL)
                                : "";

                        /** Если владелец открытого профиля указал рабочий email */
                        if (!sUserWorkingEmail.equals("") || profileIdString.equals(userId)) {

                            viewUserWorkingEmail = ltInflater
                                    .inflate(R.layout.item_profile_working_email, linLayout, false);

                            final SecretTextView userWorkingEmail = ButterKnife.findById(viewUserWorkingEmail,
                                    R.id.descriptionCardWorkingEmail);

                            SecretTextView titleUserWorkingEmail = ButterKnife.findById(viewUserWorkingEmail,
                                    R.id.titleCardWorkingEmail);

                            editUserWorkingEmail = ButterKnife.findById(viewUserWorkingEmail,
                                    R.id.editWorkingEmail);

                            userWorkingEmail.setText(sUserWorkingEmail);
                            editUserWorkingEmail.setText(sUserWorkingEmail);

                            if (profileIdString.equals(userId)) {
                                ViewSwitcher switcher = ButterKnife.findById(viewUserWorkingEmail,
                                        R.id.switcherWorkingEmail);

                                switcher.showNext();
                            }

                            /** Добавление элемента в контейнер {@link ProfileActivity#linLayout} */
                            linLayout.addView(viewUserWorkingEmail);

                            titleUserWorkingEmail.setDuration(3000);
                            titleUserWorkingEmail.show();

                            userWorkingEmail.setDuration(3000);
                            userWorkingEmail.show();

                        }

                        loadingDots.setVisibility(View.INVISIBLE);

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

        /**
         * Проверка, каким способом была загружена фотография.
         */
        if (resultCode == Activity.RESULT_OK) {

            /**
             * Адрес выбранной фотографии.
             */
            String selectedPhoto;

            /**
             * Фото из галереи.
             */
            if (requestCode == GALLERY_REQUEST) {

                /**
                 * Устанавливает путь до фотографии.
                 */
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                selectedPhoto = galleryPhoto.getPath();

                /**
                 * Загружает фотографию на сервер.
                 */
                try {

                    /**
                     * Данные фотографии кодируются на устройстве и раскодируют на сервере.
                     */
                    Bitmap bitmap = ImageLoader.init().from(selectedPhoto).getBitmap();
                    String encodedImage = ImageBase64.encode(bitmap);

                    HashMap<String, String> postData = new HashMap<>();

                    postData.put(IMAGE, encodedImage);
                    postData.put(ID, userId);


                    PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if (s.contains(UPLOAD_IMAGE_SUCCESS_CODE)) {
                                Toast.makeText(ProfileActivity.this, SUCCESS, Toast.LENGTH_SHORT).show();
                                changeActivityCompat(ProfileActivity.this);
                            } else {
                                Toast.makeText(ProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    /**
                     * Посылаем фото на сервер.
                     */
                    task.execute(
                            SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                                    + SERVER_UPLOAD_IMAGE_METHOD
                    );

                    /**
                     * Обработка возможных исключений.
                     */
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(ProfileActivity.this, ERROR_WITH_CONNECTION, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Toast.makeText(ProfileActivity.this, ERROR_WITH_URL, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Toast.makeText(ProfileActivity.this, ERROR_WITH_PROTOCOL, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Toast.makeText(ProfileActivity.this, ERROR_WITH_ENCODING, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            /**
             * Фото с камеры.
             */
            } else if(requestCode == CAMERA_REQUEST) {

                /**
                 * Получает путь.
                 */
                selectedPhoto = cameraPhoto.getPhotoPath();

                /**
                 * Загружает на сервер.
                 */
                try {

                    /**
                     * Данные фотографии кодируются на устройстве и раскодируют на сервере.
                     */
                    Bitmap bitmap = ImageLoader.init().from(selectedPhoto).getBitmap();
                    String encodedImage = ImageBase64.encode(bitmap);

                    HashMap<String, String> postData = new HashMap<>();

                    postData.put(IMAGE, encodedImage);
                    postData.put(ID, userId);

                    PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if (s.contains(UPLOAD_IMAGE_SUCCESS_CODE)) {
                                Toast.makeText(ProfileActivity.this, SUCCESS, Toast.LENGTH_SHORT).show();
                                changeActivityCompat(ProfileActivity.this);
                            } else {
                                Toast.makeText(ProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    /**
                     * Посылаем фото на сервер.
                     */
                    task.execute(
                            SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                                    + SERVER_UPLOAD_IMAGE_METHOD
                    );

                    /**
                     * Обработка возможных исключений.
                     */
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(ProfileActivity.this, ERROR_WITH_CONNECTION, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Toast.makeText(ProfileActivity.this, ERROR_WITH_URL, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Toast.makeText(ProfileActivity.this, ERROR_WITH_PROTOCOL, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Toast.makeText(ProfileActivity.this, ERROR_WITH_ENCODING, Toast.LENGTH_SHORT).show();
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
                    + AMPERSAND + GET_ID + EQUALS + profileIdString;

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
    private class CreateDialog extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object... strings) {
            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_DIALOG_SCRIPT
                    + SERVER_CREATE_DIALOG_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = SENDER_ID + EQUALS + userId
                    + AMPERSAND + ADDRESSEE_ID + EQUALS + profileIdString;

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

                Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(DIALOG_ID, dataJsonObj.getInt(DIALOG_ID));
                intent.putExtra(ID, profileIdString);
                intent.putExtra(NAME, sUserName);
                ProfileActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.activity_right_in,
                        R.anim.activity_diagonaltranslate);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Класс для отправки измененных данных о пользователе на сервер.
     */
    private class PostData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {


            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_SHORT_EDIT_PARAMETERS_METHOD;


            String live = sUserPlaceLiving;

            String city = "";
            String country = "";


            int i = 0;
            while (i < live.length() && live.charAt(i) != ',') {
                city += String.valueOf(live.charAt(i));
                i ++;
            }

            i ++;
            for (int j = i; j < live.length(); j++) {
                country += String.valueOf(live.charAt(j));
            }

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = USER_ID + EQUALS + userId
                    + AMPERSAND + CITY + EQUALS + city
                    + AMPERSAND + COUNTRY + EQUALS + country
                    + AMPERSAND + STUDY + EQUALS + sUserPlaceStudy
                    + AMPERSAND + WORK + EQUALS + sUserPlaceWork
                    + AMPERSAND + WORK_EMAIL + EQUALS + sUserWorkingEmail
                    + AMPERSAND + BIRTHDAY + EQUALS + formatDateBack(sUserBirthday);


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
                resultCode = Integer.parseInt(dataJsonObj.getString(GET_EDIT_RESULT));

                /**
                 * Обработка полученного кода ответа.
                 */
                switch (resultCode) {
                    /** В случае успешного выполнения */
                    case 700:

                        /**
                         * Переходим в профиль.
                         * Изменения прошли успешно.
                         */
//                        changeActivityCompat(SettingsActivity.this,
//                                new Intent(SettingsActivity.this, ProfileActivity.class));

                        break;
                    /**
                     * При попытке сменить пароль, текущий пароль был указан неверно.
                     * Изменение не вступили в силу.
                     **/
                    case 701:
                        /**
                         * Формирование уведомления об ошибке.
                         */
                        inititializeAlertDialogWithRefresh(ProfileActivity.this,
                                getResources().getString(R.string.password_error),
                                getResources().getString(R.string.refill_password_field),
                                getResources().getString(R.string.btn_ok),
                                ProfileActivity.this);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}