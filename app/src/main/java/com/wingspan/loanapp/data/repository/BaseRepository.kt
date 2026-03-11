package com.wingspan.loanapp.data.repository

import com.google.gson.Gson
import com.wingspan.loanapp.data.ApiError
import com.wingspan.loanapp.utils.ApiResult
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseRepository {

    protected  suspend fun <T> safeApiCall(
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
                    ApiResult.Success(Unit as T)   // delete and update has no body only 200
                }

            } else {
                //hable all error codes here
                val code = response.code()
                val errorBody = response.errorBody()?.string()

                println("HTTP Code: $code")
                println("Raw Error Body: $errorBody")

                val message = try {
                    val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                    apiError?.error ?: apiError?.message ?: "Something went wrong"
                } catch (e: Exception) {
                    println("JSON Parsing Failed: ${e.message}")
                    "Unexpected error"
                }

                when (code) {

                    in 400..499 -> {
                        // Client errors
                        ApiResult.Error("Client Error: $message")
                    }

                    in 500..599 -> {
                        // Server errors
                        ApiResult.Error("Server Error. Please try again later")
                    }

                    else -> {
                        ApiResult.Error("Unexpected HTTP error")
                    }
                }
            }
        } catch (e: IOException) {
            //IO exception means network failed.
            ApiResult.Error(
                "Network error. Please check your connection.",
                e
            )

        }catch (e: SocketTimeoutException) {
            ApiResult.Error("Server is taking too long to respond. Please try again.", e)
        } catch (e: Exception) {

            ApiResult.Error(
                e.message ?: "Unexpected error occurred",
                e
            )
        }
    }
}