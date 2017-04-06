package ru.velkonost.lume.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Velkonost
 */

public class Checkbox implements Parcelable {

    private int id;
    private int cardId;
    private boolean done;
    private String title;


    public Checkbox(int id, int cardId, boolean done, String title) {
        this.id = id;
        this.cardId = cardId;
        this.done = done;
        this.title = title;
    }

    protected Checkbox(Parcel in) {
        id = in.readInt();
        cardId = in.readInt();
        done = in.readByte() != 0;
        title = in.readString();
    }

    public static final Creator<Checkbox> CREATOR = new Creator<Checkbox>() {
        @Override
        public Checkbox createFromParcel(Parcel in) {
            return new Checkbox(in);
        }

        @Override
        public Checkbox[] newArray(int size) {
            return new Checkbox[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(cardId);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeString(title);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
