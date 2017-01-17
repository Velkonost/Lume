package ru.velkonost.lume.descriptions;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {

    private int id;
    private int amountParticipants;
    private String name;
    private String description;

    private boolean isBelong;

    private int[] userIds;

    public Card(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Card(int id, int amountParticipants, String name) {
        this.id = id;
        this.amountParticipants = amountParticipants;
        this.name = name;
    }

    public Card(int id, int amountParticipants, String name, boolean isBelong) {
        this.id = id;
        this.amountParticipants = amountParticipants;
        this.name = name;
        this.isBelong = isBelong;
    }

    public Card(int id, String name, int[] userIds) {
        this.id = id;
        this.name = name;
        this.userIds = userIds;
    }

    public Card(int id, String name, String description, int[] userIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userIds = userIds;
    }

    private Card(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();

    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
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
        parcel.writeString(description);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int[] getUserIds() {
        return userIds;
    }

    public int getAmountParticipants() {
        return amountParticipants;
    }

    public boolean isBelong() {
        return isBelong;
    }
}


