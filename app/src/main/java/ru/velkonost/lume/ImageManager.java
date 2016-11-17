package ru.velkonost.lume;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Velkonost
 *
 * Класс, обеспечивающий два метода,
 * которые позволяют устанавливать изображение в указанный ImageView,
 * скачанное по заданному URL
 * */
public class ImageManager {
    private final static String TAG = "ImageManager";

    /** Пустой конструктор */
    private ImageManager () {}

    /**
     * Метод, устанавливающий картинку, скачанную с интернета
     * @param iUrl Урл-адрес картинки
     * @param iView ImageView, для которого необходимо установить картинку
     * */
    public static void fetchImage(final String iUrl, final ImageView iView) {
        if ( iUrl == null || iView == null )
            return;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                /**
                 * Получение картинки из параллельного потока, установка в {@param iView}
                 */
                final Bitmap image = (Bitmap) message.obj;
                iView.setImageBitmap(image);
            }
        };

        final Thread thread = new Thread() {
            @Override
            public void run() {
                /**
                 * Скачивание изображение по заданному {@param iUrl}
                 * {@link ImageManager#downloadImage(String)}
                 * */
                final Bitmap image = downloadImage(iUrl);
                if (image != null) {
                    Log.v(TAG, "Got image by URL: " + iUrl);
                    /**
                     * Отправка полученных данных в handler,
                     * который отвечает за установку этой картинки на элемент ImageView.
                     */
                    final Message message = handler.obtainMessage(1, image);
                    handler.sendMessage(message);
                }
            }
        };
        /** Установка стандартного изображения, если не удалось скачать по заданному {@param iUrl} */
        iView.setImageResource(R.drawable.noavatar);

        /** Понижение приоритета потока, чтобы он не забирал ресурсы,
         *  необходимые для других частей приложения */
        thread.setPriority(3);
        thread.start();
    }

    /**
     * Функция скачивает картинку по полученному URL
     * @param iUrl Адрес картинки
     * @return Данные о картинке типа Bitmap
     */
    public static Bitmap downloadImage(String iUrl) {
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream buf_stream = null;

        try {
            Log.v(TAG, "Starting loading image by URL: " + iUrl);

            /**
             * Открывается новое соединение, выстраиваются необходимые параметры.
             */
            conn = (HttpURLConnection) new URL(iUrl).openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");

            /** Соединяемся */
            conn.connect();

            /**
             * Открытие потока.
             * Получение данных.
             * */
            buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);

            /** Конвертация данных в тип Bitmap */
            bitmap = BitmapFactory.decodeStream(buf_stream);

            /** Закрытие потока */
            buf_stream.close();
            /** Разрываем соединение */
            conn.disconnect();

            buf_stream = null;
            conn = null;

            /** Обработка ошибок: логирование */
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Url parsing was failed: " + iUrl);
        } catch (IOException ex) {
            Log.d(TAG, iUrl + " does not exists");
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "Out of memory!!!");
            return null;
        } finally {
            /**
             * Напоследок все за собой закрываем, если этого еще не сделали
             */
            if ( buf_stream != null )
                try { buf_stream.close(); } catch (IOException ex) {}
            if ( conn != null )
                conn.disconnect();
        }
        return bitmap;
    }
}