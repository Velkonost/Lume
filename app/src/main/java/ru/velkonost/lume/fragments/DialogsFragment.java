package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.gridDialogs)
    GridView rv;

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
        ButterKnife.bind(this, view);

        adapter = new DialogListAdapter(context, mContacts);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 3);
//        rv.setLayoutManager(glm);

        adjustGridView(rv);
        rv.setAdapter(adapter);

        return view;
    }

    private void adjustGridView(GridView gridView) {

        gridView.setColumnWidth(dp2px(100));
        gridView.setNumColumns(3);
        gridView.setStretchMode(GridView.STRETCH_SPACING);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public void refreshContacts (List<DialogContact> mContacts) {
        adapter.setData(mContacts);
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
