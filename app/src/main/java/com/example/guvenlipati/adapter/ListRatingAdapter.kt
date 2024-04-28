package com.example.guvenlipati

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guvenlipati.home.UserRatingPair
import com.example.guvenlipati.models.Rating
import com.example.guvenlipati.models.User

class ListRatingAdapter(
    private val context: Context,
    private val userRatingPairs: List<UserRatingPair>
) : RecyclerView.Adapter<ListRatingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rating_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userRatingPair = userRatingPairs[position]
        holder.ratingCard.animation =
            AnimationUtils.loadAnimation(context, R.anim.recyclerview_anim)
        holder.bind(userRatingPair.user, userRatingPair.rating)
    }

    override fun getItemCount(): Int = userRatingPairs.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ratingCard: LinearLayout = view.findViewById(R.id.ratingCard)
        private val userPhotoImageView: ImageView = view.findViewById(R.id.userPhotoImageView)
        private val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        private val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        private val commentTextView: TextView = view.findViewById(R.id.commentTextView)
        private val ratingJobTextView: TextView = view.findViewById(R.id.ratingJobTextView)
        private val commentTimeTextView: TextView = view.findViewById(R.id.commentTimeTextView)

        fun bind(user: User?, rating: Rating?) {
            userNameTextView.text = user?.userName ?: "Unknown User"
            ratingBar.rating = rating!!.rating.toFloat()
            commentTextView.text = rating.comment
            ratingJobTextView.text = rating.petName + "-" + rating.jobType
            commentTimeTextView.text = rating.commentTime


            Glide.with(context)
                .load(user?.userPhoto ?: R.drawable.men_image)
                .placeholder(R.drawable.men_image)
                .into(userPhotoImageView)
        }
    }
}
