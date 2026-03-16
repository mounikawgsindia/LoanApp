package com.wingspan.loanapp.data.repository

import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.OtpRequest
import com.wingspan.loanapp.data.OtpResponse
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.ResponseData
import com.wingspan.loanapp.data.network.ApiServices
import com.wingspan.loanapp.utils.ApiResult
import javax.inject.Inject

class HomeScreenRepository @Inject constructor(private var apiService: ApiServices): BaseRepository() {


    suspend fun sendCaliculationOtp(phone: String,name:String): ApiResult<OtpResponse> =
        safeApiCall { apiService.sendCaliculationOtp(
            com.wingspan.loanapp.data.sendCaliculationOtp(
                phone.toString(),name)
        ) }

    suspend fun verifyOtp(request: OtpVerifyRequest): ApiResult<ResponseData> =
        safeApiCall { apiService.verifyOtp(request) }

    suspend fun verifyNumber(phone:String): ApiResult<ResponseData> =
        safeApiCall { apiService.verifyNumber(OtpRequest(phone)) }


}