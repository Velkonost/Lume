package ru.velkonost.lume.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import ru.velkonost.lume.Contact;
import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.PNG;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.ImageManager.fetchImage;


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

        if (holder.userName.getText().toString().equals(item.getLogin()))
            holder.userWithoutName.setImageResource(R.drawable.withoutname);
        else
            holder.userLogin.setText(item.getLogin());

        /** Формирование адреса, по которому лежит аватар пользователя */
        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                + SERVER_AVATAR + SLASH + item.getAvatar()
                + SLASH + item.getId() + PNG;

        fetchImage(avatarURL, holder.userAvatar);

        holder.mRelativeLayout.setId(Integer.parseInt(item.getId()));
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(data.get(position).getId().charAt(0));
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

//            itemView.setOnClickListener(this);

            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutContact);

            userName = (TextView) itemView.findViewById(R.id.userName);
            userLogin = (TextView) itemView.findViewById(R.id.userLogin);
            userWithoutName = (ImageView) itemView.findViewById(R.id.userWithoutName);
            userAvatar = (ImageView) itemView.findViewById(R.id.userAvatar);

        }

//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(context, ProfileActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(ID, id);
//            context.startActivity(intent);
//        }
    }
}