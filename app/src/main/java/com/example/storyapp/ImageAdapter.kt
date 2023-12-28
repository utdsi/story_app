package com.digiauxilio.storyapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class ImageAdapter(private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.UsersViewHolder>() {

    private var list = ArrayList<ImageRequest>()

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ImageRequest) {
            with(itemView) {
                val imageView = findViewById<ImageView>(R.id.khokho)
                Picasso.get().load(data.image).into(imageView)

                imageView.setOnClickListener {
                    //Toast.makeText(context,"${data.image}",Toast.LENGTH_SHORT).show()

                    val intent = Intent(context, ShareImageActivity::class.java)
                    intent.putExtra("image_url", data.image)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun addList(items: ArrayList<ImageRequest>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}