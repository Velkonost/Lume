package ru.velkonost.lume.descriptions;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Velkonost
 *
 * Модель комментария карточки
 */
public class CardComment implements Parcelable {

    /**
     * Свойство - идентификатор
     */
    private int id;

    /**
     * Свойство - идентификатор пользователя, создавшего комментарий
     */
    private int userId;

    /**
     * Свойство - идентификатор карточки, к которой принадлежит комментарий
     */
    private int cardId;

    /**
     * Свойство - содержание
     */
    private String mText;

    /**
     * Свойство - дата создания
     */
    private String mDate;

    /**
     * Свойство - имя (или логин) пользователя, создавшего комментарий
     */
    private String userName;

    public CardComment(int id, String userName, int cardId, String text, String date) {
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
