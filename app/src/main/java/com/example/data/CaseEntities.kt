package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "legal_cases")
data class LegalCase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val docketNumber: String,
    val opposingParty: String,
    val caseNotes: String = "",
    val externalIntegrations: String = "" // Holds consolidated raw content imported from Claude, ChatGPT, etc.
)

@Entity(tableName = "misconduct_events")
data class MisconductEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val employeeName: String,
    val agencyOrContractor: String, // e.g., "CPS", "MDHHS", "SCAO", "Visit Supervisor", "Private Foster Agency", "Medical Provider"
    val violationDate: String,
    val policyViolated: String,
    val constitutionalRightViolated: String, // e.g. "First", "Fourth", "Fourteenth", "Policy Only"
    val evidenceDescription: String,
    val isAnonymous: Boolean = false // Anonymous submission to Victim's Voice
)

@Entity(tableName = "case_documents")
data class CaseDocument(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val title: String,
    val docType: String, // e.g., "SCAO Form", "Motion Response", "One-Pager Summarizer", "Custom Filing"
    val content: String,
    val isFilingComplete: Boolean = false,
    val serviceDetails: String = "", // Service tracking and confirmation
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_reminders")
data class TaskReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val title: String,
    val dueDate: String,
    val isCompleted: Boolean = false,
    val reminderTiming: String = "1 Day Before", // e.g., "2 Hours Before", "12 Hours Before", "1 Day Before", "3 Days Before"
    val repetition: String = "Once",            // e.g., "Once", "Daily", "Weekly", "Progressive Alarm Loop"
    val notificationSound: String = "Calm Chime",// e.g., "Calm Chime", "Gentle Synth", "Vibration Pulse", "Silence"
    val customAction: String = "None",           // e.g., "None", "Email Self Copy", "Block Calendar Buffer Hours", "Priority Focus Overlay"
    val associatedDeviceCalendars: String = "Google Calendar,Outlook" // Comma-separated synced calendars list
)

@Entity(tableName = "document_checklists")
data class DocumentChecklist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val itemName: String,
    val isRequired: Boolean = true,
    val isAttached: Boolean = false
)

@Entity(tableName = "chronological_facts")
data class ChronologicalFact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val factDate: String,         // e.g. "2026-05-30" or "05/30/2026 14:30"
    val description: String,      // Sequence of occurred fact
    val actorsInvolved: String,   // Who made the statement/action
    val violatedPolicy: String,   // Contradicted MDHHS/SCAO policy section
    val supportingEvidence: String, // e.g., "Exhibit A (Audio Transcripts)"
    val category: String = "Note", // "Note", "Photo", "Psychiatry", "Class Attendance", "Email", "Call/Text Log", "Document"
    val associatedDocsJson: String = "" // JSON list of sub-folder items: [{"id": "...", "type": "evidence|notes|drafts|documents", "title": "...", "content": "..."}]
)

@Entity(tableName = "sticky_notes")
data class StickyNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val title: String,
    val content: String,
    val colorHex: String,         // e.g. "Amber", "Cyan", "Pink", "Emerald", "Lavender"
    val isPinned: Boolean = false,
    val noteStyle: String = "Sticky Note", // Comma-separated combination: "PIN,HIGHLIGHTER,STICKY,CITATION,CHECKLIST"
    val fontSize: Int = 11,
    val fontFamily: String = "Monospace",
    val checklistItemsJson: String = "[]" // optional checklist
)

@Entity(tableName = "ai_chat_threads")
data class AiChatThread(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val sessionName: String,
    val fullThreadJson: String, // List of messages [{"role": "user|assistant", "text": "...", "timestamp": "..."}]
    val timestamp: Long = System.currentTimeMillis(),
    val reminderDate: String = "" // Chats should trigger reminders as well
)

@Entity(tableName = "case_expenses")
data class CaseExpense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val category: String,         // e.g., "Filing Fee", "Process Server", "Printing/Ink", "Lost Wages", "Gas/Travel", "Emotional/Health Cost", "Tortious Interference Loss"
    val date: String,             // e.g. "05/30/2026"
    val amount: Double,
    val description: String,
    val receiptReference: String  // e.g., "Receipt PDF / Exhibit C"
)


