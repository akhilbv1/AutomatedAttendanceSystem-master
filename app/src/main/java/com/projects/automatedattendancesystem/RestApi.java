package com.projects.automatedattendancesystem;


import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApi {

    @GET("sendsms.aspx")
    Single<Response<Void>> postMessage(@Query("userid") String userId,@Query("password") String password,@Query("sender") String sender,@Query("mobileno") String mobileNumber,@Query("msg") String message);

    @FormUrlEncoded
    @POST("send/")
    Single<Error> sendMessage(@Field("apikey") String apikey, @Field("message") String message, @Field("sender") String  sender, @Field("numbers") String numbers);

}
