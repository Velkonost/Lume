package ru.velkonost.lume.descriptions;

import android.os.Parcel;
import android.os.Parcelable;

public class BoardColumn implements Parcelable {

    private int id;
    private String name;

    public BoardColumn(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private BoardColumn(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<BoardColumn> CREATOR = new Creator<BoardColumn>() {
        @Override
        public BoardColumn createFromParcel(Parcel in) {
            return new BoardColumn(in);
        }

        @Override
        public BoardColumn[] newArray(int size) {
            return new BoardColumn[size];
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

    public String getName() {
        return name;
    }
}
