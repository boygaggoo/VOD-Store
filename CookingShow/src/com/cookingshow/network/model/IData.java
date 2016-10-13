package com.cookingshow.network.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class IData implements Parcelable {
    
    public ContentValues toContentValues() {
        return null;
    }

    public IData parseJsonObject(String jsonStr) {
        return null;
    }

    public List<? extends IData> parseJsonArray(String jsonArray) {
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    
    public static final Creator<IData> CREATOR = new Creator<IData>() {

        @Override
        public IData createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public IData[] newArray(int size) {
            return null;
        }
    };
}
