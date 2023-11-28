package com.example.storyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(private val languageArray: MutableList<LanguageRequest>) : RecyclerView.Adapter<LanguageViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_value, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        return holder.bindView(languageArray[position])
    }

    override fun getItemCount(): Int {
        return languageArray.size
    }
}


class LanguageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    private val languageTextView: TextView = itemView.findViewById(R.id.textViewValue)

    fun bindView(languageArray: LanguageRequest){
        languageTextView.text = languageArray.name
    }
}

