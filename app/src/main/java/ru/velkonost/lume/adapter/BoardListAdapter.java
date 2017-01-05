package ru.velkonost.lume.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.Message;

public class BoardListAdapter extends RecyclerView.Adapter<BoardListAdapter.BoardViewHolder>{

    private List<Message> data;
    private LayoutInflater inflater;
    private Context context;

    public BoardListAdapter(Context context, List<Message> data) {
        this.context = context;
        this.data = data;

        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Message> data) {
        this.data = data;
    }

    @Override
    public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_board, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BoardViewHolder holder, int position) {
        Message item = data.get(position);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BoardViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        BoardViewHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.messageText);
        }
    }
}
