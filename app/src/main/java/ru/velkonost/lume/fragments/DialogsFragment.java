package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.DialogListAdapter;
import ru.velkonost.lume.descriptions.DialogContact;

public class DialogsFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_dialogs;

    private List<DialogContact> mContacts;
    private DialogListAdapter adapter;
    protected View view;
    protected Context context;

    public static DialogsFragment getInstance(Context context, List<DialogContact> contacts) {
        Bundle args = new Bundle();
        DialogsFragment fragment = new DialogsFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setContacts(contacts);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        GridView gridView = (GridView) view.findViewById(R.id.gridDialogs);


//        gridView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new DialogListAdapter(getActivity(), mContacts);
        gridView.setAdapter((ListAdapter) adapter);
        return view;
    }

    public void refreshContacts (List<DialogContact> mContacts) {
//        adapter.setData(mContacts);
        adapter.notifyDataSetChanged();
    }

    public void setContext (Context context) {this.context = context;}

    public void setContacts(List<DialogContact> mContacts) {
        this.mContacts = mContacts;
    }
}
