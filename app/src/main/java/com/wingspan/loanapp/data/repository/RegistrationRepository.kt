package com.wingspan.loanapp.data.repository

import android.util.Log
import com.google.gson.Gson
import com.wingspan.loanapp.data.ApiError
import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.OtpRequest
import com.wingspan.loanapp.data.OtpResponse
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.ResponseData
import com.wingspan.loanapp.data.network.ApiServices
import com.wingspan.loanapp.utils.ApiResult
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject


open class RegistrationRepository @Inject constructor(private var apiService: ApiServices) :
    BaseRepository(){


    suspend fun sendOtp(phone: String): ApiResult<OtpResponse> =
        safeApiCall { apiService.sendOtp(OtpRequest(phone.toString())) }

    suspend fun verifyOtp(request: OtpVerifyRequest): ApiResult<ResponseData> =
        safeApiCall { apiService.verifyOtp(request) }

    suspend fun submitForm(userId:String,formData: FormData): ApiResult<ResponseData> =
        safeApiCall { apiService.submitForm(userId,formData) }



}