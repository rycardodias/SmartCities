package ipvc.estg.smartcities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.smartcities.R
import ipvc.estg.smartcities.dataclasses.Place

class LineAdapter (val list: ArrayList<Place>):RecyclerView.Adapter<LineViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerline, parent, false)
        return LineViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {

        val currentPlace = list[position]
        holder.title.text = currentPlace.title
        holder.description.text = currentPlace.description
    }

}

class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.tv_title)
    val description: TextView = itemView.findViewById(R.id.tv_description)
}