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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.CardListAdapter;
import ru.velkonost.lume.descriptions.Card;

import static ru.velkonost.lume.Constants.AMOUNT;
import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BELONG;
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
import static ru.velkonost.lume.Managers.Initializations.changeActivityCompat;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;
import static ru.velkonost.lume.fragments.BoardColumnsTabsFragmentAdapter.tabsColumnOrder;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class ColumnFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.fragment_column;

    private int columnId;

    private String cardName;
    private String cardDescription;

    private FloatingActionButton addCardButton;

    protected GetData mGetData;

    private List<Card> data;
    private ArrayList<String> cids;

    public static ColumnFragment getInstance(Context context, int columnId, String columnName) {
        Bundle args = new Bundle();
        ColumnFragment fragment = new ColumnFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setColumnId(columnId);
        fragment.setTitle(columnName);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        data = new ArrayList<>();
        cids = new ArrayList<>();

        addCardButton = (FloatingActionButton) getActivity().findViewById(R.id.btnAddCard);
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

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

                String userId = loadText(context, ID);

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

                    data.add(new Card(
                            Integer.parseInt(cids.get(i)), Integer.parseInt(columnInfo.getString(AMOUNT)),
                            columnInfo.getString(NAME), columnInfo.getBoolean(BELONG)
                    ));
                }

                RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerViewColumn);
                rv.setLayoutManager(new LinearLayoutManager(context));
                rv.setAdapter(new CardListAdapter(data, getContext()));

                rv.addOnScrollListener(new RecyclerView.OnScrollListener(){
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                        if (dy > 0) addCardButton.hide();
                        else if (dy < 0) addCardButton.show();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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
        }
    }
}
