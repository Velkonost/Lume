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

import ru.velkonost.lume.Depository;
import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.Contact;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.CARD_ID;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_INVITE_IN_CARD_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.managers.ImageManager1.fetchImage;
import static ru.velkonost.lume.managers.ImageManager1.getCircleMaskedBitmap;
import static ru.velkonost.lume.activity.BoardCardActivity.popupWindowCardInvite;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

public class CardInviteListAdapter extends RecyclerView.Adapter<CardInviteListAdapter.CardInviteViewHolder> {

    private List<Contact> data;
    private LayoutInflater inflater;
    private Context context;
    private int cardId;
    private int curContactId;

    public CardInviteListAdapter(Context context, List<Contact> data, int cardId) {
        this.context = context;
        this.data = data;
        this.cardId = cardId;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public CardInviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_board_invite, parent, false);

        return new CardInviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardInviteViewHolder holder, int position) {
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

        holder.mRelativeLayout.setId(Integer.parseInt(item.getId()));

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Invite invite = new Invite();
                invite.execute();
                curContactId = Integer.parseInt(holder.id);

                Depository.setRefreshPopup(true);
                popupWindowCardInvite.dismiss();

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

    class CardInviteViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mRelativeLayout;
        String id;
        TextView userName;
        TextView userLogin;
        ImageView userWithoutName;
        ImageView userAvatar;

        CardInviteViewHolder(View itemView) {
            super(itemView);

            mRelativeLayout = (LinearLayout) itemView.findViewById(R.id.relativeLayoutContact);

            userName = (TextView) itemView.findViewById(R.id.userName);
            userLogin = (TextView) itemView.findViewById(R.id.userLogin);
            userWithoutName = (ImageView) itemView.findViewById(R.id.userWithoutName);
            userAvatar = (ImageView) itemView.findViewById(R.id.userAvatar);

        }
    }

    private class Invite extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_INVITE_IN_CARD_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
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
