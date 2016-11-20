package ru.velkonost.lume;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Initializations.inititializeAlertDialog;
import static ru.velkonost.lume.PhoneDataStorage.loadText;
import static ru.velkonost.lume.net.ServerConnection.hasConnection;


/**
 * @author Velkonost
 * Стартовая активность, предлагающая зарегистрироваться или войти.
 * Если пользователь входил ранее, то сразу перебрасывается на страницу своего профиля.
 */
public class WelcomeActivity extends Activity {

    /** Намерение на следующую активность */
    private Intent mIntentNext;

    private static final int LAYOUT = R.layout.activity_welcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);


        /**
         * Проверяет интернет-соединение на данном устройстве.
         * {@link WelcomeActivity#checkConnection()}
         **/
        checkConnection();


//        checkCookieId();
    }

    /**
     * Проверяет отсутствие интернет-соединения.
     * Показывает соответствующее уведомление.
     * {@link Initializations#inititializeAlertDialog(Context, String, String, String)}
     */
    private void checkConnection(){

        /**
         * При отсутствии интернет-соединения появляется соответствующее уведомление.
         */
        if (!hasConnection(this)) {
            inititializeAlertDialog(WelcomeActivity.this,
                    getResources().getString(R.string.connection_error),
                    getResources().getString(R.string.no_connection),
                    getResources().getString(R.string.btn_ok));

            Button btnLogin = (Button) findViewById(R.id.btnLoginWelcome);
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed));
            btnLogin.setClickable(false);

            Button btnRegistration = (Button) findViewById(R.id.btnRegistrationWelcome);
            btnRegistration.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed));
            btnRegistration.setClickable(false);
        }

        /**
         * Запускает проверку при создании активности, если имеется интернет-соединение.
         * {@link WelcomeActivity#checkCookieId()}
         */
        else checkCookieId();
    }

    /**
     * Функция служит обработчкиком событий для кнопок
     * {@link WelcomeActivity#LAYOUT}
     * @param view Нажатая кнопка
     */
    public void chooseWayToEnter(View view) {
        switch (view.getId()) {
            /**
             * Если нажата кнопка регистрации - переходим в активность с регистрацией
             * {@link RegistrationActivity}
             */
            case R.id.btnRegistrationWelcome:
                mIntentNext = new Intent(this, RegistrationActivity.class);
                break;
            /**
             * Если нажата кнопка входа - переходим в активность со входом.
             * {@link LoginActivity}
             * {@link Initializations#changeActivityCompat(Activity, Intent)}
             */
            case R.id.btnLoginWelcome:
                mIntentNext = new Intent(this, LoginActivity.class);
                break;
        }
        changeActivityCompat(WelcomeActivity.this, mIntentNext);
        finish();
    }


    /**
     * Функция служит для проверки того, заходил ли пользователь с этого устройства ранее.
     * При положительном ответе перебрасывает на профиль пользователя
     * {@link PhoneDataStorage#loadText(Context, String)}
     */
    private void checkCookieId() {
        String cookieId = loadText(WelcomeActivity.this, ID);

        /**
         * Проверяет на наличие своеобразных "cookie" пользователя.
         * В случае положительного ответа переходим на профиль пользователя.
         * {@link ProfileActivity}
         * {@link Initializations#changeActivityCompat(Activity, Intent)}
         */
        if(cookieId.length() != 0){
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            changeActivityCompat(WelcomeActivity.this, profileIntent);
            finish();
        }
    }
}
