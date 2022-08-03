package com.example.kosmanajemen;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiEndPoint {
    @FormUrlEncoded
    @POST("api/tubes/insertone")
    Call<ModelKos> insertOne(
        @Field("nama") String nama,
        @Field("harga") int harga,
        @Field("alamat") String alamat,
        @Field("geotagging") String geotagging
    );

    @FormUrlEncoded
    @POST("api/tubes/updateone")
    Call<ModelKos> updateOne(
            @Field("id") int id,
            @Field("nama") String nama,
            @Field("harga") int harga,
            @Field("alamat") String alamat,
            @Field("geotagging") String geotagging
    );

    @POST("api/tubes/readall")
    Call<ModelKos> readAll();

    @FormUrlEncoded
    @POST("api/tubes/deleteone")
    Call<ModelKos> deleteOne(@Field("id") int id);
}
