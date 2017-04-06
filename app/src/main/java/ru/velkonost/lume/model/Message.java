package ru.velkonost.lume.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Velkonost
 *
 * Модель сообщения
 */
public class Message implements Parcelable {

    /**
     * Свойство - проверка, от кого отправлено сообщение
     */
    private boolean fromMe;

    /**
     * Свойство - идентификатор сообщения
     */
    private int id;

    /**
     * Свойство - идентификатор пользователя, создавшего сообщение
     */
    private int userId;

    /**
     * Свойство - идентификатор диалога, к которому принадлежит сообщение
     */
    private int dialogId;

    /**
     * Свойство - статут прочтения
     */
    private int mStatus;

    /**
     * Свойство - содержимое
     */
    private String mText;

    /**
     * Свойство - дата создания
     */
    private String mDate;

    /**
     * Свойство - проверка на наличие в списке.
     * Параметр необходим для обновление состояния списка сообщений
     */
    private boolean isExist;

    public Message(boolean fromMe, int id, int userId, int dialogId,
                   int status, String text, String date) {
        this.fromMe = fromMe;
        this.id = id;
        this.userId = userId;
        this.dialogId = dialogId;
        mStatus = status;
        mText = text;
        mDate = date;

        this.isExist = false;
    }

    private Message(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        dialogId = in.readInt();
        mStatus = in.readInt();
        mText = in.readString();
        mDate = in.readString();

    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
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
        parcel.writeInt(dialogId);
        parcel.writeInt(mStatus);
        parcel.writeString(mText);
        parcel.writeString(mDate);
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getDialogId() {
        return dialogId;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getText() {
        return mText;
    }

    public String getDate() {
        return mDate;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }
}
