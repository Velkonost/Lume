package ru.velkonost.lume.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.MessageActivity;
import ru.velkonost.lume.activity.ProfileActivity;
import ru.velkonost.lume.descriptions.DialogContact;

import static ru.velkonost.lume.Constants.DIALOG_ID;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Managers.SetImageManager.fetchImage;

public class DialogListAdapterRv extends RecyclerView.Adapter<DialogListAdapterRv.DialogListViewHolder> {

    private List<DialogContact> data;
    private Context mContext;

    ViewGroup parent;

    public DialogListAdapterRv(List<DialogContact> data, Context mContext) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public DialogListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog, parent, false);
        this.parent = parent;

        return new DialogListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DialogListViewHolder holder, int position) {
        final DialogContact dialogContact = data.get(position);

        /** Формирование адреса, по которому лежит аватар пользователя */
        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                + SERVER_AVATAR + SLASH + dialogContact.getAvatar()
                + SLASH + dialogContact.getId() + JPG;

        holder.userId.setText(dialogContact.getId());

        if (!dialogContact.isAvatar()){
            fetchImage(avatarURL, holder.avatar, false, false);
            Bitmap bitmap = ((BitmapDrawable) holder.avatar.getDrawable()).getBitmap();
            holder.avatar.setImageBitmap(bitmap);

            dialogContact.setIsAvatar(true);
        }

        if (dialogContact.getUnreadMessages() != 0){
            holder.unreadMessages.setText(String.valueOf(dialogContact.getUnreadMessages()));
            holder.unreadMessages.setVisibility(View.VISIBLE);
        }

        final String collocutor = dialogContact.getName().length() == 0
                ? dialogContact.getLogin()
                : dialogContact.getSurname().length() == 0
                ? dialogContact.getLogin()
                : dialogContact.getName() + " " + dialogContact.getSurname();


        holder.lluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.unreadMessages.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, MessageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(DIALOG_ID, Integer.parseInt(dialogContact.getDialogId()));
                        intent.putExtra(ID, Integer.parseInt(dialogContact.getId()));
                        intent.putExtra(NAME, collocutor);
                        mContext.startActivity(intent);
                    }
                }, 150);
            }
        });


        holder.lluser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                TSnackbar snackbar = TSnackbar.make(parent,
                        dialogContact.getName().length() == 0
                                ? dialogContact.getLogin()
                                : dialogContact.getSurname().length() == 0
                                ? dialogContact.getLogin()
                                : dialogContact.getName() + " " + dialogContact.getSurname(),
                        TSnackbar.LENGTH_SHORT);

                snackbar.setActionTextColor(Color.WHITE);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                TextView textView = (TextView) snackbarView
                        .findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(18);
                snackbar.show();

                snackbarView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ID, Integer.parseInt(dialogContact.getId()));
                        mContext.startActivity(intent);
                    }
                });

                return true;
            }
        });


    }

    public void setData(List<DialogContact> data) {
        this.data = data;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class DialogListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userId)
        TextView userId;

        @BindView(R.id.unreadMessages)
        TextView unreadMessages;

        @BindView(R.id.avatar)
        ImageView avatar;

        @BindView(R.id.lluser)
        RelativeLayout lluser;

        public DialogListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
