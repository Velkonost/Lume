package ru.velkonost.lume.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;

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

        tabs.put(0, ColumnFragment.getInstance(context, 23));
        tabs.put(1, ColumnFragment.getInstance(context, 31));
        tabs.put(2, ColumnFragment.getInstance(context, 32));
        tabs.put(3, ColumnFragment.getInstance(context, 33));
    }
}
