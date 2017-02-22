package ru.velkonost.lume.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.velkonost.lume.Managers.InitializationsManager;
import ru.velkonost.lume.Managers.PhoneDataStorageManager;
import ru.velkonost.lume.R;
import ru.velkonost.lume.Managers.TypefaceUtil;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.PASSWORD;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_REGISTRATION_METHOD;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.Managers.InitializationsManager.inititializeAlertDialog;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.saveText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;
import static ru.velkonost.lume.net.ServerConnection.hasConnection;


/**
 * @author Velkonost
 * Стартовая активность, предлагающая зарегистрироваться или войти.
 * Если пользователь входил ранее, то сразу перебрасывается на страницу своего профиля.
 */
public class WelcomeActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_welcome;

    /** Намерение на следующую активность */
    private Intent mIntentNext;

    /** Cвойство - введенный пользователем логин */
    private EditText inputLogin;
    /** Cвойство - введенный пользователем пароль */
    private EditText inputPassword;

    /**
     * Свойство - описание верхней панели инструментов приложения.
     */
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /** {@link InitializationsManager#initToolbar(Toolbar, int)}  */
        initToolbar(WelcomeActivity.this, toolbar, ""); /** Инициализация */

        /** Прикрепляет поля к view-элементам */
        inputLogin = (EditText) findViewById(R.id.loginLogIn);
        inputPassword = (EditText) findViewById(R.id.passwordLogIn);


        Drawable drawableLogin = inputLogin.getBackground(); // get current EditText drawable
        drawableLogin.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); // change the drawable color

        Drawable drawablePassword = inputPassword.getBackground(); // get current EditText drawable
        drawablePassword.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); // change the drawable color

        if (Build.VERSION.SDK_INT > 16) {
            inputLogin.setBackground(drawableLogin); // set the new drawable to EditText
            inputPassword.setBackground(drawablePassword); // set the new drawable to EditText
        } else {
            inputLogin.setBackgroundDrawable(drawableLogin); // use setBackgroundDrawable because setBackground required API 16
            inputPassword.setBackgroundDrawable(drawablePassword); // use setBackgroundDrawable because setBackground required API 16
        }

        /**
         * Проверяет интернет-соединение на данном устройстве.
         * {@link WelcomeActivity#checkConnection()}
         **/
        checkConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Проверяет отсутствие интернет-соединения.
     * Показывает соответствующее уведомление.
     * {@link InitializationsManager#inititializeAlertDialog(Context, String, String, String)}
     */
    private void checkConnection(){

        /**
         * При отсутствии интернет-соединения появляется соответствующее уведомление.
         * {@link ru.velkonost.lume.net.ServerConnection#hasConnection(Context)}
         */
        if (!hasConnection(this)) {
            inititializeAlertDialog(WelcomeActivity.this,
                    getResources().getString(R.string.connection_error),
                    getResources().getString(R.string.no_connection),
                    getResources().getString(R.string.btn_ok));

            /**
             * Закрашивает кнопки, делает некликабельными.
             */

            inputLogin.setEnabled(false);
            inputPassword.setEnabled(false);
        }

        /**
         * Запускает проверку при создании активности, если имеется интернет-соединение.
         * {@link WelcomeActivity#checkCookieId()}
         */
        else checkCookieId();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getGroupId();

        switch (id){
            case 0:

                /**
                 * Отправляем данные на сервер.
                 */
                if (inputLogin.getText().length() != 0
                        && inputPassword.getText().length() != 0)
                    new SignIn().execute();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Функция служит для проверки того, заходил ли пользователь с этого устройства ранее.
     * При положительном ответе перебрасывает на профиль пользователя
     * {@link PhoneDataStorageManager#loadText(Context, String)}
     */
    private void checkCookieId() {
        String cookieId = loadText(WelcomeActivity.this, ID);

        /**
         * Проверяет на наличие своеобразных "cookie" пользователя.
         * В случае положительного ответа переходим на профиль пользователя.
         * {@link ProfileActivity}
         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
         */
        if(cookieId.length() != 0){
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            changeActivityCompat(WelcomeActivity.this, profileIntent);
            finish();
        }
    }

    class SignIn extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {
            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String loginURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_REGISTRATION_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = LOGIN + EQUALS
                    + inputLogin.getText().toString() + AMPERSAND + PASSWORD
                    + EQUALS + inputPassword.getText().toString();

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(loginURL, params);
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
                resultCode = Integer.parseInt(dataJsonObj.getString(LOGIN));

                /**
                 * Обработка полученного кода ответа.
                 */
                switch (resultCode){



                    /**
                     * В случае успешного выполнения.
                     **/
                    case 100:

                        /**
                         * Получение id вошедшего пользователя, запись его в файл на устройстве.
                         */
                        String id = dataJsonObj.getString(ID);
                        saveText(WelcomeActivity.this, ID, id);
                        saveText(WelcomeActivity.this, LOGIN, inputLogin.getText().toString());

                        /**
                         * Переход на новую активность - профиль вошедшего пользователя.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         */
                        Intent profileIntent = new Intent(WelcomeActivity.this, ProfileActivity.class);
                        changeActivityCompat(WelcomeActivity.this, profileIntent);
                        finish();

                        break;

                    /**
                     * В случае успешного выполнения.
                     **/
                    case 200:
                        /**
                         * Получение id вошедшего пользователя, запись его в файл на устройстве.
                         */
                        String id2 = dataJsonObj.getString(ID);
                        saveText(WelcomeActivity.this, ID, id2);
                        saveText(WelcomeActivity.this, LOGIN, inputLogin.getText().toString());

                        /**
                         * Переход на новую активность - профиль вошедшего пользователя.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         */
                        Intent profileIntent2 = new Intent(WelcomeActivity.this, ProfileActivity.class);
                        changeActivityCompat(WelcomeActivity.this, profileIntent2);
                        finish();

                        break;
                    /**
                     * Неправильно введен логин или пароль.
                     **/
                    case 201:
                        /**
                         * Формирование уведомления об ошибке.
                         */
                        inititializeAlertDialog(WelcomeActivity.this,
                                getResources().getString(R.string.authorization_error),
                                getResources().getString(R.string.incorrectly_introduce_login_or_password),
                                getResources().getString(R.string.btn_ok));
                        break;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
