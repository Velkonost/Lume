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
import static ru.velkonost.lume.Initializations.inititializeAlertDialog;
import static ru.velkonost.lume.PhoneDataStorage.saveText;


public class LoginActivity extends Activity{

    private static final int LAYOUT = R.layout.activity_login;


    public static String LOG_TAG = "my_log";
    private EditText inputLogin;
    private EditText inputPassword;
    private Button btnLogInCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        inputLogin = (EditText) findViewById(R.id.loginLogIn);
        inputPassword = (EditText) findViewById(R.id.passwordLogIn);
        btnLogInCommit = (Button) findViewById(R.id.btnLogIn);

        btnLogInCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputLogin.getText().length() != 0
                        && inputPassword.getText().length() != 0)
                new SignIn().execute();
            }
        });
    }
    class SignIn extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {
            String loginURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_ACCOUNT_SCRIPT
                    + SERVER_LOGIN_METHOD;
            @SuppressWarnings("WrongThread") String params = LOGIN + EQUALS
                    + inputLogin.getText().toString() + AMPERSAND + PASSWORD
                    + EQUALS + inputPassword.getText().toString();

            byte[] data;
            InputStream is;
            BufferedReader reader;
            String resultJson = "";


            try {
                URL url = new URL(loginURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestProperty("Content-Length", ""
                        + Integer.toString(params.getBytes().length));
                OutputStream os = httpURLConnection.getOutputStream();
                data = params.getBytes("UTF-8");
                os.write(data);

                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                Log.i("Data", String.valueOf(responseCode));

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

            int resultCode;
            JSONObject dataJsonObj;

            try {
                dataJsonObj = new JSONObject(strJson);
                resultCode = Integer.parseInt(dataJsonObj.getString(LOGIN));

                switch (resultCode){
                    case 200:
                        String id = dataJsonObj.getString(ID);
                        saveText(LoginActivity.this, ID, id);
                        Intent profileIntent = new Intent(LoginActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        finish();
                        break;
                    case 201:
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

