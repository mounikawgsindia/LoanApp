package com.wingspan.loanapp.data.network

import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServices {


    //registration
    @POST("user/register")
    suspend fun submitForm(@Body request: FormData): Response<ResponseData>

    //send otp
    @POST("loans/send-otp")
    suspend fun sendOtp(@Path("phone") phone:String): Response<ResponseData>

    //verify otp
    @POST("loans/verify-otp")
    suspend fun verifyOtp(@Body request :OtpVerifyRequest): Response<ResponseData>
}