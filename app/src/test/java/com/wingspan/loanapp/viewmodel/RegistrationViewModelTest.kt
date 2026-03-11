package com.wingspan.loanapp.viewmodel

import android.util.Log
import com.wingspan.loanapp.data.FormData
import com.wingspan.loanapp.data.OtpResponse
import com.wingspan.loanapp.data.OtpVerifyRequest
import com.wingspan.loanapp.data.ResponseData
import com.wingspan.loanapp.data.repository.RegistrationRepository
import com.wingspan.loanapp.ui.theme.registration.RegistrationState
import com.wingspan.loanapp.ui.theme.registration.RegistrationViewModel
import com.wingspan.loanapp.utils.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*      // assertEquals, assertTrue, assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.mock
import kotlinx.coroutines.test.runTest
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.mockito.kotlin.*

import io.mockk.every
import io.mockk.mockkStatic

@ExtendWith(MockitoExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    lateinit var viewModel: RegistrationViewModel
    private lateinit var repository: RegistrationRepository

    // Test dispatcher for coroutine
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(RegistrationRepository::class.java)
        viewModel = RegistrationViewModel(repository)

        // Mock all Log.d calls so unit tests won't crash
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testOnNameChange() {
        viewModel.onNameChange("mounika")
        assertEquals("mounika",viewModel.state.name)
        assertNull(viewModel.state.nameError)
    }

    @Test
    fun onEmailChange() {
        viewModel.onEmailChange("test@gmailcom")
        assertEquals("test@gmailcom",viewModel.state.email)
        assertNull(viewModel.state.emailError)
    }

    @Test
    fun onMobileChange() {
        viewModel.onMobileChange("8122312323")
        assertEquals("8122312323",viewModel.state.mobile)
        assertNull(viewModel.state.mobileError)
    }

    @Test
    fun testOnDobChange() {
        viewModel.onDobChange("01-01-1990")
        assertEquals("01-01-1990", viewModel.state.dob)
        assertNull(viewModel.state.dobError)
    }
    @Test
    fun testonOtpChange(){
        viewModel.onOtpChange("234567")
        assertEquals("234567",viewModel.state.otp)
        assertNull(viewModel.state.otpError)
    }

    @ParameterizedTest
    @CsvSource(
        "'', test@gmail.com, 9876543210, 01-01-2000, false",
        "Mounika, '', 9876543210, 01-01-2000, false",
        "Mounika, test@gmail.com, 12345, 01-01-2000, false",
        "Mounika, test@gmail.com, 9876543210, '', false",
        "Mounika, test@gmail.com, 9876543210, 01-01-2000, true"

    )
    fun testValidatePersonalDetails_cases(
        name: String,
        email: String,
        mobile: String,
        dob: String,
        expected: Boolean
    ) {

        viewModel.onNameChange(name)
        viewModel.onEmailChange(email)
        viewModel.onMobileChange(mobile)
        viewModel.onDobChange(dob)

        val result = viewModel.validatePersonalDetails()

        assertEquals(expected, result)
    }

    @Test
    fun validateMobile_emptyMobile_returnsError() {
        viewModel.onMobileChange("")
        val result = viewModel.validateMobile()
        assertEquals("Enter mobile number", result)
    }

    @Test
    fun validateMobile_invalidLength_returnsError() {

        viewModel.onMobileChange("12345")
        val result = viewModel.validateMobile()
        assertEquals("Enter a valid 10-digit mobile number", result)
    }

    @Test
    fun validateMobile_validMobile_returnsNull() {
        viewModel.onMobileChange("9876543210")


        val result = viewModel.validateMobile()

        assertNull(result)
    }
    @Test
    fun testvalidateLoanDetails_notvalid() {
        viewModel.onLoanTypeChange("Home loan")
        viewModel.onEmploymentTypeChange("test")
        viewModel.onMonthlyIncomeChange("ert")


        val result = viewModel.validateLoanDetails()
        assertFalse(result)
        assertNull(viewModel.loanState.loanTypeError)
        assertNull(viewModel.loanState.employmentError)
        assertEquals("Invalid income",viewModel.loanState.incomeError)

    }


    // ======= Financial Details =======
    @ParameterizedTest
    @CsvSource(
        "350,500000,true",
        "'',500000,false",
        "350,'',false",
        "1000,500000,false",    // invalid CIBIL
        "350,0,true"             // valid EMI
    )
    fun testValidateFinancialDetails_cases(
        cibil: String,
        emi: String,
        expected: Boolean
    ) {
        viewModel.onCibilChange(cibil)
        viewModel.onPresentEmiChange(emi)
        val result = viewModel.validateFinancialDetails()
        assertEquals(expected, result)
    }

    //Loan
    @ParameterizedTest
    @CsvSource(
        "'', '', false",                 // both empty → invalid
        "'123 Main St', '', false",      // missing PIN → invalid
        "'', '500001', false",           // missing address → invalid
        "'123 Main St', '500001', true", // valid
        "'123 Main St', '5000', false"   // invalid PIN → invalid
    )
    fun testValidateAddressDetails_cases(address: String, pin: String, expected: Boolean) {
        viewModel.onAddressChange(address)
        viewModel.onPinCodeChange(pin)

        val result = viewModel.validateAddressDetails()

        assertEquals(expected, result)
    }


    //address
    @ParameterizedTest
    @CsvSource(
        "'', '', '', false",                // all empty → invalid
        "'Home Loan', '', '', false",       // missing income & employment → invalid
        "'', '500000', '', false",          // missing loan type & employment → invalid
        "'Personal Loan', 'abc', 'Salaried', false", // invalid income → invalid
        "'Home Loan', '500000', 'Salaried', true"   // valid
    )
    fun testValidateLoanDetails_cases(
        loanType: String,
        monthlyIncome: String,
        employmentType: String,
        expected: Boolean
    ) {
        viewModel.onLoanTypeChange(loanType)
        viewModel.onMonthlyIncomeChange(monthlyIncome)
        viewModel.onEmploymentTypeChange(employmentType)

        val result = viewModel.validateLoanDetails()

        assertEquals(expected, result)
    }
    @Test
    fun testvalidateFinancialDetails_valid() {
        viewModel.onCibilChange("360")
        viewModel.onPresentEmiChange("4000")

        val result = viewModel.validateFinancialDetails()
        assertTrue(result)
        assertNull(viewModel.financialState.cibilError)
        assertNull(viewModel.financialState.emiError)

    }

    @Test
    fun testvalidateAddressDetails_valid() {
        viewModel.onAddressChange("sr nager")
        viewModel.onPinCodeChange("500038")

        val result = viewModel.validateAddressDetails()
        assertTrue(result)
        assertNull(viewModel.addressState.addressError)
        assertNull(viewModel.addressState.pinError)

    }


    @Test
    fun onLoanTypeChange() {
        viewModel.onLoanTypeChange("Home Loan")
        assertEquals("Home Loan",viewModel.loanState.loanType)
        assertNull(viewModel.loanState.loanTypeError)
    }

    @Test
    fun onMonthlyIncomeChange() {
        viewModel.onMonthlyIncomeChange("500000")
        assertEquals("500000",viewModel.loanState.monthlyIncome)
        assertNull(viewModel.loanState.incomeError)
    }

    @Test
    fun onEmploymentTypeChange() {
        viewModel.onEmploymentTypeChange("Salary")
        assertEquals("Salary",viewModel.loanState.employmentType)
        assertNull(viewModel.loanState.employmentError)
    }

    @Test
    fun onCibilChange() {
        viewModel.onCibilChange("350")
        assertEquals("350",viewModel.financialState.cibilScore)
        assertNull(viewModel.financialState.cibilError)
    }

    @Test
    fun onPresentEmiChange() {
        viewModel.onPresentEmiChange("2000")
        assertEquals("2000",viewModel.financialState.presentEmi)
        assertNull(viewModel.financialState.emiError)
    }

    @Test
    fun onAddressChange() {
        viewModel.onAddressChange("sr nagar")
        assertEquals("sr nagar",viewModel.addressState.address)
        assertNull(viewModel.addressState.addressError)
    }

    @Test
    fun onPinCodeChange() {
        viewModel.onPinCodeChange("500038")
        assertEquals("500038",viewModel.addressState.pinCode)
        assertNull(viewModel.addressState.pinError)
    }

//otp validation
@Test
fun validateOTP_emptyMobile_returnsError() {

    viewModel.onOtpChange("")
    val result = viewModel.validateOtp()

    assertEquals("Enter Otp number", result)
}

    @Test
    fun validateOTP_invalidLength_returnsError() {

        viewModel.onOtpChange("243")
        val result = viewModel.validateOtp()

        assertEquals("Enter a valid 6-digit otp number", result)
    }

    @Test
    fun validateOTP_validMobile_returnsNull() {
        viewModel.onOtpChange("243453")
        val result = viewModel.validateOtp()
        assertNull(result)
    }


    @Test
    fun `sendOtp updates state to OtpSent on success`() = runTest {


        val response = ApiResult.Success(
            OtpResponse(success = true, message = "Otp sent successfully")
        )
        whenever(repository.sendOtp(any<String>())).thenReturn(response)

        viewModel.sendOtp("8125342434")
        testDispatcher.scheduler.advanceUntilIdle() // Let coroutine finish
        assertTrue(viewModel.uiState.value is RegistrationState.OtpSent)
    }

    @Test
    fun `sendOtp updates state to OtpSent on fail`() = runTest {


        val response = ApiResult.Error("Invalid phone number")
        whenever(repository.sendOtp(any<String>())).thenReturn(response)

        viewModel.sendOtp("812534")
        testDispatcher.scheduler.advanceUntilIdle() // Let coroutine finish
        val state = viewModel.uiState.value
        assertTrue(viewModel.uiState.value is RegistrationState.Error)
        assertEquals("Invalid phone number",(viewModel.uiState.value as RegistrationState.Error).message)
    }


    @Test
    fun `verifyotp updates state to OtpSent on success`() = runTest {


        val response = ApiResult.Success(
            ResponseData(
                success = true,
                msg = "",
                error = "",
            )
        )
        var request=OtpVerifyRequest(
            phone = "8967789098",
            otp = "456789"
        )
        whenever(repository.verifyOtp(any())).thenReturn(response)

        viewModel.verifyOtp(request)
        testDispatcher.scheduler.advanceUntilIdle() // Let coroutine finish
        assertTrue(viewModel.uiState.value is RegistrationState.OtpVerified)
    }

    @Test
    fun `verifyotp updates state to OtpSent on fail`() = runTest {


        val response = ApiResult.Error("Failed to verify Otp")
        var request=OtpVerifyRequest(
            phone = "8967789098",
            otp = "456789"
        )
        whenever(repository.verifyOtp(any())).thenReturn(response)

        viewModel.verifyOtp(request)
        testDispatcher.scheduler.advanceUntilIdle() // Let coroutine finish
        assertTrue(viewModel.uiState.value is RegistrationState.Error)
    }


    @Test
    fun `subnitform updates state to OtpSent on success`() = runTest {


        val response = ApiResult.Success(
            ResponseData(
                success = true,
                msg = "",
                error = "",
            )
        )

        whenever(repository.submitForm(any<String>(),any())).thenReturn(response)

        viewModel.submitForm()
        testDispatcher.scheduler.advanceUntilIdle() // Let coroutine finish
        assertTrue(viewModel.uiState.value is RegistrationState.FormSubmitted)
    }

    @Test
    fun `subnitform updates state to OtpSent on fail`() = runTest {


        val response = ApiResult.Error("Failed toSubmit Form")

        whenever(repository.submitForm(any<String>(),any())).thenReturn(response)

        viewModel.submitForm()
        testDispatcher.scheduler.advanceUntilIdle() // Let coroutine finish
        assertTrue(viewModel.uiState.value is RegistrationState.Error)
    }

}