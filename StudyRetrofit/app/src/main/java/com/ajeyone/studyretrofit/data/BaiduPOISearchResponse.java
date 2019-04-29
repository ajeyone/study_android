package com.ajeyone.studyretrofit.data;

import java.util.List;

public class BaiduPOISearchResponse {
    public int status;
    public String message;
    public List<BaiduPOIBean> results;

    @Override
    public String toString() {
        return "BaiduPOISearchResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", results.count=" + (results != null ? results.size() : 0) +
                '}';
    }
}
