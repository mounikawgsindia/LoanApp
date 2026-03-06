package com.wingspan.loanapp.viewmodel

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegistrationViewModelTest {

    lateinit var viewModel: RegistrationViewModel

    @Before
    fun setUp() {
        viewModel = RegistrationViewModel()
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
    fun testValidatePersonalDetails_valid() {
        viewModel.onNameChange("John Doe")
        viewModel.onEmailChange("mounika.moravaneni14@gmail.comj")
        viewModel.onMobileChange("9876543210")
        viewModel.onDobChange("01-01-1990")

        val result = viewModel.validatePersonalDetails()
        assertTrue(result)
        assertNull(viewModel.state.nameError)
        assertNull(viewModel.state.emailError)
        assertNull(viewModel.state.mobileError)
        assertNull(viewModel.state.dobError)
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



}