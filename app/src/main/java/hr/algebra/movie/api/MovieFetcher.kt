package hr.algebra.movie.api

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import hr.algebra.movie.MOVIES_PROVIDER_CONTENT_URI
import hr.algebra.movie.MovieReceiver
import hr.algebra.movie.framework.sendBroadcast
import hr.algebra.movie.handler.downloadImageAndStore
import hr.algebra.movie.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieFetcher (private val context: Context){
    private val moviesApi: MoviesApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val originalRequest: Request = chain.request()

                        // Add headers to the original request
                        val modifiedRequest: Request = originalRequest.newBuilder()
                            .addHeader("accept", "application/json")
                            .addHeader(
                                "Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiZGI2MDU3YTI5YjZmYmU5ZTM2NTNlOTY2MmFmNTY4NSIsInN1YiI6IjY1NjQ3MmY0ZDk1NTRiMDBjNjE3NWFiMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.yWmjtY4VMe1kOXevhua-6T64aUmoCvFCUF1RcwAZavU"
                            )
                            .build()

                        chain.proceed(modifiedRequest)
                    }
                    .build()
            )
            .build()
        moviesApi = retrofit.create(MoviesApi::class.java)
    }


    fun fetchItems(page: Int) {
        val request = moviesApi.fetchItems(page)
        request.enqueue(object : Callback<MovieDiscoverItem> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<MovieDiscoverItem>,
                response: Response<MovieDiscoverItem>
            ) {
                response.body()?.let { populateItems(it) }
            }

            override fun onFailure(call: Call<MovieDiscoverItem>, t: Throwable) {
                Log.e(javaClass.name, t.toString(), t)
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun populateItems(moviesDiscoverItem: MovieDiscoverItem) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.run {
            moviesDiscoverItem.results.forEach {
                Log.d("MOVIE_FETCHER", "Fetched movie: ${it.title}, API poster_path: ${it.poster_path}")

                val values = ContentValues().apply {
                    put(Item::id.name, it.id)
                    put(Item::title.name, it.title)
                    put(Item::overview.name, it.overview)
                    put(Item::poster_path.name, it.poster_path) // Store raw TMDB path
                }

                context.contentResolver.insert(MOVIES_PROVIDER_CONTENT_URI, values)
            }
        }
    }





}