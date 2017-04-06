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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.CardCommentListAdapter;
import ru.velkonost.lume.model.CardComment;

/**
 * @author Velkonost
 *
 * Комментарии карточки
 */
public class CardCommentsFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_card_comments;

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<CardComment> mCardComments;

    private CardCommentListAdapter adapter;

    protected View view;

    protected Context context;

    @BindView(R.id.cvComments)
    CardView cv;

    @BindView(R.id.recyclerViewComments)
    RecyclerView recyclerView;

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
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new CardCommentListAdapter(getActivity(), mCardComments);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) cv.setVisibility(View.INVISIBLE);

        return view;
    }

    public void setCardComments(List<CardComment> cardComments) {
        mCardComments= cardComments;
    }

    /**
     * Обновление состояния списка комментариев карточки
     */
    public void refreshComments(List<CardComment> mCardComments) {

        adapter.setData(mCardComments);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 0) cv.setVisibility(View.VISIBLE);

    }

    public void setContext(Context context) {
        this.context = context;
    }
}
