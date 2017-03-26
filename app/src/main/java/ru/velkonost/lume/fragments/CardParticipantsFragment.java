package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.BoardParticipantsHorizontalListAdapter;
import ru.velkonost.lume.descriptions.BoardParticipant;

/**
 * @author Velkonost
 *
 * Участники карточки
 */
public class CardParticipantsFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_board_participants;

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<BoardParticipant> mBoardsParticipants;

    protected View view;

    protected Context context;

    @BindView(R.id.rvParticipants)
    RecyclerView recyclerView;

    public static CardParticipantsFragment getInstance(Context context, List<BoardParticipant> participants) {
        Bundle args = new Bundle();
        CardParticipantsFragment fragment = new CardParticipantsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setParticipants(participants);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        BoardParticipantsHorizontalListAdapter adapter =
                new BoardParticipantsHorizontalListAdapter(getActivity(), mBoardsParticipants);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setParticipants(List<BoardParticipant> mParticipants) {
        this.mBoardsParticipants = mParticipants;
    }
}