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

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.ContactListAdapter;
import ru.velkonost.lume.descriptions.Contact;

public class ContactsFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_contact;

    private List<Contact> mContacts;
    private List<Contact> mContactsCopy;
    private ContactListAdapter adapter;
    protected View view;
    protected Context context;

    private FloatingActionButton fabGoSearch;

    public static ContactsFragment getInstance(Context context, List<Contact> contacts) {
        Bundle args = new Bundle();
        ContactsFragment fragment = new ContactsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setContacts(contacts);
        fragment.setContactsCopy();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        FastScrollRecyclerView recyclerView = (FastScrollRecyclerView)
                view.findViewById(R.id.recycleViewContact);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new ContactListAdapter(getActivity(), mContacts);
        recyclerView.setAdapter(adapter);

        fabGoSearch = (FloatingActionButton) view.findViewById(R.id.btnGoSearch);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fabGoSearch.hide();
                else if (dy < 0)
                    fabGoSearch.show();
            }
        });

        return view;
    }

    public void refreshContacts (List<Contact> mContacts) {
        adapter.setData(mContacts);
        adapter.notifyDataSetChanged();
    }

    public void removeItem (List<Contact> mContacts, int position) {
        adapter.setData(mContacts);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, mContacts.size());
    }

    public void search(String text, boolean empty, boolean let) {
        if (let) {
            if (!text.isEmpty()) {

                mContacts.clear();
                text = text.toLowerCase();

                for (Contact item : mContactsCopy)
                    if (item.getName().toLowerCase().contains(text)
                            || item.getSurname().toLowerCase().contains(text)
                            || item.getLogin().toLowerCase().contains(text))
                        mContacts.add(item);

                adapter.setData(mContacts);
            } else if (empty) {
                mContacts.clear();
                mContacts.addAll(mContactsCopy);
                adapter.setData(mContacts);
            }
        }

//        adapter.notifyDataSetChanged();
    }

    public void setContext (Context context) {this.context = context;}

    public void setContacts(List<Contact> mContacts) {
        this.mContacts = mContacts;
    }
    public void setContactsCopy() {
        mContactsCopy = new ArrayList<>();
        mContactsCopy.addAll(mContacts);
    }
}
