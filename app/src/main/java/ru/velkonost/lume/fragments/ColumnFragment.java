package ru.velkonost.lume.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.CardListAdapter;
import ru.velkonost.lume.descriptions.Card;

import static ru.velkonost.lume.Constants.AMOUNT;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BELONG;
import static ru.velkonost.lume.Constants.CARD_COLOR;
import static ru.velkonost.lume.Constants.COLUMN_ID;
import static ru.velkonost.lume.Constants.COLUMN_IDS;
import static ru.velkonost.lume.Constants.DESCRIPTION;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_CARD_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_COLUMN_INFO_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER_IDS;
import static ru.velkonost.lume.Managers.InitializationsManager.changeActivityCompat;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;
import static ru.velkonost.lume.fragments.BoardColumnsTabsFragmentAdapter.tabsColumnOrder;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

/**
 * @author Velkonost
 *
 * Колонки доски, получение их карточек
 */
public class ColumnFragment extends BaseTabFragment {

    private static final int LAYOUT = R.layout.fragment_column;

    /**
     * Свойство - идентификатор колонки
     */
    private int columnId;

    /**
     * Свойство - название карточки
     */
    private String cardName;

    /**
     * Свойство - описание карточки
     */
    private String cardDescription;

    private CardListAdapter adapter;

    private FloatingActionButton addCardButton;

    /**
     * Свойство - экземпляр класса {@link GetData}
     */
    protected GetData mGetData;

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<Card> data;

    /**
     * Свойство - идентификаторы карточек
     */
    private ArrayList<String> cids;

    /**
     * Свойство - идентификатор пользователя
     */
    private String userId;

    @BindView(R.id.recyclerViewColumn)
    RecyclerView rv;

    /**
     * Свойство - элемент, символизирующий загрузку данных
     */
    @BindView(R.id.loadingDots)
    LoadingDots loadingDots;

    public static ColumnFragment getInstance(Context context, int columnId, String columnName) {
        Bundle args = new Bundle();
        ColumnFragment fragment = new ColumnFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setColumnId(columnId);
        fragment.setTitle(columnName);
        fragment.initialize();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ButterKnife.bind(this, view);

        addCardButton = ButterKnife.findById(getActivity(), R.id.btnAddCard);
        addCardButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams  params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(dp2px(5), dp2px(20), dp2px(5), dp2px(20));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getResources().getString(R.string.create_card));

                final EditText inputName =
                        (EditText) getLayoutInflater(savedInstanceState)
                                .inflate(R.layout.item_edittext_style, null);
                inputName.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                inputName.setLayoutParams(params);
                inputName.setHint(getResources().getString(R.string.enter_card_name));
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(inputName);

                final EditText inputDesc =
                        (EditText) getLayoutInflater(savedInstanceState)
                                .inflate(R.layout.item_edittext_style, null);
                inputDesc.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                inputDesc.setLayoutParams(params);
                inputDesc.setHint(getResources().getString(R.string.enter_card_description));
                layout.addView(inputDesc);


                builder.setView(layout)
                        .setPositiveButton(getResources().getString(R.string.btn_ok),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cardName = inputName.getText().toString();
                                cardDescription = inputDesc.getText().toString();

                                if (cardName.length() != 0) {

                                    columnId =
                                            tabsColumnOrder.get(((ViewPager) getActivity()
                                                    .findViewById(R.id.viewPagerColumns))
                                                    .getCurrentItem());

                                    AddCard addCard = new AddCard();
                                    addCard.execute();

                                    changeActivityCompat(getActivity());

                                } else dialog.cancel();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        mGetData = new GetData();
        mGetData.execute();

        return view;
    }

    /**
     * Конвертер из dp в px
     *
     * @param dp - значения в dp
     * @return - значение в px
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Инициализация основных элементов
      */
    public void initialize() {
        data = new ArrayList<>();
        cids = new ArrayList<>();
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    /**
     * Получение информации о колонке
     */
    private class GetData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_COLUMN_INFO_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = COLUMN_ID + EQUALS + columnId
                    + AMPERSAND + ID + EQUALS + loadText(context, ID);

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

            initialize();

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
                JSONArray cidsJSON = dataJsonObj.getJSONArray(COLUMN_IDS);
                JSONArray uidsJSON = dataJsonObj.getJSONArray(USER_IDS);

                userId = loadText(context, ID);

                for (int i = 0; i < cidsJSON.length(); i++) {
                    cids.add(cidsJSON.getString(i));
                }

                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < cids.size(); i++) {

                    /**
                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
                     */
                    JSONObject columnInfo = dataJsonObj.getJSONObject(cids.get(i));

                    int columnOrder = ((ViewPager) getActivity()
                            .findViewById(R.id.viewPagerColumns))
                            .getCurrentItem();


                    data.add(new Card(
                            Integer.parseInt(cids.get(i)), Integer.parseInt(columnInfo.getString(AMOUNT)),
                            columnInfo.getString(NAME), columnInfo.getBoolean(BELONG),
                            columnInfo.getInt(CARD_COLOR), columnOrder
                    ));
                }


//                Log.i("KEKE0", String.valueOf(columnOrder));
                adapter = new CardListAdapter(data, getContext());
                rv.setLayoutManager(new LinearLayoutManager(context));
                rv.setAdapter(adapter);

                rv.addOnScrollListener(new RecyclerView.OnScrollListener(){
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                        if (dy > 0) addCardButton.hide();
                        else if (dy < 0) addCardButton.show();
                    }
                });

                loadingDots.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Обновление данных о колонке
     */
    private class RefreshData extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_COLUMN_INFO_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = COLUMN_ID + EQUALS + columnId
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
                JSONArray cidsJSON = dataJsonObj.getJSONArray(COLUMN_IDS);
                JSONArray uidsJSON = dataJsonObj.getJSONArray(USER_IDS);


                for (int i = 0; i < cidsJSON.length(); i++) {
                    if (!cids.contains(cidsJSON.getString(i)))
                        cids.add(cidsJSON.getString(i));
                }

                Log.i("KEKE", String.valueOf(cids));


                /**
                 * Составление view-элементов с краткой информацией о пользователях
                 */
                for (int i = 0; i < cids.size(); i++) {
                    boolean exist = false;


                    /**
                     * Получение JSON-объекта с информацией о конкретном пользователе по его идентификатору.
                     */

                    JSONObject columnInfo = dataJsonObj.getJSONObject(cids.get(i));

                    for (int j = 0; j < data.size(); j++){
                        if (data.get(j).getId() == Integer.parseInt(cids.get(i))) {
                            exist = true;
                            break;
                        }
                    }

                    int columnOrder = ((ViewPager) getActivity()
                            .findViewById(R.id.viewPagerColumns))
                            .getCurrentItem();

                    if (!exist)
                        data.add(new Card(
                                Integer.parseInt(cids.get(i)), Integer.parseInt(columnInfo.getString(AMOUNT)),
                                columnInfo.getString(NAME), columnInfo.getBoolean(BELONG),
                                columnInfo.getInt(CARD_COLOR), columnOrder
                        ));
                }

                adapter.setData(data);
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Добавление карточки
     */
    private class AddCard extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {


            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_ADD_CARD_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = COLUMN_ID + EQUALS + columnId
                    + AMPERSAND + NAME + EQUALS + cardName
                    + AMPERSAND + DESCRIPTION + EQUALS + cardDescription;

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
//            new RefreshData().execute();
        }
    }
}
