package com.wingspan.loanapp.ui.theme.homescreen

import android.util.Log
import com.wingspan.loanapp.data.repository.HomeScreenRepository
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class HomeScreenViewmodelTest {
    private lateinit var viewmodel: HomeScreenViewmodel
    private lateinit var repository: HomeScreenRepository

    // Test dispatcher for coroutine
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
      repository= mock(HomeScreenRepository::class.java)
        viewmodel = HomeScreenViewmodel(repository)

        // Mock all Log.d calls so unit tests won't crash
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun onloanAmountChange() {
        var request ="100"
        viewmodel.onloanAmountChange(request)
        assertEquals("100",viewmodel.caliculaterState.loanAmount)
        assertNull(viewmodel.caliculaterState.loanAmountError)
    }

    @Test
    fun onintrestRateChange() {

        viewmodel.onintrestRateChange("5")
        assertEquals("5",viewmodel.caliculaterState.intrestRate)
        assertNull(viewmodel.caliculaterState.intrestRateError)
    }

    @Test
    fun onTenureChange() {

        viewmodel.onTenureChange("5")
        assertEquals("5",viewmodel.caliculaterState.tenure)
        assertNull(viewmodel.caliculaterState.tenureError)
    }

    @Test
    fun caliculaterValidation() {
    }

}