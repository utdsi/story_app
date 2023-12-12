package com.example.storyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(val context: Context,private val languageArray: MutableList<LanguageRequest>) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_value, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
         holder.languageTextView.text = languageArray[position].name
        holder.itemView.setOnClickListener {
            Toast.makeText(context,languageArray[position].name,Toast.LENGTH_SHORT).show()
            val sharedPreferences = context.getSharedPreferences("language", AppCompatActivity.MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()

            // write all the data entered by the user in SharedPreference and apply
            myEdit.putString("language_id", languageArray[position].languages_id.toString())

            myEdit.apply()
        }
    }

    override fun getItemCount(): Int {
        return languageArray.size
    }

    class LanguageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val languageTextView: TextView = itemView.findViewById(R.id.textViewValue)

    }
}




