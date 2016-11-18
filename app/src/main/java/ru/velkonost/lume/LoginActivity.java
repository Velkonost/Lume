package ru.velkonost.lume;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.PASSWORD;
import static ru.velkonost.lume.Constants.URL.SERVER_ACCOUNT_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_LOGIN_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Initializations.inititializeAlertDialog;
import static ru.velkonost.lume.PhoneDataStorage.saveText;

/**
 * @author Velkonost
 *
 * Класс для входа пользователя в приложение.
 *
 */
public class LoginActivity extends Activity{

    private static final int LAYOUT = R.layout.activity_login;

    /** Cвойство - введенный пользователем логин */
    private EditText inputLogin;
    /** Cвойство - введенный пользователем пароль */
    private EditText inputPassword;
    /** Cвойство - кнопка подтверждения отправления данных на сервер */
    private Button btnLogInCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        /** Прикрепляет поля к view-элементам */
        inputLogin = (EditText) findViewById(R.id.loginLogIn);
        inputPassword = (EditText) findViewById(R.id.passwordLogIn);
        btnLogInCommit = (Button) findViewById(R.id.btnLogIn);

        /**
         * Ставит обработчик событий на кнопку {@link LoginActivity#btnLogInCommit}
         **/
        btnLogInCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Если поля заполнены, тогда запускается параллельый поток,
                 *      в котором происходит взаимодействие с сервером
                 * */
                if (inputLogin.getText().length() != 0
                        && inputPassword.getText().length() != 0)
                new SignIn().execute();
            }
        });
    }

    /**
     * Класс для работы с сервером, работает в параллельном потоке.
     **/
    class SignIn extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {
            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String loginURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_LOGIN_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = LOGIN + EQUALS
                    + inputLogin.getText().toString() + AMPERSAND + PASSWORD
                    + EQUALS + inputPassword.getText().toString();

            byte[] data;
            InputStream is;
            BufferedReader reader;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            try {
                /**
                 * Устанавливает соединение.
                 */
                URL url = new URL(loginURL);
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
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();
                Log.i("RESULT", resultJson);

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
                resultCode = Integer.parseInt(dataJsonObj.getString(LOGIN));

                /**
                 * Обработка полученного кода ответа.
                 */
                switch (resultCode){
                    case 200:
                        /**
                         * Получение id вошедшего пользователя, запись его в файл на устройстве.
                         */
                        String id = dataJsonObj.getString(ID);
                        saveText(LoginActivity.this, ID, id);

                        /**
                         * Переход на новую активность - профиль вошедшего пользователя.
                         * {@link Initializations#changeActivityCompat(Activity, Intent)}
                         */
                        Intent profileIntent = new Intent(LoginActivity.this, ProfileActivity.class);
                        changeActivityCompat(LoginActivity.this, profileIntent);
                        finish();
                        break;
                    case 201:
                        /**
                         * Формирование уведомления об ошибке.
                         */
                        inititializeAlertDialog(LoginActivity.this,
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

