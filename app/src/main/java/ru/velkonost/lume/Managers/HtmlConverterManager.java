package ru.velkonost.lume.Managers;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * @author Velkonost
 *
 * Обработка текста
 */
public class HtmlConverterManager {

    /**
     * Замена html-тегов
     */
    public static Spanned fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        else
            return Html.fromHtml(text);
    }

}
