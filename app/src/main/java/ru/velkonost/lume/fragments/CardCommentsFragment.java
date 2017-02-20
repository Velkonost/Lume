package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.CardCommentListAdapter;
import ru.velkonost.lume.descriptions.CardComment;

public class CardCommentsFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_card_comments;

    private List<CardComment> mCardComments;
    private RecyclerView recyclerView;
    private CardCommentListAdapter adapter;
    protected View view;
    protected Context context;
    private CardView cv;

    public static CardCommentsFragment getInstance(Context context, List<CardComment> cardComments) {
        Bundle args = new Bundle();
        CardCommentsFragment fragment = new CardCommentsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setCardComments(cardComments);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        cv = (CardView) view.findViewById(R.id.cvComments);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new CardCommentListAdapter(getActivity(), mCardComments);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) cv.setVisibility(View.INVISIBLE);

        return view;
    }

    public void setCardComments(List<CardComment> cardComments) {
        mCardComments= cardComments;
    }

    public void refreshComments(List<CardComment> mCardComments) {

        adapter.setData(mCardComments);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 0) cv.setVisibility(View.VISIBLE);

    }

    public void setContext(Context context) {
        this.context = context;
    }
}
