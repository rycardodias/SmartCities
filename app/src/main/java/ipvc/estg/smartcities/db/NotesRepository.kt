package ipvc.estg.smartcities.db

import androidx.lifecycle.LiveData
import ipvc.estg.smartcities.dao.NotesDao
import ipvc.estg.smartcities.entities.Notes

class NotesRepository(private val notesDao: NotesDao) {
    val allNotes: LiveData<List<Notes>> = notesDao.getAll()

    suspend fun insert(notes: Notes) {
        notesDao.insert(notes)
    }

    suspend fun deleteById(id: Int) {
        notesDao.deleteById(id)
    }

//    suspend fun editById(id: Int) {
//        notesDao.editById(id)
//    }


}