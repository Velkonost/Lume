package ru.velkonost.lume.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.velkonost.lume.Depository;
import ru.velkonost.lume.descriptions.BoardColumn;

public class BoardColumnsTabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public BoardColumnsTabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();

        List<BoardColumn> boardColumns = Depository.getBoardColumns();

        for (int i = 0; i < boardColumns.size(); i++) {
            tabs.put(i, ColumnFragment.getInstance(context, boardColumns.get(i).getId(),
                    boardColumns.get(i).getName()));
        }
    }
}
