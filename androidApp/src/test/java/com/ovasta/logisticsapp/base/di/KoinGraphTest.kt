package com.ovasta.logisticsapp.base.di

import android.app.Application
import android.content.Context
import io.ktor.client.plugins.logging.Logger
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

class KoinGraphTest {

    /** Every definition's constructor parameters are bound, so the app can't crash on a missing one. */
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `app modules resolve`() {
        module { includes(appModules) }.verify(
            // Context comes from androidContext(); the Logger only exists in debug builds
            extraTypes = listOf(Context::class, Application::class, Logger::class)
        )
    }
}
