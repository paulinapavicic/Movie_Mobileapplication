package hr.algebra.movie.handler

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import hr.algebra.movie.factory.createGetHttpUrlConnection
import java.io.File
import java.net.HttpURLConnection
import java.nio.file.Files
import java.nio.file.Paths

const val IMAGE_API_URL = "https://image.tmdb.org/t/p/original"
@RequiresApi(Build.VERSION_CODES.O)
fun downloadImageAndStore(context: Context, url: String?): String? {
    if (url.isNullOrEmpty()) {
        Log.e("IMAGE_HANDLER", "Invalid image URL")
        return null
    }

    val fullUrl = "https://image.tmdb.org/t/p/original$url" // Construct full URL
    val filename = url.substring(url.lastIndexOf("/") + 1) // Extract filename
    val file: File = createLocaleFile(context, filename)

    try {
        Log.d("IMAGE_HANDLER", "Downloading image from: $fullUrl")

        val con: HttpURLConnection = createGetHttpUrlConnection(fullUrl)

        if (con.responseCode != HttpURLConnection.HTTP_OK) {
            Log.e("IMAGE_HANDLER", "Failed to fetch image. Response Code: ${con.responseCode}")
            return null
        }

        Files.copy(con.inputStream, file.toPath())

        if (file.exists()) {
            Log.d("IMAGE_HANDLER", "Image saved successfully at: ${file.absolutePath}")
            return file.absolutePath
        } else {
            Log.e("IMAGE_HANDLER", "Image saving failed.")
        }
    } catch (e: Exception) {
        Log.e("IMAGE_HANDLER", "Exception while downloading: ${e.message}", e)
    }

    return null
}




fun createLocaleFile(context: Context, filename: String): File {
    val dir = context.applicationContext.getExternalFilesDir(null)
    val file = File(dir, filename)
    if (file.exists()) file.delete()
    return file
}

