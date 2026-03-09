package com.wingspan.loanapp.data.repository

import retrofit2.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
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
import javax.inject.Inject


open class RegistrationRepository @Inject constructor(private var apiService: ApiServices) {


    suspend fun sendOtp(phone: String): ApiResult<OtpResponse> =
        safeApiCall { apiService.sendOtp(OtpRequest(phone)) }

    suspend fun verifyOtp(request: OtpVerifyRequest): ApiResult<ResponseData> =
        safeApiCall { apiService.verifyOtp(request) }

    suspend fun submitForm(userId:String,formData: FormData): ApiResult<ResponseData> =
        safeApiCall { apiService.submitForm(userId,formData) }


    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): ApiResult<T> {

        return try {

            val response = apiCall()

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    ApiResult.Success(Unit as T)   // handles Unit responses
                }

            } else {

                val errorBody = response.errorBody()?.string()

                val message = try {

                    val json = JSONObject(errorBody ?: "")

                    json.optString("error")
                        .ifEmpty { json.optString("message") }
                        .ifEmpty { json.optString("details") }
                        .ifEmpty { "Something went wrong" }

                } catch (e: Exception) {
                    "Something went wrong"
                }

                ApiResult.Error(message)
            }

        } catch (e: IOException) {

            ApiResult.Error(
                "Network error. Please check your connection.",
                e
            )

        } catch (e: Exception) {

            ApiResult.Error(
                e.message ?: "Unexpected error occurred",
                e
            )
        }
    }
}