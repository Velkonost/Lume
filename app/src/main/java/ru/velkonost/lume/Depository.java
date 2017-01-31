package ru.velkonost.lume;

import java.util.List;

import ru.velkonost.lume.descriptions.BoardColumn;

public class Depository {

    private static List<BoardColumn> mBoardColumns;
    private static String boardId;
    private static boolean refreshPopup;

    public static List<BoardColumn> getBoardColumns() {
        return mBoardColumns;
    }

    public static void setBoardColumns(List<BoardColumn> boardColumns) {
        mBoardColumns = boardColumns;
    }

    public static String getBoardId() {
        return boardId;
    }

    public static void setBoardId(String boardId) {
        Depository.boardId = boardId;
    }

    public static boolean isRefreshPopup() {
        return refreshPopup;
    }

    public static void setRefreshPopup(boolean refreshPopup) {
        Depository.refreshPopup = refreshPopup;
    }
}
