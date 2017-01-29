package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.BoardWelcomeColumnListAdapter;
import ru.velkonost.lume.descriptions.BoardColumn;

public class BoardWelcomeColumnFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_board_welcome_columns;

    private List<BoardColumn> mColumns;
    private BoardWelcomeColumnListAdapter adapter;
    protected View view;
    protected Context context;
    private String boardId;

    public static BoardWelcomeColumnFragment getInstance(Context context, List<BoardColumn> columns,
                                                         String boardId) {
        Bundle args = new Bundle();
        BoardWelcomeColumnFragment fragment = new BoardWelcomeColumnFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setColumns(columns);
        fragment.setBoardId(boardId);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        GridView gridView = (GridView) view.findViewById(R.id.gridColumns);

        adapter = new BoardWelcomeColumnListAdapter(getActivity(), mColumns, boardId);
        gridView.setAdapter(adapter);
        adjustGridView(gridView);
        return view;
    }

    private void adjustGridView(GridView gridView) {
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setNumColumns(2);
        gridView.setHorizontalSpacing(5);

    }

    public void refreshColumns (List<BoardColumn> mColumns) {
        adapter.setData(mColumns);
        adapter.notifyDataSetChanged();
    }

    public void setContext (Context context) {this.context = context;}
    public void setBoardId (String boardId) {this.boardId = boardId;}

    public void setColumns(List<BoardColumn> mColumns) {
        this.mColumns= mColumns;
    }
}
