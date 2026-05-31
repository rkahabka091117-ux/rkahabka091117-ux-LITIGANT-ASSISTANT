package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {

    // --- Legal Cases ---
    @Query("SELECT * FROM legal_cases ORDER BY id DESC")
    fun getAllCases(): Flow<List<LegalCase>>

    @Query("SELECT * FROM legal_cases WHERE id = :caseId LIMIT 1")
    suspend fun getCaseById(caseId: Int): LegalCase?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(legalCase: LegalCase): Long

    @Update
    suspend fun updateCase(legalCase: LegalCase)

    @Delete
    suspend fun deleteCase(legalCase: LegalCase)

    // --- Misconduct Events ---
    @Query("SELECT * FROM misconduct_events ORDER BY id DESC")
    fun getAllMisconductEvents(): Flow<List<MisconductEvent>>

    @Query("SELECT * FROM misconduct_events WHERE caseId = :caseId ORDER BY id DESC")
    fun getMisconductByCase(caseId: Int): Flow<List<MisconductEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMisconduct(event: MisconductEvent): Long

    @Delete
    suspend fun deleteMisconduct(event: MisconductEvent)

    // --- Case Documents ---
    @Query("SELECT * FROM case_documents WHERE caseId = :caseId ORDER BY timestamp DESC")
    fun getDocumentsByCase(caseId: Int): Flow<List<CaseDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: CaseDocument): Long

    @Delete
    suspend fun deleteDocument(doc: CaseDocument)

    // --- Task Reminders ---
    @Query("SELECT * FROM task_reminders WHERE caseId = :caseId ORDER BY isCompleted ASC, id DESC")
    fun getRemindersByCase(caseId: Int): Flow<List<TaskReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: TaskReminder): Long

    @Update
    suspend fun updateReminder(reminder: TaskReminder)

    @Delete
    suspend fun deleteReminder(reminder: TaskReminder)

    // --- Document Checklists ---
    @Query("SELECT * FROM document_checklists WHERE caseId = :caseId ORDER BY id ASC")
    fun getChecklistsByCase(caseId: Int): Flow<List<DocumentChecklist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: DocumentChecklist): Long

    @Update
    suspend fun updateChecklist(checklist: DocumentChecklist)

    @Query("DELETE FROM document_checklists WHERE id = :id")
    suspend fun deleteChecklistById(id: Int)

    // --- Chronological Facts Timeline ---
    @Query("SELECT * FROM chronological_facts WHERE caseId = :caseId ORDER BY factDate ASC, id ASC")
    fun getFactsByCase(caseId: Int): Flow<List<ChronologicalFact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFact(fact: ChronologicalFact): Long

    @Delete
    suspend fun deleteFact(fact: ChronologicalFact)

    // --- Sticky Notes ---
    @Query("SELECT * FROM sticky_notes WHERE caseId = :caseId ORDER BY isPinned DESC, id DESC")
    fun getStickyNotesByCase(caseId: Int): Flow<List<StickyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStickyNote(note: StickyNote): Long

    @Update
    suspend fun updateStickyNote(note: StickyNote)

    @Delete
    suspend fun deleteStickyNote(note: StickyNote)

    // --- AI Chat Threads ---
    @Query("SELECT * FROM ai_chat_threads WHERE caseId = :caseId ORDER BY timestamp DESC")
    fun getChatThreadsByCase(caseId: Int): Flow<List<AiChatThread>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatThread(thread: AiChatThread): Long

    @Delete
    suspend fun deleteChatThread(thread: AiChatThread)

    // --- Case Expenses & Damage Ledger ---
    @Query("SELECT * FROM case_expenses WHERE caseId = :caseId ORDER BY id DESC")
    fun getExpensesByCase(caseId: Int): Flow<List<CaseExpense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: CaseExpense): Long

    @Delete
    suspend fun deleteExpense(expense: CaseExpense)
}
