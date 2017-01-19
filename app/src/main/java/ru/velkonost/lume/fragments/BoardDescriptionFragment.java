package ru.velkonost.lume.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.BOARD_DESCRIPTION;
import static ru.velkonost.lume.Managers.PhoneDataStorage.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorage.loadText;

public class BoardDescriptionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_board_description, container, false);

        ((TextView) view.findViewById(R.id.boardDescription))
                .setText(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                ? Html.fromHtml(loadText(getActivity(), BOARD_DESCRIPTION),
                                Html.FROM_HTML_MODE_LEGACY)
                                : Html.fromHtml(loadText(getActivity(), BOARD_DESCRIPTION))
                );

        deleteText(getActivity(), BOARD_DESCRIPTION);

        return view;
    }

}
