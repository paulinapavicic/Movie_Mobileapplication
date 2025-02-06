package hr.algebra.movie.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val API_URL = "https://api.themoviedb.org/3/"
interface MoviesApi {

    @GET("discover/movie?include_adult=false&include_video=false&language=en-US&sort_by=popularity.desc")
    fun fetchItems(@Query("page") page:Int = 1): Call<MovieDiscoverItem>
}