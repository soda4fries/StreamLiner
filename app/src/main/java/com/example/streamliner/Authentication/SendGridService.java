package com.example.streamliner.Authentication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SendGridService {
    @Headers({
            "Authorization: Bearer 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",
            "Content-Type: application/json"
    })
    @POST("v3/mail/send")
    Call<Void> sendEmail(@Body EmailRequest emailRequest);


}
