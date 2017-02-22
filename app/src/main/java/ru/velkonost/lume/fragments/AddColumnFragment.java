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

import java.io.IOException;

import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.BoardWelcomeActivity;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_COLUMN_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class AddColumnFragment extends BaseTabFragment {
    private static final int LAYOUT = R.layout.fragment_add_column;

    private EditText createColumnName;
    private String boardId;

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
        setHasOptionsMenu(true);


        createColumnName = (EditText) view.findViewById(R.id.editName);
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

                    Intent intent = new Intent(context, BoardWelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(BOARD_ID, Integer.parseInt(boardId));
                    context.startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.activity_right_in,
                            R.anim.activity_diagonaltranslate);

                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.activity_right_in,
                            R.anim.activity_diagonaltranslate);
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
        }
    }
}
