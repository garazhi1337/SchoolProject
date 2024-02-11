package com.example.schoolproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class Question implements Parcelable {
    private Integer seconds;
    private HashMap<String, Boolean> answerOne;
    private HashMap<String, Boolean> answerTwo;
    private HashMap<String, Boolean> answerThree;
    private HashMap<String, Boolean> answerFour;
    private String questionText;
    private String photoUrl;
    private String id;

    protected Question(Parcel in) {
        if (in.readByte() == 0) {
            seconds = null;
        } else {
            seconds = in.readInt();
        }
        questionText = in.readString();
        photoUrl = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Question() {
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public HashMap<String, Boolean> getAnswerOne() {
        return answerOne;
    }

    public void setAnswerOne(HashMap<String, Boolean> answerOne) {
        this.answerOne = answerOne;
    }

    public HashMap<String, Boolean> getAnswerTwo() {
        return answerTwo;
    }

    public void setAnswerTwo(HashMap<String, Boolean> answerTwo) {
        this.answerTwo = answerTwo;
    }

    public HashMap<String, Boolean> getAnswerThree() {
        return answerThree;
    }

    public void setAnswerThree(HashMap<String, Boolean> answerThree) {
        this.answerThree = answerThree;
    }

    public HashMap<String, Boolean> getAnswerFour() {
        return answerFour;
    }

    public void setAnswerFour(HashMap<String, Boolean> answerFour) {
        this.answerFour = answerFour;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (seconds == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(seconds);
        }
        parcel.writeString(questionText);
        parcel.writeString(photoUrl);
    }
}
