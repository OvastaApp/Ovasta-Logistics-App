package com.ovasta.logisticsapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

/** POSTs [body] as JSON. */
internal suspend fun HttpClient.postJson(path: String, body: Any): HttpResponse =
    post(path) {
        contentType(ContentType.Application.Json)
        setBody(body)
    }

/** PATCHes [body] as JSON. */
internal suspend fun HttpClient.patchJson(path: String, body: Any): HttpResponse =
    patch(path) {
        contentType(ContentType.Application.Json)
        setBody(body)
    }

/**
 * POSTs a multipart form with one JPEG file part, plus [fields] as text parts.
 *
 * @param part name of the file part, e.g. "image"
 * @param fileName file name sent for the part, e.g. "image.jpg"
 */
internal suspend fun HttpClient.postJpeg(
    path: String,
    part: String,
    fileName: String,
    jpeg: ByteArray,
    fields: Map<String, String> = emptyMap(),
): HttpResponse = submitFormWithBinaryData(
    url = path,
    formData = formData {
        fields.forEach { (name, value) -> append(name, value) }
        append(part, jpeg, Headers.build {
            append(HttpHeaders.ContentType, "image/jpeg")
            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
        })
    }
)
