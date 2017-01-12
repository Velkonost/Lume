package ru.velkonost.lume;


import ru.velkonost.lume.Managers.PhoneDataStorage;

/**
 * @author Velkonost
 * Класс, предназначенный для хранения основных констант, используемых в коде приложения.
 * */
public class Constants {

    public static final String EQUALS = "=";
    public static final String AMPERSAND = "&";
    public static final String HYPHEN = "-";
    public static final String PLUS = "+";

    public static final String LOGIN = "login";
    public static final String REGISTRATION = "registration";
    public static final String PASSWORD = "password";
    public static final String PREV_PASSWORD = "prev_password";
    public static final String NEW_PASSWORD = "new_password";
    public static final String EMAIL = "email";
    public static final String USER = "user";
    public static final String ID = "id";
    public static final String IDS = "ids";
    public static final String DIALOG_ID = "did";
    public static final String ADDRESSEE_ID = "addresseeId";
    public static final String SENDER_ID = "senderId";
    public static final String MESSAGE_ID = "mid";
    public static final String MESSAGE_IDS = "mids";

    public static final String BOARD_ID = "bid";
    public static final String BOARD_IDS = "bids";

    public static final String BOARD_NAME = "bname";
    public static final String BOARD_DESCRIPTION = "bdescription";

    public static final String USER_IDS = "uids";

    public static final String COLUMN_IDS = "cids";

    public static final String IMAGE = "image";

    public static final String USER_ID = "user_id";
    public static final String SEND_ID = "send_id";
    public static final String GET_ID = "get_id";

    public static final String SEARCH = "search";
    public static final String STATUS = "status";
    public static final String TEXT = "text";
    public static final String DATE = "date";

    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String WORK_EMAIL = "work_email";
    public static final String COUNTRY = "country";
    public static final String CITY = "city";
    public static final String AVATAR = "avatar";
    public static final String BIRTHDAY = "birthday";
    public static final String STUDY = "study";
    public static final String WORK = "work";
    public static final String CONTACT = "contact";

    public static final String UNREAD_MESSAGES = "unread";

    public static final String GET_DATA = "getdata";
    public static final String GET_DATA_SETTINGS = "getdatasettings";
    public static final String GET_EDIT_RESULT = "edit";
    public static final String ADD_CONTACT = "addContact";

    /** Класс констант, относящихся исключительно к построению URL */
    public static class URL {
        public static final String SERVER_PROTOCOL = "http://";
        public static final String SERVER_HOST = "vh156342.eurodir.ru";
        public static final String SERVER_ACCOUNT_SCRIPT = "/account";
        public static final String SERVER_DIALOG_SCRIPT = "/dialogs";
        public static final String SERVER_KANBAN_SCRIPT = "/kanban";
        public static final String SERVER_RESOURCE = "/resource";
        public static final String SERVER_AVATAR = "/avatar";

        public static final String SERVER_LOGIN_METHOD = "/login";
        public static final String SERVER_REGISTRATION_METHOD = "/registration";
        public static final String SERVER_GET_DATA_METHOD = "/getData";
        public static final String SERVER_GET_DATA_SETTINGS_METHOD = "/getDataSettings";
        public static final String SERVER_ADD_CONTACT_METHOD = "/addContact";
        public static final String SERVER_SEARCH_METHOD = "/search";
        public static final String SERVER_GET_CONTACTS_METHOD = "/getContacts";
        public static final String SERVER_UPLOAD_IMAGE_METHOD = "/editImage";
        public static final String SERVER_EDIT_PARAMETERS_METHOD = "/edit";

        public static final String SERVER_CREATE_DIALOG_METHOD = "/createDialog";
        public static final String SERVER_SHOW_DIALOGS_METHOD = "/showDialogs";
        public static final String SERVER_SHOW_MESSAGES_METHOD = "/showMessages";
        public static final String SERVER_SEND_MESSAGE_METHOD = "/sendMessage";

        public static final String SERVER_SHOW_BOARDS_METHOD = "/showBoards";
        public static final String SERVER_GET_BOARD_INFO_METHOD = "/getBoardInfo";
        public static final String SERVER_GET_BOARD_PARTICIPANTS_METHOD = "/getBoardInfo";

    }

    /** Класс констант, описывающих результаты операций */
    public static class RESULT {
        public static final String SUCCESS = "Success";
        public static final String ERROR = "Error";

        public static final String ERROR_WITH_CONNECTION = "Error with connection";
        public static final String ERROR_WITH_URL = "Error with url";
        public static final String ERROR_WITH_PROTOCOL = "Error with protocol";
        public static final String ERROR_WITH_ENCODING = "Error with encode";
    }

    public static final String SLASH = "/";
    public static final String JPG = ".jpg";
    public static final String ZERO = "0";

    public static final int BOARD_LAST_CONTRIBUTED_USER = 6;


    public static final String UPLOAD_IMAGE_SUCCESS_CODE = "500";

    public static final int MARQUEE_REPEAT_LIMIT = 1000000000;
    public final static String TAG_IMAGE_MANAGER = "ImageManager";



    public static final int GALLERY_REQUEST = 22131;
    public static final int CAMERA_REQUEST = 13323;

    /**
     *  Название файла, с которым приложения осуществляет взаимодействие
     * {@link PhoneDataStorage}
     * */
    public static final String APP_PREFERENCES = "UserSettings";
}
