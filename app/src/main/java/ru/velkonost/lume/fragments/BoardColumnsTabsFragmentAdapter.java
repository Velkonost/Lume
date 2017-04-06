package ru.velkonost.lume.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.velkonost.lume.Depository;
import ru.velkonost.lume.model.BoardColumn;

/**
 * @author Velkonost
 *
 * Список колонок в виде табов, получение их содержимого
 */
public class BoardColumnsTabsFragmentAdapter extends FragmentPagerAdapter {

    /**
     * Свойство - список табов-колонок
     */
    private Map<Integer, BaseTabFragment> tabs;

    /**
     * Свойство - порядок колонок
     *
     * Идентификатор - позиция
     */
    static Map<Integer, Integer> tabsColumnOrder;

    /**
     * Свойство - позиция последней колонки
     */
    public static int last = 0;

    /**
     * Свойство - идентификатор доски
     */
    private String boardId;

    public BoardColumnsTabsFragmentAdapter(Context context, FragmentManager fm, String boardId) {
        super(fm);

        this.boardId = boardId;

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

    /**
     * Составление табов
     */
    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        tabsColumnOrder = new HashMap<>();

        List<BoardColumn> boardColumns = Depository.getBoardColumns();
        last = 0;

        for (int i = 0; i < boardColumns.size(); i++) {
            tabs.put(i, ColumnFragment.getInstance(context, boardColumns.get(i).getId(),
                    boardColumns.get(i).getName()));

            tabsColumnOrder.put(i, boardColumns.get(i).getId());

            last = i;
        }
        tabs.put(last + 1, AddColumnFragment.getInstance(context, boardId));
    }


}
