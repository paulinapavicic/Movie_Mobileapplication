package hr.algebra.movie

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import hr.algebra.movie.adapter.ItemPagerAdapter
import hr.algebra.movie.dao.MovieSqlHelper
import hr.algebra.movie.databinding.ActivityItemPagerBinding
import hr.algebra.movie.model.Item

const val ITEM_POSITION = "hr.algebra.movie.item_position"

class ItemPagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemPagerBinding
    private lateinit var items: MutableList<Item>
    private var itemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initPager()
    }

    private fun initPager() {
        items = fetchMoviesFromDatabase()
        itemPosition = intent.getIntExtra(ITEM_POSITION, 0)

        // Set up ViewPager2 with the adapter
        binding.viewPager.adapter = ItemPagerAdapter(this, items)
        binding.viewPager.currentItem = itemPosition
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }


    private fun fetchMoviesFromDatabase(): MutableList<Item> {
        val moviesList = mutableListOf<Item>()
        val dbHelper = MovieSqlHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM items", null)
        while (cursor.moveToNext()) {
            val movie = Item(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                overview = cursor.getString(cursor.getColumnIndexOrThrow("overview")),
                poster_path = cursor.getString(cursor.getColumnIndexOrThrow("poster_path")),
                backdrop_path = cursor.getString(cursor.getColumnIndexOrThrow("backdrop_path")),
                release_date = cursor.getString(cursor.getColumnIndexOrThrow("release_date")),
                vote_average = cursor.getDouble(cursor.getColumnIndexOrThrow("vote_average")),
                vote_count = cursor.getInt(cursor.getColumnIndexOrThrow("vote_count")),
                popularity = cursor.getDouble(cursor.getColumnIndexOrThrow("popularity")),
                adult = cursor.getInt(cursor.getColumnIndexOrThrow("adult")) == 1,
                video = cursor.getInt(cursor.getColumnIndexOrThrow("video")) == 1,
                original_language = cursor.getString(cursor.getColumnIndexOrThrow("original_language")),
                original_title = cursor.getString(cursor.getColumnIndexOrThrow("original_title")),
                genre_ids = emptyList() // Modify this if you need to parse genres
            )
            moviesList.add(movie)
        }
        cursor.close()
        db.close()

        return moviesList
    }
}