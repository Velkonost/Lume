package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.BoardWelcomeColumnListAdapter;
import ru.velkonost.lume.model.BoardColumn;

/**
 * @author Velkonost
 *
 * Колонки на первоначальной активности доски
 */
public class BoardWelcomeColumnFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_board_welcome_columns;

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<BoardColumn> mColumns;

    private BoardWelcomeColumnListAdapter adapter;

    protected View view;

    protected Context context;

    /**
     * Свойство - идентификатор доски
     */
    private String boardId;

    @BindView(R.id.gridColumns)
    GridView gridView;

    @BindView(R.id.noColumns)
    TextView textView;

    public static BoardWelcomeColumnFragment getInstance(
            Context context,
            List<BoardColumn> columns,
            String boardId
    ) {


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
        ButterKnife.bind(this, view);

        adapter = new BoardWelcomeColumnListAdapter(getActivity(), mColumns, boardId);
        gridView.setAdapter(adapter);
        adjustGridView(gridView);

        if (gridView.getCount() == 0){
            gridView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);

        } else textView.setTextSize(0);

        return view;
    }

    /**
     * Настройка layout'а
     */
    private void adjustGridView(GridView gridView) {
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setNumColumns(2);
        gridView.setHorizontalSpacing(5);

    }

    /**
     * Обновление состояния списка колонок
     */
    public void refreshColumns (List<BoardColumn> mColumns) {
        gridView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
        textView.setTextSize(0);

        adapter.setData(mColumns);
        adapter.notifyDataSetChanged();

    }

    public void setContext (Context context) {this.context = context;}

    public void setBoardId (String boardId) {this.boardId = boardId;}

    public void setColumns(List<BoardColumn> mColumns) {
        this.mColumns= mColumns;
    }
}
