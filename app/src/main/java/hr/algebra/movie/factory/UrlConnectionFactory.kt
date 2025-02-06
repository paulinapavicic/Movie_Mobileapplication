package hr.algebra.movie.factory

import java.net.HttpURLConnection
import java.net.URL

private const val TIMEOUT = 10000
private const val METHOD_GET = "GET"
private const val USER_AGENT = "User-Agent"
private const val MOZILLA = "Mozilla/5.0"

fun createGetHttpUrlConnection(url: String): HttpURLConnection {
    val connection = URL(url)
    return (connection.openConnection() as HttpURLConnection).apply {
        connectTimeout = TIMEOUT
        readTimeout = TIMEOUT
        requestMethod = METHOD_GET
        addRequestProperty(USER_AGENT, MOZILLA)
    }
}