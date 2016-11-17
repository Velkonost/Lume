package ru.velkonost.lume;


/**
 * @author Velkonost
 * Класс, предназначенный для хранения основных констант, используемых в коде приложения.
 * */
public class Constants {

    public static final String EQUALS = "=";
    public static final String AMPERSAND = "&";

    public static final String LOGIN = "login";
    public static final String REGISTRATION = "registration";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String ID = "id";

    public static final String USER_ID = "user_id";

    public static final String SEARCH = "search";

    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String WORK_EMAIL = "work_email";
    public static final String COUNTRY = "country";
    public static final String CITY = "city";
    public static final String AVATAR = "avatar";
    public static final String BIRTHDAY = "birthday";
    public static final String STUDY = "study";
    public static final String WORK = "work";

    public static final String GET_DATA = "getdata";

    /** Класс констант, относящихся исключительно к построению URL */
    public static class URL {
        public static final String SERVER_PROTOCOL = "http://";
        public static final String SERVER_HOST = "vh156342.eurodir.ru";
        public static final String SERVER_ACCOUNT_SCRIPT = "/account";
        public static final String SERVER_RESOURCE = "/resource";
        public static final String SERVER_AVATAR = "/avatar";

        public static final String SERVER_LOGIN_METHOD = "/login";
        public static final String SERVER_GET_DATA_METHOD = "/getData";
        public static final String SERVER_SEARCH_METHOD = "/search";
        public static final String SERVER_REGISTRATION_METHOD = "/registration";

    }

    public static final String SLASH = "/";
    public static final String PNG = ".png";
    public static final String ZERO = "0";

    /**
     *  Название файла, с которым приложения осуществляет взаимодействие
     * {@link PhoneDataStorage}
     * */
    public static final String APP_PREFERENCES = "UserSettings";

    public static final int TAB_ONE = 0;
    public static final int TAB_TWO = 1;
    public static final int TAB_THREE = 2;




}
