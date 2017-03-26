package ru.velkonost.lume.descriptions;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Velkonost
 *
 * Модель участника доски
 */
public class BoardParticipant implements Parcelable {

    /**
     * Свойство - идентификатор
     */
    private int id;

    /**
     * Свойство - номер папки, в которой располагается аватар
     */
    private int avatar;

    /**
     * Свойство - логин
     */
    private String login;

    /**
     * Свойство - проверка, является ли контакт последним в списке
     */
    private boolean last;

    /**
     * Свойство - количество, сколько участников осталось после контакта
     */
    private int reminded;

    /**
     * Свойство - идентификатор доски
     */
    private int boardId;

    public BoardParticipant(int id, int avatar, String login, boolean last, int reminded, int boardId) {
        this.id = id;
        this.avatar = avatar;
        this.login = login;
        this.last = last;
        this.reminded = reminded;
        this.boardId = boardId;
    }

    private BoardParticipant(Parcel in) {
        id = in.readInt();
        avatar = in.readInt();
        login = in.readString();
        reminded = in.readInt();
        boardId = in.readInt();
    }

    public static final Creator<BoardParticipant> CREATOR = new Creator<BoardParticipant>() {
        @Override
        public BoardParticipant createFromParcel(Parcel in) {
            return new BoardParticipant(in);
        }

        @Override
        public BoardParticipant[] newArray(int size) {
            return new BoardParticipant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(avatar);
        parcel.writeString(login);
        parcel.writeInt(reminded);
        parcel.writeInt(boardId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getReminded() {
        return reminded;
    }

    public void setReminded(int reminded) {
        this.reminded = reminded;
    }

    public int getBoardId() {
        return boardId;
    }

}
