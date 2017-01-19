package ru.velkonost.lume.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.BoardCardActivity;
import ru.velkonost.lume.descriptions.Card;

import static ru.velkonost.lume.Constants.CARD_ID;
import static ru.velkonost.lume.Constants.CARD_NAME;
import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardListViewHolder> {

    private List<Card> data;
    private Context mContext;

    public CardListAdapter(List<Card> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public CardListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_column_card, parent, false);

        return new CardListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardListViewHolder holder, int position) {
        final Card item = data.get(position);

        final int id = item.getId();

        holder.title.setText(item.getName());
        holder.title.setSelected(true);
        holder.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.title.setHorizontallyScrolling(true);
        holder.title.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);

        holder.amount.setText(String.valueOf(item.getAmountParticipants()));
        holder.amount.setVisibility(View.VISIBLE);

        if (item.isBelong()){
            holder.isBelong.setVisibility(View.VISIBLE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, BoardCardActivity.class);
                intent.putExtra(CARD_ID, id);
                intent.putExtra(CARD_NAME, item.getName());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CardListViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;

        TextView title;
        TextView amount;

        ImageView isBelong;

        public CardListViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            amount = (TextView) itemView.findViewById(R.id.numberParticipants);

            isBelong = (ImageView) itemView.findViewById(R.id.isYouParticipant);
        }
    }
}
