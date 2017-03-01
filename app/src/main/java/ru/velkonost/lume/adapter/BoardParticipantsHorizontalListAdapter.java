package ru.velkonost.lume.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.ProfileActivity;
import ru.velkonost.lume.descriptions.BoardParticipant;

import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Managers.SetImageManager.fetchImage;
import static ru.velkonost.lume.Managers.SetImageManager.getCircleMaskedBitmap;

public class BoardParticipantsHorizontalListAdapter
        extends RecyclerView.Adapter<BoardParticipantsHorizontalListAdapter.BoardParticipantsViewHolder> {

    private List<BoardParticipant> data;
    private LayoutInflater inflater;
    private Context context;

    public BoardParticipantsHorizontalListAdapter(Context context, List<BoardParticipant> data) {
        this.context = context;
        this.data = data;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public BoardParticipantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_board_participant, parent, false);
        return new BoardParticipantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BoardParticipantsViewHolder holder, int position) {
        final BoardParticipant item = data.get(position);


        /** Формирование адреса, по которому лежит аватар пользователя */
        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                + SERVER_AVATAR + SLASH + item.getAvatar()
                + SLASH + item.getId() + JPG;

        holder.userAvatar.setBackgroundResource(0);
        fetchImage(avatarURL, holder.userAvatar, true, false);

        Bitmap bitmap = ((BitmapDrawable) holder.userAvatar.getDrawable()).getBitmap();
        holder.userAvatar.setImageBitmap(getCircleMaskedBitmap(bitmap, 25));


        holder.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ID, item.getId());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<BoardParticipant> data) {
        this.data = data;
    }


    class BoardParticipantsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar) ImageView userAvatar;

        BoardParticipantsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
