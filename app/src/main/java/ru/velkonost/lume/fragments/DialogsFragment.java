package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.DialogListAdapter;
import ru.velkonost.lume.descriptions.DialogContact;

public class DialogsFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_dialogs;

    private List<DialogContact> mContacts;
    private List<DialogContact> mContactsCopy;
    private DialogListAdapter adapter;
    protected View view;
    protected Context context;

    public static DialogsFragment getInstance(Context context, List<DialogContact> contacts) {
        Bundle args = new Bundle();
        DialogsFragment fragment = new DialogsFragment();

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

        GridView gridView = (GridView) view.findViewById(R.id.gridDialogs);

        adapter = new DialogListAdapter(getActivity(), mContacts);
        gridView.setAdapter(adapter);
        adjustGridView(gridView);
        return view;
    }

    private void adjustGridView(GridView gridView) {

        gridView.setColumnWidth(200);
        gridView.setHorizontalSpacing(5);
        gridView.setNumColumns(GridView.AUTO_FIT);

        gridView.setStretchMode(GridView.STRETCH_SPACING);
    }

    public void refreshContacts (List<DialogContact> mContacts) {
        adapter.setData(mContacts);
        adapter.notifyDataSetChanged();
    }

    public void search(String text, boolean empty, boolean let) {
        if (let) {
            if (!text.isEmpty()) {

                mContacts.clear();
                text = text.toLowerCase();

                for (DialogContact item : mContactsCopy)
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
    }


    public void setContext (Context context) {this.context = context;}

    public void setContacts(List<DialogContact> mContacts) {
        this.mContacts = mContacts;
    }

    public void setContactsCopy() {
        mContactsCopy = new ArrayList<>();
        mContactsCopy.addAll(mContacts);
    }
}
