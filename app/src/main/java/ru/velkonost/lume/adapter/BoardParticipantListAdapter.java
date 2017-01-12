package ru.velkonost.lume.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.BoardParticipant;

import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Managers.ImageManager.fetchImage;
import static ru.velkonost.lume.Managers.ImageManager.getCircleMaskedBitmap;
import static ru.velkonost.lume.R.id.userId;

public class BoardParticipantListAdapter extends ArrayAdapter {

    private List<BoardParticipant> data;
    private Context mContext;

    public BoardParticipantListAdapter(Context context, List<BoardParticipant> data) {
        super(context, R.layout.item_board_participant, data);
        mContext = context;
        this.data = data;
    }

    public void setData(List<BoardParticipant> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull final ViewGroup parent) {
        final BoardParticipant boardParticipant = data.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_board_participant, null);
        }

        /** Формирование адреса, по которому лежит аватар пользователя */
        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                + SERVER_AVATAR + SLASH + boardParticipant.getAvatar()
                + SLASH + boardParticipant.getId() + JPG;

        ((TextView) convertView.findViewById(R.id.userId)).setText(boardParticipant.getId());

        fetchImage(avatarURL, (ImageView) convertView.findViewById(R.id.avatar), true, false);
        Bitmap bitmap = ((BitmapDrawable) ((ImageView) convertView.findViewById(R.id.avatar))
                .getDrawable()).getBitmap();
        ((ImageView) convertView.findViewById(R.id.avatar))
                .setImageBitmap(getCircleMaskedBitmap(bitmap, 25));


//        final View finalConvertView = convertView;
//        (convertView.findViewById(R.id.lluser)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                (finalConvertView.findViewById(R.id.unreadMessages)).setVisibility(View.INVISIBLE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(mContext, MessageActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra(DIALOG_ID, Integer.parseInt(dialogContact.getDialogId()));
//                        intent.putExtra(ID, Integer.parseInt(dialogContact.getId()));
//                        mContext.startActivity(intent);
//                    }
//                }, 350);
//            }
//        });
//
//        (convertView.findViewById(R.id.lluser)).setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                TSnackbar snackbar = TSnackbar.make(parent,
//                        dialogContact.getName().length() == 0
//                                ? dialogContact.getLogin()
//                                : dialogContact.getSurname().length() == 0
//                                ? dialogContact.getLogin()
//                                : dialogContact.getName() + " " + dialogContact.getSurname(),
//                        TSnackbar.LENGTH_SHORT);
//
//                snackbar.setActionTextColor(Color.WHITE);
//                View snackbarView = snackbar.getView();
//                snackbarView.setBackgroundColor(Color.parseColor("#CC00CC"));
//                TextView textView = (TextView) snackbarView
//                        .findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
//                textView.setTextColor(Color.YELLOW);
//                snackbar.show();
//
//                return true;
//            }
//        });

        return convertView;
    }

}
