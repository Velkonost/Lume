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

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.BoardParticipantsListAdapter;
import ru.velkonost.lume.descriptions.Contact;

public class BoardAllParticipantsFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_board_all_participants;

    private List<Contact> mContacts;
    private BoardParticipantsListAdapter adapter;
    protected View view;
    protected Context context;

    public static ContactsFragment getInstance(Context context, List<Contact> contacts) {
        Bundle args = new Bundle();
        ContactsFragment fragment = new ContactsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setContacts(contacts);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        FastScrollRecyclerView recyclerView = (FastScrollRecyclerView)
                view.findViewById(R.id.recyclerViewAllParticipants);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new BoardParticipantsListAdapter(getActivity(), mContacts);
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
