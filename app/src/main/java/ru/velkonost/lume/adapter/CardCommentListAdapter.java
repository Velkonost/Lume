package ru.velkonost.lume.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.CardComment;

public class CardCommentListAdapter
        extends RecyclerView.Adapter<CardCommentListAdapter.CardCommentViewHolder> {

    private List<CardComment> data;
    private LayoutInflater inflater;
    private Context context;

    public CardCommentListAdapter(Context context, List<CardComment> data) {
        this.context = context;
        this.data = data;

        inflater = LayoutInflater.from(context);
    }

    public void setData(List<CardComment> data) {
        this.data = data;
    }

    @Override
    public CardCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new CardCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardCommentViewHolder holder, int position) {
        CardComment item = data.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            holder.commentText.setText(Html.fromHtml(item.getText(), Html.FROM_HTML_MODE_LEGACY));
        else
            holder.commentText.setText(Html.fromHtml(item.getText()));

        holder.userName.setText(item.getUserName());
        holder.commentDate.setText(item.getDate());

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class CardCommentViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView commentDate;
        TextView commentText;

        CardCommentViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.userName);
            commentDate = (TextView) itemView.findViewById(R.id.commentDate);
            commentText = (TextView) itemView.findViewById(R.id.messageText);
        }
    }
}