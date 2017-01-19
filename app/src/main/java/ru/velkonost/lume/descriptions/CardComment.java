package ru.velkonost.lume.descriptions;

import android.os.Parcel;
import android.os.Parcelable;

public class CardComment implements Parcelable {

    private int id;
    private int userId;
    private int cardId;
    private String mText;
    private String mDate;
    private String userName;

    public CardComment(int id, int userId, int cardId, String text, String date, String userName) {
        this.id = id;
        this.userId = userId;
        this.cardId = cardId;
        this.userName = userName;
        mText = text;
        mDate = date;

    }

    private CardComment(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        mText = in.readString();
        mDate = in.readString();

    }

    public static final Creator<CardComment> CREATOR = new Creator<CardComment>() {
        @Override
        public CardComment createFromParcel(Parcel in) {
            return new CardComment(in);
        }

        @Override
        public CardComment[] newArray(int size) {
            return new CardComment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userId);
        parcel.writeString(mText);
        parcel.writeString(mDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getUserName() {
        return userName;
    }
}
