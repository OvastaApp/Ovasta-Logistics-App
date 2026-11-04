package com.ovasta.logisticsapp.data.remote

import com.ovasta.logisticsapp.data.RemoteConstants
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Cache
import okhttp3.Protocol
import java.io.File

/** OkHttp engine with the same disk cache and HTTP/1.1-only setting the app used before Ktor. */
fun createAndroidHttpEngine(cacheDir: File): HttpClientEngine = OkHttp.create {
    config {
        cache(Cache(File(cacheDir, RemoteConstants.CACHE_NAME), RemoteConstants.CACHE_SIZE))
        protocols(listOf(Protocol.HTTP_1_1))
    }
}
