package ru.velkonost.lume.net;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author Velkonost
 *
 * Класс отвечает за соединение с сервером, отправку и получение данных.
 */
public class ServerConnection {

    /**
     * Свойство - http-соединение
     */
    private static HttpURLConnection httpURLConnection = null;

    /** Свойство - код ответа, полученных от сервера */
    private static String resultJson = "";

    /**
     * Проверка интернет-соединения.
     **/
    public static boolean hasConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Соединение, отправка данных, получение ответа
     **/
    public static String getJSON(String sUrl, String params) throws IOException {

        try {

            /**
             * Устанавливает соединение.
             */
            URL url = new URL(sUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();

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
            byte[] data = params.getBytes("UTF-8");
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
            InputStream is = httpURLConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            httpURLConnection.disconnect();
        }
        return resultJson;
    }
}
