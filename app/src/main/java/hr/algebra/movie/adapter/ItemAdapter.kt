package hr.algebra.movie.adapter

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.movie.ITEM_POSITION
import hr.algebra.movie.ItemPagerActivity
import hr.algebra.movie.MOVIES_PROVIDER_CONTENT_URI
import hr.algebra.movie.MovieReceiver
import hr.algebra.movie.R
import hr.algebra.movie.model.Item
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class ItemAdapter (private val context: Context, private val items: MutableList<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(){  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivItem = itemView.findViewById<ImageView>(R.id.ivItem)
    private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
    fun bind(item: Item) {
        val imageUrl = "https://image.tmdb.org/t/p/w500" + item.backdrop_path // Use backdrop_path

        Picasso.get()
            .load(imageUrl)
            .error(R.drawable.popcorn) // Default image if loading fails
            .transform(RoundedCornersTransformation(50, 5))
            .into(ivItem)

        tvTitle.text = item.title

    }

}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ItemPagerActivity::class.java)
            intent.putExtra(ITEM_POSITION, position)
            context.startActivity(intent)

            // Send broadcast with movie title
            val broadcastIntent = Intent(context, MovieReceiver::class.java)
            broadcastIntent.putExtra("MOVIE_TITLE", item.title)
            context.sendBroadcast(broadcastIntent)
        }


        holder.bind(item)

    }
}
