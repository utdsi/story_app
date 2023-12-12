package com.example.storyapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso



class ImageAdapter(val context: Context,private val imageUrlList: List<ImageRequest>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrlList[position].image
        // Use Picasso to load images from URLs into ImageView
        Picasso.get().load(imageUrl).into(holder.imageView)

        holder.imageView.setOnClickListener {

            val intent = Intent(context,ShareImageActivity::class.java)
            intent.putExtra("image_url",imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return imageUrlList.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.khokho)
    }
}





