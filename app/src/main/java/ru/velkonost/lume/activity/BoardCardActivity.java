package ru.velkonost.lume.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.velkonost.lume.Depository;
import ru.velkonost.lume.Managers.Initializations;
import ru.velkonost.lume.Managers.PhoneDataStorage;
import ru.velkonost.lume.Managers.ValueComparator;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.CardInviteListAdapter;
import ru.velkonost.lume.adapter.CardMoveListAdapter;
import ru.velkonost.lume.descriptions.BoardColumn;
import ru.velkonost.lume.descriptions.BoardParticipant;
import ru.velkonost.lume.descriptions.CardComment;
import ru.velkonost.lume.descriptions.Contact;
import ru.velkonost.lume.fragments.BoardDescriptionFragment;
import ru.velkonost.lume.fragments.CardCommentsFragment;
import ru.velkonost.lume.fragments.CardParticipantsFragment;
import ru.velkonost.lume.fragments.MessagesFragment;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BOARD_DESCRIPTION;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.BOARD_LAST_CONTRIBUTED_USER;
import static ru.velkonost.lume.Constants.CARD_DESCRIPTION;
import static ru.velkonost.lume.Constants.CARD_ID;
import static ru.velkonost.lume.Constants.CARD_NAME;
import static ru.velkonost.lume.Constants.COLUMN_IDS;
import static ru.velkonost.lume.Constants.COMMENT;
import static ru.velkonost.lume.Constants.COMMENT_IDS;
import static ru.velkonost.lume.Constants.DATE;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.IDS;
import static ru.velkonost.lume.Constants.LOGIN;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.SURNAME;
import static ru.velkonost.lume.Constants.TEXT;
import static ru.velkonost.lume.Constants.URL.SERVER_CARD_ADD_COMMENT_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_CHANGE_CARD_SETTINGS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_BOARD_COLUMNS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_BOARD_PARTICIPANTS_TO_INVITE_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_CARD_INFO_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_LEAVE_CARD_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER;
import static ru.velkonost.lume.Constants.USER_IDS;
import static ru.velkonost.lume.Managers.DateConverter.formatDate;
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.Initializations.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.saveText;
import static ru.velkonost.lume.R.layout.popup_board_invite_list;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class BoardCardActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_board_card;

    /**
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

    /**
     * Свойство - описание верхней панели инструментов приложения.
     */
    private Toolbar toolbar;

    /**
     * Свойство - описание {@link SearchActivity#LAYOUT}
     */
    private DrawerLayout drawerLayout;

    private int cardId;

    private List<BoardParticipant> mCardParticipants;

    private List<CardComment> mCardComments;

    private GetCardData mGetCardData;

    private ArrayList<String> commentIds;

    private CardCommentsFragment mCommentsFragment;

    private TimerCheckComments mTimerCheckComments;

    private EditText mEditTextComment;

    private String userId;

    private RecyclerView recyclerViewInvite;
    private View popupViewInvite;
    public static PopupWindow popupWindowCardInvite;

    private RecyclerView recyclerViewColumns;
    private View popupViewColumns;
    public static PopupWindow popupWindowColumns;

    private List<Contact> mContacts;

    private List<BoardColumn> mBoardColumns;

    /**
     * Идентификаторы пользователей, некоторые данные которых соответствуют искомой информации.
     **/
    private ArrayList<String> ids;
    private ArrayList<String> cids;

    /**
     * Контакты авторизованного пользователя.
     *
     * Ключ - идентификатор пользователя.
     * Значение - его полное имя или логин.
     **/
    private Map<String, String> contacts;

    private String boardId;

    private GetContacts mGetContacts;

    private GetBoardColumns mGetBoardColumns;

    private String cardName;

    private EditText editCardName;

    private String cardDescription;

    private BoardDescriptionFragment descriptionFragment;

    private Menu menu;

    private ImageView imageArrowSend;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);
        setTheme(R.style.AppTheme_Cursor);

        mCardParticipants = new ArrayList<>();
        mContacts = new ArrayList<>();
        mBoardColumns = new ArrayList<>();
        ids = new ArrayList<>();
        cids = new ArrayList<>();
        contacts = new HashMap<>();

        mCardComments = new ArrayList<>();
        mGetCardData = new GetCardData();
        mGetContacts = new GetContacts();
        mGetBoardColumns = new GetBoardColumns();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_board_card);

        mEditTextComment = (EditText) findViewById(R.id.editComment);
        editCardName = (EditText) findViewById(R.id.editCardName);
        imageArrowSend = (ImageView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        cardName = intent.getExtras().getString(CARD_NAME);
        cardId = intent.getExtras().getInt(CARD_ID);

        /**
         * Получение id пользователя.
         * {@link PhoneDataStorage#loadText(Context, String)}
         **/
        userId = loadText(BoardCardActivity.this, ID);
        boardId = Depository.getBoardId();

        /** {@link Initializations#initToolbar(Toolbar, int)}  */
        initToolbar(BoardCardActivity.this, toolbar, cardName); /** Инициализация */
        initNavigationView(); /** Инициализация */

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        popupViewInvite = layoutInflater.inflate(popup_board_invite_list, null);
        popupViewColumns = layoutInflater.inflate(popup_board_invite_list, null);

        popupWindowCardInvite = new PopupWindow(popupViewInvite,
                WRAP_CONTENT, height - dp2px(120));

        popupWindowColumns = new PopupWindow(popupViewColumns,
                WRAP_CONTENT, height - dp2px(120));


        recyclerViewInvite = (RecyclerView) popupViewInvite
                .findViewById(R.id.recyclerViewBoardInvite);

        recyclerViewColumns = (RecyclerView) popupViewColumns
                .findViewById(R.id.recyclerViewBoardInvite);


        mEditTextComment.addTextChangedListener(new TextWatcher() {


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imageArrowSend.setColorFilter(ContextCompat
                            .getColor(BoardCardActivity.this, R.color.colorMessageBackground));
                } else {
                    imageArrowSend.setColorFilter(ContextCompat
                            .getColor(BoardCardActivity.this, R.color.colorPrimary));

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mGetCardData.execute();
        mGetContacts.execute();
        mGetBoardColumns.execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTimerCheckComments = new TimerCheckComments(100000000, 5000);
                mTimerCheckComments.start();

            }
        }, 5000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_card, menu);

        this.menu = menu;
        return true;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                BoardCardActivity.this.getResources().getDisplayMetrics());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

                toolbar.setTitle("");
                editCardName.setVisibility(View.VISIBLE);
                editCardName.setText(cardName);

                descriptionFragment.showNext();


                menu.findItem(R.id.action_settings).setVisible(false);
                menu.findItem(R.id.action_move).setVisible(false);
                menu.findItem(R.id.action_invite).setVisible(false);
                menu.findItem(R.id.action_leave).setVisible(false);

                menu.findItem(R.id.action_agree).setVisible(true);

                menu.findItem(R.id.action_agree).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        cardName = editCardName.getText().toString();
                        cardDescription = descriptionFragment.getText();

                        toolbar.setTitle(cardName);
                        descriptionFragment.changeText();
                        descriptionFragment.showNext();

                        editCardName.setVisibility(View.INVISIBLE);

                        menu.findItem(R.id.action_settings).setVisible(true);
                        menu.findItem(R.id.action_invite).setVisible(true);
                        menu.findItem(R.id.action_move).setVisible(true);
                        menu.findItem(R.id.action_leave).setVisible(true);

                        menu.findItem(R.id.action_agree).setVisible(false);

                        ChangeCardSettings changeCardSettings = new ChangeCardSettings();
                        changeCardSettings.execute();


                        InputMethodManager inputMethodManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        getCurrentFocus().clearFocus();

                        return false;
                    }
                });


                break;
            case R.id.action_move:

                popupWindowColumns.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (Depository.isRefreshPopup())
                            changeActivityCompat(BoardCardActivity.this);
                        Depository.setRefreshPopup(false);

                    }
                });


                popupWindowColumns.setTouchable(true);
                popupWindowColumns.setFocusable(true);
                popupWindowColumns.setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(android.R.color.transparent)));
                popupWindowColumns.setOutsideTouchable(true);

                popupWindowColumns.showAtLocation(popupViewColumns, Gravity.CENTER, 0, 0);

                break;
            case R.id.action_invite:

                popupWindowCardInvite.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (Depository.isRefreshPopup())
                            changeActivityCompat(BoardCardActivity.this);
                        Depository.setRefreshPopup(false);

                    }
                });


                popupWindowCardInvite.setTouchable(true);
                popupWindowCardInvite.setFocusable(true);
                popupWindowCardInvite.setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(android.R.color.transparent)));
                popupWindowCardInvite.setOutsideTouchable(true);

                popupWindowCardInvite.showAtLocation(popupViewInvite, Gravity.CENTER, 0, 0);

                break;
            case R.id.action_leave:

                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.leave_card))
                        .setMessage(getResources().getString(R.string.ask_confirmation))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(getResources().getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LeaveCard leaveCard = new LeaveCard();
                                        leaveCard.execute();

                                        Intent intent = new Intent(BoardCardActivity.this,
                                                BoardCardActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(CARD_ID, cardId);
                                        intent.putExtra(CARD_NAME, cardName);
                                        BoardCardActivity.this.startActivity(intent);

                                        finish();
                                    }
                                })
                        .create().show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (mTimerCheckComments != null)
            mTimerCheckComments.cancel();
    }

    public void addComment(View view) {
        if (mEditTextComment.getText().toString().length() == 0) return;

        AddComment addComment = new AddComment();
        addComment.execute();

        mEditTextComment.setText("");

        RefreshComments mRefreshComments = new RefreshComments();
        mRefreshComments.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_card);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    /**
     * Рисует боковую панель навигации.
     **/
    private void initNavigationView() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                /** Инициализируем намерение на следующую активность */
                switch (menuItem.getItemId()) {

                    /** Переход на профиль данного пользователя */
                    case R.id.navigationProfile:
                        nextIntent = new Intent(BoardCardActivity.this, ProfileActivity.class);
                        break;

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(BoardCardActivity.this, ContactsActivity.class);
                        break;

                    /** Переход на страницу напоминаний, созданных данным пользователем */
                    case R.id.navigationReminder:
                        break;

                    /** Переход на страницу сообщений данного пользователя */
                    case R.id.navigationMessages:
                        nextIntent = new Intent(BoardCardActivity.this, DialogsActivity.class);
                        break;

                    /** Переход на страницу досок карточной версии канбан-системы */
                    case R.id.navigationBoards:
                        nextIntent = new Intent(BoardCardActivity.this, BoardsListActivity.class);
                        break;

                    /** Переход на страницу индивидуальных настроек для данного пользователя */
                    case R.id.navigationSettings:
                        nextIntent = new Intent(BoardCardActivity.this, SettingsActivity.class);
                        break;

                    /**
                     * Завершение сессии даного пользователя на данном устройстве.
                     * Удаляем всю информацию об авторизованном пользователе.
                     * Переход на страницу приветствия {@link WelcomeActivity}
                     **/
                    case R.id.navigationLogout:
                        deleteText(BoardCardActivity.this, ID);
                        nextIntent = new Intent(BoardCardActivity.this, WelcomeActivity.class);
                        break;
                }

                /**
                 * Переход на следующую активность.
                 * {@link Initializations#changeActivityCompat(Activity, Intent)}
                 * */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link Initializations#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardCardActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardCardActivity.this, ID).equals("")) finishAffinity();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_board_card);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    public class TimerCheckComments extends CountDownTimer {

        TimerCheckComments(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            RefreshComments mRefreshComments = new RefreshComments();
            mRefreshComments.execute();
        }

        @Override
        public void onFinish() {
        }
    }

    private class GetCardData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_CARD_INFO_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            /** Свойство - полученный JSON–объект*/
            JSONObject dataJsonObj;

            try {

                /**
                 * Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);

                /**
                 * Получение идентификаторов найденных пользователей.
                 */
                JSONArray idsJSON = dataJsonObj.getJSONArray(USER_IDS);
                JSONArray cidsJSON = dataJsonObj.getJSONArray(COMMENT_IDS);

                cardDescription = dataJsonObj.getString(CARD_DESCRIPTION);


                ArrayList<String> uids = new ArrayList<>();
                commentIds = new ArrayList<>();

                for (int i = 0; i < idsJSON.length(); i++) {
                    uids.add(idsJSON.getString(i));
                }

                for (int i = 0; i < cidsJSON.length(); i++) {
                    commentIds.add(cidsJSON.getString(i));
                }


                for (int i = 0; i < uids.size(); i++) {
                    String participantId = uids.get(i);

                    JSONObject userInfo = dataJsonObj.getJSONObject(participantId);

                    mCardParticipants.add(new BoardParticipant(
                            Integer.parseInt(participantId),
                            Integer.parseInt(userInfo.getString(AVATAR)),
                            userInfo.getString(LOGIN),
                            BOARD_LAST_CONTRIBUTED_USER == i + 1, uids.size() - i, cardId
                    ));

                    if (BOARD_LAST_CONTRIBUTED_USER == i) break;

                }

                for (int i = 0; i < commentIds.size(); i++) {

                    String commentId = commentIds.get(i) + COMMENT;

                    JSONObject commentInfo = dataJsonObj.getJSONObject(commentId);

                    String formattedCommentDate = formatDate(commentInfo.getString(DATE).substring(0, 10));

                    mCardComments.add(new CardComment(
                            Integer.parseInt(commentId.substring(0, commentId.length() - 7)),
                            commentInfo.getString(USER), cardId,
                            commentInfo.getString(TEXT),
                            formattedCommentDate.equals(new SimpleDateFormat("dd-MM-yyyy")
                                    .format(Calendar.getInstance().getTime()))
                                    ? commentInfo.getString(DATE)
                                    .substring(11, commentInfo.getString(DATE).length())
                                    : new SimpleDateFormat("dd MMM yyyy")
                                    .format(new SimpleDateFormat("dd-MM-yyyy")
                                            .parse(formattedCommentDate))
                    ));

                }

                Collections.reverse(mCardComments);

                saveText(BoardCardActivity.this, BOARD_DESCRIPTION, cardDescription);

                descriptionFragment = new BoardDescriptionFragment();
                CardParticipantsFragment cardParticipantsFragment
                        = CardParticipantsFragment.getInstance(BoardCardActivity.this, mCardParticipants);
                mCommentsFragment
                        = CardCommentsFragment.getInstance(BoardCardActivity.this, mCardComments);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.add(R.id.descriptionContainer, descriptionFragment);
                transaction.add(R.id.participantsContainer, cardParticipantsFragment);
                transaction.add(R.id.commentsContainer, mCommentsFragment);

                transaction.commit();

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private class RefreshComments extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_CARD_INFO_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            /** Свойство - полученный JSON–объект*/
            JSONObject dataJsonObj;

            try {

                /**
                 * Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);

                Collections.reverse(mCardComments);

                /**
                 * Получение идентификаторов найденных пользователей.
                 */
                JSONArray idsJSON = dataJsonObj.getJSONArray(COMMENT_IDS);

                for (int i = 0; i < idsJSON.length(); i++){
                    if (!commentIds.contains(idsJSON.getString(i)))
                        commentIds.add(0, idsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < commentIds.size(); i++) {
                    boolean exist = false;

                    String commentId = commentIds.get(i) + COMMENT;

                    /**
                     * Получение JSON-объекта с информацией о конкретном сообщении по его идентификатору.
                     */
                    JSONObject commentInfo = dataJsonObj.getJSONObject(commentId);

                    for (int j = 0; j < mCardComments.size(); j++){
                        if (mCardComments.get(j).getId() == commentInfo.getInt(ID)) {
                            exist = true;
                            break;
                        }
                    }

                    String formattedCommentDate = formatDate(commentInfo.getString(DATE).substring(0, 10));

                    if (!exist){
                        mCardComments.add(new CardComment(
                                Integer.parseInt(commentId.substring(0, commentId.length() - 7)),
                                commentInfo.getString(USER), cardId,
                                commentInfo.getString(TEXT),
                                formattedCommentDate.equals(new SimpleDateFormat("dd-MM-yyyy")
                                        .format(Calendar.getInstance().getTime()))
                                        ? commentInfo.getString(DATE)
                                        .substring(11, commentInfo.getString(DATE).length())
                                        : new SimpleDateFormat("dd MMM yyyy")
                                        .format(new SimpleDateFormat("dd-MM-yyyy")
                                                .parse(formattedCommentDate))
                        ));
                    }
                }

                Collections.reverse(mCardComments);

                /**
                 * Добавляем фрагмент на экран.
                 * {@link MessagesFragment}
                 */
                if(!isFinishing()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    mCommentsFragment.refreshComments(mCardComments);
                    ft.replace(R.id.commentsContainer, mCommentsFragment);
                    ft.commitAllowingStateLoss();
                }

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private class AddComment extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CARD_ADD_COMMENT_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + ID + EQUALS + userId
                    + AMPERSAND + TEXT + EQUALS + mEditTextComment.getText().toString();

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
        }
    }
    private class LeaveCard extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_LEAVE_CARD_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + ID + EQUALS + userId;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
        }
    }
    private class GetContacts extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_BOARD_PARTICIPANTS_TO_INVITE_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            /** Свойство - полученный JSON–объект*/
            JSONObject dataJsonObj;

            try {

                /**
                 * Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);

                /**
                 * Получение идентификаторов найденных пользователей.
                 */
                JSONArray idsJSON = dataJsonObj.getJSONArray(IDS);

                for (int i = 0; i < idsJSON.length(); i++){
                    ids.add(idsJSON.getString(i));
                }

                /**
                 * Заполнение Map{@link contacts} для последующей сортировки контактов.
                 *
                 * По умолчанию идентификатору контакта соответствует его полное имя.
                 *
                 * Если такогого не имеется, то устанавливает взамен логин.
                 **/
                for (int i = 0; i < ids.size(); i++){
                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));

                    contacts.put(
                            ids.get(i),
                            userInfo.getString(NAME).length() != 0
                                    ? userInfo.getString(SURNAME).length() != 0
                                    ? userInfo.getString(NAME) + " " + userInfo.getString(SURNAME)
                                    : userInfo.getString(LOGIN) : userInfo.getString(LOGIN)
                    );
                }

                /** Создание и инициализация Comparator{@link ValueComparator} */
                Comparator<String> comparator = new ValueComparator<>((HashMap<String, String>) contacts);

                /** Помещает отсортированную Map */
                TreeMap<String, String> sortedContacts = new TreeMap<>(comparator);
                sortedContacts.putAll(contacts);

                /** "Обнуляет" хранилище идентификаторов */
                ids = new ArrayList<>();

                /** Заполняет хранилище идентификаторов */
                for (String key : sortedContacts.keySet()) {
                    ids.add(key);
                }

                /** "Поворачивает" хранилище идентификаторов */
                Collections.reverse(ids);

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < ids.size(); i++) {

                    /**
                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
                     */
                    JSONObject userInfo = dataJsonObj.getJSONObject(ids.get(i));

                    mContacts.add(new Contact(userInfo.getString(ID), userInfo.getString(NAME),
                            userInfo.getString(SURNAME), userInfo.getString(LOGIN),
                            Integer.parseInt(userInfo.getString(AVATAR))));
                }

                recyclerViewInvite.setLayoutManager(new LinearLayoutManager(BoardCardActivity.this));
                recyclerViewInvite.setAdapter(new CardInviteListAdapter(BoardCardActivity.this,
                        mContacts, cardId));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class GetBoardColumns extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_BOARD_COLUMNS_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            /** Свойство - полученный JSON–объект*/
            JSONObject dataJsonObj;

            try {

                /**
                 * Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);

                /**
                 * Получение идентификаторов найденных пользователей.
                 */
                JSONArray idsJSON = dataJsonObj.getJSONArray(COLUMN_IDS);

                for (int i = 0; i < idsJSON.length(); i++){
                    cids.add(idsJSON.getString(i));
                }


                for (int i = 0; i < cids.size(); i++){
                    JSONObject columnInfo = dataJsonObj.getJSONObject(cids.get(i));
                    mBoardColumns.add(
                            new BoardColumn(Integer.parseInt(cids.get(i)), columnInfo.getString(NAME))
                    );

                }

                recyclerViewColumns.setLayoutManager(new LinearLayoutManager(BoardCardActivity.this));
                recyclerViewColumns.setAdapter(new CardMoveListAdapter(BoardCardActivity.this,
                        mBoardColumns, cardId));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class ChangeCardSettings extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CHANGE_CARD_SETTINGS_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + CARD_NAME + EQUALS + cardName
                    + AMPERSAND + CARD_DESCRIPTION + EQUALS + cardDescription;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
        }
    }
}
