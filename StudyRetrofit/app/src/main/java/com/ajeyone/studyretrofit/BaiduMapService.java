package com.ajeyone.studyretrofit;

import com.ajeyone.studyretrofit.data.BaiduPOISearchResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BaiduMapService {
    @GET("search")
    Call<BaiduPOISearchResponse> searchPOI_Call(@Query("query") String query,
                                                @Query("location") String location,
                                                @Query("radius") int radius,
                                                @Query("output") String output,
                                                @Query("ak") String ak);

    @GET("search")
    Observable<BaiduPOISearchResponse> searchPOI_Observable(@Query("query") String query,
                                                            @Query("location") String location,
                                                            @Query("radius") int radius,
                                                            @Query("output") String output,
                                                            @Query("ak") String ak);
}
