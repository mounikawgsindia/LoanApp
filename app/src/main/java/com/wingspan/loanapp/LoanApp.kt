package com.wingspan.loanapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class LoanApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}