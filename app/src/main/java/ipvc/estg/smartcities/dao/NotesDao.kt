package ipvc.estg.smartcities.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ipvc.estg.smartcities.entities.Notes

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes_table")
    fun getAll(): LiveData<List<Notes>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notes: Notes)

    @Query("DELETE FROM notes_table")
    suspend fun deleteAll()

}