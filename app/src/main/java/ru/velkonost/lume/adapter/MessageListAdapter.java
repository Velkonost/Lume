package ru.velkonost.lume.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.Message;

public class MessageListAdapter
        extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

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

        holder.mTextView.setText(item.getText());

        if(item.isFromMe()) {
            holder.mTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.rectangle_message_from));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;

            holder.mTextView.setLayoutParams(params);
        }
        else
            holder.mTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.rectangle_message_to));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        MessageViewHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.messageText);
        }
    }
}
