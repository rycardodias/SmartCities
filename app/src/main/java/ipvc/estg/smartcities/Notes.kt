package ipvc.estg.smartcities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.smartcities.adapter.NotesAdapter
import ipvc.estg.smartcities.entities.Notes
import ipvc.estg.smartcities.viewModel.NotesViewModel
import java.util.*

class Notes : AppCompatActivity(), NotesAdapter.onItemClickListener {
    private lateinit var notesViewModel: NotesViewModel
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)


        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NotesAdapter(this,this)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)


        //view model
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        notesViewModel.allNotes.observe(this, {notes -> notes?.let { adapter.setNotes(it) }})

        //fab
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            val intent = Intent(this@Notes, AddNotes::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode== newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val title = data?.getStringExtra(AddNotes.TITLE)
            val description = data?.getStringExtra(AddNotes.DESCRIPTION)
            val date = data?.getStringExtra(AddNotes.DATE)

            val notes = Notes(title = title, description = description, date = date)
            notesViewModel.insert(notes)
        } else {
            Toast.makeText(applicationContext, getString(R.string.field_is_empty), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(position: Int, title: String) {
        Toast.makeText(this, position.toString() + " - " + title, Toast.LENGTH_SHORT).show()
    }

}