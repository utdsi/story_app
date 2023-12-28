package com.digiauxilio.storyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(
    val context: Context,
    private val languageArray: MutableList<LanguageRequest>
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_value, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.languageTextView.text = languageArray[position].name

        if (position == selectedItemPosition) {
            holder.languageTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.white
                )
            )
            holder.languageTextView.setBackgroundResource(R.drawable.language_circle)
        } else {
            // Reset the text color for non-selected items
            holder.languageTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.black
                )
            )
            holder.languageTextView.setBackgroundResource(0)
        }

        holder.itemView.setOnClickListener {

            selectedItemPosition = holder.adapterPosition

            notifyDataSetChanged()
            Toast.makeText(context, languageArray[position].name, Toast.LENGTH_SHORT).show()
            val sharedPreferences =
                context.getSharedPreferences("language", AppCompatActivity.MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()

            // write all the data entered by the user in SharedPreference and apply
            myEdit.putString("language_id", languageArray[position].languages_id.toString())

            myEdit.apply()
        }
    }

    override fun getItemCount(): Int {
        return languageArray.size
    }

    class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val languageTextView: TextView = itemView.findViewById(R.id.textViewValue)

    }
}


//class LanguageAdapter(private val context: Context, private val languages: List<String>) :
//    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {
//
//    private var selectedItemPosition: Int = RecyclerView.NO_POSITION
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.language, parent, false)
//        return LanguageViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
//        val language = languages[position]
//        holder.languageName.text = language
//
//        // Set the text color based on the selected item
//        if (position == selectedItemPosition) {
//            holder.languageName.setTextColor(ContextCompat.getColor(context, android.R.color.white))
//            holder.languageName.setBackgroundResource(R.drawable.circle_background)
//        } else {
//            // Reset the text color for non-selected items
//            holder.languageName.setTextColor(ContextCompat.getColor(context, android.R.color.black))
//            holder.languageName.setBackgroundResource(0)
//        }
//
//        // Click listener for the item
//        holder.itemView.setOnClickListener {
//            // Update the selected item position
//            selectedItemPosition = holder.adapterPosition
//
//            // Notify the adapter that data has changed, triggering a rebind of items
//            notifyDataSetChanged()
//
//            // Perform your other click actions if needed
//            Toast.makeText(context, "nfnjrnfjwe", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return languages.size
//    }
//
//    // ViewHolder for each item in the RecyclerView
//    class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val languageName: TextView = itemView.findViewById(R.id.languageName)
//    }
//}




