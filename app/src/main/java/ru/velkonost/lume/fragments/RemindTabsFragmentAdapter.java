package ru.velkonost.lume.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;

import static ru.velkonost.lume.Constants.REMIND_DONE_INDEX;
import static ru.velkonost.lume.Constants.REMIND_TODO_INDEX;

public class RemindTabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public RemindTabsFragmentAdapter(Context context, FragmentManager fm) {
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

        tabs.put(0, RemindFragment.getInstance(context, REMIND_TODO_INDEX));
        tabs.put(1, RemindFragment.getInstance(context, REMIND_DONE_INDEX));
    }

}
