package com.example.testapp.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class generates a baseline profile for the app.
 * It runs the app and records which code is used during a typical session.
 * Android uses this information to pre-compile your code, making it run
 * significantly smoother and start up faster.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "com.example.testapp",
        // This includes all the code used from app startup through the first screen
        includeInStartupProfile = true
    ) {
        // Start the app
        pressHome()
        startActivityAndWait()
        
        // You can add robot-like interactions here to optimize specific parts:
        // Example: waitForIdle()
    }
}
