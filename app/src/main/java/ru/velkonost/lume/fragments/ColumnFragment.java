package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.CardListAdapter;
import ru.velkonost.lume.descriptions.Card;

import static ru.velkonost.lume.Constants.COLUMN_ID;
import static ru.velkonost.lume.Constants.COLUMN_IDS;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_COLUMN_INFO_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER_IDS;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class ColumnFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.fragment_column;

    private int columnId;

    protected GetData mGetData;

    private List<Card> data;
    private ArrayList<String> cids;

    public static ColumnFragment getInstance(Context context, int columnId) {
        Bundle args = new Bundle();
        ColumnFragment fragment = new ColumnFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setColumnId(columnId);
        fragment.setTitle(String.valueOf(columnId));

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        data = new ArrayList<>();
        cids = new ArrayList<>();

        mGetData = new GetData();
        mGetData.execute();

        return view;
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
            @SuppressWarnings("WrongThread") String params = COLUMN_ID + EQUALS + columnId;

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

                int amountParticipants = uidsJSON.length();

                for (int i = 0; i < cidsJSON.length(); i++){
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
                            Integer.parseInt(cids.get(i)), columnInfo.getString(NAME))
                    );
                }

                RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerViewColumn);
                rv.setLayoutManager(new LinearLayoutManager(context));
                rv.setAdapter(new CardListAdapter(data));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
