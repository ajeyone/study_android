package com.ajeyone.studyretrofit.data;

import androidx.annotation.NonNull;

public class BaiduLocation {
    public double lat;
    public double lng;

    @NonNull
    @Override
    public String toString() {
        return "BaiduLocation{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
