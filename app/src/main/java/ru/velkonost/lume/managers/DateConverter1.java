package ru.velkonost.lume.managers;

import static ru.velkonost.lume.Constants.HYPHEN;

public class DateConverter1 {
    /**
     * Форматирование даты из вида, полученного с сервер - YYYY-MM-DD
     *                в вид, необходимый для отображения - DD-MM-YYYY
     **/
    public static String formatDate(String dateInStr) {

        String day, month, year;

        /** Разделяем строку на три ключевый строки */
        day = String.valueOf(dateInStr.charAt(dateInStr.length() - 2))
                + dateInStr.charAt(dateInStr.length() - 1);

        month = String.valueOf(dateInStr.charAt(dateInStr.length() - 5))
                + dateInStr.charAt(dateInStr.length() - 4);

        year = String.valueOf(dateInStr.charAt(dateInStr.length() - 10))
                + dateInStr.charAt(dateInStr.length() - 9)
                + dateInStr.charAt(dateInStr.length() - 8)
                + dateInStr.charAt(dateInStr.length() - 7);

        /** Соединяем все воедино */
        return day
                + HYPHEN + month
                + HYPHEN + year;
    }

    public static String formatDateBack(String dateInStr) {

        String day, month, year;

        /** Разделяем строку на три ключевый строки */
        year = String.valueOf(dateInStr.charAt(dateInStr.length() - 4))
                + dateInStr.charAt(dateInStr.length() - 3)
                + dateInStr.charAt(dateInStr.length() - 2)
                + dateInStr.charAt(dateInStr.length() - 1);

        month = String.valueOf(dateInStr.charAt(dateInStr.length() - 7))
                + dateInStr.charAt(dateInStr.length() - 6);

        day = String.valueOf(dateInStr.charAt(dateInStr.length() - 10))
                + dateInStr.charAt(dateInStr.length() - 9);

        /** Соединяем все воедино */
        return year
                + HYPHEN + month
                + HYPHEN + day;
    }

}
