package com.menuexpress.equipo6.menuexpress.Remote;

import com.menuexpress.equipo6.menuexpress.Model.MyResponse;
import com.menuexpress.equipo6.menuexpress.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAKGy70bQ:APA91bFQZ0NL3f3PlKFBESWxzpgcqm34MgYpec76O2_S3hTnWdNx2K9G6OZFTWIu0gsrVLHTrGXVDK5xiJg0H65PdwIYY7sLXWmskTWcv2z-pn02nufI54SfzOVEs6d83F9XWdXnaIpn"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
