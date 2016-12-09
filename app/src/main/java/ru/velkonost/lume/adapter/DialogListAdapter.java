package ru.velkonost.lume.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.ProfileActivity;
import ru.velkonost.lume.descriptions.DialogContact;

import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Managers.ImageManager.fetchImage;
import static ru.velkonost.lume.Managers.ImageManager.getCircleMaskedBitmap;
import static ru.velkonost.lume.R.id.userId;

public class DialogListAdapter extends ArrayAdapter {

    private List<DialogContact> data;
    private Context mContext;

    public DialogListAdapter(Context context, List<DialogContact> data) {
        super(context, R.layout.item_dialog, data);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, @NonNull final ViewGroup parent) {
        final DialogContact dialogContact = (DialogContact) getItem(position);
        if (convertView == null) {
            convertView =
                    LayoutInflater.from(getContext()).inflate(R.layout.item_dialog, null);
        }

        /** Формирование адреса, по которому лежит аватар пользователя */
        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                + SERVER_AVATAR + SLASH + dialogContact.getAvatar()
                + SLASH + dialogContact.getId() + JPG;

        ((TextView) convertView.findViewById(userId)).setText(dialogContact.getId());
        fetchImage(avatarURL, (ImageView) convertView.findViewById(R.id.avatar), true, false);

        Bitmap bitmap = ((BitmapDrawable) ((ImageView) convertView.findViewById(R.id.avatar)).getDrawable()).getBitmap();
        ((ImageView) convertView.findViewById(R.id.avatar)).setImageBitmap(getCircleMaskedBitmap(bitmap, 25));

        (convertView.findViewById(R.id.lluser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ID, Integer.parseInt(dialogContact.getId()));
                        mContext.startActivity(intent);
                    }
                }, 350);
            }


        });
        (convertView.findViewById(R.id.lluser)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                CoordinatorLayout coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);

                Snackbar snack = Snackbar.make(parent,
                        dialogContact.getName().length() == 0
                                ? dialogContact.getLogin()
                                : dialogContact.getSurname().length() == 0
                                ? dialogContact.getLogin()
                                : dialogContact.getName() + " " + dialogContact.getSurname(),
                        Snackbar.LENGTH_SHORT);
                snack.show();


//                Toast.makeText(mContext,
//                        dialogContact.getName().length() == 0
//                        ? dialogContact.getLogin()
//                        : dialogContact.getSurname().length() == 0
//                        ? dialogContact.getLogin()
//                        : dialogContact.getName() + " " + dialogContact.getSurname(),
//                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return convertView;
    }

}
