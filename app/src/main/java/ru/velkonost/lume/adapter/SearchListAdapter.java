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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.ProfileActivity;
import ru.velkonost.lume.model.SearchContact;

import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Managers.HtmlConverterManager.fromHtml;
import static ru.velkonost.lume.Managers.SetImageManager.fetchImage;
import static ru.velkonost.lume.Managers.SetImageManager.getCircleMaskedBitmap;

/**
 * @author Velkonost
 *
 * Список контактов, частично или полностью соответствующих данным, по которым искал пользователь
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter{

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<SearchContact> data;

    private LayoutInflater inflater;

    private Context context;

    public SearchListAdapter(Context context, List<SearchContact> data) {
        this.context = context;
        this.data = data;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public SearchListAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_search_block, parent, false);
        return new SearchListAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchListAdapter.SearchViewHolder holder, int position) {
        SearchContact item = data.get(position);
        holder.id = item.getId();
        holder.userName.setText(
                fromHtml(
                    item.getName().length() == 0
                            ? item.getLogin()
                            : item.getSurname().length() == 0
                            ? item.getLogin()
                            : item.getName() + " " + item.getSurname()
                )
        );

        holder.userName.setSelected(true);
        holder.userName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.userName.setHorizontallyScrolling(true);
        holder.userName.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);

        if (holder.userName.getText().toString().equals(item.getLogin()))
            holder.userAvatar.setImageResource(R.drawable.withoutname);
        else {
            holder.userLogin.setText(item.getLogin());

            /** Формирование адреса, по которому лежит аватар пользователя */
            String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                    + SERVER_AVATAR + SLASH + item.getAvatar()
                    + SLASH + item.getId() + JPG;

            fetchImage(avatarURL, holder.userAvatar, true, false);
            Bitmap bitmap = ((BitmapDrawable)holder.userAvatar.getDrawable()).getBitmap();
            holder.userAvatar.setImageBitmap(getCircleMaskedBitmap(bitmap, 25));
        }

        holder.userLogin.setSelected(true);
        holder.userLogin.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.userLogin.setHorizontallyScrolling(true);
        holder.userLogin.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);


        /**
         * Формируется место проживания из имеющихся данных.
         **/
        holder.livingPlace.setText(
                item.getCountry().length() != 0
                ? item.getCity().length() != 0
                ? item.getCountry() + ", " + item.getCity()
                : "" : ""
        );


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

    public void setData(List<SearchContact> data) {
        this.data = data;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        String id;

        @BindView(R.id.relativeLayoutSearch) LinearLayout mRelativeLayout;
        @BindView(R.id.livingPlace) TextView livingPlace;
        @BindView(R.id.userName) TextView userName;
        @BindView(R.id.userLogin) TextView userLogin;
        @BindView(R.id.userAvatar) ImageView userAvatar;

        SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
