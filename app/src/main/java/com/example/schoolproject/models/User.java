package com.example.schoolproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable, Comparable{

    private String username, password, email, uid, pfpLink;

    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        email = in.readString();
        uid = in.readString();
        pfpLink = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPfpLink() {
        return pfpLink;
    }

    public void setPfpLink(String pfpLink) {
        this.pfpLink = pfpLink;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User(String username, String password, String email, String uid) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.uid = uid;
    }

    public User() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(uid);
        dest.writeString(pfpLink);
    }

    @Override
    public int compareTo(Object o) {
        User user = (User) o;
        return 0;
    }
}
