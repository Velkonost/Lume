package ru.velkonost.lume;

import java.util.ArrayList;
import java.util.List;

import ru.velkonost.lume.descriptions.BoardColumn;

public class Depository {

    private static List<BoardColumn> mBoardColumns;

    public static List<BoardColumn> getBoardColumns() {
        return mBoardColumns;
    }

    public static void setBoardColumns(List<BoardColumn> boardColumns) {
        mBoardColumns = boardColumns;
    }

    public static void resetBoardColumns() {
        mBoardColumns = new ArrayList<>();
    }
}
