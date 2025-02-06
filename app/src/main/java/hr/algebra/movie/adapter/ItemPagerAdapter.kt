package hr.algebra.movie.adapter



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.movie.R
import hr.algebra.movie.model.Item
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class ItemPagerAdapter(private val context: Context, private val items: List<Item>) :
    RecyclerView.Adapter<ItemPagerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivItem: ImageView = itemView.findViewById(R.id.ivPagerItem)
        val tvTitle: TextView = itemView.findViewById(R.id.tvPagerTitle)
        val tvReleaseDate: TextView = itemView.findViewById(R.id.tvPagerReleaseDate)
        val tvVoteAverage: TextView = itemView.findViewById(R.id.tvPagerVoteAverage)
        val tvOverview: TextView = itemView.findViewById(R.id.tvPagerOverview)

        fun bind(item: Item) {
            tvTitle.text = item.title
            tvReleaseDate.text = item.release_date
            tvVoteAverage.text = item.vote_average.toString()
            tvOverview.text = item.overview


            // Base URL for TMDB images
            val IMAGE_API_URL = "https://image.tmdb.org/t/p/w500"

            // Load image using poster_path if available, otherwise use backdrop_path
            val imageUrl = when {
                !item.poster_path.isNullOrEmpty() -> "$IMAGE_API_URL${item.poster_path}"
                !item.backdrop_path.isNullOrEmpty() -> "$IMAGE_API_URL${item.backdrop_path}"
                else -> null // No image available
            }

            // Load image using Picasso
            imageUrl?.let {
                Picasso.get()
                    .load(it)
                    .error(R.drawable.popcorn) // Fallback image
                    .transform(RoundedCornersTransformation(50, 5))
                    .into(ivItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pager, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
