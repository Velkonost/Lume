package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.BoardListAdapter;
import ru.velkonost.lume.model.Board;
import ru.velkonost.lume.patterns.SecretTextView;

/**
 * @author Velkonost
 *
 * Список досок, в которых участвует пользователь
 */
public class BoardsFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_board;

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<Board> mBoards;

    private BoardListAdapter adapter;

    protected View view;

    protected Context context;

    @BindView(R.id.btnAddBoard)
    FloatingActionButton fabAddBoard;

    @BindView(R.id.recyclerViewBoards)
    RecyclerView recyclerView;

    @BindView(R.id.zero_boards)
    SecretTextView zeroContacts;

    @BindView(R.id.no_boards)
    RelativeLayout noContactsRl;

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
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0) fabAddBoard.hide();
                else if (dy < 0) fabAddBoard.show();
            }
        });

        adapter = new BoardListAdapter(getActivity(), mBoards);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            noContactsRl.setVisibility(View.VISIBLE);

            zeroContacts.setDuration(1500);
            zeroContacts.show();
        }

        return view;
    }

    /**
     * Обновление состояния списка досок
     */
    public void refreshBoards (List<Board> mBoards) {
        adapter.setData(mBoards);
        adapter.notifyDataSetChanged();
    }



    public void setContext (Context context) {this.context = context;}

    public void setBoards(List<Board> mBoards) {
        this.mBoards = mBoards;
    }
}
