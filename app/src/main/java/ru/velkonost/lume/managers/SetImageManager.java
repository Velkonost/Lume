package ru.velkonost.lume.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.velkonost.lume.R;
import uk.co.senab.photoview.PhotoViewAttacher;

import static ru.velkonost.lume.Constants.TAG_IMAGE_MANAGER;


/**
 * @author Velkonost
 *
 * Класс, отвечающий за работу с картинками.
 *
 * Позволяет скачивать и устанавливать картинки из интернета.
 *
 * Позволяет делать форму отображения круглой.
 * */
public class SetImageManager {

    /** Пустой конструктор */
    private SetImageManager() {}

    /**
     * Метод, устанавливающий картинку, скачанную с интернета
     * @param iUrl Урл-адрес картинки
     * @param iView ImageView, для которого необходимо установить картинку
     * */
    public static void fetchImage(final String iUrl, final ImageView iView,
                                  final boolean isCircle, final boolean isAttachable) {
        if ( iUrl == null || iView == null )
            return;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                /**
                 * Получение картинки из параллельного потока, установка в {@param iView}
                 */
                Bitmap image = (Bitmap) message.obj;
                if (isCircle) image = getCircleMaskedBitmap(image, 25);
                iView.setImageBitmap(image);

                if (isAttachable)
                    new PhotoViewAttacher(iView);
            }
        };

        final Thread thread = new Thread() {
            @Override
            public void run() {
                /**
                 * Скачивание изображение по заданному {@param iUrl}
                 * {@link SetImageManager#downloadImage(String)}
                 * */
                final Bitmap image = downloadImage(iUrl);
                if (image != null) {
                    Log.v(TAG_IMAGE_MANAGER, "Got image by URL: " + iUrl);
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
            Log.v(TAG_IMAGE_MANAGER, "Starting loading image by URL: " + iUrl);

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
            Log.e(TAG_IMAGE_MANAGER, "Url parsing was failed: " + iUrl);
        } catch (IOException ex) {
            Log.d(TAG_IMAGE_MANAGER, iUrl + " does not exists");
        } catch (OutOfMemoryError e) {
            Log.w(TAG_IMAGE_MANAGER, "Out of memory!!!");
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


    /**
     * Функция приводит переданную картинку к заданному масштабу.
     *
     * @param source Переданная картинка.
     * @param size Требуемое значение для одной из сторон картинки.
     * @return Возвращает готовую картинку.
     */
    public static Bitmap scaleTo(Bitmap source, int size) {

        /**
         * Получаем размеры переданной картинки.
         **/
        int destWidth = source.getWidth();
        int destHeight = source.getHeight();

        /**
         * Приводим размеры в соответствии заданным параметрам.
         **/
        destHeight = destHeight * size / destWidth;
        destWidth = size;

        /**
         * Если высота оказалась меньше, чем нужно.
         **/
        if (destHeight < size) {
            destWidth = destWidth * size / destHeight;
            destHeight = size;
        }

        /**
         * Рисуем картинку.
         **/
        Bitmap destBitmap = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(destBitmap);
        canvas.drawBitmap(source,
                new Rect(0, 0, source.getWidth(), source.getHeight()),
                new Rect(0, 0, destWidth, destHeight),
                new Paint(Paint.ANTI_ALIAS_FLAG));

        return destBitmap;
    }

    /**
     * Данный способ основан на применении специального режима комбинирования двух изображений.
     *
     * Этот специальный режим позволяет создать маску из второго изображения, задав прозрачность нужных пикселей.
     *
     * @param source Картинка, с которой необходимо работать.
     * @param radius Радиус, который должна приобрести картинка.
     * @return Возвращает готовую картинку.
     */
    public static Bitmap getCircleMaskedBitmap(Bitmap source, int radius) {
        /** Если картинка не передана */
        if (source == null) {
            return null;
        }

        /** Задаем диаметр в зависимости от радиуса */
        int diam = radius << 1;

        /** Масштабируем картинку */
        Bitmap scaledBitmap = scaleTo(source, diam);


        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Rect rect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(radius, radius, radius, paint);

        /**
         *  С помощью объекта Paint задается режим преобразования пикселей с помощью метода setXfermode.
         *  {@link Paint#setXfermode(Xfermode)}
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, rect, paint);

        return targetBitmap;
    }
}