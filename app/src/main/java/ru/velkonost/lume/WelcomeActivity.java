package ru.velkonost.lume;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.PhoneDataStorage.loadText;

public class WelcomeActivity extends Activity {

    private Intent mIntentNext;
    private static final int LAYOUT = R.layout.activity_welcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        checkCookieId();
    }

    public void chooseWayToEnter(View view) {
        switch (view.getId()) {
            case R.id.btnRegistrationWelcome:
                mIntentNext = new Intent(this, RegistrationActivity.class);
                break;
            case R.id.btnLoginWelcome:
                mIntentNext = new Intent(this, LoginActivity.class);
                break;
        }
        startActivity(mIntentNext);
    }

    public void checkCookieId() {
        String cookieId = loadText(WelcomeActivity.this, ID);
        if(cookieId.length() != 0){
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);
        }
    }
}
