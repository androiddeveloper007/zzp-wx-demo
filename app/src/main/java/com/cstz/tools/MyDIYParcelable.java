package com.cstz.tools;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZP on 2016/10/14.
 */

public class MyDIYParcelable implements Parcelable{

    public ArrayList<List<Map<String, Object>>> bundlelist = new ArrayList<List<Map<String, Object>>>();

    public MyDIYParcelable() {

    }

    private MyDIYParcelable(Parcel in) {
        bundlelist  = in.readArrayList(ArrayList.class.getClassLoader());
    }

    public static final Creator<MyDIYParcelable> CREATOR = new Creator<MyDIYParcelable>() {
        @Override
        public MyDIYParcelable createFromParcel(Parcel in) {
            return new MyDIYParcelable(in);
        }

        @Override
        public MyDIYParcelable[] newArray(int size) {
            return new MyDIYParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(bundlelist);
    }
}
