package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.BoardParticipantsListAdapter;
import ru.velkonost.lume.descriptions.Contact;

/**
 * @author Velkonost
 *
 * Список всех участников доски
 */
public class BoardAllParticipantsFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_board_all_participants;

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<Contact> mContacts;

    private BoardParticipantsListAdapter adapter;

    protected View view;

    protected Context context;

    @BindView(R.id.recyclerViewAllParticipants) FastScrollRecyclerView recyclerView;

    public static BoardAllParticipantsFragment getInstance(Context context, List<Contact> contacts) {
        Bundle args = new Bundle();
        BoardAllParticipantsFragment fragment = new BoardAllParticipantsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setContacts(contacts);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ButterKnife.bind(this, view);

        adapter = new BoardParticipantsListAdapter(getActivity(), mContacts);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void refreshContacts (List<Contact> mContacts) {
        adapter.setData(mContacts);
        adapter.notifyDataSetChanged();
    }

    public void setContext (Context context) { this.context = context; }

    public void setContacts(List<Contact> mContacts) {
        this.mContacts = mContacts;
    }

}
