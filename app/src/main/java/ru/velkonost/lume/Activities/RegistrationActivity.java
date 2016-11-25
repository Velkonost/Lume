package ru.velkonost.lume.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.EMAIL;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.PASSWORD;
import static ru.velkonost.lume.Constants.REGISTRATION;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_REGISTRATION_METHOD;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.inititializeAlertDialog;
import static ru.velkonost.lume.Managers.PhoneDataStorage.saveText;
import static ru.velkonost.lume.net.ServerConnection.getJSON;


/**
 * @author Velkonost
 *
 * Класс для регистрации пользователя в приложении.
 *
 */
public class RegistrationActivity extends Activity {

    private static final int LAYOUT = R.layout.activity_registration;

    /** Cвойство - введенный пользователем логин */
    private EditText inputLogin;

    /** Cвойство - введенный пользователем пароль */
    private EditText inputPassword;

    /** Cвойство - введенный пользователем повтор пароля */
    private EditText inputRepeatPassword;

    /** Cвойство - введенный пользователем email */
    private EditText inputEmail;

    /** Cвойство - кнопка подтверждения отправления данных на сервер */
    protected Button btnRegistrationCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        /** Прикрепляет поля к view-элементам */
        inputLogin = (EditText) findViewById(R.id.loginRegistration);
        inputPassword = (EditText) findViewById(R.id.passwordRegistration);
        inputRepeatPassword = (EditText) findViewById(R.id.repeatPasswordRegistration);
        inputEmail = (EditText) findViewById(R.id.emailRegistration);

        btnRegistrationCommit = (Button) findViewById(R.id.btnRegistration);

        /**
         * Ставит обработчик событий на кнопку {@link RegistrationActivity#btnRegistrationCommit}
         **/
        btnRegistrationCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Если поля заполнены и повтор пароля введен правильно,
                 * тогда запускается параллельый поток,
                 *      в котором происходит взаимодействие с сервером
                 * */
                if (inputLogin.getText().length() != 0
                        && inputPassword.getText().length() != 0
                        && inputRepeatPassword.getText().length() != 0
                        && inputEmail.getText().length() != 0)
                    if ((inputPassword.getText().toString()).
                            equals(inputRepeatPassword.getText().toString()))
                        new SignUp().execute();
                    else

                    /**
                     * Иначе формируется уведомление об ошибке.
                     */
                        inititializeAlertDialog(RegistrationActivity.this,
                            getResources().getString(R.string.registration_error),
                            getResources().getString(R.string.not_coincide_passwords),
                            getResources().getString(R.string.btn_ok));
            }
        });
    }

    /**
     * Класс для работы с сервером, работает в параллельном потоке.
     **/
    class SignUp extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String registrationURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_REGISTRATION_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = LOGIN + EQUALS
                    + inputLogin.getText().toString() + AMPERSAND + PASSWORD
                    + EQUALS + inputPassword.getText().toString() + AMPERSAND
                    + EMAIL + EQUALS + inputEmail.getText().toString();

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(registrationURL, params);
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
                resultCode = Integer.parseInt(dataJsonObj.getString(REGISTRATION));

                /**
                 * Обработка полученного кода ответа.
                 */
                switch (resultCode) {

                    /**
                     * В случае успешного выполнения.
                     **/
                    case 100:

                        /**
                         * Получение id вошедшего пользователя, запись его в файл на устройстве.
                         */
                        String id = dataJsonObj.getString(ID);
                        saveText(RegistrationActivity.this, ID, id);

                        /**
                         * Переход на новую активность - профиль вошедшего пользователя.
                         * {@link Initializations#changeActivityCompat(Activity, Intent)}
                         */
                        Intent profileIntent = new Intent(RegistrationActivity.this, ProfileActivity.class);
                        changeActivityCompat(RegistrationActivity.this, profileIntent);
                        finish();
                        break;

                    /**
                     * Email уже занят.
                     **/
                    case 101:

                        /**
                         * Формирование уведомления об ошибке.
                         */
                        inititializeAlertDialog(RegistrationActivity.this,
                                getResources().getString(R.string.registration_error),
                                getResources().getString(R.string.email_already_exist),
                                getResources().getString(R.string.btn_ok));
                        break;

                    /**
                     * Логин уже занят.
                     **/
                    case 102:

                        /**
                         * Формирование уведомления об ошибке.
                         */
                        inititializeAlertDialog(RegistrationActivity.this,
                                getResources().getString(R.string.registration_error),
                                getResources().getString(R.string.login_already_exist),
                                getResources().getString(R.string.btn_ok));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
