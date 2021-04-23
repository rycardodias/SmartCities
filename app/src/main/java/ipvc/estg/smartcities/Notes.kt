package ipvc.estg.smartcities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.smartcities.adapter.NotesAdapter
import ipvc.estg.smartcities.entities.Notes
import ipvc.estg.smartcities.viewModel.NotesViewModel
import java.util.*


class Notes : AppCompatActivity(), NotesAdapter.onItemClickListener {
    private lateinit var notesViewModel: NotesViewModel
    private val addNotesRequest = 1
    private val editNotesRequest = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NotesAdapter(this, this)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(recyclerview.context, DividerItemDecoration.VERTICAL))

        //view model
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        notesViewModel.allNotes.observe(this, { notes -> notes?.let { adapter.setNotes(it) } })

        //fab
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@Notes, AddNotes::class.java)
            startActivityForResult(intent, addNotesRequest)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val id = data?.getIntExtra(AddNotes.ID, 0)
            val title = data?.getStringExtra(AddNotes.TITLE)
            val description = data?.getStringExtra(AddNotes.DESCRIPTION)
            val date = data?.getStringExtra(AddNotes.DATE)

            if (requestCode == addNotesRequest) {
                val notes = Notes(title = title, description = description, date = date)
                notesViewModel.insert(notes)

            } else if (requestCode == editNotesRequest) {
                val notes = Notes(id = id, title = title, description = description, date = date)
                notesViewModel.update(notes)
            }
        } else {
            Toast.makeText(applicationContext, getString(R.string.field_is_empty), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditClick(id: Int, title: String, description: String) {
        val intent = Intent(this@Notes, AddNotes::class.java)
        intent.putExtra("ID", id)
        intent.putExtra("TITLE", title)
        intent.putExtra("DESCRIPTION", description)
        startActivityForResult(intent, editNotesRequest)
    }

    override fun onDeleteClick(id: Int) {
        notesViewModel.deleteById(id)
    }

    // MENU DE OPÇOES
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.LoginData), Context.MODE_PRIVATE)
        if (sharedPreferences.getInt("id", 0) == 0) {
            menu!!.findItem(R.id.loginMenu).setVisible(true)
        } else {
            menu!!.findItem(R.id.mapMenu).setVisible(true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.loginMenu -> {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.mapMenu -> {
                val intent = Intent(this, Maps::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}