package ru.velkonost.lume.descriptions;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Velkonost
 *
 * Модель доски
 */
public class Board implements Parcelable {

    /**
     * Свойство - идентификатор
     */
    private int id;

    /**
     * Свойство - название
     */
    private String name;

    // in the future, notification for user
//    private int status;


    public Board(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private Board(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
