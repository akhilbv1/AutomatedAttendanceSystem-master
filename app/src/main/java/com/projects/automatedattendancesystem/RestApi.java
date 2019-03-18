package com.projects.automatedattendancesystem;

import com.projects.automatedattendancesystem.Pojo.MainList;
import com.projects.automatedattendancesystem.Pojo.MessageBody;
import com.projects.automatedattendancesystem.Pojo.MultipleSmsPojo;
import com.projects.automatedattendancesystem.Pojo.SinglePojo;

import java.util.List;

import io.reactivex.Single;
import okhttp3.RequestBody;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {

    @GET("sendsms.aspx")
    Single<Response<Void>> postMessage(@Query("userid") String userId,@Query("password") String password,@Query("sender") String sender,@Query("mobileno") String mobileNumber,@Query("msg") String message);

    @FormUrlEncoded
    @POST("bulk_json")
    Single<Error> postMessage(@Field("apikey") String apikey, @Field("data") String data);

    @FormUrlEncoded
    @POST("send/")
    Single<Error> sendMessage(@Field("apikey") String apikey, @Field("message") String message, @Field("sender") String  sender, @Field("numbers") String numbers);

    @POST("send/")
    Single<Void> sendMessage(@Body List<SinglePojo> singlePojos);
}
