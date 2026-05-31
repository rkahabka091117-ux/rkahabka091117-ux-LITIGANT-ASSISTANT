package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LegalCase::class,
        MisconductEvent::class,
        CaseDocument::class,
        TaskReminder::class,
        DocumentChecklist::class,
        ChronologicalFact::class,
        StickyNote::class,
        CaseExpense::class,
        AiChatThread::class
    ],
    version = 5,
    exportSchema = false
)
abstract class CaseDatabase : RoomDatabase() {
    abstract val caseDao: CaseDao

    companion object {
        @Volatile
        private var INSTANCE: CaseDatabase? = null

        fun getInstance(context: Context): CaseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CaseDatabase::class.java,
                    "case_vault_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
