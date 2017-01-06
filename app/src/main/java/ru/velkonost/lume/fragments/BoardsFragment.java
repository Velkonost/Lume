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

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.BoardListAdapter;
import ru.velkonost.lume.descriptions.Board;

public class BoardsFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_board;

    private List<Board> mBoards;
    private BoardListAdapter adapter;
    protected View view;
    protected Context context;

    public static BoardsFragment getInstance(Context context, List<Board> boards) {
        Bundle args = new Bundle();
        BoardsFragment fragment = new BoardsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setBoards(boards);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewBoards);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new BoardListAdapter(getActivity(), mBoards);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void refreshBoards (List<Board> mBoards) {
        adapter.setData(mBoards);
        adapter.notifyDataSetChanged();
    }

    public void setContext (Context context) {this.context = context;}

    public void setBoards(List<Board> mBoards) {
        this.mBoards = mBoards;
    }
}
