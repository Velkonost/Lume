package ru.velkonost.lume.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.BOARD_DESCRIPTION;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.deleteText;
import static ru.velkonost.lume.Managers.PhoneDataStorageManager.loadText;

/**
 * @author Velkonost
 *
 * Описание доски
 */
public class BoardDescriptionFragment extends Fragment {

    @BindView(R.id.switcherBoardDescription) ViewSwitcher switcher;

    /**
     * Свойство - описание доски
     */
    private String text;

    @BindView(R.id.boardDescription) TextView mTextView;
    @BindView(R.id.editBoardDescription) EditText mEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_board_description, container, false);
        ButterKnife.bind(this, view);

        text = loadText(getActivity(), BOARD_DESCRIPTION);

        mTextView.setText(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                ? Html.fromHtml(text,
                                Html.FROM_HTML_MODE_LEGACY)
                                : Html.fromHtml(text)
                );

        mEditText.setText(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                ? Html.fromHtml(text,
                                Html.FROM_HTML_MODE_LEGACY)
                                : Html.fromHtml(text)
                );

        deleteText(getActivity(), BOARD_DESCRIPTION);

        return view;
    }

    /**
     * Переключение с режима редактирования на режим чтения, и наоборот
     */
    public void showNext(){ switcher.showNext(); }

    public void changeText() { mTextView.setText(mEditText.getText().toString()); }

    /**
     * Сохранение изменений
     */
    public void setText(String text) { this.text = text; }

    public String getText() { return mEditText.getText().toString(); }


}
