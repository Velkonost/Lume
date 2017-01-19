package ru.velkonost.lume.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.CardComment;
import ru.velkonost.lume.descriptions.Message;

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
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new CardCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardCommentViewHolder holder, int position) {
        CardComment item = data.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            holder.mTextView.setText(Html.fromHtml(item.getText(), Html.FROM_HTML_MODE_LEGACY));
        else
            holder.mTextView.setText(Html.fromHtml(item.getText()));

        LinearLayout.LayoutParams params
                = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);

        if(item.isFromMe()) {
            holder.mTextView.setBackground(ContextCompat.getDrawable(context,
                    R.drawable.rectangle_message_from));

            params.gravity = Gravity.RIGHT;

        } else {

            if (!item.isExist() && item.getStatus() == 1) {

                Drawable[] layers = new Drawable[2];
                layers[0] = ContextCompat.getDrawable(context, R.drawable.rectangle_message_to_unread);
                layers[1] = ContextCompat.getDrawable(context, R.drawable.rectangle_message_to);

                TransitionDrawable transition = new TransitionDrawable(layers);
                holder.mTextView.setBackground(transition);
                transition.startTransition(6000);

            } else {
                holder.mTextView.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.rectangle_message_to));
            }

            params.gravity = Gravity.LEFT;
        }

//        params.setMargins(dp2px(10), dp2px(5), dp2px(10), dp2px(5));
//        holder.mTextView.setLayoutParams(params);
//
//        holder.mTextView.setPadding(dp2px(20), dp2px(10), dp2px(20), dp2px(10));

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class CardCommentViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        CardCommentViewHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.messageText);
        }
    }
}
