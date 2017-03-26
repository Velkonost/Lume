package ru.velkonost.lume.descriptions;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Velkonost
 *
 * Модель карточки
 */
public class Card implements Parcelable {

    /**
     * Свойство - идентификатор
     */
    private int id;

    /**
     * Свойство - количество участников
     */
    private int amountParticipants;

    /**
     * Свойство - название
     */
    private String name;

    /**
     * Свойство - описание
     */
    private String description;

    /**
     * Свойство - цвет фона
     */
    private int color;

    /**
     * Свойство - положение в колонке
     */
    private int columnOrder;

    /**
     * Свойство - проверка, состоит ли авторизованный пользователь в карточке
     */
    private boolean isBelong;

    /**
     * Свойство - список идентификаторов участников
     */
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

    public Card(int id, int amountParticipants, String name, boolean isBelong, int color, int columnOrder) {
        this.id = id;
        this.amountParticipants = amountParticipants;
        this.name = name;
        this.isBelong = isBelong;
        this.color = color;
        this.columnOrder = columnOrder;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColumnOrder() {
        return columnOrder;
    }

    public void setColumnOrder(int columnOrder) {
        this.columnOrder = columnOrder;
    }
}


