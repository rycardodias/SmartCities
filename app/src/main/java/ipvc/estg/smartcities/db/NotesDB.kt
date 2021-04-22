package ipvc.estg.smartcities.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ipvc.estg.smartcities.dao.NotesDao
import ipvc.estg.smartcities.entities.Notes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*


@Database(entities = arrayOf(Notes::class), version = 1)
abstract class NotesDB : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    private class WordDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var notesDao = database.notesDao()

//                    notesDao.deleteAll()
//
                    var notes = Notes(1, "Reunião PGM", "Reuniao de mobile as 16h", Date().toString())
                    notesDao.insert(notes)
                    notes = Notes(2, "Relatorio Projeto", "Enviar relatório dia 26/03", Date().toString())
                    notesDao.insert(notes)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NotesDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotesDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            // criacao da base de dados
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDB::class.java,
                    "notes_database"
                )
                    //destruicao
//                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}