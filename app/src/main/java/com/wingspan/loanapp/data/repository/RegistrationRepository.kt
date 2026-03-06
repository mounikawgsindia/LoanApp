package com.wingspan.loanapp.data.repository

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.network.ApiServices
import java.io.IOException
import javax.inject.Inject
import com.wingspan.loanapp.utils.Result

class RegistrationRepository @Inject constructor(private var apiService: ApiServices) {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun sendOtp(phone: String): Result<Unit> = safeApiCall {
        apiService.sendOtp(phone)  // your Retrofit call
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun verifyOtp(request: OtpVerifyRequest): Result<Unit> = safeApiCall {
        apiService.verifyOtp(request)
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun submitForm(formData: FormData): Result<Unit> = safeApiCall {
        apiService.submitForm(formData)
    }

    // --- Generic wrapper ---
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            val response = apiCall()
            Result.Success(response)
        } catch (e: IOException) {
            Result.Error("Network error. Please check your connection.", e)
        } catch (e: HttpException) {
            Result.Error("Server error: ${e.message}", e)
        } catch (e: Exception) {
            Result.Error("Unexpected error occurred", e)
        }
    }
}