package ru.velkonost.lume;


import ru.velkonost.lume.Managers.PhoneDataStorageManager;

/**
 * @author Velkonost
 * Класс, предназначенный для хранения основных констант, используемых в коде приложения.
 * */
public class Constants {

    public static final String EQUALS = "=";
    public static final String AMPERSAND = "&";
    public static final String HYPHEN = "-";
    public static final String PLUS = "+";


    public static final String AMOUNT = "amount";
    public static final String BELONG = "belong";
    public static final String CARD_COLOR = "card_color";

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

    public static final String COLUMN_ID = "cid";
    public static final String COLUMN_IDS = "cids";
    public static final String COLUMN_ORDER = "column_order";

    public static final String CARD_ID = "card_id";
    public static final String CARD_NAME = "card_name";
    public static final String CARD_DESCRIPTION = "card_description";

    public static final String COMMENT = "comment";
    public static final String COMMENT_IDS = "comment_ids";
    public static final String CHECKBOX_IDS = "cb_ids";

    public static final String IMAGE = "image";

    public static final String USER_ID = "user_id";
    public static final String SEND_ID = "send_id";
    public static final String GET_ID = "get_id";

    public static final String SEARCH = "search";
    public static final String STATUS = "status";
    public static final String TEXT = "text";
    public static final String TITLE = "title";
    public static final String DONE = "done";
    public static final String DATE = "date";

    public static final String NAME = "name";
    public static final String PREVIOUS_NAME = "previous_name";
    public static final String DESCRIPTION = "description";
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

    public static final String PARTICIPANTS = "participants";
    public static final String POSITION = "position";

    public static final String USER_PLACE_LIVING = "user_place_living";
    public static final String USER_BIRTHDAY = "user_birthday";
    public static final String USER_PLACE_WORK = "user_place_work";
    public static final String USER_PLACE_STUDY = "user_place_study";
    public static final String USER_WORKING_EMAIL = "user_working_email";


    public static final long MAX_DATE = 1000000000000L;


    public static final int REMIND_TODO_INDEX = 0;
    public static final int REMIND_DONE_INDEX = 1;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    /** Класс констант, относящихся исключительно к построению URL */
    public static class URL {
        public static final String SERVER_PROTOCOL = "http://";
        public static final String SERVER_HOST = "vh156342.eurodir.ru";
        public static final String SERVER_ACCOUNT_SCRIPT = "/account";
        public static final String SERVER_DIALOG_SCRIPT = "/dialogs";
        public static final String SERVER_KANBAN_SCRIPT = "/kanban";
        public static final String SERVER_REMIND_SCRIPT = "/remind";
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
        public static final String SERVER_SHORT_EDIT_PARAMETERS_METHOD = "/short_edit";

        public static final String SERVER_CREATE_DIALOG_METHOD = "/createDialog";
        public static final String SERVER_SHOW_DIALOGS_METHOD = "/showDialogs";
        public static final String SERVER_SHOW_MESSAGES_METHOD = "/showMessages";
        public static final String SERVER_SEND_MESSAGE_METHOD = "/sendMessage";

        public static final String SERVER_SHOW_BOARDS_METHOD = "/showBoards";
        public static final String SERVER_GET_BOARD_INFO_METHOD = "/getBoardInfo";
        public static final String SERVER_GET_COLUMN_INFO_METHOD = "/getColumnInfo";
        public static final String SERVER_GET_BOARD_PARTICIPANTS_METHOD = "/getBoardParticipants";
        public static final String SERVER_GET_CARD_PARTICIPANTS_METHOD = "/getCardParticipants";
        public static final String SERVER_GET_CARD_INFO_METHOD = "/getCardInfo";
        public static final String SERVER_CARD_ADD_COMMENT_METHOD = "/cardAddComment";
        public static final String SERVER_CARD_SET_DATE_METHOD = "/setDate";

        public static final String SERVER_LEAVE_BOARD_METHOD = "/leaveBoard";
        public static final String SERVER_LEAVE_CARD_METHOD = "/leaveCard";
        public static final String SERVER_INVITE_IN_BOARD_METHOD = "/inviteInBoard";
        public static final String SERVER_INVITE_IN_CARD_METHOD = "/inviteInCard";
        public static final String SERVER_CHANGE_BOARD_SETTINGS_METHOD = "/changeBoardSettings";
        public static final String SERVER_CHANGE_CARD_SETTINGS_METHOD = "/changeCardSettings";
        public static final String SERVER_CHANGE_CARD_COLOR_METHOD = "/changeCardColor";
        public static final String SERVER_GET_BOARD_PARTICIPANTS_TO_INVITE_METHOD = "/getInBoardToInvite";
        public static final String SERVER_GET_BOARD_COLUMNS_METHOD = "/getBoardColumns";
        public static final String SERVER_MOVE_CARD_METHOD = "/moveCard";
        public static final String SERVER_ADD_COLUMN_METHOD = "/addColumn";
        public static final String SERVER_ADD_CARD_METHOD = "/addCard";
        public static final String SERVER_ADD_BOARD_METHOD = "/addBoard";
        public static final String SERVER_CHANGE_COLUMN_SETTINGS_METHOD = "/changeColumnSettings";

        public static final String SERVER_ADD_TASK_METHOD = "/addTask";

        public static final String SERVER_GET_MAP_MARKERS_METHOD = "/getMapMarkers";
        public static final String SERVER_ADD_MAP_MARKER_METHOD = "/addMapMarker";
        public static final String SERVER_REMOVE_MAP_MARKER_METHOD = "/removeMapMarker";

        public static final String SERVER_CARD_SET_CHECKBOX_DONE_METHOD = "/setCheckboxDone";
        public static final String SERVER_CARD_SET_CHECKBOX_UNDONE_METHOD = "/setCheckboxUndone";
        public static final String SERVER_CARD_ADD_CHECKBOX_METHOD = "/addCheckbox";

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
    public static final int MAX_COLUMNS_IN_FIXED_MODE = 5;

    public final static String TAG_IMAGE_MANAGER = "SetImageManager";



    public static final int GALLERY_REQUEST = 22131;
    public static final int CAMERA_REQUEST = 13323;

    /**
     *  Название файла, с которым приложения осуществляет взаимодействие
     * {@link PhoneDataStorageManager}
     * */
    public static final String APP_PREFERENCES = "UserSettings";
}
