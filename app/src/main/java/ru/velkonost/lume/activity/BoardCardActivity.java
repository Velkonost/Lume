package ru.velkonost.lume.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eyalbira.loadingdots.LoadingDots;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Depository;
import ru.velkonost.lume.Managers.InitializationsManager;
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.Managers.ValueComparatorManager;
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

import static android.graphics.Color.WHITE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.BOARD_DESCRIPTION;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.BOARD_LAST_CONTRIBUTED_USER;
import static ru.velkonost.lume.Constants.CARD_COLOR;
import static ru.velkonost.lume.Constants.CARD_DESCRIPTION;
import static ru.velkonost.lume.Constants.CARD_ID;
import static ru.velkonost.lume.Constants.CARD_NAME;
import static ru.velkonost.lume.Constants.COLUMN_IDS;
import static ru.velkonost.lume.Constants.COLUMN_ORDER;
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
import static ru.velkonost.lume.Constants.URL.SERVER_CARD_SET_DATE_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_CHANGE_CARD_COLOR_METHOD;
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
import static ru.velkonost.lume.Constants.USER_PLACE_LIVING;
import static ru.velkonost.lume.Constants.USER_PLACE_STUDY;
import static ru.velkonost.lume.Constants.USER_PLACE_WORK;
import static ru.velkonost.lume.Constants.USER_WORKING_EMAIL;
import static ru.velkonost.lume.Managers.DateConverterManager.formatDate;
import static ru.velkonost.lume.Managers.DateConverterManager.formatDateBack;
import static ru.velkonost.lume.Managers.HtmlConverterManager.fromHtml;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.saveText;
import static ru.velkonost.lume.R.layout.popup_board_invite_list;
import static ru.velkonost.lume.net.ServerConnection.getJSON;


/**
 * @author Velkonost
 *
 * Класс, описывающий открытую карточку
 *
 */
public class BoardCardActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_board_card;

    /**
     * Свойство - описание верхней панели инструментов приложения
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Свойство - описание {@link BoardCardActivity#LAYOUT}
     */
    @BindView(R.id.activity_board_card)
    DrawerLayout drawerLayout;

    /**
     * Свойство - поле для написания нового комментария
     */
    @BindView(R.id.editComment)
    EditText mEditTextComment;

    /**
     * Свойство - поле для редактирования названия карточки
     */
    @BindView(R.id.editCardName)
    EditText editCardName;

    /**
     * Свойство - иконка для добавления комментария
     */
    @BindView(R.id.imageView)
    ImageView imageArrowSend;

    /**
     * Свойство - элемент, символизирующий загрузку данных
     */
    @BindView(R.id.loadingDots)
    LoadingDots loadingDots;

    /**
     * Свойство - боковая панель
     */
    @BindView(R.id.navigation)
    NavigationView navigationView;

    @BindView(R.id.fab_menu)
    FloatingActionMenu mFloatingActionMenu;

    @BindView(R.id.fab_background)
    FloatingActionButton fabBackground;

    @BindView(R.id.fab_date)
    FloatingActionButton fabDate;

    @BindView(R.id.fab_checkbox)
    FloatingActionButton fabCheckbox;

    @BindView(R.id.fab_field)
    FloatingActionButton fabField;

    @BindView(R.id.fab_map)
    FloatingActionButton fabMap;

    @BindView(R.id.fog)
    View fogView;

    @BindView(R.id.card_date)
    TextView tvCardDate;

    /**
     * Свойство - отображение участников доски для приглашения в карточку
     */
    private View popupViewInvite;

    /**
     * Свойство - отображение колонок доски для перемещения карточки
     */
    private View popupViewColumns;


    public static PopupWindow popupWindowCardInvite;
    public static PopupWindow popupWindowColumns;

    private RecyclerView recyclerViewInvite;
    private RecyclerView recyclerViewColumns;

    /**
     * Свойство - следующая активность.
     */
    private Intent nextIntent;

    /**
     * Свойство - идентификатор карточки
     */
    private int cardId;

    /**
     * Свойство - список участников карточки
     */
    private List<BoardParticipant> mCardParticipants;

    /**
     * Свойство - список комментариев карточки
     */
    private List<CardComment> mCardComments;

    /**
     * Свойство - список индентификаторов комментариев карточки
     */
    private ArrayList<String> commentIds;

    private CardCommentsFragment mCommentsFragment;

    private TimerCheckComments mTimerCheckComments;

    /**
     * Свойство - идентификатор авторизованного пользователя
     */
    private String userId;

    /**
     * Свойство - список участников доски
     */
    private List<Contact> mContacts;

    /**
     * Свойство - список колонок доски
     */
    private List<BoardColumn> mBoardColumns;

    private ArrayList<String> ids;
    private ArrayList<String> cids;

    /**
     * Контакты авторизованного пользователя.
     *
     * Ключ - идентификатор пользователя.
     * Значение - его полное имя или логин.
     **/
    private Map<String, String> contacts;

    /**
     * Свойство - идентификатор доски
     */
    private String boardId;

    /**
     * Свойство - экземпляр класса {@link GetCardData}
     */
    private GetCardData mGetCardData;

    /**
     * Свойство - экземпляр класса {@link GetContacts}
     */
    private GetContacts mGetContacts;

    /**
     * Свойство - экземпляр класса {@link GetBoardColumns}
     */
    private GetBoardColumns mGetBoardColumns;

    /**
     * Свойство - название карточки
     */
    private String cardName;

    /**
     * Свойство - описание карточки
     */
    private String cardDescription;

    private BoardDescriptionFragment descriptionFragment;

    /**
     * Свойство - меню активности
     */
    private Menu menu;

    /**
     * Свойство - текст комментария
     */
    private String textComment;

    /**
     * Свойство - фон карточки
     */
    private int backgroundColor;

    /**
     * Свойство - порядок карточки в колонке
     */
    private int columnOrder;

    /**
     * Свойство - элемент для выбора даты.
     */
    private DatePickerDialog datePicker;

    private String cardDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBase();
        getData();
        initialize();

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setEditTextCommentListener();
        executeTasks();
        startTimer();
        initDatePicker();


        mFloatingActionMenu.setClosedOnTouchOutside(true);

        mFloatingActionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    fogView.setVisibility(View.VISIBLE);
                } else fogView.setVisibility(View.INVISIBLE);
            }
        });

        fabDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker.show();

            }
        });

    }

    /**
     * Инициализация календаря.
     **/
    private void initDatePicker(){
        /**
         * Использует для получения даты.
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Требуется для дальнейшего преобразования даты в строку.
         */
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat
                = new SimpleDateFormat("dd-MM-yyyy");

        /**
         * Создает объект и инициализирует обработчиком события выбора даты и данными для даты по умолчанию.
         */
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int yearr, int monthOfYear, int dayOfMonth) {
                Calendar newCal = Calendar.getInstance();
                newCal.set(yearr, monthOfYear, dayOfMonth);

                cardDate = dateFormat.format(newCal.getTime());
                tvCardDate.setText(cardDate);


                int day = Integer.parseInt(cardDate.substring(0, 2));
                int month = Integer.parseInt(cardDate.substring(3, 5));
                int year = Integer.parseInt(cardDate.substring(6, 10));

                if (cardDate.equals(new SimpleDateFormat("dd-MM-yyyy") //сегодня
                        .format(Calendar.getInstance().getTime()))) {
                    tvCardDate.setBackgroundColor(ContextCompat
                            .getColor(BoardCardActivity.this, R.color.card_date_today));
                } else if ( //будущее
                        (year > Calendar.getInstance().get(Calendar.YEAR))
                                || (
                                !(year < Calendar.getInstance().get(Calendar.YEAR))
                                        && (month > Calendar.getInstance().get(Calendar.MONTH)))
                                || (
                                !(year < Calendar.getInstance().get(Calendar.YEAR))
                                        && !(month < Calendar.getInstance().get(Calendar.MONTH))
                                        && (day > Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        )) {

                    tvCardDate.setBackgroundColor(ContextCompat
                            .getColor(BoardCardActivity.this, R.color.card_date_soon));

                }

                else //прошедшее
                    tvCardDate.setBackgroundColor(ContextCompat
                            .getColor(BoardCardActivity.this, R.color.card_date_done));

                new SetDate().execute();
            }
        },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
    }



    /**
     * Установка первоначальных настроек активности
     */
    private void setBase() {

        setContentView(LAYOUT);
        ButterKnife.bind(this);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

    }

    /**
     * Инициализация основных элементов
     */
    private void initialize() {
        mCardParticipants = new ArrayList<>();
        mContacts = new ArrayList<>();
        mBoardColumns = new ArrayList<>();
        ids = new ArrayList<>();
        cids = new ArrayList<>();
        mCardComments = new ArrayList<>();
        contacts = new HashMap<>();

        mGetCardData = new GetCardData();
        mGetContacts = new GetContacts();
        mGetBoardColumns = new GetBoardColumns();

        initToolbar(BoardCardActivity.this, toolbar, cardName);
        initNavigationView();
        initializePopups();
    }

    /**
     * Получение данных (отсутствует получение с интернета)
     */
    private void getData() {
        getFromFile();
        getFromDepository();
        getExtras();
    }

    /**
     * Получение данных из специального файла приложения
     */
    private void getFromFile() {
        userId = loadText(BoardCardActivity.this, ID);
    }

    /**
     * Получение данных из временного хранилища приложения
     */
    private void getFromDepository() {
        boardId = Depository.getBoardId();
    }

    /**
     * Получение данных из предыдущей активности
     */
    private void getExtras() {
        Intent intent = getIntent();
        cardName = intent.getExtras().getString(CARD_NAME);
        cardId = intent.getExtras().getInt(CARD_ID);
        columnOrder = intent.getExtras().getInt(COLUMN_ORDER);
    }

    /**
     * Вызов процессов, происходящих в параллельных потоках
     */
    private void executeTasks() {
        mGetCardData.execute();
        mGetContacts.execute();
        mGetBoardColumns.execute();
    }

    /**
     * Инициализация всплывающих окон
     */
    private void initializePopups() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        initializePopupInvite(layoutInflater, height);
        initializePopupColumns(layoutInflater, height);
    }

    /**
     * Инициализация всплывающего окна для приглашения участников
     *
     * @param layoutInflater
     * @param height - высота окна
     */
    private void initializePopupInvite(LayoutInflater layoutInflater, int height) {

        popupViewInvite = layoutInflater.inflate(popup_board_invite_list, null);
        popupWindowCardInvite = new PopupWindow(popupViewInvite,
                WRAP_CONTENT, height - dp2px(120));

        recyclerViewInvite = ButterKnife.findById(popupViewInvite, R.id.recyclerViewBoardInvite);


    }

    /**
     * Инициализация всплывающего окна для перемещения карточки
     *
     * @param layoutInflater
     * @param height - высота окна
     */
    private void initializePopupColumns(LayoutInflater layoutInflater, int height) {

        popupViewColumns = layoutInflater.inflate(popup_board_invite_list, null);
        popupWindowColumns = new PopupWindow(popupViewColumns,
                WRAP_CONTENT, height - dp2px(120));

        recyclerViewColumns = ButterKnife.findById(popupViewColumns, R.id.recyclerViewBoardInvite);
    }

    /**
     * Установка слушателя на поле для редактирования нового комментария
     * {@link BoardCardActivity#mEditTextComment}
     */
    private void setEditTextCommentListener() {

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

    }

    /**
     * Запуск таймера
     */
    private void startTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTimerCheckComments = new TimerCheckComments(100000000, 5000);
                mTimerCheckComments.start();

            }
        }, 5000);
    }

    /**
     * Также используется для инициализации {@link BoardCardActivity#menu}
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_card, menu);

        this.menu = menu;
        return true;
    }

    /**
     * Конвертер из dp в px
     *
     * @param dp - значения в dp
     * @return - значение в px
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                BoardCardActivity.this.getResources().getDisplayMetrics());
    }

    /**
     * Изменение состояний пунктов меню
     */
    private void showAgreeMenu() {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_move).setVisible(false);
        menu.findItem(R.id.action_invite).setVisible(false);
        menu.findItem(R.id.action_leave).setVisible(false);
        menu.findItem(R.id.action_change_color).setVisible(false);

        menu.findItem(R.id.action_agree).setVisible(true);
    }

    /**
     * Изменение состояний пунктов меню
     */
    private void hideAgreeMenu() {
        menu.findItem(R.id.action_settings).setVisible(true);
        menu.findItem(R.id.action_invite).setVisible(true);
        menu.findItem(R.id.action_move).setVisible(true);
        menu.findItem(R.id.action_leave).setVisible(true);
        menu.findItem(R.id.action_change_color).setVisible(true);

        menu.findItem(R.id.action_agree).setVisible(false);
    }

    /**
     * Установка слушателя на пункт меню
     */
    private void setAgreeMenuListener() {

        menu.findItem(R.id.action_agree).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                acceptCardChanges();
                new ChangeCardSettings().execute();
                hideKeyBoard();

                return false;
            }
        });
    }

    /**
     * Принятие настроек
     */
    private void acceptCardChanges() {

        cardName = editCardName.getText().toString();
        cardDescription = descriptionFragment.getText();

        toolbar.setTitle(fromHtml(cardName));
        descriptionFragment.changeText();
        descriptionFragment.showNext();

        editCardName.setVisibility(View.INVISIBLE);

        hideAgreeMenu();
    }

    /**
     * Установка слушателя на всплывающее окно для перемещения карточки
     *
     * Открытие этого окна
     */
    private void setPopupColumnsListener() {

        popupWindowColumns.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (Depository.isRefreshPopup())
                    changeActivityCompat(BoardCardActivity.this);
                Depository.setRefreshPopup(false);

            }
        });

        popupColumnsShow();

    }

    /**
     * Установка слушателя на всплывающее окно для приглашения участников
     *
     * Открытие этого окна
     */
    private void setPopupInviteListener() {

        popupWindowCardInvite.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (Depository.isRefreshPopup())
                    changeActivityCompat(BoardCardActivity.this);
                Depository.setRefreshPopup(false);

            }
        });

        popupInviteShow();
    }

    /**
     * Открытие окна для перемещения карточки
     */
    private void popupColumnsShow() {

        popupWindowColumns.setTouchable(true);
        popupWindowColumns.setFocusable(true);
        popupWindowColumns.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(android.R.color.transparent)));
        popupWindowColumns.setOutsideTouchable(true);

        popupWindowColumns.showAtLocation(popupViewColumns, Gravity.CENTER, 0, 0);

    }

    /**
     * Открытие карточки для приглашения участников
     */
    private void popupInviteShow() {

        popupWindowCardInvite.setTouchable(true);
        popupWindowCardInvite.setFocusable(true);
        popupWindowCardInvite.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(android.R.color.transparent)));
        popupWindowCardInvite.setOutsideTouchable(true);

        popupWindowCardInvite.showAtLocation(popupViewInvite, Gravity.CENTER, 0, 0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

                toolbar.setTitle("");
                editCardName.setVisibility(View.VISIBLE);
                editCardName.setText(fromHtml(cardName));
                descriptionFragment.showNext();

                showAgreeMenu();
                setAgreeMenuListener();

                break;
            case R.id.action_move:

                setPopupColumnsListener();

                break;
            case R.id.action_invite:

                setPopupInviteListener();

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
                                        new LeaveCard().execute();
                                        refreshActivity();

                                    }
                                })
                        .create().show();

                break;
            case R.id.action_change_color:

                ColorPickerDialogBuilder
                        .with(BoardCardActivity.this)
                        .setTitle(getResources().getString(R.string.choose_color))
                        .initialColor(WHITE)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {}
                        })
                        .setPositiveButton(getResources().getString(R.string.btn_ok),
                                new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor,
                                                Integer[] allColors) {
                                backgroundColor = selectedColor;
                                new ChangeCardColor().execute();
                                drawerLayout.setBackgroundColor(backgroundColor);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .build()
                        .show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Обновление активности
     */
    private void refreshActivity() {

        Intent intent = new Intent(BoardCardActivity.this,
                BoardCardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CARD_ID, cardId);
        intent.putExtra(CARD_NAME, cardName);
        BoardCardActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.activity_right_in,
                R.anim.activity_diagonaltranslate);

        finish();
        overridePendingTransition(R.anim.activity_right_in,
                R.anim.activity_diagonaltranslate);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTimerCheckComments != null)
            mTimerCheckComments.cancel();
    }

    /**
     * Добавление комментария
     * @param view
     */
    public void addComment(View view) {
        if (mEditTextComment.getText().toString().length() == 0) return;

        textComment = mEditTextComment.getText().toString();

        AddComment addComment = new AddComment();
        addComment.execute();

        mEditTextComment.setText("");

        RefreshComments mRefreshComments = new RefreshComments();
        mRefreshComments.execute();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(BoardCardActivity.this, BoardColumnsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(COLUMN_ORDER, columnOrder);
            intent.putExtra(BOARD_ID, boardId);
            BoardCardActivity.this.startActivity(intent);
            finish();
        }
    }

    /**
     * Скрытие клавиатуры
     */
    private void hideKeyBoard() {

        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        getCurrentFocus().clearFocus();

    }

    private ActionBarDrawerToggle initializeToggle() {
        return new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.view_navigation_open, R.string.view_navigation_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyBoard();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideKeyBoard();
            }
        };
    }

    /**
     * Инициализация заголовка боковой панели
     */
    private void initializeNavHeader() {
        View header = navigationView.getHeaderView(0);
        initializeNavHeaderLogin(header);
        initializeNavHeaderAskQuestion(header);
    }

    /**
     * Инициализация элемента в заголовке боковой панели
     * @param header - заголовок боковой панели
     */
    private void initializeNavHeaderAskQuestion(View header) {

        ImageView askQuestion = ButterKnife.findById(header, R.id.askQuestion);

        askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardCardActivity.this,
                                new Intent(BoardCardActivity.this, FAQBotActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    /**
     * Инициализация элемента в заголовке боковой панели
     * @param header - заголовок боковой панели
     */
    private void initializeNavHeaderLogin(View header) {

        TextView navHeaderLogin = ButterKnife.findById(header, R.id.userNameHeader);
        navHeaderLogin.setText(fromHtml(loadText(BoardCardActivity.this, LOGIN)));

        navHeaderLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardCardActivity.this,
                                new Intent(BoardCardActivity.this, ProfileActivity.class));
                    }
                }, 350);

                drawerLayout.closeDrawer(GravityCompat.START);

            }
        });

    }

    /**
     * Установка слушателя на боковую панель
     */
    private void setNavigationViewListener() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                /** Инициализируем намерение на следующую активность */
                switch (menuItem.getItemId()) {

                    /** Переход на контакты данного пользователя */
                    case R.id.navigationContacts:
                        nextIntent = new Intent(BoardCardActivity.this, ContactsActivity.class);
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
                 * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                 * */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (loadText(BoardCardActivity.this, ID).equals("")) {
                            deleteText(BoardCardActivity.this, USER_PLACE_LIVING);
                            deleteText(BoardCardActivity.this, USER_PLACE_STUDY);
                            deleteText(BoardCardActivity.this, USER_PLACE_WORK);
                            deleteText(BoardCardActivity.this, USER_WORKING_EMAIL);
                        }

                        /**
                         * Обновляет страницу.
                         * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                         * */
                        changeActivityCompat(BoardCardActivity.this, nextIntent);
                    }
                }, 350);


                /** Если был осуществлен выход из аккаунта, то закрываем активность профиля */
                if (loadText(BoardCardActivity.this, ID).equals("")) finishAffinity();

                drawerLayout.closeDrawer(GravityCompat.START);

                return false;
            }
        });
    }

    /**
     * Инициализация боковой панели навигации.
     **/
    private void initNavigationView() {

        ActionBarDrawerToggle toggle = initializeToggle();
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        initializeNavHeader();
        setNavigationViewListener();
    }

    /**
     * Таймер для обновления состояния списка комментариев
     */
    private class TimerCheckComments extends CountDownTimer {

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

    /**
     * Получение данных карточки
     */
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

            /* Свойство - полученный JSON–объект */
            JSONObject dataJsonObj;

            try {

                /*
                  Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);

                /**
                 * Получение идентификаторов найденных пользователей.
                 */
                JSONArray idsJSON = dataJsonObj.getJSONArray(USER_IDS);
                JSONArray cidsJSON = dataJsonObj.getJSONArray(COMMENT_IDS);

                cardDescription = dataJsonObj.getString(CARD_DESCRIPTION);

                cardDate = formatDate(dataJsonObj.getString(DATE));
                tvCardDate.setText(cardDate);


                int day = Integer.parseInt(cardDate.substring(0, 2));
                int month = Integer.parseInt(cardDate.substring(3, 5));
                int year = Integer.parseInt(cardDate.substring(6, 10));

                if (cardDate.equals(new SimpleDateFormat("dd-MM-yyyy") //сегодня
                        .format(Calendar.getInstance().getTime()))) {
                    tvCardDate.setBackgroundColor(ContextCompat
                            .getColor(BoardCardActivity.this, R.color.card_date_today));
                } else if ( //будущее
                        (year > Calendar.getInstance().get(Calendar.YEAR))
                        || (
                                !(year < Calendar.getInstance().get(Calendar.YEAR))
                                && (month > Calendar.getInstance().get(Calendar.MONTH)))
                        || (
                                !(year < Calendar.getInstance().get(Calendar.YEAR))
                                && !(month < Calendar.getInstance().get(Calendar.MONTH))
                                && (day > Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        )) {

                    tvCardDate.setBackgroundColor(ContextCompat
                            .getColor(BoardCardActivity.this, R.color.card_date_soon));

                }

                else //прошедшее
                    tvCardDate.setBackgroundColor(ContextCompat
                            .getColor(BoardCardActivity.this, R.color.card_date_done));

                backgroundColor = dataJsonObj.getInt(CARD_COLOR);
                drawerLayout.setBackgroundColor(backgroundColor);

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

                    String formattedCommentDate
                            = formatDate(commentInfo.getString(DATE).substring(0, 10));

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

                loadingDots.setVisibility(View.INVISIBLE);

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Обновление состояния списка комментариев
     */
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

    /**
     * Добавление нового комментария
     */
    private class AddComment extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            String commentToAdd = textComment;

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
                    + AMPERSAND + TEXT + EQUALS + commentToAdd;

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

    /**
     * Выход из карточки
     */
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

    /**
     * Получение участников доски
     */
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

                /** Создание и инициализация Comparator{@link ValueComparatorManager} */
                Comparator<String> comparator = new ValueComparatorManager<>((HashMap<String, String>) contacts);

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

    /**
     * Получение колонок доски
     */
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

    /**
     * Изменение настроек карточки
     */
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

    /**
     * Изменение фона карточки
     */
    private class ChangeCardColor extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CHANGE_CARD_COLOR_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + CARD_COLOR + EQUALS + backgroundColor;

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

    private class SetDate extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CARD_SET_DATE_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + DATE + EQUALS + formatDateBack(cardDate);

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
