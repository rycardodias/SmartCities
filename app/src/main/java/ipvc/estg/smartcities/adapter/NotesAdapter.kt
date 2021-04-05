package ipvc.estg.smartcities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.smartcities.R
import ipvc.estg.smartcities.entities.Notes


class NotesAdapter internal constructor(
    context: Context, private val listener: onItemClickListener
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Notes>()

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val description: TextView = itemView.findViewById(R.id.tv_description)
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val editButton: ImageButton = itemView.findViewById(R.id.bt_edit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.cm_delete)

        init {
            itemView.setOnClickListener(this)
        }

        // metodo do click no item da lista
        override fun onClick(v: View?) {
            val position = adapterPosition
            val id = notes[position].id!!;
            val title = notes[position].title.toString()
//            if (position != RecyclerView.NO_POSITION) {
//                listener.onItemClick(position, id, title)
//            }
        }
    }

    interface onItemClickListener {
        fun onEditClick(id: Int, title: String, description: String)
        fun onDeleteClick(id: Int)
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
        holder.editButton.setOnClickListener {
            listener.onEditClick(current.id!!, current.title!!, current.description!!)
        }
        holder.deleteButton.setOnClickListener {
            listener.onDeleteClick(current.id!!)
        }
    }

    internal fun setNotes(notes: List<Notes>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

}

