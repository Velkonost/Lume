package ru.velkonost.lume;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Initializations.changeActivityCompat;
import static ru.velkonost.lume.PhoneDataStorage.loadText;


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
         * Запускает проверку при создании активности.
         * {@link WelcomeActivity#checkCookieId()}
         */
        checkCookieId();
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
    public void checkCookieId() {
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
