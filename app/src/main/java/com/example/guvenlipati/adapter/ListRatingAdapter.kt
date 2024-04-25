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
import com.example.guvenlipati.models.Rating
import com.example.guvenlipati.models.User

class ListRatingAdapter(
    private val context: Context,
    private val userList: List<User>,
    private val ratingList: List<Rating>
) : RecyclerView.Adapter<ListRatingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ratingCard.animation = AnimationUtils.loadAnimation(context, R.anim.recyclerview_anim)
        if (position < userList.size && position < ratingList.size) {
            holder.bind(
                userList[position],
                ratingList[position]
            )
        }
    }

    override fun getItemCount(): Int = ratingList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ratingCard: LinearLayout = view.findViewById(R.id.ratingCard)
        private val userPhotoImageView = view.findViewById<ImageView>(R.id.userPhotoImageView)
        private val userNameTextView = view.findViewById<TextView>(R.id.userNameTextView)
        private val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        private val commentTextView = view.findViewById<TextView>(R.id.commentTextView)

        fun bind(user: User, rating: Rating) {
            userNameTextView.text = user.userName
            ratingBar.rating = rating.rating.toFloat()
            commentTextView.text = rating.comment

            Glide.with(context)
                .load(user.userPhoto)
                .placeholder(R.drawable.men_image)
                .into(userPhotoImageView)
        }
    }
}
