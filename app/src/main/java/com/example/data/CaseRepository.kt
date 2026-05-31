package com.example.data

import kotlinx.coroutines.flow.Flow

class CaseRepository(private val caseDao: CaseDao) {

    // --- Legal Cases ---
    val allCases: Flow<List<LegalCase>> = caseDao.getAllCases()

    suspend fun getCaseById(caseId: Int): LegalCase? = caseDao.getCaseById(caseId)

    suspend fun insertCase(legalCase: LegalCase): Long = caseDao.insertCase(legalCase)

    suspend fun updateCase(legalCase: LegalCase) = caseDao.updateCase(legalCase)

    suspend fun deleteCase(legalCase: LegalCase) = caseDao.deleteCase(legalCase)

    // --- Misconduct Events ---
    val allMisconductEvents: Flow<List<MisconductEvent>> = caseDao.getAllMisconductEvents()

    fun getMisconductByCase(caseId: Int): Flow<List<MisconductEvent>> = caseDao.getMisconductByCase(caseId)

    suspend fun insertMisconduct(event: MisconductEvent): Long = caseDao.insertMisconduct(event)

    suspend fun deleteMisconduct(event: MisconductEvent) = caseDao.deleteMisconduct(event)

    // --- Case Documents ---
    fun getDocumentsByCase(caseId: Int): Flow<List<CaseDocument>> = caseDao.getDocumentsByCase(caseId)

    suspend fun insertDocument(doc: CaseDocument): Long = caseDao.insertDocument(doc)

    suspend fun deleteDocument(doc: CaseDocument) = caseDao.deleteDocument(doc)

    // --- Task Reminders ---
    fun getRemindersByCase(caseId: Int): Flow<List<TaskReminder>> = caseDao.getRemindersByCase(caseId)

    suspend fun insertReminder(reminder: TaskReminder): Long = caseDao.insertReminder(reminder)

    suspend fun updateReminder(reminder: TaskReminder) = caseDao.updateReminder(reminder)

    suspend fun deleteReminder(reminder: TaskReminder) = caseDao.deleteReminder(reminder)

    // --- Document Checklists ---
    fun getChecklistsByCase(caseId: Int): Flow<List<DocumentChecklist>> = caseDao.getChecklistsByCase(caseId)

    suspend fun insertChecklist(checklist: DocumentChecklist): Long = caseDao.insertChecklist(checklist)

    suspend fun updateChecklist(checklist: DocumentChecklist) = caseDao.updateChecklist(checklist)

    suspend fun deleteChecklistById(id: Int) = caseDao.deleteChecklistById(id)

    // --- Chronological Facts ---
    fun getFactsByCase(caseId: Int): Flow<List<ChronologicalFact>> = caseDao.getFactsByCase(caseId)

    suspend fun insertFact(fact: ChronologicalFact): Long = caseDao.insertFact(fact)

    suspend fun deleteFact(fact: ChronologicalFact) = caseDao.deleteFact(fact)

    // --- Sticky Notes ---
    fun getStickyNotesByCase(caseId: Int): Flow<List<StickyNote>> = caseDao.getStickyNotesByCase(caseId)

    suspend fun insertStickyNote(note: StickyNote): Long = caseDao.insertStickyNote(note)

    suspend fun updateStickyNote(note: StickyNote) = caseDao.updateStickyNote(note)

    suspend fun deleteStickyNote(note: StickyNote) = caseDao.deleteStickyNote(note)

    // --- AI Chat Threads ---
    fun getChatThreadsByCase(caseId: Int): Flow<List<AiChatThread>> = caseDao.getChatThreadsByCase(caseId)

    suspend fun insertChatThread(thread: AiChatThread): Long = caseDao.insertChatThread(thread)

    suspend fun deleteChatThread(thread: AiChatThread) = caseDao.deleteChatThread(thread)

    // --- Case Expenses & Damage Ledger ---
    fun getExpensesByCase(caseId: Int): Flow<List<CaseExpense>> = caseDao.getExpensesByCase(caseId)

    suspend fun insertExpense(expense: CaseExpense): Long = caseDao.insertExpense(expense)

    suspend fun deleteExpense(expense: CaseExpense) = caseDao.deleteExpense(expense)
}
