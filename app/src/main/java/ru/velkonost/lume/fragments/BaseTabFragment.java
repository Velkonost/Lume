package ru.velkonost.lume.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * @author Velkonost
 *
 * Собраны общие признаки всех tab-фрагментов
 */
public class BaseTabFragment extends Fragment {

    /**
     * Свойство - заголовок таба
     */
    private String title;

    protected Context context;

    protected View view;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
