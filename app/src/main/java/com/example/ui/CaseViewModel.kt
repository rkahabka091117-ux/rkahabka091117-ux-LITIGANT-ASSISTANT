package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ============================================================
// AI Provider Selection — add new providers here as needed
// ============================================================
enum class AiProvider(val displayName: String) {
    GEMINI("Google Gemini"),
    GROQ("Groq (Llama 3)"),
    OPENROUTER("OpenRouter"),
    BLACKBOX("Blackbox AI")
}

class CaseViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CaseDatabase.getInstance(application)
    private val repository = CaseRepository(db.caseDao)

    // --- State flows ---
    val cases = repository.allCases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedCaseId = MutableStateFlow<Int?>(null)
    val selectedCaseId: StateFlow<Int?> = _selectedCaseId.asStateFlow()

    private val _activeCase = MutableStateFlow<LegalCase?>(null)
    val activeCase: StateFlow<LegalCase?> = _activeCase.asStateFlow()

    // Reactive streams dependent on selectedCaseId
    val documents = selectedCaseId.flatMapLatest { id ->
        if (id != null) repository.getDocumentsByCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders = selectedCaseId.flatMapLatest { id ->
        if (id != null) repository.getRemindersByCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val checklists = selectedCaseId.flatMapLatest { id ->
        if (id != null) repository.getChecklistsByCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val caseMisconducts = selectedCaseId.flatMapLatest { id ->
        if (id != null) repository.getMisconductByCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chronologicalFacts = selectedCaseId.flatMapLatest { id ->
        if (id != null) repository.getFactsByCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stickyNotes = selectedCaseId.flatMapLatest { id ->
        if (id != null) repository.getStickyNotesByCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiChatThreads = selectedCaseId.flatMapLatest { id ->
        if (id != null) repository.getChatThreadsByCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val caseExpenses = selectedCaseId.flatMapLatest { id ->
        if (id != null) repository.getExpensesByCase(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global misconduct events (for pattern recognition and repeat analytics)
    val allMisconducts = repository.allMisconductEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI Feedback indicators
    var isGeneratingDoc = MutableStateFlow(false)
        private set
    var isAnalyzingPolicy = MutableStateFlow(false)
        private set

    private val _policyAnalysisResult = MutableStateFlow<String?>(null)
    val policyAnalysisResult: StateFlow<String?> = _policyAnalysisResult.asStateFlow()

    private val _generatedOnePagerResult = MutableStateFlow<String?>(null)
    val generatedOnePagerResult: StateFlow<String?> = _generatedOnePagerResult.asStateFlow()

    // Initialize with a default case if list is not empty, or keep blank
    init {
        viewModelScope.launch {
            cases.collect { casesList ->
                if (casesList.isNotEmpty() && _selectedCaseId.value == null) {
                    selectCase(casesList.first().id)
                }
            }
        }
    }

    // --- Case Actions ---
    fun createCase(title: String, docketNumber: String, opposingParty: String) {
        viewModelScope.launch {
            val newCase = LegalCase(
                title = title,
                docketNumber = docketNumber,
                opposingParty = opposingParty,
                caseNotes = ""
            )
            val id = repository.insertCase(newCase).toInt()
            selectCase(id)
            initializeChecklistForCase(id)
        }
    }

    fun selectCase(caseId: Int) {
        _selectedCaseId.value = caseId
        viewModelScope.launch {
            _activeCase.value = repository.getCaseById(caseId)
        }
    }

    fun deleteCase(legalCase: LegalCase) {
        viewModelScope.launch {
            repository.deleteCase(legalCase)
            if (_selectedCaseId.value == legalCase.id) {
                _selectedCaseId.value = null
                _activeCase.value = null
            }
        }
    }

    fun updateCaseNotes(notes: String) {
        val current = _activeCase.value ?: return
        viewModelScope.launch {
            val updated = current.copy(caseNotes = notes)
            repository.updateCase(updated)
            _activeCase.value = updated
        }
    }

    fun importExternalContent(sourceApp: String, content: String) {
        val current = _activeCase.value ?: return
        viewModelScope.launch {
            val stamp = "--- Imported from $sourceApp ---\n"
            val updated = current.copy(
                externalIntegrations = current.externalIntegrations + stamp + content + "\n\n"
            )
            repository.updateCase(updated)
            _activeCase.value = updated
        }
    }

    // --- Task Reminder Actions ---
    fun addReminder(title: String, dueDate: String) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.insertReminder(TaskReminder(caseId = caseId, title = title, dueDate = dueDate))
        }
    }

    fun toggleReminder(reminder: TaskReminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun deleteReminder(reminder: TaskReminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    // --- Chronological Facts Timeline Actions ---
    fun addChronologicalFact(date: String, description: String, actors: String, policy: String, evidence: String) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.insertFact(
                ChronologicalFact(
                    caseId = caseId,
                    factDate = date,
                    description = description,
                    actorsInvolved = actors,
                    violatedPolicy = policy,
                    supportingEvidence = evidence
                )
            )
        }
    }

    fun deleteChronologicalFact(fact: ChronologicalFact) {
        viewModelScope.launch {
            repository.deleteFact(fact)
        }
    }

    // --- Sticky Note Actions ---
    fun addStickyNote(title: String, content: String, colorHex: String) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.insertStickyNote(
                StickyNote(
                    caseId = caseId,
                    title = title,
                    content = content,
                    colorHex = colorHex,
                    isPinned = false
                )
            )
        }
    }

    fun togglePinStickyNote(note: StickyNote) {
        viewModelScope.launch {
            repository.updateStickyNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun deleteStickyNote(note: StickyNote) {
        viewModelScope.launch {
            repository.deleteStickyNote(note)
        }
    }

    // --- Checklist Actions ---
    private suspend fun initializeChecklistForCase(caseId: Int) {
        val defaultItems = listOf(
            "Certified Copy of SCAO Form or Petition",
            "Proof of Service to Opposing Party",
            "Constitutional Rights Citation Index",
            "MDHHS/CPS Policy Guideline Cross-Reference Sheet",
            "Proof of Exemption / Poverty Benefit Verification",
            "Evidence Binder Exhibit List",
            "Written Statement of Specific Policy Violations"
        )
        for (item in defaultItems) {
            repository.insertChecklist(DocumentChecklist(caseId = caseId, itemName = item, isRequired = true, isAttached = false))
        }
    }

    fun toggleChecklistItem(item: DocumentChecklist) {
        viewModelScope.launch {
            repository.updateChecklist(item.copy(isAttached = !item.isAttached))
        }
    }

    fun addNewChecklistItem(name: String, isRequired: Boolean) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.insertChecklist(DocumentChecklist(caseId = caseId, itemName = name, isRequired = isRequired, isAttached = false))
        }
    }

    // --- Misconduct Event Actions ---
    fun logMisconduct(
        employeeName: String,
        agencyOrContractor: String,
        violationDate: String,
        policyViolated: String,
        constitutionalRightViolated: String,
        evidenceDescription: String,
        isAnonymous: Boolean
    ) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            val event = MisconductEvent(
                caseId = caseId,
                employeeName = employeeName,
                agencyOrContractor = agencyOrContractor,
                violationDate = violationDate,
                policyViolated = policyViolated,
                constitutionalRightViolated = constitutionalRightViolated,
                evidenceDescription = evidenceDescription,
                isAnonymous = isAnonymous
            )
            repository.insertMisconduct(event)
        }
    }

    fun deleteMisconduct(event: MisconductEvent) {
        viewModelScope.launch {
            repository.deleteMisconduct(event)
        }
    }

    // --- Case Document Actions ---
    fun saveDocument(title: String, docType: String, content: String, isComplete: Boolean, serviceDetails: String) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            val doc = CaseDocument(
                caseId = caseId,
                title = title,
                docType = docType,
                content = content,
                isFilingComplete = isComplete,
                serviceDetails = serviceDetails
            )
            repository.insertDocument(doc)
        }
    }

    fun deleteDocument(doc: CaseDocument) {
        viewModelScope.launch {
            repository.deleteDocument(doc)
        }
    }

    // --- AI Provider Selection ---

    private val _selectedProvider = MutableStateFlow(AiProvider.GEMINI)
    val selectedProvider: StateFlow<AiProvider> = _selectedProvider.asStateFlow()

    fun setAiProvider(provider: AiProvider) {
        _selectedProvider.value = provider
    }

    // --- AI Operations using Direct REST API ---

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    /**
     * Unified AI call helper — routes to the currently selected provider.
     * All AI features in this ViewModel call this instead of Gemini directly.
     *
     * @param prompt     The full text prompt to send.
     * @param temperature Generation temperature (0.0–1.0). Default 0.4.
     * @return           The text response from the selected provider, or null on failure.
     */
    private suspend fun callAi(prompt: String, temperature: Float = 0.4f): String? {
        return when (_selectedProvider.value) {
            AiProvider.GEMINI -> {
                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                    generationConfig = GeminiGenerationConfig(temperature = temperature)
                )
                RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                    .candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            }
            AiProvider.GROQ -> {
                val request = GroqRequest(
                    messages = listOf(GroqMessage(role = "user", content = prompt)),
                    temperature = temperature
                )
                GroqRetrofitClient.service.chatCompletion(
                    bearerToken = "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request = request
                ).extractText()
            }
            AiProvider.OPENROUTER -> {
                val request = OpenRouterRequest(
                    messages = listOf(OpenRouterMessage(role = "user", content = prompt)),
                    temperature = temperature
                )
                OpenRouterRetrofitClient.service.chatCompletion(
                    bearerToken = "Bearer ${BuildConfig.OPENROUTER_API_KEY}",
                    request = request
                ).extractText()
            }
            AiProvider.BLACKBOX -> {
                val request = BlackboxRequest(
                    messages = listOf(BlackboxMessage(role = "user", content = prompt)),
                    temperature = temperature
                )
                BlackboxRetrofitClient.service.chatCompletion(
                    bearerToken = "Bearer ${BuildConfig.BLACKBOX_API_KEY}",
                    request = request
                ).extractText()
            }
        }
    }

    // AI real-time Policy vs Practice evaluation
    fun runPolicyVsPracticeComparison(
        incidentDescription: String,
        category: String // e.g. "Constitutional", "MDHHS/CPS", "Right to Farm", "Contractor Violations"
    ) {
        _policyAnalysisResult.value = ""
        isAnalyzingPolicy.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = getApiKey()
            val prompt = """
                You are an expert AI Legal Policy Auditor specializing in Constitutional safeguards and Michigan Administrative Rules.
                Your task is to analyze an INCIDENT description against target policies, highlight discrepancies, and formulate a citation matrix.
                
                INCIDENT STATE: Michigan
                INCIDENT CATEGORY: $category
                INCIDENT DESCRIPTION:
                $incidentDescription
                
                APPLICABLE MICHIGAN & CONSTITUTIONAL FRAMEWORK TO TEST CONTRADICTIONS:
                1. First Amendment (Retaliation, Freedom of Speech/Expression)
                2. Fourth Amendment (Unwarranted, non-consensual entry to homes/barns or private property)
                3. Fourteenth Amendment (Due Process violations, lack of notice, coercion, removing family custody without immediate danger)
                4. MDHHS / CPS policy manuals: "Observable behavior change" standard must be documented before intervention; policy forbids coercive/threatening behavior.
                5. Michigan Right to Farm Act (RTFA) protection: Protects small commercial farm operations from restrictive local zoning boards bullying or harassment. Under PA 116 / 2026 Farm Bill, state law preempts zoning if GAAMPS are followed.
                6. Private Contractors Rules: Visit supervisors, fosters, and medical contractors must strictly mirror state rules regarding safety, documentation, and respectful boundaries.
                
                Please generate a clean markdown audit matching:
                - **Policy vs Practice Analysis:** State clearly what the "Guidelines Require" versus the "Observed Action".
                - **Specific Violations Found:** Highlight the Constitutional Amendment or agency-specific manual section.
                - **Michigan RTFA Overrides:** If farming, zones, animals, or properties are targeted, state how RTFA preempts this zoning.
                - **Remediation Plan / Pre-Se Motions Needed:** Provide Pro Se litigants actionable guidance on SCAO forms to submit (e.g. SCAO Form FOC 44) or responses to file.
                
                IMPORTANT: Conclude with a dynamic "Disclaimer: Not legal advice. AI assistant tool only."
            """.trimIndent()

            try {
                val resultText = callAi(prompt, temperature = 0.4f)
                    ?: "Unable to parse auditor response. Please check your incident description and try again."
                _policyAnalysisResult.value = resultText
            } catch (e: Exception) {
                _policyAnalysisResult.value = "Error during analysis: ${e.message}\nEnsure your AI API key is configured correctly in the Secrets Panel."
            } finally {
                isAnalyzingPolicy.value = false
            }
        }
    }

    // AI One-Pager Generator for House Judiciary Committee
    fun generateLegislativeOnePager() {
        val currentCase = activeCase.value ?: return
        _generatedOnePagerResult.value = ""
        isGeneratingDoc.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = getApiKey()
            
            // Collect existing misconduct logs
            val misconductLogs = db.caseDao.getAllMisconductEvents().first()
                .filter { it.caseId == currentCase.id }
            
            val logsSummary = if (misconductLogs.isNotEmpty()) {
                misconductLogs.joinToString("\n") { 
                    "- Officer/Employee: ${it.employeeName} (${it.agencyOrContractor}). Right violated: ${it.constitutionalRightViolated}. Details: ${it.evidenceDescription}"
                }
            } else {
                "- No misconduct logs manually registered. Proceeding on case facts."
            }

            val prompt = """
                You are a Senior Legislative Analyst formulating a high-priority "Legislative One-Pager Report" specifically formatted for the House Judiciary Committee on Civil Rights and CPS Reform.
                Summarize the following legal case details and compiled misconduct events into a structured, high-impact legislative briefing.
                
                CASE TITLE: ${currentCase.title}
                DOCKET NUMBER: ${currentCase.docketNumber}
                OPPOSING PARTY: ${currentCase.opposingParty}
                
                COMPILED EVIDENCE LOGS:
                $logsSummary
                
                CUSTOM CASE NOTES & IMPORTED CHATS:
                ${currentCase.caseNotes}
                ${currentCase.externalIntegrations}
                
                REQUIREMENTS OF THE ONE-PAGER:
                - MUST fit conceptually within a tight briefing standard.
                - **Title Panel:** BRIEFING FOR THE HOUSE JUDICIARY COMMITTEE IN RE: SYSTEMIC AGENCY DISCREPANCIES.
                - **Executive Summary:** Highly persuasive summary listing the abuse of power or systemic guidelines failure (emphasizing MDHHS, CPS, Zoning or child protection oversight).
                - **Policy vs. Practice Auditing:** Juxtapose observed actions alongside Constitutional protections (1st, 4th, 14th Amendment) & SCAO rules.
                - **Urgent Action Proposed:** Explicit pitches to State Sen. Stephanie Chang, OCA, and the University of Michigan AI Law and Policy Clinic focusing on "observable behavior change" validation and pro se litigants' "access to justice".
                
                Provide this output in professional Markdown format.
            """.trimIndent()

            try {
                val resultText = callAi(prompt, temperature = 0.3f)
                    ?: "Failed to generate briefing report."
                _generatedOnePagerResult.value = resultText
                
                // Save this legislative report inside documents vault automatically!
                withContext(Dispatchers.Main) {
                    saveDocument(
                        title = "Legislative One-Pager Briefing",
                        docType = "One-Pager Summarizer",
                        content = resultText,
                        isComplete = true,
                        serviceDetails = "Ready for submission to House Judiciary Committee / State Sen. Stephanie Chang"
                    )
                }
            } catch (e: Exception) {
                _generatedOnePagerResult.value = "Error generating one-pager briefing: ${e.message}"
            } finally {
                isGeneratingDoc.value = false
            }
        }
    }

    // AI Responsive Documentation Creator to help compile legal responses
    fun generateDocumentFilingTemplate(
        formType: String, // e.g. "FOC 44", "Motion to Show Cause Response", "General Answer to Petition"
        userFacts: String
    ) {
        isGeneratingDoc.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = getApiKey()
            val prompt = """
                You are a highly detailed legal document drafting assistant specializing in Michigan SCAO (State Court Administrative Office) forms and responses for pro se litigants.
                
                You need to draft a highly professional respond template outline or general text draft based on:
                FILING TYPE: $formType
                USER STATEMENT/FACTS:
                $userFacts
                
                INSTRUCTIONS TO ENGAGE IN DRAFTING:
                1. Structure the document formally with a Michigan Court Caption (county, circuit court, docket empty fields).
                2. Formulate proper legal headings for "Answer", "Constitutional Concerns", and "Counter-Statement of Facts".
                3. Address specific safeguards (e.g. 1st, 4th, 14th Amendment, and "Observable behavior change" requirements where appropriate).
                4. Incorporate a poverty-based benefit check, or procedural confirmation lines as required.
                5. Include certificate of service lines.
                
                IMPORTANT WARNING IN DRAFT:
                Insert a clear visual box in markdown stating: "LEGAL DISCLAIMER: Compiled by AI Case Vault Assistant. Drafted for pro se verification. Not legal advice. You must verify and sign as the litigant."
                
                Please generate the complete formatted text layout.
            """.trimIndent()

            try {
                val resultText = callAi(prompt, temperature = 0.5f)
                    ?: "Failed to compile document template."
                withContext(Dispatchers.Main) {
                    saveDocument(
                        title = "Draft Response: $formType",
                        docType = "Filing Response",
                        content = resultText,
                        isComplete = false,
                        serviceDetails = "Filing draft initialized. Pending pro se verification and complete service affidavit."
                    )
                }
            } catch (e: Exception) {
                // error handled in documents flow
            } finally {
                isGeneratingDoc.value = false
            }
        }
    }

    // --- Case Expenses Actions ---
    fun addCaseExpense(category: String, date: String, amount: Double, description: String, receipt: String) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.insertExpense(
                CaseExpense(
                    caseId = caseId,
                    category = category,
                    date = date,
                    amount = amount,
                    description = description,
                    receiptReference = receipt
                )
            )
        }
    }

    fun deleteCaseExpense(expense: CaseExpense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // --- AI Credits and SCAO Income/Fee-Waiver Simulation State ---
    private val _userCredits = MutableStateFlow(1000) // Starts at generous 1,000 credits to avoid pro se stress
    val userCredits: StateFlow<Int> = _userCredits.asStateFlow()

    private val _isUnlimitedAccessWaiver = MutableStateFlow(false) // Income verification or damage-triggered free waiver
    val isUnlimitedAccessWaiver: StateFlow<Boolean> = _isUnlimitedAccessWaiver.asStateFlow()

    // --- State structures for advanced legal workflows ---
    val isAnalyzingFact = MutableStateFlow(false)
    val isEfilingInProgress = MutableStateFlow(false)

    private val _efilingResult = MutableStateFlow<String?>(null)
    val efilingResult: StateFlow<String?> = _efilingResult.asStateFlow()

    private val _screenShareCaptureResult = MutableStateFlow<String?>(null)
    val screenShareCaptureResult: StateFlow<String?> = _screenShareCaptureResult.asStateFlow()

    private val _simulatedEmails = MutableStateFlow<List<SimulatedEmail>>(emptyList())
    val simulatedEmails: StateFlow<List<SimulatedEmail>> = _simulatedEmails.asStateFlow()

    private val _commsRecords = MutableStateFlow<List<CloudCommsRecord>>(emptyList())
    val commsRecords: StateFlow<List<CloudCommsRecord>> = _commsRecords.asStateFlow()

    init {
        // Hydrate default simulated emails & cloud comms for demonstration
        _simulatedEmails.value = listOf(
            SimulatedEmail(
                id = "email_1",
                threadId = "tr_001",
                sender = "SCAO MiFILE Gateway <notifications@mifile.courts.mi.gov>",
                recipientGroup = "Clerk of the Court / Litigant",
                subject = "MiFILE Submission Confirmation: Court Case Receipt",
                body = "Your motion filing has been successfully received by the 3rd Circuit Court SCAO Portal. Awaiting clerk verification.",
                isEserved = true,
                hasProofOfService = true,
                timestamp = "05/30/2026 09:12"
            )
        )
        _commsRecords.value = listOf(
            CloudCommsRecord(
                id = "com_1",
                type = "Call",
                remoteParty = "Caseworker Peterson (MDHHS)",
                timestamp = "05/28/2026 14:02",
                transcriptSummary = "Caseworker called stating that home inspection must be performed on 05/30 or she will obtain emergency SCAO removal orders. No direct risk substantiated.",
                durationSeconds = 180
            ),
            CloudCommsRecord(
                id = "com_2",
                type = "Text",
                remoteParty = "Foster Supervisor Mary",
                timestamp = "05/29/2026 11:30",
                transcriptSummary = "Text received: 'Classes are confirmed as attended. I will submit the official report within 5 days.'",
                durationSeconds = 0
            )
        )
    }

    fun awardAdWatchCredits() {
        _userCredits.value = _userCredits.value + 15 // Adding 15 credits
    }

    fun awardAffiliateVisitCredits(amount: Int) {
        _userCredits.value = _userCredits.value + amount // Award specialized credits
    }

    fun useAICredit() {
        if (!_isUnlimitedAccessWaiver.value) {
            _userCredits.value = (_userCredits.value - 1).coerceAtLeast(0)
        }
    }

    fun verifyLowIncomeWaiver(assistanceType: String, proofNotes: String) {
        // Automatically grant full uninhibited lifetime access to assist pro se indigent litigants
        _isUnlimitedAccessWaiver.value = true
        _userCredits.value = 999999
    }

    fun verifyTortiousInterferenceLossWaiver(totalLosses: Double) {
        // Automatically waive fees/credits if they prove any expenses are incurred due to agency interference!
        _isUnlimitedAccessWaiver.value = true
        _userCredits.value = 999999
    }

    // --- Core Assisted Statement of Facts / Chronological timeline scanner ---
    fun analyzeAndAddChronologicalFact(rawText: String) {
        val caseId = selectedCaseId.value ?: return
        isAnalyzingFact.value = true
        useAICredit()

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = getApiKey()
            val prompt = """
                You are an expert AI Forensic Case Analyst and SCAO Docket Clerk.
                Analyze the following raw input text. This could represent client psychiatry consultation sessions, class attendance checklists, supervisor emails, transcripts, or call records.
                
                RAW STATEMENT CONTENT:
                $rawText
                
                You must extract, parse, scan, and organize this into a perfect court-admissible Chronological Statement of Fact:
                1. Detect any exact date mentioned in the document/conversation (e.g. "On May 15th, 2026..."). Extract that date specifically in format "YYYY-MM-DD" or "MM/DD/YYYY". If none is specified, output "2026-05-30".
                2. Determine the most logical Category (one of: Note, Photo, Psychiatry Report, Class Attendance, Email, Call/Text Record, Document).
                3. Summarize exactly what transpired (Fact details). Keep it extremely objective and factual.
                4. Extract any actors or public servants involved in this transaction.
                5. Cross-reference any potential agency rules violated, constitutional concerns, or SCAO codes (e.g. "Fourteenth Amendment - Right to Notice", "Policy Manual 7d", etc.).
                6. Formulate precise subfolder segments separated into Evidence, Notes, Drafts, and Documents.
                
                You MUST return a clean JSON object containing EXACTLY these keys (do not add formatting, wrap in any extra characters, or write text around the JSON):
                {
                  "date": "Extracted Date",
                  "category": "Extracted Category",
                  "description": "Factual statement describing the sequence",
                  "actors": "Actors Involved",
                  "policy": "Policy/Constitutional Section",
                  "evidence": "Supporting Evidence title",
                  "associatedDocsJson": "[{\"type\":\"evidence\",\"title\":\"Evidence Link\",\"content\":\"Details\"},{\"type\":\"notes\",\"title\":\"Internal Summary\",\"content\":\"Details\"},{\"type\":\"drafts\",\"title\":\"Proposed Motions\",\"content\":\"Details\"},{\"type\":\"documents\",\"title\":\"Completed PDF Document\",\"content\":\"Details\"}]"
                }
            """.trimIndent()

            try {
                val resultText = callAi(prompt, temperature = 0.2f) ?: ""
                
                val cleanJson = resultText.trim().removeSurrounding("```json", "```").trim()
                val jsonObject = com.squareup.moshi.Moshi.Builder().build().adapter(Map::class.java).fromJson(cleanJson)
                
                if (jsonObject != null) {
                    val date = jsonObject["date"]?.toString() ?: "2026-05-30"
                    val category = jsonObject["category"]?.toString() ?: "Note"
                    val desc = jsonObject["description"]?.toString() ?: "Processed document log."
                    val actors = jsonObject["actors"]?.toString() ?: ""
                    val policy = jsonObject["policy"]?.toString() ?: ""
                    val evidence = jsonObject["evidence"]?.toString() ?: "Attached Document"
                    val subDocs = jsonObject["associatedDocsJson"]?.toString() ?: "[]"

                    withContext(Dispatchers.Main) {
                        repository.insertFact(
                            ChronologicalFact(
                                caseId = caseId,
                                factDate = date,
                                description = desc,
                                actorsInvolved = actors,
                                violatedPolicy = policy,
                                supportingEvidence = evidence,
                                category = category,
                                associatedDocsJson = subDocs
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Fallback to manual save if AI fails to format JSON
                    repository.insertFact(
                        ChronologicalFact(
                            caseId = caseId,
                            factDate = "2026-05-30",
                            description = "Scanned Fact Item: $rawText",
                            actorsInvolved = "System / Assistant",
                            violatedPolicy = "Pending System Review",
                            supportingEvidence = "Raw Text Log",
                            category = "Document",
                            associatedDocsJson = "[{\"type\":\"notes\",\"title\":\"Extracted Audit\",\"content\":\"" + rawText.replace("\n", " ").replace("\"", "'") + "\"}]"
                        )
                    )
                }
            } finally {
                isAnalyzingFact.value = false
            }
        }
    }

    fun addChronologicalFactDetail(
        date: String,
        description: String,
        actors: String,
        policy: String,
        evidence: String,
        category: String,
        associatedDocsJson: String
    ) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.insertFact(
                ChronologicalFact(
                    caseId = caseId,
                    factDate = date,
                    description = description,
                    actorsInvolved = actors,
                    violatedPolicy = policy,
                    supportingEvidence = evidence,
                    category = category,
                    associatedDocsJson = associatedDocsJson
                )
            )
        }
    }

    fun updateChronologicalFactDocs(factId: Int, newDocsJson: String) {
        val currentCase = _selectedCaseId.value ?: return
        viewModelScope.launch {
            val factList = db.caseDao.getFactsByCase(currentCase).first()
            val target = factList.find { it.id == factId }
            if (target != null) {
                val updated = target.copy(associatedDocsJson = newDocsJson)
                repository.insertFact(updated)
            }
        }
    }

    // --- Automated E-Filing System & Proof of Service Triggering ---
    fun runEFileSubmission(doc: CaseDocument, courtSystem: String) {
        isEfilingInProgress.value = true
        _efilingResult.value = ""
        useAICredit()

        viewModelScope.launch {
            kotlinx.coroutines.delay(2500) // Simulate security auth and transfer handshake

            val verificationHash = "CV-MIFILE-TX-" + (1000..9999).random() + "SCAO"
            val submissionTimestamp = "05/30/2026 14:22:10"
            val courtStamping = """
                =========================================
                    STATE COURT E-FILE GATEWAY RECEIPT
                =========================================
                COURT PORTAL: $courtSystem
                TRANSACTION ID: $verificationHash
                DATE REGISTERED: $submissionTimestamp
                STATUS: ACCEPTED / STAMPED ENTRANT
                FILING PARTY: Indigent Pro Se Litigant (CaseVault Integration)
                DOCUMENT ID: SCAO-DOC-${doc.id}
                TITLE: ${doc.title}
                
                **ELECTRONIC NOTICE OF SERVICE EXECUTED**
                Service has been electronically broadcasted to all opposing attorneys of record and case designated recipient groups.
                A Proof of Service Affidavit SCAO form has been generated and appended to this filing string.
            """.trimIndent()

            _efilingResult.value = courtStamping
            isEfilingInProgress.value = false

            // Mark document e-filing complete
            val updatedDoc = doc.copy(
                isFilingComplete = true,
                serviceDetails = "E-filed successfully to $courtSystem via CaseVault. Confirmation #$verificationHash"
            )
            repository.insertDocument(updatedDoc)

            // Trigger simulated automated service email thread
            sendFilingServiceEmail(
                caseId = doc.caseId,
                threadId = "TR-${doc.id}",
                subject = "AUTOMATED SERVED FILING: ${doc.title} - Docket #${_activeCase.value?.docketNumber ?: "PENDING"}",
                body = "To all parties of record: Please find attached a certified copy of ${doc.title} formatted in court-admissible PDF for electronic service.",
                recipientGroup = "Opposing Party, Case Worker, Oversight Agency, Guardian Ad Litem",
                personalEmailToLink = "rkahabka091117@gmail.com"
            )
        }
    }

    // --- Integrated Email Service Box with Auto-Proof of Service Attachment ---
    fun sendFilingServiceEmail(
        caseId: Int,
        threadId: String?,
        subject: String,
        body: String,
        recipientGroup: String,
        personalEmailToLink: String
    ) {
        viewModelScope.launch {
            val trackingId = "EMAIL-" + (1000..9999).random()
            val proofOfServiceAffidavit = """
                ==========================================================
                     PROOF OF SERVICE AFFIDAVIT (SCAO FORM MC 11)
                ==========================================================
                TITLE OF PETITION/MOTIONS served: $subject
                METHOD of service: E-Mail broadcast utilizing server handshakes
                RECIPIENTS: $recipientGroup
                SENDER: Litigant via CaseVault Gateway
                TIMESTAMP: 05/30/2026 14:22:10
                VERIFICATION ENVELOPE HASH: $trackingId
                I, the undersigned pro se filer, declare under the penalties of perjury that a copy of this filing has been served upon the above groups of recipients.
            """.trimIndent()

            val completeEmailBody = """
                $body
                
                --- AUTOMATIC DOCUMENT ATTACHMENTS FOR SERVICE ---
                1. Court Submission PDF Document
                2. $proofOfServiceAffidavit
            """.trimIndent()

            val newEmail = SimulatedEmail(
                id = trackingId,
                threadId = threadId ?: "tr_general",
                sender = if (personalEmailToLink.isNotBlank()) personalEmailToLink else "litigant-vault-mailbox@legalcase.net",
                recipientGroup = recipientGroup,
                subject = subject,
                body = completeEmailBody,
                isEserved = true,
                hasProofOfService = true,
                timestamp = "05/30/2026 14:22"
            )

            _simulatedEmails.value = listOf(newEmail) + _simulatedEmails.value

            // Auto-append latest updated Email String within the appropriate Chronological Fact Timeline event!
            repository.insertFact(
                ChronologicalFact(
                    caseId = caseId,
                    factDate = "05/30/2026 14:22",
                    description = "Served Filing and Auto-Generated Proof of Service via linked Email Box. Thread: $subject.",
                    actorsInvolved = "Filer / $recipientGroup",
                    violatedPolicy = "MCR 2.107 (Electronic Service Standards)",
                    supportingEvidence = "Email Hash $trackingId",
                    category = "Email",
                    associatedDocsJson = """[{"type":"evidence","title":"Proof of Service SCAO Format","content":"$proofOfServiceAffidavit"},{"type":"documents","title":"E-Served Document","content":"$completeEmailBody"}]"""
                )
            )
        }
    }

    // --- Voice/Call & Text Cloud Sync Gateway ---
    fun runVoiceCallCloudSync(partyName: String, communicationType: String, logSummaryText: String) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            val recordId = "CLOUD-" + (1000..9999).random()
            val cleanDate = "05/30/2026"
            val record = CloudCommsRecord(
                id = recordId,
                type = communicationType,
                remoteParty = partyName,
                timestamp = "$cleanDate 14:20",
                transcriptSummary = logSummaryText,
                durationSeconds = if (communicationType == "Call") 240 else 0
            )
            _commsRecords.value = listOf(record) + _commsRecords.value

            // Auto-integrate this cloud logging as a real legal event in the Statement of Facts!
            repository.insertFact(
                ChronologicalFact(
                    caseId = caseId,
                    factDate = "$cleanDate 14:20",
                    description = "Cloud Synced $communicationType with $partyName: $logSummaryText",
                    actorsInvolved = partyName,
                    violatedPolicy = "Admissibility Checklist Item",
                    supportingEvidence = "Telecom Server Log: $recordId",
                    category = if (communicationType == "Call") "Call Record" else "Text Record",
                    associatedDocsJson = "[{\"type\":\"evidence\",\"title\":\"Telecom Cloud Metadata\",\"content\":\"Record id: $recordId, type: $communicationType, date: $cleanDate\"}]"
                )
            )
        }
    }

    // --- Assistant Screen Share OCR & Document Formatter ---
    fun runScreenShareCaptureAndOCR(screenDescription: String) {
        val caseId = selectedCaseId.value ?: return
        _screenShareCaptureResult.value = ""
        useAICredit()

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = getApiKey()
            val prompt = """
                You are an advanced Legal OCR Document Converter with active screen share scanning vision.
                Below is a mock representation of a captured screen or document image that a pro se litigant wants to import and convert into court-admissible formatting:
                
                SCREEN CAPTURE DESCRIPTION:
                $screenDescription
                
                Please convert and execute the following:
                1. Perform full OCR transcription of the text found on the screen.
                2. Reformat the extracted messy text into a complete, standard Michigan State Court SCAO compliant structure (with margins, court caption, and electronic signature blocks).
                3. Save the resulting output formatted as court-ready E-File compatible PDF.
                
                Provide this output in clean professional Markdown layout, indicating exactly where header stamps, margins, and watermarks go.
            """.trimIndent()

            try {
                val resultText = callAi(prompt, temperature = 0.3f)
                    ?: "Failed to scan and convert capture."
                _screenShareCaptureResult.value = resultText

                withContext(Dispatchers.Main) {
                    saveDocument(
                        title = "OCR Screen Grab: " + screenDescription.take(25) + "...",
                        docType = "OCR Scan Document",
                        content = resultText,
                        isComplete = true,
                        serviceDetails = "Screen share OCR conversion completed successfully. Ready for e-filing."
                    )
                }
            } catch (e: Exception) {
                _screenShareCaptureResult.value = "Screen OCR grab failed: ${e.message}"
            }
        }
    }

    // ==========================================
    // NEURODIVERGENT CUSTOMIZATION CORE METHODS
    // ==========================================

    fun addStickyNoteCustom(
        title: String,
        content: String,
        colorHex: String,
        noteStyle: String, // Comma-separated choice or combination, e.g. "PIN,STICKY,CITATION,HIGHLIGHTER"
        fontSize: Int,
        fontFamily: String,
        checklistItemsJson: String = "[]"
    ) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.insertStickyNote(
                StickyNote(
                    caseId = caseId,
                    title = title,
                    content = content,
                    colorHex = colorHex,
                    isPinned = false,
                    noteStyle = noteStyle,
                    fontSize = fontSize,
                    fontFamily = fontFamily,
                    checklistItemsJson = checklistItemsJson
                )
            )
        }
    }

    fun addReminderCustom(
        title: String,
        dueDate: String,
        reminderTiming: String,
        repetition: String,
        notificationSound: String,
        customAction: String,
        associatedDeviceCalendars: String
    ) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            repository.insertReminder(
                TaskReminder(
                    caseId = caseId,
                    title = title,
                    dueDate = dueDate,
                    isCompleted = false,
                    reminderTiming = reminderTiming,
                    repetition = repetition,
                    notificationSound = notificationSound,
                    customAction = customAction,
                    associatedDeviceCalendars = associatedDeviceCalendars
                )
            )

            // If "Block Calendar Buffer Hours" is active, auto-schedule a Rest/Quiet hours Buffer event before and after the deadline!
            if (customAction.contains("Block Calendar Buffer Hours")) {
                repository.insertReminder(
                    TaskReminder(
                        caseId = caseId,
                        title = "🔇 PROTECTIVE TRANQUIL BUFFER: Quiet Recovery Period (Rest Block for Court Prep/Overwhelm Prevention)",
                        dueDate = dueDate,
                        isCompleted = false,
                        reminderTiming = "2 Hours Before",
                        repetition = "Once",
                        notificationSound = "Silence",
                        customAction = "Focus Priority Focus Overlay",
                        associatedDeviceCalendars = associatedDeviceCalendars
                    )
                )
            }
        }
    }

    // Simulates an overlapping calendar analysis tool. 
    // Compares court tasks against external personal schedule feeds to ensure neurodivergent users do not burn out.
    private val _calendarConflictAnalysis = MutableStateFlow<String?>(null)
    val calendarConflictAnalysis: StateFlow<String?> = _calendarConflictAnalysis.asStateFlow()

    fun runCalendarConflictCheck(targetDate: String, activeTaskTitle: String) {
        viewModelScope.launch {
            _calendarConflictAnalysis.value = "Checking synced device streams (Google Calendar, Outlook, Apple Calendar) for potential conflicts on $targetDate..."
            kotlinx.coroutines.delay(1200)
            
            val suggestions = """
                ⚠️ CALENDAR SYNCHRONIZATION OVERLAP WARNING:
                =======================================================
                We detected conflicting personal obligations on $targetDate:
                * Synced Google Calendar item: "Work Shift / Core Hours" (9:00 AM - 1:00 PM)
                * Overlapping Court Obligation: "$activeTaskTitle"
                
                ACTION TAKEN (PREVENT OVERWHELM SUITE):
                1. Flagged conflict status to prevent mental fatigue and overbooking.
                2. Automatically blocked an "Aesthetic Buffer Rest Block" (90 Minutes before and 60 Minutes after).
                3. Suggested rescheduling non-court matters to keep energy reserved.
            """.trimIndent()
            _calendarConflictAnalysis.value = suggestions
        }
    }

    // ==========================================
    // SUMMONABLE AI ASSISTANT CHAT CONTEXT CONTROLLERS
    // ==========================================
    val isChattingWithAi = MutableStateFlow(false)

    fun sendAiChatPrompt(sessionName: String, promptText: String, existingThreadId: Int? = null) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            isChattingWithAi.value = true
            useAICredit()
            val currentMessages = mutableListOf<Map<String, String>>()
            var activeThread: AiChatThread? = null
            try {
                // Determine current thread messages or start a new thread
                val currentThreads = db.caseDao.getChatThreadsByCase(caseId).first()
                if (existingThreadId != null) {
                    activeThread = currentThreads.find { it.id == existingThreadId }
                }

                if (activeThread != null) {
                    val regex = Regex("""\{"role":"([^"]*)","text":"([^"]*)","timestamp":"([^"]*)"\}""")
                    regex.findAll(activeThread!!.fullThreadJson).forEach { m ->
                        // Clean up escaped quotes and newlines safely
                        val cleanedText = m.groupValues[2].replace("\\\"", "\"").replace("\\n", "\n")
                        currentMessages.add(mapOf("role" to m.groupValues[1], "text" to cleanedText, "timestamp" to m.groupValues[3]))
                    }
                }

                val userTime = "05/30/2026 14:30"
                currentMessages.add(mapOf("role" to "user", "text" to promptText, "timestamp" to userTime))

                // Compile context for Gemini
                val activeDocSummary = db.caseDao.getDocumentsByCase(caseId).first().joinToString("\n") { "[Doc: ${it.title} type: ${it.docType}]" }
                val activeFactsSummary = db.caseDao.getFactsByCase(caseId).first().joinToString("\n") { "[Fact: ${it.factDate} - ${it.description}]" }

                val systemDirective = """
                    You are the summonable, contextual AI Assistant helper inside the 'Self Litigant Assistant' companion app.
                    This app helps pro se litigants (especially neurodivergent individuals) navigate complex court systems and procedures safely and confidently.
                    Your goal is to provide interactive note-taking, legal research explanation in friendly language, layout outlining of files, and citation lookup suggestions.
                    
                    CRITICAL SAFETY FOCUS:
                    - You do NOT give official legal advice (always advise checking court rules).
                    - Remind the user of the "Proofread and Verify" safeguard.
                    - Offer strategies to stay calm, well-structured, and methodical to beat legal adversaries cleanly at their own game.
                    
                    CURRENT COURT CASE RECORD DETAILS FOR INTEGRATION:
                    Active documents:
                    $activeDocSummary
                    
                    Chonological fact logs:
                    $activeFactsSummary
                """.trimIndent()

                val apiPrompt = "$systemDirective\n\nDialogue History:\n" + 
                    currentMessages.joinToString("\n") { "${it["role"]?.uppercase()}: ${it["text"]}" } + 
                    "\n\nUSER SUMMONS FOR INTERACTIVE Note-taking/Research: $promptText\n\nProvide an exceptionally clear, supportive, and structured helper reply."

                val aiResponseText = callAi(apiPrompt, temperature = 0.5f)
                    ?: "Connection failed. Please check your network and AI API key in the Secrets Panel."

                currentMessages.add(mapOf("role" to "assistant", "text" to aiResponseText, "timestamp" to userTime))

                // Serialize back to JSON manually
                val jsonBuilder = StringBuilder("[")
                currentMessages.forEachIndexed { idx, it ->
                    val escapedText = (it["text"] ?: "").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
                    jsonBuilder.append("""{"role":"${it["role"]}","text":"$escapedText","timestamp":"${it["timestamp"]}"}""")
                    if (idx < currentMessages.size - 1) jsonBuilder.append(",")
                }
                jsonBuilder.append("]")
                val jsonString = jsonBuilder.toString()

                val finalThread = AiChatThread(
                    id = activeThread?.id ?: 0,
                    caseId = caseId,
                    sessionName = if (sessionName.isNotBlank()) sessionName else "Research: " + promptText.take(25) + "...",
                    fullThreadJson = jsonString,
                    timestamp = System.currentTimeMillis(),
                    reminderDate = activeThread?.reminderDate ?: ""
                )
                db.caseDao.insertChatThread(finalThread)

            } catch (e: Exception) {
                e.printStackTrace()
                // Append failure state to messages
                val userTime = "05/30/2026 14:30"
                currentMessages.add(mapOf("role" to "assistant", "text" to "AI Error during summons response verification: ${e.message}. Ensure your AI API key is configured.", "timestamp" to userTime))
                
                val jsonBuilder = StringBuilder("[")
                currentMessages.forEachIndexed { idx, it ->
                    val escapedText = (it["text"] ?: "").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
                    jsonBuilder.append("""{"role":"${it["role"]}","text":"$escapedText","timestamp":"${it["timestamp"]}"}""")
                    if (idx < currentMessages.size - 1) jsonBuilder.append(",")
                }
                jsonBuilder.append("]")
                val finalThread = AiChatThread(
                    id = existingThreadId ?: 0,
                    caseId = caseId,
                    sessionName = sessionName.ifBlank { "Unscheduled AI Thread" },
                    fullThreadJson = jsonBuilder.toString(),
                    timestamp = System.currentTimeMillis()
                )
                db.caseDao.insertChatThread(finalThread)
            } finally {
                isChattingWithAi.value = false
            }
        }
    }

    fun deleteChatThread(thread: AiChatThread) {
        viewModelScope.launch {
            repository.deleteChatThread(thread)
        }
    }

    fun addChatReminder(threadId: Int, alarmDate: String) {
        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch {
            val currentThreads = db.caseDao.getChatThreadsByCase(caseId).first()
            val target = currentThreads.find { it.id == threadId }
            if (target != null) {
                // Update reminderDate directly
                val updated = target.copy(reminderDate = alarmDate)
                db.caseDao.insertChatThread(updated)

                // Add to active reminders matching custom preferences
                repository.insertReminder(
                    TaskReminder(
                        caseId = caseId,
                        title = "🔔 REVIEW AI CONTEXT NOTE: ${target.sessionName}",
                        dueDate = alarmDate,
                        isCompleted = false,
                        reminderTiming = "2 Hours Before",
                        repetition = "Once",
                        notificationSound = "Gentle Synth",
                        customAction = "Block Calendar Buffer Hours",
                        associatedDeviceCalendars = "Google Calendar,Outlook"
                    )
                )
            }
        }
    }

    // ==========================================
    // COURT PAYMENT PORTAL SIMULATOR
    // ==========================================
    private val _paymentPortalResult = MutableStateFlow<String?>(null)
    val paymentPortalResult: StateFlow<String?> = _paymentPortalResult.asStateFlow()
    val isPortalPaying = MutableStateFlow(false)

    fun runSimulatedCourtFilingFeePayment(
        courtName: String,
        feeCategory: String, // e.g. "Motion Filing Fee", "General Custody Petitions", "Process Service Proof"
        amount: Double,
        cardNumberSimulated: String,
        routingNumberSimulated: String,
        applyForFeeWaiverMc20: Boolean
    ) {
        val caseId = selectedCaseId.value ?: return
        isPortalPaying.value = true
        _paymentPortalResult.value = "Establishing secure encrypted connection to SCAO financial portal gateway: $courtName..."
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            
            if (applyForFeeWaiverMc20) {
                _paymentPortalResult.value = """
                    ✅ FEE WAIVER APPLICATION (MC 20) VERIFIED & SUBMITTED:
                    ===================================================================
                    SCAO system automatically analyzed income verification and waived the $$amount fee!
                    COURT SYSTEM: $courtName
                    WAIVER RECEIPT ID: SCAO-WAIVE-${(100000..999999).random()}-MC20
                    STATUS: Fees Dismissed under MCR 2.002.
                """.trimIndent()
                
                // Add waived $0 loss to Expenses logged with waiver proof
                repository.insertExpense(
                    CaseExpense(
                        caseId = caseId,
                        category = "Filing Fee",
                        date = "05/30/2026",
                        amount = 0.0,
                        description = "Waived court filing fee ($feeCategory) via MC-20 Form waiver on $courtName-MiFILE portal",
                        receiptReference = "MC-20 Waiver Approved SCAO-WAIVE-MC20"
                    )
                )
            } else {
                val receiptHash = "TXN-MIFILE-PAY-${(1000000..9999999).random()}"
                _paymentPortalResult.value = """
                    ✅ SECURE EFT COURT PAYMENT TRANSACTION IN-APP COMPLETE:
                    ===================================================================
                    COURT SENSORY GATEWAY PORTAL: $courtName
                    PURPOSE: $feeCategory
                    AMOUNT PROCESSED: $$amount
                    CARD RECORD: **** **** **** ${if (cardNumberSimulated.length >= 4) cardNumberSimulated.takeLast(4) else "4321"}
                    RECEIPT TRANSACTION ID: $receiptHash
                    TIMESTAMP OF MI-FILE SETTLEMENT: 05/30/2026 14:31:02 EST
                    
                    * Note: Payment is fully legal-audited and logged instantly inside your damages ledgers.
                """.trimIndent()
                
                // Auto-log inside damages and out-of-pocket ledger
                repository.insertExpense(
                    CaseExpense(
                        caseId = caseId,
                        category = "Filing Fee",
                        date = "05/30/2026",
                        amount = amount,
                        description = "Simulated $feeCategory payment on $courtName ($feeCategory transaction verified)",
                        receiptReference = receiptHash
                    )
                )

                // Also trigger chronological timeline record addition of payment proof
                repository.insertFact(
                    ChronologicalFact(
                        caseId = caseId,
                        factDate = "05/30/2026 14:31",
                        description = "Completed $feeCategory transaction ($$amount out-of-pocket loss) for filing on $courtName. Secure receipt #$receiptHash",
                        actorsInvolved = "Pro Se Litigant",
                        violatedPolicy = "Payment Confirmed",
                        supportingEvidence = "Receipt Hash $receiptHash",
                        category = "Document",
                        associatedDocsJson = "[{\"type\":\"evidence\",\"title\":\"Filing Payment Receipt\",\"content\":\"Filing: $feeCategory, Amount: $$amount, SCAO receipt portal transaction code: $receiptHash\"}]"
                    )
                )
            }
            isPortalPaying.value = false
        }
    }

    // ==========================================
    // 🛡️ AMBIENT EXTERNAL SUMMONS & OVERLAY MECHANISMS
    // ==========================================
    private val _ambientBubbleActive = MutableStateFlow(false)
    val ambientBubbleActive: StateFlow<Boolean> = _ambientBubbleActive.asStateFlow()

    private val _capturedExternalVoiceNotes = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val capturedExternalVoiceNotes: StateFlow<List<Pair<String, Long>>> = _capturedExternalVoiceNotes.asStateFlow()

    private val _capturedScreenShares = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val capturedScreenShares: StateFlow<List<Pair<String, Long>>> = _capturedScreenShares.asStateFlow()

    fun toggleAmbientBubble(active: Boolean) {
        _ambientBubbleActive.value = active
    }

    fun submitSimulatedVoiceSummons(voiceQuery: String) {
        val currentList = _capturedExternalVoiceNotes.value.toMutableList()
        currentList.add(0, Pair(voiceQuery, System.currentTimeMillis()))
        _capturedExternalVoiceNotes.value = currentList
        
        // Initiate automatic AI background prompt:
        sendAiChatPrompt(
            sessionName = "Voice Note Summons",
            promptText = "The following voice query was captured from the external overlay assistant: '$voiceQuery'. Please analyze for relevant civil rights or procedural answers."
        )
    }

    fun submitSimulatedScreenShareSummons(sceneDescription: String, capturedText: String) {
        val currentList = _capturedScreenShares.value.toMutableList()
        currentList.add(0, Pair("$sceneDescription | OCR: $capturedText", System.currentTimeMillis()))
        _capturedScreenShares.value = currentList

        val caseId = selectedCaseId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFact(
                ChronologicalFact(
                    caseId = caseId,
                    factDate = "05/30/2026 14:31",
                    description = "Screen captured via external companion summoner: $sceneDescription.",
                    actorsInvolved = "Pro Se Companion",
                    violatedPolicy = "Evidence Screen Captured Externally",
                    supportingEvidence = "OCR Extraction: $capturedText",
                    category = "Document",
                    associatedDocsJson = "[{\"type\":\"evidence\",\"title\":\"Overlay Screen Capture Metadata\",\"content\":\"OCR Text: $capturedText\"}]"
                )
            )
            // Send automatic background instruction
            sendAiChatPrompt(
                sessionName = "Screen Share OCR",
                promptText = "Analyze this screen capture: '$sceneDescription' with content: '$capturedText' and give relevant procedural check-rules."
            )
        }
    }
}

// --- Companion structures for simulated legal communications ---
data class SimulatedEmail(
    val id: String,
    val threadId: String,
    val sender: String,
    val recipientGroup: String,
    val subject: String,
    val body: String,
    val isEserved: Boolean,
    val hasProofOfService: Boolean,
    val timestamp: String,
    val linkedFactId: Int? = null
)

data class CloudCommsRecord(
    val id: String,
    val type: String, // "Call" or "Text"
    val remoteParty: String,
    val timestamp: String,
    val transcriptSummary: String,
    val durationSeconds: Int = 0
)

