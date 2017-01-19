package ru.velkonost.lume;

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

}
