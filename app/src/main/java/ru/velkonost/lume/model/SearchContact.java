package ru.velkonost.lume.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Velkonost
 *
 * Модель контакта поиска
 */
public class SearchContact implements Parcelable {

    /**
     * Свойство - идентификатор
     */
    private String id;

    /**
     * Свойство - имя
     */
    private String name;

    /**
     * Свойство - фамилия
     */
    private String surname;

    /**
     * Свойство - логин
     */
    private String login;

    /**
     * Свойство - город проживания
     */
    private String city;

    /**
     * Свойство - страна проживания
     */
    private String country;

    /**
     * Свойство - место учебы
     */
    private String study;

    /**
     * Свойство - место работы
     */
    private String work;

    /**
     * Свойство - номер папки, в которой располагается аватар
     */
    private int avatar;

    public SearchContact() {}

    public SearchContact(String id, String name, String surname, String login,
                         String city, String country, String study, String work, int avatar) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.city = city;
        this.country = country;
        this.study = study;
        this.work = work;
        this.avatar = avatar;
    }

    private SearchContact(Parcel in) {
        id = in.readString();
        name = in.readString();
        surname = in.readString();
        login = in.readString();
        avatar = in.readInt();
    }


    public static final Creator<SearchContact> CREATOR = new Creator<SearchContact>() {
        @Override
        public SearchContact createFromParcel(Parcel in) {
            return new SearchContact(in);
        }

        @Override
        public SearchContact[] newArray(int size) {
            return new SearchContact[size];
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }
}
