package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.descriptions.Card;

public class ColumnFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.fragment_column;

    public static ColumnFragment getInstance(Context context) {
        Bundle args = new Bundle();
        ColumnFragment fragment = new ColumnFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.menu_item_boards));

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerViewColumn);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(new RemindListAdapter(createMockRemindListData()));

        return view;
    }

    private List<Card> createMockRemindListData() {
        List<Card> data = new ArrayList<>();
//        data.add(new RemindDTO("Item 1", "test1"));
//        data.add(new RemindDTO("Item 2", "tsest1"));
//        data.add(new RemindDTO("Item 3", "3"));
//        data.add(new RemindDTO("Item 4", "23"));
//        data.add(new RemindDTO("Item 5", "123"));
//        data.add(new RemindDTO("Item 6", "123"));

        return data;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
