package com.wingspan.loanapp.data.repository

import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.OtpResponse
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.ResponseData
import com.wingspan.loanapp.data.network.ApiServices
import com.wingspan.loanapp.utils.ApiResult
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class RegistrationRepositoryTest {

    private lateinit var apiServices: ApiServices
    private lateinit var repository: RegistrationRepository

    @BeforeEach
    fun setUp() {
        //fake api service
        apiServices = mock(ApiServices::class.java)
        repository = RegistrationRepository(apiServices)
    }


    @Test
    fun testSendOtpApi() = runTest {
        testApiEdgeCases(
            apiServiceCall = { apiServices.sendOtp(any()) },
            repoCall = { repository.sendOtp("8125342434") },
            successResponse = OtpResponse(true, "OTP sent", "123456"),
            errorJson = """{"error":"Invalid phone"}""",
            errorMessageOnFailure = "Invalid phone"
        )
    }

    @Test
    fun testVerifyOtpApi() = runTest {
        testApiEdgeCases(
            apiServiceCall = { apiServices.verifyOtp(any()) },
            repoCall = { repository.verifyOtp(OtpVerifyRequest("8125342434", "123456")) },
            successResponse = ResponseData(
                msg = "verify successfully",
                error = "",
                success = true
            ),
            errorJson = """{"error":"OTP invalid"}""",
            errorMessageOnFailure = "OTP invalid"
        )
    }

    @Test
    fun testSubmitFormApi() = runTest {
        val formData = FormData(
            name ="mounika" ,
            dob ="2017-08-09" ,
            loanType = "Home loan",
            income = "70000",
            email = "test@gmail.com",
            employment = "test",
            cibil = "350",
            emi = "3000",
            address ="sr nagar",
            pincode = "500038"
        ) // Example, use your real data
        testApiEdgeCases(
            apiServiceCall = { apiServices.submitForm(any(), any()) },
            repoCall = { repository.submitForm("user123", formData) },
            successResponse = ResponseData(
                msg = "submit successfully",
                error = "",
                success = true
            ),
            errorJson = """{"error":"Form invalid"}""",
            errorMessageOnFailure = "Form invalid"
        )
    }

    // Generic helper
    private suspend fun <T> testApiEdgeCases(
        apiServiceCall: suspend () -> Response<T>,
        repoCall: suspend () -> ApiResult<T>,
        successResponse: T,
        errorJson: String,
        errorMessageOnFailure: String
    ) {
        // ✅ Success
        whenever(apiServiceCall()).thenReturn(Response.success(successResponse))
        var result = repoCall()
        assertTrue(result is ApiResult.Success)

        // ✅ API failure
        val errorBody = errorJson.toResponseBody("application/json".toMediaType())
        whenever(apiServiceCall()).thenReturn(Response.error(400, errorBody))
        result = repoCall()
        assertTrue(result is ApiResult.Error)
        assertEquals(errorMessageOnFailure, (result as ApiResult.Error).message)

        // ✅ Network error
        whenever(apiServiceCall()).thenThrow(IOException())
        result = repoCall()
        assertTrue(result is ApiResult.Error)
        assertEquals("Network error. Please check your connection.", (result as ApiResult.Error).message)

        // ✅ Timeout
        whenever(apiServiceCall()).thenThrow(SocketTimeoutException("timeout"))
        result = repoCall()
        assertTrue(result is ApiResult.Error)
        assertEquals("timeout", (result as ApiResult.Error).message)

        // ✅ Unexpected exception
        whenever(apiServiceCall()).thenThrow(RuntimeException("Unexpected"))
        result = repoCall()
        assertTrue(result is ApiResult.Error)
        assertEquals("Unexpected", (result as ApiResult.Error).message)
    }

}