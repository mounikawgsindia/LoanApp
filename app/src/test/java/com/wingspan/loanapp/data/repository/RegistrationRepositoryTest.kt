package com.wingspan.loanapp.data.repository

import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.OtpResponse
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.ResponseData
import com.wingspan.loanapp.data.network.ApiServices
import com.wingspan.loanapp.utils.ApiResult
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException

class RegistrationRepositoryTest {

    // repository Generic type so no need to test all cases like "No network","APi failed(Server crash)","Tome Out" ... empty data" one api for all this enough and "success state for remaing api"
    private lateinit var apiServices: ApiServices
    private lateinit var repository: RegistrationRepository

    @BeforeEach
    fun setUp() {
        //fake api service
        apiServices = mock(ApiServices::class.java)
        repository = RegistrationRepository(apiServices)
    }

    //send otp
    @Test
    fun `sendOtp returns Success when api call successful`() = runTest {

        val response = OtpResponse(true, "OTP Sent")

        whenever(apiServices.sendOtp(any()))
            .thenReturn(Response.success(response))

        val result = repository.sendOtp("9876543210")

        assertTrue(result is ApiResult.Success)
        assertEquals(response, (result as ApiResult.Success).data)
    }

    @Test
    fun `sendOtp returns Error when api returns error response`() = runTest {

        val errorJson = """{"error":"Invalid phone number"}"""

        val errorBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiServices.sendOtp(any()))
            .thenReturn(Response.error(400, errorBody))

        val result = repository.sendOtp("123")

        assertTrue(result is ApiResult.Error)
        assertEquals("Invalid phone number", (result as ApiResult.Error).message)
    }

    @Test
    fun `sendOtp returns Error when api returns error response catch Something went wrong `() = runTest {

        val errorJson = """{"detail":"Invalid phone number"}"""

        val errorBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiServices.sendOtp(any()))
            .thenReturn(Response.error(400, errorBody))

        val result = repository.sendOtp("123")

        assertTrue(result is ApiResult.Error)
        assertEquals("Something went wrong", (result as ApiResult.Error).message)
    }

    @Test
    fun `sendOtp returns Error on network failure`() = runTest {
        whenever(apiServices.sendOtp(any())).thenAnswer {
            throw IOException("Network error")
        }
        val result = repository.sendOtp("9876543210")
        assertTrue(result is ApiResult.Error)
        assertEquals(
            "Network error. Please check your connection.",
            (result as ApiResult.Error).message
        )
    }

    @Test
    fun `sendOtp returns Error on unexpected exception`() = runTest {

        whenever(apiServices.sendOtp(any()))
            .thenThrow(RuntimeException("Server crashed"))

        val result = repository.sendOtp("9876543210")

        assertTrue(result is ApiResult.Error)
        assertEquals("Server crashed", (result as ApiResult.Error).message)
    }

    @Test
    fun `sendOtp handles null response body`() = runTest {

        whenever(apiServices.sendOtp(any()))
            .thenReturn(Response.success(null))

        val result = repository.sendOtp("9876543210")

        assertTrue(result is ApiResult.Success)
    }

    //verify otp
    @Test
    fun `verifyOTp returns Success when api call successful`() = runTest {

        val response = ResponseData(
            msg = "",
            error = "",
            success = true
        )

        whenever(apiServices.verifyOtp(any()))
            .thenReturn(Response.success(response))

        val result = repository.verifyOtp(OtpVerifyRequest(
            phone = "81234567895",
            otp = "234567"
        ))

        assertTrue(result is ApiResult.Success)
        assertEquals(response, (result as ApiResult.Success).data)
    }

    //submit form
    //verify otp
    @Test
    fun `submitForm returns Success when api call successful`() = runTest {

        val response = ResponseData(
            msg = "",
            error = "",
            success = true
        )

        whenever(apiServices.submitForm(any(),any()))
            .thenReturn(Response.success(response))

        val result = repository.submitForm("123",FormData(
            name = "test",
            dob = "test",
            loanType = "test",
            income = "test",
            email = "test@gmail.com",
            employment = "test",
            cibil = "test",
            emi = "test",
            address = "srnager",
            pincode = "567890"
        ))

        assertTrue(result is ApiResult.Success)
        assertEquals(response, (result as ApiResult.Success).data)
    }

}