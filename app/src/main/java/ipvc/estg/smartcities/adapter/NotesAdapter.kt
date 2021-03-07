package ipvc.estg.smartcities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.smartcities.entities.Notes
import ipvc.estg.smartcities.R

class NotesAdapter internal constructor(
    context: Context,
    private val listener: onItemClickListener
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Notes>()

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val description: TextView = itemView.findViewById(R.id.tv_description)
        val date: TextView = itemView.findViewById(R.id.tv_date)

        init {
            itemView.setOnClickListener(this)
        }

        // metodo do click no item da lista
        override fun onClick(v: View?) {
            val position = adapterPosition
            val title = notes[position].title.toString()
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, title)
            }
        }
    }

    interface onItemClickListener {
        fun onItemClick(position: Int, title: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerline, parent, false)
        return NotesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val current = notes[position]
        holder.title.text = current.title
        holder.description.text = current.description
        holder.date.text = current.date.toString()
    }

    internal fun setNotes(notes: List<Notes>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

}

