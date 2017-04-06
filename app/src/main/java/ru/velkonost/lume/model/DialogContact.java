package ru.velkonost.lume.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Velkonost
 *
 * Модель диалога
 */
public class DialogContact implements Parcelable {

    /**
     * Свойство - идентификатор пользователя
     */
    private String id;

    /**
     * Свойство - идентификатор диалога
     */
    private String dialogId;

    /**
     * Свойство - имя адресата
     */
    private String name;

    /**
     * Свойство - фамилия адресата
     */
    private String surname;

    /**
     * Свойство - логин адресата
     */
    private String login;

    /**
     * Свойство - количество непрочитанных авторизованным пользователем сообщений
     */
    private int unreadMessages;

    /**
     * Свойство - номер папки, в которой располагается аватар
     */
    private int avatar;

    /**
     * Свойство - проверка на наличие аватара
     */
    private boolean isAvatar;

    /**
     * Свойство - проверка на наличие в диалоге сообщений
     */
    private boolean isEmpty;

    public DialogContact(String id, String dialogId, String name, String surname, String login,
                         int unreadMessages, int avatar, boolean isEmpty) {
        this.id = id;
        this.dialogId = dialogId;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.unreadMessages = unreadMessages;
        this.avatar = avatar;
        this.isEmpty = isEmpty;

        this.isAvatar = false;
    }

    private DialogContact(Parcel in) {
        id = in.readString();
        dialogId = in.readString();
        name = in.readString();
        surname = in.readString();
        login = in.readString();
        unreadMessages = in.readInt();
        avatar = in.readInt();
    }

    public static final Creator<DialogContact> CREATOR = new Creator<DialogContact>() {
        @Override
        public DialogContact createFromParcel(Parcel in) {
            return new DialogContact(in);
        }

        @Override
        public DialogContact[] newArray(int size) {
            return new DialogContact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(dialogId);
        parcel.writeString(name);
        parcel.writeString(surname);
        parcel.writeString(login);
        parcel.writeInt(unreadMessages);
        parcel.writeInt(avatar);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getLogin() {
        return login;
    }

    public int getAvatar() {
        return avatar;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setIsAvatar(boolean isAvatar) {
        this.isAvatar = isAvatar;
    }

    public boolean isAvatar() {
        return isAvatar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getDialogId() {
        return dialogId;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}

