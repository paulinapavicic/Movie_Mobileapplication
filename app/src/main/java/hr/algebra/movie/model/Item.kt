package hr.algebra.movie.model

data class Item(
    val id: Long?,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String,
    val release_date: String,
    val vote_average: Double,
    val vote_count: Int,
    val popularity: Double,
    val adult: Boolean,
    val video: Boolean,
    val original_language: String,
    val original_title: String,
    val genre_ids: List<Int>
)
