package ru.velkonost.lume.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.Card;

import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardListViewHolder> {

    private List<Card> data;

    public CardListAdapter(List<Card> data) {
        this.data = data;
    }

    @Override
    public CardListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_column_card, parent, false);

        return new CardListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardListViewHolder holder, int position) {
        Card item = data.get(position);

        holder.title.setText(item.getName());
        holder.title.setSelected(true);
        holder.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.title.setHorizontallyScrolling(true);
        holder.title.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CardListViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        TextView desc;

        public CardListViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
