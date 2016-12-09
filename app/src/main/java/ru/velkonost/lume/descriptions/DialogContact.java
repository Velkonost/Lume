package ru.velkonost.lume.descriptions;

import android.os.Parcel;
import android.os.Parcelable;

public class DialogContact implements Parcelable {

    private String id;
    private String name;
    private String surname;
    private String login;
    private int avatar;

    public DialogContact(String id, String name, String surname, String login, int avatar) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.avatar = avatar;
    }

    private DialogContact(Parcel in) {
        id = in.readString();
        name = in.readString();
        surname = in.readString();
        login = in.readString();
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
        parcel.writeString(name);
        parcel.writeString(surname);
        parcel.writeString(login);
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
}
