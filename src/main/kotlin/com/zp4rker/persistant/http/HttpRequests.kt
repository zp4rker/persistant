package com.zp4rker.persistant.http

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * @author zp4rker
 */

fun request(
    method: String,
    baseUrl: String,
    parameters: Map<String, Any?> = mapOf(),
    headers: Map<String, String> = mapOf(),
    content: String? = null
): String {
    val url = URL("$baseUrl${
        if (parameters.isNotEmpty()) parameters.filter { it.value != null }.map {
            "${it.key}=${it.value.toString().urlEncode()}"
        }.joinToString("&", "?") else ""
    }"
    )
    with(url.openConnection() as HttpURLConnection) {
        requestMethod = method.lowercase()
        headers.forEach { addRequestProperty(it.key, it.value) }
        addRequestProperty("User-Agent", "Persistant;${this::class.java.`package`.implementationVersion}")
        content?.let {
            doOutput = true
            outputStream.use { os -> os.writer().use { wr -> wr.write(content) } }
        }

        val response = runCatching { inputStream.use { it.reader().use { r -> r.readText() } } }.getOrNull() ?: errorStream?.use {
            it.reader().use { r -> r.readText() }
        } ?: ""

        return response
    }
}

fun String.urlEncode(): String = URLEncoder.encode(this, "utf8")