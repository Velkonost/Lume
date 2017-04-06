package ru.velkonost.lume.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Depository;
import ru.velkonost.lume.R;
import ru.velkonost.lume.model.Contact;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_INVITE_IN_BOARD_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Managers.SetImageManager.fetchImage;
import static ru.velkonost.lume.Managers.SetImageManager.getCircleMaskedBitmap;
import static ru.velkonost.lume.activity.BoardWelcomeActivity.popupWindowBoardInvite;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

/**
 * @author Velkonost
 *
 * Список контактов, которые можно пригласить в доску
 */
public class BoardInviteListAdapter extends RecyclerView.Adapter<BoardInviteListAdapter.BoardInviteViewHolder> {

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<Contact> data;

    private LayoutInflater inflater;

    /**
     * Свойство - идентификатор доски
     */
    private int boardId;

    /**
     * Свойство - идентификатор контакта
     */
    private int curContactId;

    public BoardInviteListAdapter(Context context, List<Contact> data, int boardId) {

        this.data = data;
        this.boardId = boardId;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public BoardInviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_board_invite, parent, false);

        return new BoardInviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BoardInviteViewHolder holder, int position) {
        Contact item = data.get(position);
        holder.id = item.getId();
        holder.userName.setText(
                item.getName().length() == 0
                        ? item.getLogin()
                        : item.getSurname().length() == 0
                        ? item.getLogin()
                        : item.getName() + " " + item.getSurname()
        );

        holder.userName.setSelected(true);
        holder.userName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.userName.setHorizontallyScrolling(true);
        holder.userName.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);

        if (holder.userName.getText().toString().equals(item.getLogin()))
            holder.userWithoutName.setImageResource(R.drawable.withoutname);
        else
            holder.userLogin.setText(item.getLogin());

        holder.userLogin.setSelected(true);
        holder.userLogin.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.userLogin.setHorizontallyScrolling(true);
        holder.userLogin.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);

        /** Формирование адреса, по которому лежит аватар пользователя */
        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                + SERVER_AVATAR + SLASH + item.getAvatar()
                + SLASH + item.getId() + JPG;

        fetchImage(avatarURL, holder.userAvatar, true, false);
        Bitmap bitmap = ((BitmapDrawable)holder.userAvatar.getDrawable()).getBitmap();
        holder.userAvatar.setImageBitmap(getCircleMaskedBitmap(bitmap, 25));

        holder.mLinearLayout.setId(Integer.parseInt(item.getId()));

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Invite invite = new Invite();
                invite.execute();
                curContactId = Integer.parseInt(holder.id);

                Depository.setRefreshPopup(true);
                popupWindowBoardInvite.dismiss();

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Contact> data) {
        this.data = data;
    }

    class BoardInviteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relativeLayoutContact) LinearLayout mLinearLayout;
        @BindView(R.id.userName) TextView userName;
        @BindView(R.id.userLogin) TextView userLogin;
        @BindView(R.id.userWithoutName) ImageView userWithoutName;
        @BindView(R.id.userAvatar) ImageView userAvatar;

        String id;

        BoardInviteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    /**
     * Приглашение контакта в доску
     */
    private class Invite extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_INVITE_IN_BOARD_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = BOARD_ID + EQUALS + boardId
                    + AMPERSAND + ID + EQUALS + curContactId;

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
