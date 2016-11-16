package ru.velkonost.lume;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileFrame extends LinearLayout {
    private CardView mCardView;
    private TextView mTitle;
    private TextView mDescription;

    public ProfileFrame(Context context) {
        super(context);
        initComponent();
    }

    public ProfileFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponent();
    }

    public ProfileFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponent();
    }

    private void initComponent() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_two_line_block, this);
        mCardView = (CardView) findViewById(R.id.cardView);
        mTitle = (TextView) findViewById(R.id.titleCardProfile);
        mDescription = (TextView) findViewById(R.id.descriptionCardProfile);


//        updateFields();
    }

    public void setCardView(CardView cardView) {
        mCardView = cardView;
    }

    public void setCardViewColor(int color) {
        mCardView.setBackgroundColor(color);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setDescription(String description) {
        mDescription.setText(description);
    }

    public CardView getCardView() {
        return mCardView;
    }

    public TextView getTitle() {
        return mTitle;
    }

    public TextView getDescription() {
        return mDescription;
    }
}
