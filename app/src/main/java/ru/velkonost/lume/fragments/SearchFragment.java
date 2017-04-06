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
import ru.velkonost.lume.adapter.SearchListAdapter;
import ru.velkonost.lume.model.SearchContact;

/**
 * @author Velkonost
 *
 * Поиск по строке
 */
public class SearchFragment extends Fragment{

    private static final int LAYOUT = R.layout.fragment_search;

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<SearchContact> mContacts;

    private SearchListAdapter adapter;

    protected View view;

    protected Context context;

    @BindView(R.id.recycleViewSearch)
    FastScrollRecyclerView recyclerView;

    public static SearchFragment getInstance(Context context, List<SearchContact> contacts) {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();

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

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SearchListAdapter(getActivity(), mContacts);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void refreshContacts (List<SearchContact> mContacts) {
        adapter.setData(mContacts);
        adapter.notifyDataSetChanged();
    }

    public void setContext (Context context) {this.context = context;}

    public void setContacts(List<SearchContact> mContacts) {
        this.mContacts = mContacts;
    }
}
