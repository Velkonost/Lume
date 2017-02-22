package ru.velkonost.lume.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.BoardWelcomeActivity;
import ru.velkonost.lume.descriptions.Board;

import static ru.velkonost.lume.Constants.BOARD_ID;
import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;

public class BoardListAdapter extends RecyclerView.Adapter<BoardListAdapter.BoardViewHolder>{

    private List<Board> data;
    private LayoutInflater inflater;
    private Context context;

    public BoardListAdapter(Context context, List<Board> data) {
        this.context = context;
        this.data = data;

        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Board> data) {
        this.data = data;
    }

    @Override
    public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_board, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BoardViewHolder holder, int position) {
        final Board item = data.get(position);

        holder.boardName.setText(item.getName());
        holder.boardName.setSelected(true);
        holder.boardName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.boardName.setHorizontallyScrolling(true);
        holder.boardName.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BoardWelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(BOARD_ID, item.getId());
                context.startActivity(intent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BoardViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mRelativeLayout;
        TextView boardName;

        BoardViewHolder(View itemView) {
            super(itemView);

            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutBoard);
            boardName = (TextView) itemView.findViewById(R.id.boardName);
        }
    }
}
