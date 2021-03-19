package ipvc.estg.smartcities.viewModel

import android.R.attr.data
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import ipvc.estg.smartcities.db.NotesDB
import ipvc.estg.smartcities.db.NotesRepository
import ipvc.estg.smartcities.entities.Notes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotesViewModel(application: Application): AndroidViewModel(application) {
    private val repository: NotesRepository
    val allNotes: LiveData<List<Notes>>

    init {
        val notesDao = NotesDB.getDatabase(application, viewModelScope).notesDao()
        repository = NotesRepository(notesDao)
        allNotes = repository.allNotes
    }

    fun insert(notes: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(notes)
    }

    fun deleteById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteById(id)
    }

//    fun editById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
//        repository.editById(id)
//    }

}