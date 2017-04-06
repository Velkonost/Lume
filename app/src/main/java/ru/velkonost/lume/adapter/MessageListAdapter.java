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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.model.Message;

/**
 * @author Velkonost
 *
 * Список сообщений диалога
 */
public class MessageListAdapter
        extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<Message> data;

    private LayoutInflater inflater;

    private Context context;

    public MessageListAdapter(Context context, List<Message> data) {
        this.context = context;
        this.data = data;

        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Message> data) {
        this.data = data;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message item = data.get(position);

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

        params.setMargins(dp2px(10), dp2px(5), dp2px(10), dp2px(5));
        holder.mTextView.setLayoutParams(params);

        holder.mTextView.setPadding(dp2px(7), dp2px(5), dp2px(7), dp2px(5));

    }

    /**
     * Конвертер из dp в px
     *
     * @param dp - значения в dp
     * @return - значение в px
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.messageText) TextView mTextView;

        MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
