package ru.velkonost.lume.net;


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

    private static HttpURLConnection httpURLConnection = null;
    private static InputStream is = null;
    private static BufferedReader reader = null;
    private static byte[] data = null;

    /** Свойство - код ответа, полученных от сервера */
    private static String resultJson = "";

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
            StringBuilder buffer = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(is));

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
