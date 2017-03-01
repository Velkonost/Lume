package ru.velkonost.lume.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Depository;
import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.BoardColumnsActivity;
import ru.velkonost.lume.descriptions.BoardColumn;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.COLUMN_IDS;
import static ru.velkonost.lume.Constants.COLUMN_ORDER;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_COLUMN_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_BOARD_INFO_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.USER_IDS;
import static ru.velkonost.lume.fragments.BoardColumnsTabsFragmentAdapter.last;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class AddColumnFragment extends BaseTabFragment {
    private static final int LAYOUT = R.layout.fragment_add_column;

    @BindView(R.id.editName) EditText createColumnName;
    private String boardId;
    private List<BoardColumn> mBoardColumns;

    public static AddColumnFragment getInstance(Context context, String boardId) {
        Bundle args = new Bundle();

        AddColumnFragment fragment = new AddColumnFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setBoardId(boardId);
        fragment.setTitle("+");

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_settings, menu);
        menu.findItem(R.id.action_change).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.item1:
                if (createColumnName.getText().toString().length() != 0) {
                    AddColumn addColumn = new AddColumn();
                    addColumn.execute();

                }
        }


        return super.onOptionsItemSelected(item);

    }

    public void setContext(Context context) {
        this.context = context;
    }
    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    private class AddColumn extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_ADD_COLUMN_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId
                    + AMPERSAND + NAME + EQUALS + createColumnName.getText().toString();

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
            new GetBoardInfo().execute();
        }
    }
    private class GetBoardInfo extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_BOARD_INFO_METHOD;


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
                JSONArray idsJSON = dataJsonObj.getJSONArray(USER_IDS);
                JSONArray cidsJSON = dataJsonObj.getJSONArray(COLUMN_IDS);

                ArrayList<String> cids = new ArrayList<>();
                mBoardColumns = new ArrayList<>();


                for (int i = 0; i < cidsJSON.length(); i++) {
                    cids.add(cidsJSON.getString(i));
                }

                for (int i = 0; i < cids.size(); i++) {
                    JSONObject columnInfo = dataJsonObj.getJSONObject(cids.get(i));

                    mBoardColumns.add(new BoardColumn(
                            Integer.parseInt(columnInfo.getString(ID)),
                            columnInfo.getString(NAME),  i)
                    );
                }

                Depository.setBoardColumns(mBoardColumns);

                Intent intent = new Intent(context, BoardColumnsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(BOARD_ID, boardId);
                intent.putExtra(COLUMN_ORDER, last + 1);
                context.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_right_in,
                        R.anim.activity_diagonaltranslate);

                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.activity_right_in,
                        R.anim.activity_diagonaltranslate);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
