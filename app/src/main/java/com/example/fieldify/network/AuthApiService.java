package com.example.fieldify.network;

import com.example.fieldify.network.model.AuthResponse;
import com.example.fieldify.network.model.LoginRequest;
import com.example.fieldify.network.model.RegisterRequest;
import com.example.fieldify.network.model.SendOtpRequest;
import com.example.fieldify.network.model.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApiService {

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("api/auth/verify-otp")
    Call<AuthResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("api/auth/send-otp")
    Call<AuthResponse> sendOtp(@Body SendOtpRequest request);

    @POST("api/auth/send-otp")
    Call<AuthResponse> sendOtpQuery(@Query("email") String email);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
}