package com.example.why1.appdata

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.why1.DetailActivity
import com.example.why1.R

class S_adapter(private val items: List<S_data>) : RecyclerView.Adapter<S_adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val date: TextView = view.findViewById(R.id.date)
        val content: TextView = view.findViewById(R.id.content)
        val detailButton: Button = view.findViewById(R.id.detailButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.date.text = item.date
        holder.content.text = item.content
        holder.detailButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("title", item.title)
                putExtra("date", item.date)
                putExtra("content", item.content)
                putExtra("details", item.details)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}