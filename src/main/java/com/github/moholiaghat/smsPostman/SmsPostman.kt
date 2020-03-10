package com.github.moholiaghat.smsPostman

import mu.KotlinLogging
import okhttp3.*
import java.io.IOException
import java.util.concurrent.Executors


class SmsPostman(threadPoolSize: Int) {
    private val logger = KotlinLogging.logger { }
    private val smsThreadPool = Executors.newFixedThreadPool(threadPoolSize)
    private val httpClient = OkHttpClient()

    fun getRequest(parameters: Map<String, String>, url: String) {
        smsThreadPool.submit { getMethod(parameters, url) }
    }

    fun postRequest(parameters: Map<String, String>, url: String, headers: Map<String, String>) {
        smsThreadPool.submit { postMethod(parameters, url, headers) }
    }

    private fun getMethod(parameters: Map<String, String>, baseUrl: String) {
        val urlBuilder = HttpUrl.parse(baseUrl)!!.newBuilder()
        parameters.forEach { urlBuilder.addQueryParameter(it.key, it.value) }

        val url = urlBuilder.build().toString()
        val request = Request.Builder()
                .url(url)
                .build()

        sendRequest(request)
    }

    private fun postMethod(parameters: Map<String, String>, url: String, headers: Map<String, String>) {
        val requestBody = FormBody.Builder()
        parameters.forEach { requestBody.addEncoded(it.key, it.value) }

        val body = requestBody.build()
        val request = Request.Builder()
                .url(url)
                .post(body)
                .headers(Headers.of(headers))
                .build()

        sendRequest(request)
    }

    private fun sendRequest(request: Request) {
        try {
            try {
                httpClient.newCall(request).execute()
            } catch (e: IOException) {
                logger.error(e) { "SMS sending failed! Retrying..." }
                httpClient.newCall(request).execute()
            }
        } catch (t: Throwable) {
            error { "Sms service is not available." }
        }
    }
}
