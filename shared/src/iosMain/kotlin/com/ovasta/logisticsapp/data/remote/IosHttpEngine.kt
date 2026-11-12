package com.ovasta.logisticsapp.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

fun createIosHttpEngine(): HttpClientEngine = Darwin.create()
