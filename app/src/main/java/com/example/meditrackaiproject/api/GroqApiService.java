package com.example.meditrackaiproject.api;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GroqApiService {
    @POST("chat/completions")
    Call<JsonObject> getChatCompletion(
        @Header("Authorization") String apiKey,
        @Body JsonObject body
    );
}