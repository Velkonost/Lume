package ru.velkonost.lume.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Depository;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.BoardColumn;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.CARD_ID;
import static ru.velkonost.lume.Constants.COLUMN_ID;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_MOVE_CARD_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.activity.BoardCardActivity.popupWindowColumns;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

/**
 * @author Velkonost
 *
 * Список колонок доски, в которые можно переместить карточку
 */
public class CardMoveListAdapter extends RecyclerView.Adapter<CardMoveListAdapter.CardMoveViewHolder> {

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<BoardColumn> data;

    private LayoutInflater inflater;

    /**
     * Свойство - идентификатор карточки
     */
    private int cardId;

    /**
     * Свойство - идентификатор колонки
     */
    private int curColumnId;

    public CardMoveListAdapter(Context context, List<BoardColumn> data, int cardId) {
        this.data = data;
        this.cardId = cardId;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public CardMoveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_board_move_card, parent, false);

        return new CardMoveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardMoveViewHolder holder, int position) {
        BoardColumn item = data.get(position);
        holder.id = String.valueOf(item.getId());
        holder.userName.setText(item.getName());

        holder.userName.setSelected(true);
        holder.userName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.userName.setHorizontallyScrolling(true);
        holder.userName.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);

        holder.mRelativeLayout.setId(Integer.parseInt(String.valueOf(item.getId())));

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Move invite = new Move();
                invite.execute();
                curColumnId = Integer.parseInt(holder.id);

                Depository.setRefreshPopup(true);
                popupWindowColumns.dismiss();

            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<BoardColumn> data) {
        this.data = data;
    }

    class CardMoveViewHolder extends RecyclerView.ViewHolder {

        String id;

        @BindView(R.id.relativeLayoutContact) LinearLayout mRelativeLayout;
        @BindView(R.id.userName) TextView userName;

        CardMoveViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Перемещение карточки
     */
    private class Move extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_MOVE_CARD_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + COLUMN_ID + EQUALS + curColumnId;

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
