package com.wingspan.loanapp.data.network

import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.OtpRequest
import com.wingspan.loanapp.data.OtpResponse
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServices {


    //loan submit form
    @PUT("loans/{userId}")
    suspend fun submitForm(@Path("userId") userId :String,@Body request: FormData): Response<ResponseData>

    //send otp
    @POST("loans/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): Response<OtpResponse>

    //verify otp
    @POST("loans/verify-otp")
    suspend fun verifyOtp(@Body request :OtpVerifyRequest): Response<ResponseData>
}