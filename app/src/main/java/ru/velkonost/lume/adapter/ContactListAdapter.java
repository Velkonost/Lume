package ru.velkonost.lume.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.ProfileActivity;
import ru.velkonost.lume.descriptions.Contact;

import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Managers.ImageManager.fetchImage;
import static ru.velkonost.lume.Managers.ImageManager.getCircleMaskedBitmap;


public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private List<Contact> data;
    private LayoutInflater inflater;
    private Context context;

    public ContactListAdapter(Context context, List<Contact> data) {
        this.context = context;
        this.data = data;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_contact_block, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
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
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ID, view.getId());
                context.startActivity(intent);

            }
        });


    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        if (data.get(position).getName().length() == 0)
            return String.valueOf(data.get(position).getLogin().charAt(0));
        return String.valueOf(data.get(position).getName().charAt(0));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Contact> data) {
        this.data = data;
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mRelativeLayout;
        String id;
        TextView userName;
        TextView userLogin;
        ImageView userWithoutName;
        ImageView userAvatar;

        ContactViewHolder(View itemView) {
            super(itemView);

            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutContact);

            userName = (TextView) itemView.findViewById(R.id.userName);
            userLogin = (TextView) itemView.findViewById(R.id.userLogin);
            userWithoutName = (ImageView) itemView.findViewById(R.id.userWithoutName);
            userAvatar = (ImageView) itemView.findViewById(R.id.userAvatar);

        }
    }
}
