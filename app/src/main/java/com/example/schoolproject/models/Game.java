package com.example.schoolproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import kotlin.jvm.internal.BooleanSpreadBuilder;

public class Game implements Parcelable {
    private String pin;
    private String title;
    //private ArrayList<User> players;
    private HashMap<String, Question> questions;
    private Boolean isStarted;
    private String author;

    protected Game(Parcel in) {
        pin = in.readString();
        title = in.readString();
        byte tmpIsStarted = in.readByte();
        isStarted = tmpIsStarted == 0 ? null : tmpIsStarted == 1;
        author = in.readString();
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public HashMap<String, Question> getQuestions() {
        return questions;
    }

    public void setQuestions(HashMap<String, Question> questions) {
        this.questions = questions;
    }

    public Game(String pin, String title, HashMap<String, Question> questions, Boolean isStarted, String author) {
        this.pin = pin;
        this.title = title;
        this.questions = questions;
        this.isStarted = isStarted;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public Game() {
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getStarted() {
        return isStarted;
    }

    public void setStarted(Boolean started) {
        isStarted = started;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(pin);
        dest.writeString(title);
        dest.writeByte((byte) (isStarted == null ? 0 : isStarted ? 1 : 2));
        dest.writeString(author);
    }
}
