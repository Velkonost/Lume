package ru.velkonost.lume.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.BOARD_DESCRIPTION;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;

public class BoardDescriptionFragment extends Fragment {

    protected TextView boardDescriprion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_board_description, container, false);

        ((TextView) view.findViewById(R.id.boardDescription))
                .setText(loadText(getActivity(), BOARD_DESCRIPTION));

        return view;
    }

}
