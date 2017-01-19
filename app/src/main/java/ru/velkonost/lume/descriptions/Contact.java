package ru.velkonost.lume.descriptions;

import android.os.Parcel;
import android.os.Parcelable;


public class Contact implements Parcelable {

    private String id;
    private String name;
    private String surname;
    private String login;
    private int avatar;

    public Contact(String id, String name, String surname, String login, int avatar) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.avatar = avatar;
    }

    private Contact(Parcel in) {
        id = in.readString();
        name = in.readString();
        surname = in.readString();
        login = in.readString();
        avatar = in.readInt();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }
}
