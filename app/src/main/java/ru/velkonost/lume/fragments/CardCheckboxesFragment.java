package ru.velkonost.lume.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.adapter.CardCheckboxListAdapter;
import ru.velkonost.lume.model.Checkbox;

/**
 * @author Velkonost
 *
 * Список флажков задач карточки
 */

public class CardCheckboxesFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_card_checkbox;

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<Checkbox> mCardCheckboxes;

    private CardCheckboxListAdapter adapter;

    protected View view;

    protected Context context;

    @BindView(R.id.recyclerViewCheckboxes)
    RecyclerView recyclerView;

    public static CardCheckboxesFragment getInstance(Context context, List<Checkbox> cardCheckboxes) {
        Bundle args = new Bundle();
        CardCheckboxesFragment fragment = new CardCheckboxesFragment();

        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setCardCheckboxes(cardCheckboxes);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new CardCheckboxListAdapter(getActivity(), mCardCheckboxes);
        recyclerView.setAdapter(adapter);


        return view;
    }

    public void setCardCheckboxes(List<Checkbox> cardCheckboxes) {
        mCardCheckboxes = cardCheckboxes;
    }

    /**
     * Обновление состояния списка комментариев карточки
     */
    public void refreshCheckboxes(List<Checkbox> mCardCheckboxes) {

        adapter.setData(mCardCheckboxes);
        adapter.notifyDataSetChanged();

    }

    public void setContext(Context context) {
        this.context = context;
    }
}
