package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset

// Elegant Frosted Glass Theme Colors
private val ObsidianBackground = Color(0xFF050B18) // Rich dark blue-black tech canvas
private val CardSlate = Color(0x13FFFFFF) // 7.5% white with alpha for true frosted glass cards
private val GoldAccent = Color(0xFF60A5FA) // Electric ice-blue highlight replacing gold
private val AlertCoral = Color(0xFFF87171) // Vivid red/coral from html
private val TextWarmCream = Color(0xFFF1F5F9) // Slate-100 clear white text
private val TextMutedSlate = Color(0xFF94A3B8) // Slate-400 muted text
private val SuccessGreen = Color(0xFF4ADE80) // Cool mint active green from html

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CaseVaultApp(viewModel: CaseViewModel) {
    val cases by viewModel.cases.collectAsStateWithLifecycle()
    val activeCase by viewModel.activeCase.collectAsStateWithLifecycle()
    val selectedCaseId by viewModel.selectedCaseId.collectAsStateWithLifecycle()
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val checklists by viewModel.checklists.collectAsStateWithLifecycle()
    val caseMisconducts by viewModel.caseMisconducts.collectAsStateWithLifecycle()
    val allMisconducts by viewModel.allMisconducts.collectAsStateWithLifecycle()
    val chronologicalFacts by viewModel.chronologicalFacts.collectAsStateWithLifecycle()
    val stickyNotes by viewModel.stickyNotes.collectAsStateWithLifecycle()
    val caseExpenses by viewModel.caseExpenses.collectAsStateWithLifecycle()
    val userCredits by viewModel.userCredits.collectAsStateWithLifecycle()
    val isUnlimitedAccessWaiver by viewModel.isUnlimitedAccessWaiver.collectAsStateWithLifecycle()
    val isGeneratingDoc by viewModel.isGeneratingDoc.collectAsStateWithLifecycle()
    val selectedProvider by viewModel.selectedProvider.collectAsStateWithLifecycle()

    val ambientBubbleActive by viewModel.ambientBubbleActive.collectAsStateWithLifecycle(initialValue = false)

    var activeTab by remember { mutableStateOf(0) }
    var showDisclaimerDialog by remember { mutableStateOf(false) }
    var showCreateCaseDialog by remember { mutableStateOf(false) }
    var showMiniOverlayHub by remember { mutableStateOf(false) }
    var showProviderPickerDialog by remember { mutableStateOf(false) }

    // Navigation and Responsive layout calculation
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Background base canvas color
                drawRect(color = ObsidianBackground)
                
                // Absolute top-left soft blue fluid glow (blue-600/20 with substantial blur)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x3D2563EB), Color.Transparent),
                        center = Offset(x = size.width * -0.1f, y = size.height * -0.1f),
                        radius = size.width * 0.75f
                    ),
                    radius = size.width * 0.75f,
                    center = Offset(x = size.width * -0.1f, y = size.height * -0.1f)
                )
                
                // Absolute bottom-right soft purple fluid glow (purple-600/10 with substantial blur)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1F9333EA), Color.Transparent),
                        center = Offset(x = size.width * 1.1f, y = size.height * 0.9f),
                        radius = size.width * 0.85f
                    ),
                    radius = size.width * 0.85f,
                    center = Offset(x = size.width * 1.1f, y = size.height * 0.9f)
                )
            },
        color = Color.Transparent
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Adaptive Navigation: side rail for tablets/widescreens, bottom navigation for compact phone displays
            if (isTablet) {
                NavigationRail(
                    containerColor = CardSlate,
                    contentColor = TextWarmCream,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Balance,
                            contentDescription = "Court Icon",
                            tint = GoldAccent,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "CASE VAULT",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    NavigationRailItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = ObsidianBackground,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = TextMutedSlate,
                            unselectedTextColor = TextMutedSlate
                        )
                    )

                    NavigationRailItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Default.FolderZip, contentDescription = "Filings") },
                        label = { Text("Filings", fontSize = 11.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = ObsidianBackground,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = TextMutedSlate,
                            unselectedTextColor = TextMutedSlate
                        )
                    )

                    NavigationRailItem(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        icon = { Icon(Icons.Default.Policy, contentDescription = "Auditor") },
                        label = { Text("Policy AI", fontSize = 11.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = ObsidianBackground,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = TextMutedSlate,
                            unselectedTextColor = TextMutedSlate
                        )
                    )

                    NavigationRailItem(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        icon = { Icon(Icons.Default.Warning, contentDescription = "Misconduct") },
                        label = { Text("Misconduct", fontSize = 11.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = ObsidianBackground,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = TextMutedSlate,
                            unselectedTextColor = TextMutedSlate
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { showDisclaimerDialog = true },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "UPL Warning", tint = GoldAccent)
                    }
                }
            }

            // Main Content Area
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = CardSlate,
                            titleContentColor = TextWarmCream
                        ),
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = activeCase?.title ?: "No Active Case Selected",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (activeCase != null) {
                                        Text(
                                            text = "Docket: ${activeCase?.docketNumber} vs ${activeCase?.opposingParty}",
                                            fontSize = 12.sp,
                                            color = TextMutedSlate,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Quick Case selection dropdown / Selector button
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Button(
                                        onClick = { showCreateCaseDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "New Case",
                                            tint = ObsidianBackground,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("New Case", color = ObsidianBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // AI Provider Picker Button
                                    IconButton(
                                        onClick = { showProviderPickerDialog = true },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(GoldAccent.copy(alpha = 0.15f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SmartToy,
                                            contentDescription = "AI Provider: ${selectedProvider.displayName}",
                                            tint = GoldAccent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    // Shield Disclaimer Badge
                                    IconButton(
                                        onClick = { showDisclaimerDialog = true },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(AlertCoral.copy(alpha = 0.2f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = "UPL Disclaimer Compliance Badge",
                                            tint = AlertCoral,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    if (!isTablet) {
                        NavigationBar(
                            containerColor = CardSlate,
                            modifier = Modifier.navigationBarsPadding()
                        ) {
                            NavigationBarItem(
                                selected = activeTab == 0,
                                onClick = { activeTab = 0 },
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                label = { Text("Dash", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianBackground,
                                    selectedTextColor = GoldAccent,
                                    indicatorColor = GoldAccent,
                                    unselectedIconColor = TextMutedSlate,
                                    unselectedTextColor = TextMutedSlate
                                )
                            )
                            NavigationBarItem(
                                selected = activeTab == 1,
                                onClick = { activeTab = 1 },
                                icon = { Icon(Icons.Default.FolderZip, contentDescription = "Filings") },
                                label = { Text("Filings", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianBackground,
                                    selectedTextColor = GoldAccent,
                                    indicatorColor = GoldAccent,
                                    unselectedIconColor = TextMutedSlate,
                                    unselectedTextColor = TextMutedSlate
                                )
                            )
                            NavigationBarItem(
                                selected = activeTab == 2,
                                onClick = { activeTab = 2 },
                                icon = { Icon(Icons.Default.Policy, contentDescription = "Auditor") },
                                label = { Text("Policy AI", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianBackground,
                                    selectedTextColor = GoldAccent,
                                    indicatorColor = GoldAccent,
                                    unselectedIconColor = TextMutedSlate,
                                    unselectedTextColor = TextMutedSlate
                                )
                            )
                            NavigationBarItem(
                                selected = activeTab == 3,
                                onClick = { activeTab = 3 },
                                icon = { Icon(Icons.Default.Warning, contentDescription = "Misconduct") },
                                label = { Text("Misconduct", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianBackground,
                                    selectedTextColor = GoldAccent,
                                    indicatorColor = GoldAccent,
                                    unselectedIconColor = TextMutedSlate,
                                    unselectedTextColor = TextMutedSlate
                                )
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .background(Color.Transparent)
                ) {
                    if (cases.isEmpty()) {
                        // Empty State for First Launch
                        WelcomeAndInitialCasePrompt(
                            onCreateCase = { title, docket, opposing ->
                                viewModel.createCase(title, docket, opposing)
                            }
                        )
                    } else {
                        when (activeTab) {
                            0 -> DashboardTab(
                                activeCase = activeCase,
                                cases = cases,
                                reminders = reminders,
                                facts = chronologicalFacts,
                                stickyNotes = stickyNotes,
                                onSelectCase = { viewModel.selectCase(it) },
                                onDeleteCase = { viewModel.deleteCase(it) },
                                onAddReminder = { title, date -> viewModel.addReminder(title, date) },
                                onToggleReminder = { viewModel.toggleReminder(it) },
                                onDeleteReminder = { viewModel.deleteReminder(it) },
                                onImportChat = { source, text -> viewModel.importExternalContent(source, text) },
                                onUpdateNotes = { text -> viewModel.updateCaseNotes(text) },
                                onAddFact = { date, desc, actors, policy, evidence -> viewModel.addChronologicalFact(date, desc, actors, policy, evidence) },
                                onDeleteFact = { viewModel.deleteChronologicalFact(it) },
                                onAddStickyNote = { title, content, color -> viewModel.addStickyNote(title, content, color) },
                                onTogglePinStickyNote = { viewModel.togglePinStickyNote(it) },
                                onDeleteStickyNote = { viewModel.deleteStickyNote(it) },
                                viewModel = viewModel
                            )
                            1 -> FilingsTab(
                                documents = documents,
                                checklists = checklists,
                                expenses = caseExpenses,
                                onToggleChecklist = { viewModel.toggleChecklistItem(it) },
                                onAddChecklist = { name, req -> viewModel.addNewChecklistItem(name, req) },
                                onSaveDoc = { title, type, content, service ->
                                    viewModel.saveDocument(title, type, content, true, service)
                                },
                                onDeleteDoc = { viewModel.deleteDocument(it) },
                                onGenerateGuidanceDraft = { type, facts ->
                                    viewModel.useAICredit()
                                    viewModel.generateDocumentFilingTemplate(type, facts)
                                },
                                isGenerating = isGeneratingDoc,
                                onAddExpense = { cat, date, amt, desc, receipt ->
                                    viewModel.addCaseExpense(cat, date, amt, desc, receipt)
                                },
                                onDeleteExpense = { viewModel.deleteCaseExpense(it) },
                                onVerifyWaiver = { viewModel.verifyTortiousInterferenceLossWaiver(it) },
                                isUnlimited = isUnlimitedAccessWaiver,
                                viewModel = viewModel
                            )
                            2 -> PolicyAuditorTab(
                                viewModel = viewModel,
                                activeCase = activeCase,
                                userCredits = userCredits,
                                isUnlimited = isUnlimitedAccessWaiver
                            )
                            3 -> MisconductTab(
                                localMisconduct = caseMisconducts,
                                allMisconduct = allMisconducts,
                                onLogMisconduct = { emp, agency, date, pol, right, desc, anon ->
                                    viewModel.logMisconduct(emp, agency, date, pol, right, desc, anon)
                                },
                                onDeleteMisconduct = { viewModel.deleteMisconduct(it) },
                                activeCase = activeCase,
                                userCredits = userCredits,
                                isUnlimited = isUnlimitedAccessWaiver,
                                onAwardAdCredits = { viewModel.awardAdWatchCredits() },
                                onVerifyIncomeWaiver = { type, notes ->
                                    viewModel.verifyLowIncomeWaiver(type, notes)
                                },
                                viewModel = viewModel
                            )
                        }
                    }

                    // ========================================================
                    // 🛡️ PERSISTENT SYSTEM OVERLAY BUBBLE SIMULATOR
                    // ========================================================
                    if (ambientBubbleActive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 120.dp) // Lifted slightly above standard bottom-bar to avoid overlap
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                                .border(2.dp, TextWarmCream, CircleShape)
                                .clickable { showMiniOverlayHub = true }
                                .testTag("simulated_ambient_bubble"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = "Active Summons Overlay Launcher", tint = ObsidianBackground, modifier = Modifier.size(24.dp))
                                Text("SCAO", color = ObsidianBackground, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (showMiniOverlayHub) {
                        Dialog(onDismissRequest = { showMiniOverlayHub = false }) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = ObsidianBackground,
                                border = BorderStroke(2.dp, SuccessGreen),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .testTag("mini_overlay_hub_panel")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    // Header
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Shield, contentDescription = "Active", tint = SuccessGreen, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "SCAO Ambient System Summons",
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        IconButton(onClick = { showMiniOverlayHub = false }) {
                                            Icon(Icons.Default.Close, contentDescription = "Minimize Hub", tint = TextMutedSlate)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        "This overlay bar runs over outside applications to enable background defense journaling. Submit rapid data here:",
                                        color = TextWarmCream.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 🎤 Voice section
                                    var quickVoiceQuery by remember { mutableStateOf("") }
                                    var isRecordingOverlayVoice by remember { mutableStateOf(false) }
                                    var overlayVoiceCountdown by remember { mutableStateOf(0) }

                                    LaunchedEffect(isRecordingOverlayVoice) {
                                        if (isRecordingOverlayVoice) {
                                            overlayVoiceCountdown = 3
                                            while (overlayVoiceCountdown > 0) {
                                                kotlinx.coroutines.delay(1000)
                                                overlayVoiceCountdown--
                                            }
                                            viewModel.submitSimulatedVoiceSummons(
                                                if (quickVoiceQuery.isNotBlank()) quickVoiceQuery else "Log caseworker refusing administrative document copy under state rule"
                                            )
                                            quickVoiceQuery = ""
                                            isRecordingOverlayVoice = false
                                        }
                                    }

                                    Text("🎤 SPOKEN VOICE JOURNAL SUMMONS:", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(
                                        value = quickVoiceQuery,
                                        onValueChange = { quickVoiceQuery = it },
                                        placeholder = { Text("Spoken question/note simulator...", color = TextMutedSlate, fontSize = 11.sp) },
                                        textStyle = TextStyle(color = TextWarmCream),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = SuccessGreen,
                                            unfocusedBorderColor = TextMutedSlate.copy(alpha = 0.3f)
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (isRecordingOverlayVoice) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().background(AlertCoral.copy(alpha = 0.15f)).padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(color = AlertCoral, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("SCAO Recording waveform... $overlayVoiceCountdown s", color = AlertCoral, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { isRecordingOverlayVoice = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Mic, contentDescription = "Voice Capture", tint = ObsidianBackground)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Journal Back Voice Note", color = ObsidianBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // 🖥️ Screen frame Share sections
                                    var isCapturingOverlayScreen by remember { mutableStateOf(false) }
                                    var overlayScreenCapMsg by remember { mutableStateOf("") }

                                    LaunchedEffect(isCapturingOverlayScreen) {
                                        if (isCapturingOverlayScreen) {
                                            kotlinx.coroutines.delay(1800)
                                            viewModel.submitSimulatedScreenShareSummons(
                                                "Active Judicial Motion Docket page capture",
                                                "ERROR Code 552: Low income fee-waiver state certificate rejected."
                                            )
                                            overlayScreenCapMsg = "Screen coordinates synced directly inside Case dockets!"
                                            isCapturingOverlayScreen = false
                                        }
                                    }

                                    Text("🖥️ SCREEN CAPTURE / SHARE OCR SIM:", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (isCapturingOverlayScreen) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().background(SuccessGreen.copy(alpha = 0.15f)).padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(color = SuccessGreen, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Simulating target camera bounds capture...", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                isCapturingOverlayScreen = true
                                                overlayScreenCapMsg = ""
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.CameraAlt, contentDescription = "Ocr frame capture", tint = ObsidianBackground)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Capture Target App screen", color = ObsidianBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (overlayScreenCapMsg.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(overlayScreenCapMsg, color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Explanations of ambient UPL / flotation limits
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                "💡 SYSTEM INTEGRATION NOTE:",
                                                color = GoldAccent,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "This panel operates dynamically outside of CaseVault. Your app DOES NOT need to be open – all voice and screen share captures are securely compiled by the background Service and cached into database timelines automatically. Simply prompt CaseVault for a detailed procedural review when you return.",
                                                color = TextWarmCream.copy(alpha = 0.85f),
                                                fontSize = 10.sp,
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Disclaimer Compliance Modal (Avoid UPL Rules)
    if (showDisclaimerDialog) {
        Dialog(onDismissRequest = { showDisclaimerDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardSlate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, GoldAccent, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safe Shield",
                        tint = AlertCoral,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "LEGAL PROTOCOL DISCLAIMER",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Unauthorized Practice of Law (UPL) Safeguard",
                        color = TextWarmCream,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "To protect litigants and comply with Michigan State SCAO & State Bar directives, please be advised:\n\n" +
                                "1. This app functions strictly as an Automated Document Organizer & Policy Analysis Audit Tool to facilitate administrative readiness.\n\n" +
                                "2. It DOES NOT provide legal counsel, represent you in a tribunal, or formulate strategic legal defenses.\n\n" +
                                "3. The user assumes 100% responsibility for verifying form formatting, filing rules, and case submissions.\n\n" +
                                "4. Use of this application for pro se litigation acts to promote equal 'Access to Justice' as supported by modern Michigan Access to Justice initiatives.",
                        color = TextWarmCream,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showDisclaimerDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text("I Certify as a Pro Se Litigant", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // AI Provider Picker Dialog
    if (showProviderPickerDialog) {
        Dialog(onDismissRequest = { showProviderPickerDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0D1B2E),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI Provider",
                        tint = GoldAccent,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Select AI Provider",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose which AI backend powers all document generation and analysis.",
                        color = TextMutedSlate,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    com.example.ui.AiProvider.values().forEach { provider ->
                        val isSelected = selectedProvider == provider
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) GoldAccent.copy(alpha = 0.18f)
                                    else Color.White.copy(alpha = 0.04f)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) GoldAccent else TextMutedSlate.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.setAiProvider(provider)
                                    showProviderPickerDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setAiProvider(provider)
                                    showProviderPickerDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GoldAccent,
                                    unselectedColor = TextMutedSlate
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = provider.displayName,
                                    color = if (isSelected) GoldAccent else TextWarmCream,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = when (provider) {
                                        com.example.ui.AiProvider.GEMINI -> "Google Gemini 1.5 Flash"
                                        com.example.ui.AiProvider.GROQ -> "Llama 3 70B · Ultra-fast inference"
                                        com.example.ui.AiProvider.OPENROUTER -> "Mistral 7B via OpenRouter"
                                        com.example.ui.AiProvider.BLACKBOX -> "Blackbox AI · Code & reasoning"
                                    },
                                    color = TextMutedSlate,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    TextButton(
                        onClick = { showProviderPickerDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel", color = TextMutedSlate)
                    }
                }
            }
        }
    }

    // Create New Case Modal
    if (showCreateCaseDialog) {
        var title by remember { mutableStateOf("") }
        var docket by remember { mutableStateOf("") }
        var opposing by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showCreateCaseDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardSlate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(0.5.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Create Legal Case Record",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Case Name / Brief Title") },
                        placeholder = { Text("e.g. Smith Family Defense") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            focusedLabelColor = GoldAccent,
                            unfocusedLabelColor = TextMutedSlate,
                            unfocusedBorderColor = TextMutedSlate,
                            unfocusedTextColor = TextWarmCream,
                            focusedTextColor = TextWarmCream
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("case_title_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = docket,
                        onValueChange = { docket = it },
                        label = { Text("Docket ID / Court Number") },
                        placeholder = { Text("e.g. 26-10452-DP") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            focusedLabelColor = GoldAccent,
                            unfocusedLabelColor = TextMutedSlate,
                            unfocusedBorderColor = TextMutedSlate,
                            unfocusedTextColor = TextWarmCream,
                            focusedTextColor = TextWarmCream
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = opposing,
                        onValueChange = { opposing = it },
                        label = { Text("Opposing Party / Zoning Board") },
                        placeholder = { Text("e.g. Michigan CPS / Local Zone Board") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            focusedLabelColor = GoldAccent,
                            unfocusedLabelColor = TextMutedSlate,
                            unfocusedBorderColor = TextMutedSlate,
                            unfocusedTextColor = TextWarmCream,
                            focusedTextColor = TextWarmCream
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showCreateCaseDialog = false }) {
                            Text("Cancel", color = AlertCoral)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    viewModel.createCase(title, docket, opposing)
                                    showCreateCaseDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                        ) {
                            Text("Create Record", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeAndInitialCasePrompt(onCreateCase: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var docket by remember { mutableStateOf("") }
    var opposing by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Balance,
            contentDescription = "Gavel",
            tint = GoldAccent,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome to Legal Case Vault",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GoldAccent
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "An interactive tool for Michigan Pro Se Litigants to organize defense cases, audit policies vs actions, log contractor misconduct, and protect private rights.",
            color = TextMutedSlate,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Initial setup card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Begin by Creating Your First Case File",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Case Name") },
                    placeholder = { Text("e.g. My CPS Defense Case") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        focusedLabelColor = GoldAccent,
                        unfocusedLabelColor = TextMutedSlate,
                        unfocusedBorderColor = TextMutedSlate,
                        focusedTextColor = TextWarmCream,
                        unfocusedTextColor = TextWarmCream
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("init_case_title")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = docket,
                    onValueChange = { docket = it },
                    label = { Text("Docket Number") },
                    placeholder = { Text("e.g. SCAO-2026-FOC-44") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        focusedLabelColor = GoldAccent,
                        unfocusedLabelColor = TextMutedSlate,
                        unfocusedBorderColor = TextMutedSlate,
                        focusedTextColor = TextWarmCream,
                        unfocusedTextColor = TextWarmCream
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = opposing,
                    onValueChange = { opposing = it },
                    label = { Text("Opposing Party / Oppressor") },
                    placeholder = { Text("e.g. CPS Worker Smithe & MDHHS Agency") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        focusedLabelColor = GoldAccent,
                        unfocusedLabelColor = TextMutedSlate,
                        unfocusedBorderColor = TextMutedSlate,
                        focusedTextColor = TextWarmCream,
                        unfocusedTextColor = TextWarmCream
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onCreateCase(title, docket, opposing)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Initialize Interactive Vault", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// TAB 0: DASHBOARD & REMINDERS / PORTAL
// ==========================================
@Composable
fun DashboardTab(
    activeCase: LegalCase?,
    cases: List<LegalCase>,
    reminders: List<TaskReminder>,
    facts: List<ChronologicalFact>,
    stickyNotes: List<StickyNote>,
    onSelectCase: (Int) -> Unit,
    onDeleteCase: (LegalCase) -> Unit,
    onAddReminder: (String, String) -> Unit,
    onToggleReminder: (TaskReminder) -> Unit,
    onDeleteReminder: (TaskReminder) -> Unit,
    onImportChat: (String, String) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onAddFact: (String, String, String, String, String) -> Unit,
    onDeleteFact: (ChronologicalFact) -> Unit,
    onAddStickyNote: (String, String, String) -> Unit,
    onTogglePinStickyNote: (StickyNote) -> Unit,
    onDeleteStickyNote: (StickyNote) -> Unit,
    viewModel: CaseViewModel
) {
    var showCaseSelectDropdown by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf(activeCase?.caseNotes ?: "") }

    val isUnlimitedAccessWaiver by viewModel.isUnlimitedAccessWaiver.collectAsStateWithLifecycle(initialValue = false)
    val userCredits by viewModel.userCredits.collectAsStateWithLifecycle(initialValue = 1000)
    val ambientBubbleActive by viewModel.ambientBubbleActive.collectAsStateWithLifecycle(initialValue = false)
    val capturedExternalVoiceNotes by viewModel.capturedExternalVoiceNotes.collectAsStateWithLifecycle(initialValue = emptyList<Pair<String, Long>>())
    val capturedScreenShares by viewModel.capturedScreenShares.collectAsStateWithLifecycle(initialValue = emptyList<Pair<String, Long>>())
    val calendarConflictAnalysis by viewModel.calendarConflictAnalysis.collectAsStateWithLifecycle()
    val isAnalyzingFact by viewModel.isAnalyzingFact.collectAsStateWithLifecycle()
    val simulatedEmails by viewModel.simulatedEmails.collectAsStateWithLifecycle()
    val commsRecords by viewModel.commsRecords.collectAsStateWithLifecycle()
    val screenShareCaptureResult by viewModel.screenShareCaptureResult.collectAsStateWithLifecycle()
    val aiChatThreads by viewModel.aiChatThreads.collectAsStateWithLifecycle(initialValue = emptyList<AiChatThread>())
    val isChattingWithAi by viewModel.isChattingWithAi.collectAsStateWithLifecycle()

    // Synchronize text when activeCase changes
    LaunchedEffect(activeCase) {
        notesText = activeCase?.caseNotes ?: ""
    }

    var reminderTitle by remember { mutableStateOf("") }
    var reminderDate by remember { mutableStateOf("") }

    var importSource by remember { mutableStateOf("ChatGPT") }
    var importText by remember { mutableStateOf("") }

    var isMicRecordingSim by remember { mutableStateOf(false) }

    // --- Chronological Timeline States ---
    var factDate by remember { mutableStateOf("") }
    var factDesc by remember { mutableStateOf("") }
    var factActors by remember { mutableStateOf("") }
    var factPolicy by remember { mutableStateOf("") }
    var factEvidence by remember { mutableStateOf("") }
    var isTimelineFormExpanded by remember { mutableStateOf(false) }

    // --- Sticky Notes States ---
    var stickyTitle by remember { mutableStateOf("") }
    var stickyContent by remember { mutableStateOf("") }
    var stickyColorHex by remember { mutableStateOf("Cyan") } // Cyan, Pink, Orange, Mint
    var isStickyFormExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 📋 COMPANION DISCLAIMER BANNER: REQUIREMENT FOR "PROOFREAD & VERIFY"
        item {
            var disclConfirmed by remember { mutableStateOf(false) }
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                border = BorderStroke(1.dp, AlertCoral.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().testTag("companion_disclaimer_banner")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Proofread Safeguard", tint = AlertCoral)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📋 PRO SE SAFEGUARD: PROOFREAD & VERIFY ALL DRAFTS",
                            color = AlertCoral,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To successfully navigate complex court procedures and beat bad-faith actors under SCAO guidelines, pro se litigants must maintain manual oversight. This companion tool coordinates evidence indexing and formatting—but you MUST manually review, verify, and check citation layouts before filing.",
                        color = TextWarmCream,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = disclConfirmed,
                                onCheckedChange = { disclConfirmed = it },
                                colors = CheckboxDefaults.colors(checkedColor = SuccessGreen, uncheckedColor = TextMutedSlate)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("I attest I manually review & verify filings", color = if(disclConfirmed) SuccessGreen else TextWarmCream, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        if (disclConfirmed) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Attestation Ok", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verified", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 🛡️ EMERGENCY PRO SE STRESS WAIVER & UNLIMITED LICENSING BAR
        item {
            val isUnlimited = isUnlimitedAccessWaiver
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlimited) SuccessGreen.copy(alpha = 0.12f) else CardSlate
                ),
                border = BorderStroke(
                    1.5.dp, if (isUnlimited) SuccessGreen else GoldAccent.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emergency_unlimited_waiver_banner")
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isUnlimited) Icons.Default.AllInclusive else Icons.Default.Verified,
                                contentDescription = "Sovereign waiver icon",
                                tint = if (isUnlimited) SuccessGreen else GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUnlimited) "Sovereign Emergency Waiver: Active" else "State Credit & Cost Limitations Drawer",
                                color = if (isUnlimited) SuccessGreen else TextWarmCream,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(if (isUnlimited) SuccessGreen.copy(alpha = 0.15f) else ObsidianBackground)
                                .border(1.dp, if (isUnlimited) SuccessGreen else GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isUnlimited) "FREE UNLIMITED MODE" else "CREDITS REMAINING: $userCredits",
                                color = if (isUnlimited) SuccessGreen else GoldAccent,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "We completely agree: No one navigating the intense emotional stress of a complicated multi-case scenario with massive misconduct or state agency policy violations should EVER be held back or overwhelmed by digital constraints on their progress! Under SCAO Guidelines and Michigan Court Rule MCR 2.002, self-litigants facing high-stakes adversarial state actions are fully entitled to fee & resource waivers.",
                        color = TextWarmCream.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (isUnlimited) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SuccessGreen.copy(alpha = 0.15f))
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Waiver Active", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "MCR 2.002 fee-waiver verified. Lifetime priority AI drafting & document uploads are fully unlocked for you at zero credit cost.",
                                color = SuccessGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.verifyLowIncomeWaiver(
                                    assistanceType = "Complex Multi-Case Pro Se Waiver",
                                    proofNotes = "Activated via Sovereign Pro Se Emergency portal due to extreme state agency misconduct and multi-case load."
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VerifiedUser, tint = ObsidianBackground, contentDescription = "Activate Waiver")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ACTIVATE EMERGENCY UNLIMITED ACCESS WAIVER (FREE)",
                                color = ObsidianBackground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 🛡️ SOVEREIGN PRO SE AMBIENT OVERLAY SUMMONS SYSTEM SETUP HUB
        item {
            val ambientActive = ambientBubbleActive
            val voiceNotes = capturedExternalVoiceNotes
            val screenShares = capturedScreenShares

            var customVoiceQuery by remember { mutableStateOf("") }
            var selectedVoiceTemplate by remember { mutableStateOf("") }
            var isRecordingVoiceSim by remember { mutableStateOf(false) }
            var voiceCountdown by remember { mutableStateOf(0) }

            var selectedScreenTemplate by remember { mutableStateOf(0) }
            var isCapturingScreenSim by remember { mutableStateOf(false) }
            var screenCapturedMessage by remember { mutableStateOf("") }

            val voiceTemplates = listOf(
                "If state agency worker violates MCR 2.002 waiver timeline, what is my filing remedy?",
                "Log caseworker intimidation statement during verbal administrative visitation",
                "Does Michigan code require physical emergency custody notice hearings within 48 hours?",
                "How do I track psychological distress out-of-pocket costs with structured filings?"
            )

            val screenShareTemplates = listOf(
                Pair("County Court Filing Docket Portal", "ERROR: Docket fee waiver denied automatically. Reason: Form layout low-income verification statement not signed under rule 4b."),
                Pair("CPS Intimidating Caseworker SMS text", "From Caseworker: If you do not agree to the home visit without a lawyer, we will file a local neglect report within the hour."),
                Pair("Michigan Admin Order guidance", "Guideline AO Sec 3(c): Prior written administrative authorization is mandatory for any emergency collateral visitation removal actions.")
            )

            // countdown for simulated audio recording
            LaunchedEffect(isRecordingVoiceSim) {
                if (isRecordingVoiceSim) {
                    voiceCountdown = 3
                    while (voiceCountdown > 0) {
                        kotlinx.coroutines.delay(1000)
                        voiceCountdown--
                    }
                    val finalQuery = if (customVoiceQuery.isNotBlank()) customVoiceQuery else selectedVoiceTemplate.ifBlank { "Unscheduled voice inquiry" }
                    viewModel.submitSimulatedVoiceSummons(finalQuery)
                    customVoiceQuery = ""
                    isRecordingVoiceSim = false
                }
            }

            // delay for simulated screen frame-capture
            LaunchedEffect(isCapturingScreenSim) {
                if (isCapturingScreenSim) {
                    kotlinx.coroutines.delay(1800)
                    val capture = screenShareTemplates[selectedScreenTemplate]
                    viewModel.submitSimulatedScreenShareSummons(
                        sceneDescription = capture.first,
                        capturedText = capture.second
                    )
                    screenCapturedMessage = "Captured frame '${capture.first}' OCR indexed in background!"
                    isCapturingScreenSim = false
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                border = BorderStroke(1.5.dp, if (ambientActive) SuccessGreen else GoldAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("external_summons_setup_card")
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header Status
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (ambientActive) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Ambient service status icon",
                                tint = if (ambientActive) SuccessGreen else TextMutedSlate,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🛡️ Sovereign Ambient Summons Desk",
                                color = TextWarmCream,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Status pill
                        Box(
                            modifier = Modifier
                                .background(if (ambientActive) SuccessGreen.copy(alpha = 0.15f) else ObsidianBackground)
                                .border(1.dp, if (ambientActive) SuccessGreen else TextMutedSlate.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (ambientActive) "RUNNING IN BACKGROUND" else "STANDBY / INACTIVE",
                                color = if (ambientActive) SuccessGreen else TextMutedSlate,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Summon assistance instantly from anywhere outside the app workspace! Capture screen-shares, log hostile contractor statements, and append audio notes directly without opening the app interface. The background Service compiles responses automatically so they are cached and ready when you return.",
                        color = TextWarmCream.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Master Ambient Toggle Switch Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ObsidianBackground.copy(alpha = 0.5f))
                            .border(0.5.dp, GoldAccent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                "Persistent Ambient Bubble Widget",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Overlays quick hotkeys on dynamic device home screen.",
                                color = TextMutedSlate,
                                fontSize = 9.sp
                            )
                        }
                        Switch(
                            checked = ambientActive,
                            onCheckedChange = { viewModel.toggleAmbientBubble(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SuccessGreen,
                                checkedTrackColor = SuccessGreen.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMutedSlate,
                                uncheckedTrackColor = CardSlate
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "SIMULATE BACKGROUND OVERLAY TRANSFERS (APP CLOSED)",
                        color = GoldAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // A) Voice Summons Trigger
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, TextMutedSlate.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            "1. Voice Query Summoner",
                            color = TextWarmCream,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Simulate recording speech-to-text questions in the field:",
                            color = TextMutedSlate,
                            fontSize = 9.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Custom question field
                        OutlinedTextField(
                            value = customVoiceQuery,
                            onValueChange = { customVoiceQuery = it },
                            placeholder = { Text("Or enter custom spoken voice notes...", fontSize = 11.sp, color = TextMutedSlate) },
                            textStyle = TextStyle(color = TextWarmCream),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = TextMutedSlate.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Template chips
                        Text("Suggested Spoken Phrases:", color = TextMutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            voiceTemplates.forEach { template ->
                                Box(
                                    modifier = Modifier
                                        .background(if (selectedVoiceTemplate == template) GoldAccent.copy(alpha = 0.15f) else CardSlate)
                                        .border(0.5.dp, if (selectedVoiceTemplate == template) GoldAccent else TextMutedSlate.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                        .clickable { selectedVoiceTemplate = template }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = template.take(30) + "...",
                                        color = if (selectedVoiceTemplate == template) GoldAccent else TextWarmCream,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isRecordingVoiceSim) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AlertCoral.copy(alpha = 0.12f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = AlertCoral, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "🎤 Spoken audio recording... $voiceCountdown s remaining",
                                    color = AlertCoral,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = { isRecordingVoiceSim = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = customVoiceQuery.isNotBlank() || selectedVoiceTemplate.isNotBlank()
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = "Speak", tint = ObsidianBackground)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Transmit External Voice Note", color = ObsidianBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // B) Screen Share / Frame Capture Trigger
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, TextMutedSlate.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            "2. Screen-Share Frame Grabber & OCR",
                            color = TextWarmCream,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Simulate capture of bureaucratic portals or caseworker chats:",
                            color = TextMutedSlate,
                            fontSize = 9.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            screenShareTemplates.forEachIndexed { index, pair ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (selectedScreenTemplate == index) GoldAccent.copy(alpha = 0.12f) else Color.Transparent)
                                        .clickable { selectedScreenTemplate = index }
                                        .padding(6.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedScreenTemplate == index,
                                        onClick = { selectedScreenTemplate = index },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldAccent, unselectedColor = TextMutedSlate)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(pair.first, color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(pair.second, color = TextMutedSlate, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isCapturingScreenSim) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SuccessGreen.copy(alpha = 0.12f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "🖥️ Snapping system screenshot frame & parsing OCR...",
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    isCapturingScreenSim = true
                                    screenCapturedMessage = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ScreenShare, contentDescription = "Capture screen", tint = ObsidianBackground)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Capture Screen-Share Frame", color = ObsidianBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (screenCapturedMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(screenCapturedMessage, color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Buffer logs of captured overlays
                    if (voiceNotes.isNotEmpty() || screenShares.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "BUFFER LOG OF ASYNC CAPTURES (WAITING IN CASE CHAT):",
                            color = TextMutedSlate,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            voiceNotes.take(3).forEach { note ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CardSlate.copy(alpha = 0.8f))
                                        .border(0.5.dp, TextMutedSlate.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = "Voice note icon", tint = GoldAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Voice Note: '${note.first}'", color = TextWarmCream, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Status: Background response updated. Ready to review.", color = SuccessGreen, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            screenShares.take(3).forEach { share ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CardSlate.copy(alpha = 0.8f))
                                        .border(0.5.dp, TextMutedSlate.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ScreenShare, contentDescription = "Screen cap icon", tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Captured: ${share.first}", color = TextWarmCream, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("Indexed directly in Case dockets + Background chatbot session.", color = GoldAccent, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Case Selector Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Case Selection Database", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            text = activeCase?.title ?: "Select a Case",
                            color = TextWarmCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Box {
                        Button(
                            onClick = { showCaseSelectDropdown = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                            modifier = Modifier.border(1.dp, GoldAccent, RoundedCornerShape(8.dp))
                        ) {
                            Text("Switch Database Case", color = GoldAccent, fontSize = 12.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = GoldAccent)
                        }

                        DropdownMenu(
                            expanded = showCaseSelectDropdown,
                            onDismissRequest = { showCaseSelectDropdown = false },
                            modifier = Modifier.background(CardSlate)
                        ) {
                            cases.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.title, color = TextWarmCream) },
                                    trailingIcon = {
                                        IconButton(onClick = { onDeleteCase(c) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertCoral, modifier = Modifier.size(16.dp))
                                        }
                                    },
                                    onClick = {
                                        onSelectCase(c.id)
                                        showCaseSelectDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (activeCase != null) {
            // Reminders and Alerts Division
            item {
                var remTiming by remember { mutableStateOf("1 Day Before") }
                var remRepetition by remember { mutableStateOf("Once") }
                var remSound by remember { mutableStateOf("Calm Chime") }
                var remAction by remember { mutableStateOf("Block Calendar Buffer Hours") }
                
                // Device calendars checklist toggles
                var syncGoogle by remember { mutableStateOf(true) }
                var syncOutlook by remember { mutableStateOf(false) }
                var syncApple by remember { mutableStateOf(false) }

                val conflictState = calendarConflictAnalysis

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = "Reminders", tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Integratable Alarms & Calendar Sync", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Reminders List
                        if (reminders.isEmpty()) {
                            Text("No alerts configured. Add a hearing or response due date below.", color = TextMutedSlate, fontSize = 13.sp)
                        } else {
                            reminders.forEach { reminder ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Checkbox(
                                            checked = reminder.isCompleted,
                                            onCheckedChange = { onToggleReminder(reminder) },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = SuccessGreen,
                                                uncheckedColor = TextMutedSlate
                                            )
                                        )
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = reminder.title,
                                                    color = if (reminder.isCompleted) TextMutedSlate else TextWarmCream,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                if (reminder.notificationSound != "Silence" && !reminder.isCompleted) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.VolumeUp,
                                                        contentDescription = "Chime Active",
                                                        tint = SuccessGreen,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Due: ${reminder.dueDate} • Timing: ${reminder.reminderTiming} • Repetitive: ${reminder.repetition} • Sound: ${reminder.notificationSound}", 
                                                color = AlertCoral, 
                                                fontSize = 10.sp
                                            )
                                            if (reminder.customAction.isNotBlank() && reminder.customAction != "None") {
                                                Text(
                                                    text = "🛡️ Overwhelm Mitigation: ${reminder.customAction}", 
                                                    color = GoldAccent, 
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                    IconButton(onClick = { onDeleteReminder(reminder) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertCoral, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Divider(color = TextMutedSlate.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                        // Add reminder row inputs
                        OutlinedTextField(
                            value = reminderTitle,
                            onValueChange = { reminderTitle = it },
                            placeholder = { Text("Hearing, Filing, or Appointment Title") },
                            label = { Text("Alarm / Task Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                focusedTextColor = TextWarmCream,
                                unfocusedTextColor = TextWarmCream
                            ),
                            textStyle = TextStyle(fontSize = 13.sp),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = reminderDate,
                                onValueChange = { reminderDate = it },
                                placeholder = { Text("Date (e.g. 06/15)") },
                                label = { Text("Due Date") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldAccent,
                                    focusedTextColor = TextWarmCream,
                                    unfocusedTextColor = TextWarmCream
                                ),
                                textStyle = TextStyle(fontSize = 13.sp),
                                modifier = Modifier.weight(1f).height(56.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // Alarm sound options
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text("Alert Voice/Chime Preset", color = TextMutedSlate, fontSize = 10.sp)
                                Row {
                                    listOf("Calm Chime", "Gentle Synth", "Nature Flow", "Silence").forEach { sound ->
                                        FilterChip(
                                            selected = remSound == sound,
                                            onClick = { remSound = sound },
                                            label = { Text(sound, fontSize = 9.sp, color = if(remSound == sound) ObsidianBackground else TextWarmCream) },
                                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, containerColor = CardSlate)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Repetition Selection Options
                        Text("Repetition Schedule:", color = TextMutedSlate, fontSize = 11.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Once", "Daily Routine", "Weekly Pattern", "Progressive Alarm Loop").forEach { cycle ->
                                FilterChip(
                                    selected = remRepetition == cycle,
                                    onClick = { remRepetition = cycle },
                                    label = { Text(cycle, fontSize = 9.sp, color = if(remRepetition == cycle) ObsidianBackground else TextWarmCream) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, containerColor = CardSlate)
                                )
                            }
                        }

                        // Burnout Protective Actions
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Burnout Protective Actions Overlay:", color = TextMutedSlate, fontSize = 11.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("None", "Block Calendar Buffer Hours", "Auto-Email Backup").forEach { act ->
                                FilterChip(
                                    selected = remAction == act,
                                    onClick = { remAction = act },
                                    label = { Text(act, fontSize = 9.sp, color = if(remAction == act) ObsidianBackground else TextWarmCream) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, containerColor = CardSlate)
                                )
                            }
                        }

                        // Sync Toggles selector
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Sync Feeds Integration (Anti-Overbooking stream):", color = TextMutedSlate, fontSize = 11.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = syncGoogle, onCheckedChange = { syncGoogle = it }, colors = CheckboxDefaults.colors(checkedColor = GoldAccent))
                                Text("Google Cal", color = TextWarmCream, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = syncOutlook, onCheckedChange = { syncOutlook = it }, colors = CheckboxDefaults.colors(checkedColor = GoldAccent))
                                Text("Outlook", color = TextWarmCream, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = syncApple, onCheckedChange = { syncApple = it }, colors = CheckboxDefaults.colors(checkedColor = GoldAccent))
                                Text("Apple iCal", color = TextWarmCream, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Conflict verification checker
                            Button(
                                onClick = {
                                    if (reminderDate.isNotBlank()) {
                                        viewModel.runCalendarConflictCheck(reminderDate, reminderTitle.ifBlank { "Court Obligation Detail" })
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                                modifier = Modifier.border(0.5.dp, GoldAccent, RoundedCornerShape(8.dp)),
                                enabled = reminderDate.isNotBlank()
                            ) {
                                Icon(Icons.Default.CloudQueue, contentDescription = "Clash Check", tint = GoldAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Audit Sync Overlaps", color = GoldAccent, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    if (reminderTitle.isNotBlank()) {
                                        val packedCalendars = buildString {
                                            if (syncGoogle) append("Google,")
                                            if (syncOutlook) append("Outlook,")
                                            if (syncApple) append("Apple,")
                                        }.removeSuffix(",")

                                        viewModel.addReminderCustom(
                                            title = reminderTitle,
                                            dueDate = reminderDate.ifBlank { "06/15" },
                                            reminderTiming = remTiming,
                                            repetition = remRepetition,
                                            notificationSound = remSound,
                                            customAction = remAction,
                                            associatedDeviceCalendars = packedCalendars
                                        )
                                        reminderTitle = ""
                                        reminderDate = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text("Save Alarm Preset", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Conflict result warnings
                        if (conflictState != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ObsidianBackground),
                                border = BorderStroke(1.dp, AlertCoral.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = "Conflict Alert", tint = AlertCoral, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("AUTOMATED SCHEDULING INTERLOCK RESOLUTION", color = AlertCoral, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = conflictState!!,
                                        color = TextWarmCream,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // NEW COMPONENT: TACTICAL LEGAL STICKY NOTES WITH NEURODIVERGENT CONTROLS
            // ==========================================
            item {
                // Visual option toggles
                var optPin by remember { mutableStateOf(true) }
                var optHighlighter by remember { mutableStateOf(false) }
                var optSticky by remember { mutableStateOf(true) }
                var optCitation by remember { mutableStateOf(false) }

                var selectedFontSize by remember { mutableStateOf(12) } // 10, 12, 14, 16, 18
                var selectedFontFamily by remember { mutableStateOf("Sans-serif") } // "Sans-serif", "Monospace", "Serif"

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = "Sticky Notes", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tactical Legal Notes & Interactive Pins", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            IconButton(onClick = { isStickyFormExpanded = !isStickyFormExpanded }) {
                                Icon(
                                    imageVector = if (isStickyFormExpanded) Icons.Default.Close else Icons.Default.Add,
                                    contentDescription = "Expand/Collapse Notes Form",
                                    tint = GoldAccent
                                )
                            }
                        }

                        Text(
                            text = "Draft quick tactical arguments, citations, or pinned case concepts. Fully customizable visual styles and sizes let you tailor the workspace to accommodate how you work best.",
                            color = TextMutedSlate,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        if (isStickyFormExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ObsidianBackground.copy(alpha = 0.5f)),
                                border = BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    OutlinedTextField(
                                        value = stickyTitle,
                                        onValueChange = { stickyTitle = it },
                                        label = { Text("Note Title / Catchphrase") },
                                        placeholder = { Text("e.g., RTFA Section Protection") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldAccent,
                                            focusedTextColor = TextWarmCream,
                                            unfocusedTextColor = TextWarmCream
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = stickyContent,
                                        onValueChange = { stickyContent = it },
                                        label = { Text("Quick Note / Argument Content") },
                                        placeholder = { Text("Paste statutes, notes or brief templates here...") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldAccent,
                                            focusedTextColor = TextWarmCream,
                                            unfocusedTextColor = TextWarmCream
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // STYLE OPTIONS (COMBINATIONS ALLOWED)
                                    Text("Visual Style (Choose Any Combination):", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = optPin, onCheckedChange = { optPin = it }, colors = CheckboxDefaults.colors(checkedColor = GoldAccent))
                                            Text("Pushpin", color = TextWarmCream, fontSize = 11.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = optHighlighter, onCheckedChange = { optHighlighter = it }, colors = CheckboxDefaults.colors(checkedColor = GoldAccent))
                                            Text("Highlighter", color = TextWarmCream, fontSize = 11.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = optSticky, onCheckedChange = { optSticky = it }, colors = CheckboxDefaults.colors(checkedColor = GoldAccent))
                                            Text("Sticky Frame", color = TextWarmCream, fontSize = 11.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = optCitation, onCheckedChange = { optCitation = it }, colors = CheckboxDefaults.colors(checkedColor = GoldAccent))
                                            Text("Citation Style", color = TextWarmCream, fontSize = 11.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // TYPOGRAPHY CHOICES
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Column {
                                            Text("Font Size:", color = TextMutedSlate, fontSize = 11.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                listOf(10, 12, 14, 16, 18).forEach { size ->
                                                    FilterChip(
                                                        selected = selectedFontSize == size,
                                                        onClick = { selectedFontSize = size },
                                                        label = { Text("${size}sp", fontSize = 10.sp, color = if(selectedFontSize == size) ObsidianBackground else TextWarmCream) },
                                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, containerColor = CardSlate)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Column {
                                            Text("Font Family:", color = TextMutedSlate, fontSize = 11.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                listOf("Sans-serif", "Monospace", "Serif").forEach { family ->
                                                    FilterChip(
                                                        selected = selectedFontFamily == family,
                                                        onClick = { selectedFontFamily = family },
                                                        label = { Text(family, fontSize = 10.sp, color = if(selectedFontFamily == family) ObsidianBackground else TextWarmCream) },
                                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, containerColor = CardSlate)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Color Theme Tint:", color = TextMutedSlate, fontSize = 11.sp)
                                        listOf("Cyan", "Pink", "Orange", "Mint", "Amber", "Lavender").forEach { colorName ->
                                            val badgeColor = when(colorName) {
                                                "Cyan" -> Color(0xFF60A5FA)
                                                "Pink" -> Color(0xFFF472B6)
                                                "Orange" -> Color(0xFFFB923C)
                                                "Mint" -> Color(0xFF34D399)
                                                "Amber" -> Color(0xFFFBBF24)
                                                "Lavender" -> Color(0xFFC084FC)
                                                else -> GoldAccent
                                            }
                                            FilterChip(
                                                selected = stickyColorHex == colorName,
                                                onClick = { stickyColorHex = colorName },
                                                label = { Text(colorName, fontSize = 10.sp, color = if(stickyColorHex == colorName) ObsidianBackground else TextWarmCream) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = badgeColor,
                                                    containerColor = CardSlate
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            if (stickyContent.isNotBlank()) {
                                                val packedStyle = buildString {
                                                    if (optPin) append("PIN,")
                                                    if (optHighlighter) append("HIGHLIGHTER,")
                                                    if (optSticky) append("STICKY,")
                                                    if (optCitation) append("CITATION,")
                                                }.removeSuffix(",")

                                                viewModel.addStickyNoteCustom(
                                                    title = if(stickyTitle.isNotBlank()) stickyTitle else "Quick Note Reference",
                                                    content = stickyContent,
                                                    colorHex = stickyColorHex,
                                                    noteStyle = packedStyle,
                                                    fontSize = selectedFontSize,
                                                    fontFamily = selectedFontFamily
                                                )
                                                stickyTitle = ""
                                                stickyContent = ""
                                                isStickyFormExpanded = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Save Custom Note", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Rendering list of sticky notes
                        if (stickyNotes.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No quick sticky notes drafted for this case database. Click the '+' above to add some.", color = TextMutedSlate, fontSize = 11.sp, fontStyle = FontStyle.Italic)
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                stickyNotes.forEach { note ->
                                    val noteTint = when (note.colorHex) {
                                        "Cyan" -> Color(0xFF60A5FA)
                                        "Pink" -> Color(0xFFF472B6)
                                        "Orange" -> Color(0xFFFB923C)
                                        "Mint" -> Color(0xFF34D399)
                                        "Amber" -> Color(0xFFFBBF24)
                                        "Lavender" -> Color(0xFFC084FC)
                                        else -> GoldAccent
                                    }
                                    val noteStylePacked = note.noteStyle ?: "STICKY"
                                    val hasPinCard = noteStylePacked.contains("PIN")
                                    val hasHighlighterCard = noteStylePacked.contains("HIGHLIGHTER")
                                    val hasStickyCard = noteStylePacked.contains("STICKY")
                                    val hasCitationCard = noteStylePacked.contains("CITATION")

                                    val finalFontSize = (note.fontSize ?: 12).sp
                                    val finalFontFamily = when(note.fontFamily ?: "Sans-serif") {
                                        "Monospace" -> androidx.compose.ui.text.font.FontFamily.Monospace
                                        "Serif" -> androidx.compose.ui.text.font.FontFamily.Serif
                                        else -> androidx.compose.ui.text.font.FontFamily.SansSerif
                                    }

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (hasStickyCard) ObsidianBackground.copy(alpha = 0.45f) else CardSlate.copy(alpha = 0.8f)
                                        ),
                                        border = BorderStroke(
                                            if (hasStickyCard) 1.5.dp else 0.5.dp, 
                                            if(note.isPinned) noteTint else noteTint.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .width(230.dp)
                                            .height(200.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Row(
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                        Icon(
                                                            imageVector = if (hasPinCard) Icons.Default.LocationOn else Icons.Default.Star,
                                                            contentDescription = "Pinned",
                                                            tint = if (hasPinCard) AlertCoral else (if (note.isPinned) noteTint else TextMutedSlate.copy(alpha = 0.3f)),
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = note.title,
                                                            color = noteTint,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = { onTogglePinStickyNote(note) },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Bookmark,
                                                            contentDescription = "Toggle Pin",
                                                            tint = if (note.isPinned) noteTint else TextMutedSlate,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                
                                                // Highlighter visual effect wrapping the scroll block
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (hasHighlighterCard) noteTint.copy(alpha = 0.25f) else Color.Transparent)
                                                        .padding(if (hasHighlighterCard) 6.dp else 0.dp)
                                                ) {
                                                    Text(
                                                        text = if (hasCitationCard) "§ ${note.content} (Ref: ${note.title})" else note.content,
                                                        color = TextWarmCream,
                                                        fontSize = finalFontSize,
                                                        fontFamily = finalFontFamily,
                                                        lineHeight = (finalFontSize.value + 4).sp,
                                                        maxLines = 6,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                if (hasCitationCard) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "• SCAO FORM COMPANION INDEXED •",
                                                        color = GoldAccent,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                                    )
                                                }
                                            }
                                            Row(
                                                horizontalArrangement = Arrangement.End,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                IconButton(
                                                    onClick = { onDeleteStickyNote(note) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Delete Sticky",
                                                        tint = AlertCoral.copy(alpha = 0.6f),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =======================================================
            // NEW COMPONENT: CHRONOLOGICAL STATEMENT OF FACTS TIMELINE
            // =======================================================
            item {
                var aiScannerInput by remember { mutableStateOf("") }
                var expandedFactFolderId by remember { mutableStateOf<Int?>(null) }
                var expandedSubfolderTab by remember { mutableStateOf("evidence") } // "evidence", "notes", "drafts", "documents"
                var newSubitemTitle by remember { mutableStateOf("") }
                var newSubitemContent by remember { mutableStateOf("") }
                
                val isScanningFact = isAnalyzingFact

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Gavel, contentDescription = "Timeline", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Chronological Statement of Facts Master Timeline", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            Row {
                                IconButton(onClick = { isTimelineFormExpanded = !isTimelineFormExpanded }) {
                                    Icon(
                                        imageVector = if (isTimelineFormExpanded) Icons.Default.Close else Icons.Default.Add,
                                        contentDescription = "Expand/Collapse Timeline Form",
                                        tint = GoldAccent
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Log, scan, and construct a strict chronological sequence of actions, visits, psychiatry reports, class attendance, and caseworker statements. Organizes and orders elements automatically by date.",
                            color = TextMutedSlate,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // --- NEW: AI Forensic Document/Record Scanner ---
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ObsidianBackground.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Scan", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Assisted Forensic Incident & Document Scanner", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Paste raw letters, supervisor text streams, clinical psychiatry notes, class logs, or case worker email notifications. AI automatically scans date, actors, and issues and builds chronological statements with subfolders.", color = TextMutedSlate, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = aiScannerInput,
                                    onValueChange = { aiScannerInput = it },
                                    placeholder = { Text("Paste any messy text, transcripts, summaries, or reports here to scan...", fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldAccent,
                                        unfocusedBorderColor = TextMutedSlate.copy(alpha = 0.3f),
                                        focusedTextColor = TextWarmCream,
                                        unfocusedTextColor = TextWarmCream
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("CREDIT REQUIRED: 1 UNIT", color = GoldAccent.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    
                                    Button(
                                        onClick = {
                                            if (aiScannerInput.isNotBlank()) {
                                                viewModel.analyzeAndAddChronologicalFact(aiScannerInput)
                                                aiScannerInput = ""
                                            }
                                        },
                                        enabled = !isScanningFact && aiScannerInput.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        if (isScanningFact) {
                                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = ObsidianBackground)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Sifting Document...", color = ObsidianBackground, fontSize = 11.sp)
                                        } else {
                                            Icon(Icons.Default.UploadFile, contentDescription = "AI Button", modifier = Modifier.size(14.dp), tint = ObsidianBackground)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Upload & Scan with AI", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        if (isTimelineFormExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.8f)),
                                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("MANUAL TIMELINE RECORD ENTRY", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    var factCategorySelected by remember { mutableStateOf("Note") }
                                    val categoriesList = listOf("Note", "Photo", "Psychiatry Report", "Class Attendance", "Email", "Call/Text Record", "Document")

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = factDate,
                                            onValueChange = { factDate = it },
                                            label = { Text("Fact Date (e.g. 2026-05-15)") },
                                            placeholder = { Text("YYYY-MM-DD") },
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream, unfocusedTextColor = TextWarmCream),
                                            modifier = Modifier.weight(1.2f)
                                        )

                                        Column(modifier = Modifier.weight(1.8f)) {
                                            Text("Category / Event Type", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                                                categoriesList.forEach { cat ->
                                                    FilterChip(
                                                        selected = factCategorySelected == cat,
                                                        onClick = { factCategorySelected = cat },
                                                        label = { Text(cat, fontSize = 10.sp, color = TextWarmCream) },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = GoldAccent.copy(alpha = 0.3f),
                                                            selectedLabelColor = GoldAccent
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(
                                        value = factDesc,
                                        onValueChange = { factDesc = it },
                                        label = { Text("Exact Fact Description") },
                                        placeholder = { Text("State exactly what transpired. Maintain absolute objectivity.") },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream, unfocusedTextColor = TextWarmCream),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = factActors,
                                            onValueChange = { factActors = it },
                                            label = { Text("Public Servants Involved") },
                                            placeholder = { Text("E.g. Officer Smith") },
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream, unfocusedTextColor = TextWarmCream),
                                            modifier = Modifier.weight(1f)
                                        )

                                        OutlinedTextField(
                                            value = factEvidence,
                                            onValueChange = { factEvidence = it },
                                            label = { Text("Supporting Exhibit Location") },
                                            placeholder = { Text("E.g. Exhibit C / Audio.mp3") },
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream, unfocusedTextColor = TextWarmCream),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(
                                        value = factPolicy,
                                        onValueChange = { factPolicy = it },
                                        label = { Text("Contradicted Court Rule / Policy Code") },
                                        placeholder = { Text("E.g. 14th Amendment, MRE 801 hearsays, SCAO Rule 3") },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream, unfocusedTextColor = TextWarmCream),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            if (factDate.isNotBlank() && factDesc.isNotBlank()) {
                                                viewModel.addChronologicalFactDetail(
                                                    date = factDate,
                                                    description = factDesc,
                                                    actors = factActors,
                                                    policy = factPolicy,
                                                    evidence = factEvidence,
                                                    category = factCategorySelected,
                                                    associatedDocsJson = "[]"
                                                )
                                                factDate = ""
                                                factDesc = ""
                                                factActors = ""
                                                factPolicy = ""
                                                factEvidence = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = "Lock In Fact")
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Manually Log Into Timeline", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        if (facts.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No timeline sequence logged yet. Select '+' to manually add, or use the AI Forensic Incident Scanner above.", color = TextMutedSlate, fontSize = 11.sp, fontStyle = FontStyle.Italic)
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ObsidianBackground.copy(alpha = 0.3f)),
                                border = BorderStroke(0.5.dp, TextMutedSlate.copy(alpha = 0.15f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    // Header
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardSlate)
                                            .padding(6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("DATE & CATEGORY", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(1.0f))
                                        Text("FACT DETAILS & POLICY", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(2.0f))
                                        Text("", modifier = Modifier.width(60.dp)) // space for actions
                                    }

                                    // Helper block to parse sub-vault JSON inside facts list
                                    data class SubDocItem(val type: String, val title: String, val content: String)
                                    fun parseSubVaultItems(json: String): List<SubDocItem> {
                                        if (json.isBlank() || json == "[]") return emptyList()
                                        val items = mutableListOf<SubDocItem>()
                                        try {
                                            val regex = Regex("""\{"type":"([^"]*)","title":"([^"]*)","content":"([^"]*)"\}""")
                                            regex.findAll(json).forEach { m ->
                                                items.add(SubDocItem(m.groupValues[1], m.groupValues[2], m.groupValues[3]))
                                            }
                                        } catch (e: Exception) {}
                                        return items
                                    }

                                    // Rows
                                    facts.forEach { fact ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(4.dp)
                                                .border(0.5.dp, if (expandedFactFolderId == fact.id) GoldAccent.copy(alpha = 0.4f) else TextMutedSlate.copy(alpha = 0.1f))
                                                .padding(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                // Date / Category Column
                                                Column(modifier = Modifier.weight(1.0f)) {
                                                    Text(fact.factDate, color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    
                                                    val catColor = when (fact.category) {
                                                        "Psychiatry Report" -> Color(0xFFC084FC)
                                                        "Class Attendance" -> Color(0xFF60A5FA)
                                                        "Email" -> Color(0xFF34D399)
                                                        "Call/Text Record" -> Color(0xFFFB923C)
                                                        "Photo" -> Color(0xFFF472B6)
                                                        else -> TextMutedSlate
                                                    }
                                                    Surface(
                                                        color = catColor.copy(alpha = 0.12f),
                                                        border = BorderStroke(0.5.dp, catColor.copy(alpha = 0.5f)),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = fact.category,
                                                            color = catColor,
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }

                                                    if (fact.actorsInvolved.isNotBlank()) {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text("Agent: ${fact.actorsInvolved}", color = GoldAccent, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    }
                                                }
                                                
                                                // Fact description Column
                                                Column(modifier = Modifier.weight(2.0f)) {
                                                    Text(fact.description, color = TextWarmCream, fontSize = 11.sp, lineHeight = 14.sp)
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        if (fact.violatedPolicy.isNotBlank()) {
                                                            Surface(
                                                                color = AlertCoral.copy(alpha = 0.15f),
                                                                border = BorderStroke(0.5.dp, AlertCoral.copy(alpha = 0.5f)),
                                                                shape = RoundedCornerShape(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = "Contradiction: ${fact.violatedPolicy}",
                                                                    color = AlertCoral,
                                                                    fontSize = 8.sp,
                                                                    fontWeight = FontWeight.Medium,
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                        if (fact.supportingEvidence.isNotBlank()) {
                                                            Surface(
                                                                color = SuccessGreen.copy(alpha = 0.15f),
                                                                border = BorderStroke(0.5.dp, SuccessGreen.copy(alpha = 0.5f)),
                                                                shape = RoundedCornerShape(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = "Evidence: ${fact.supportingEvidence}",
                                                                    color = SuccessGreen,
                                                                    fontSize = 8.sp,
                                                                    fontWeight = FontWeight.Medium,
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }

                                                // Action Column
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    modifier = Modifier.width(60.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            if (expandedFactFolderId == fact.id) {
                                                                expandedFactFolderId = null
                                                            } else {
                                                                expandedFactFolderId = fact.id
                                                            }
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Folder,
                                                            contentDescription = "Open Folders",
                                                            tint = if (expandedFactFolderId == fact.id) GoldAccent else TextMutedSlate,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }

                                                    IconButton(
                                                        onClick = { onDeleteFact(fact) },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Delete,
                                                            contentDescription = "Delete Fact",
                                                            tint = AlertCoral.copy(alpha = 0.7f),
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            // --- EXPANDED SUBFOLDER STRUCTURING (EVIDENCE, NOTES, DRAFTS, DOCUMENTS) ---
                                            if (expandedFactFolderId == fact.id) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Card(
                                                    colors = CardDefaults.cardColors(containerColor = ObsidianBackground),
                                                    border = BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.3f)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Icon(Icons.Default.FolderOpen, contentDescription = "Subfolder", tint = GoldAccent, modifier = Modifier.size(14.dp))
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text("EVENT VAULT SUBFOLDER: EVENT #${fact.id}", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                            }
                                                            Text("SEPARATED COPIES", color = TextMutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                        }

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        // Subfolder Navigation Tabs
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            listOf(
                                                                "evidence" to "📂 EVIDENCE",
                                                                "notes" to "📝 NOTES",
                                                                "drafts" to "✍️ DRAFTS",
                                                                "documents" to "📄 DOCUMENTS"
                                                            ).forEach { (tabId, label) ->
                                                                Surface(
                                                                    color = if (expandedSubfolderTab == tabId) GoldAccent.copy(alpha = 0.25f) else CardSlate,
                                                                    border = BorderStroke(0.5.dp, if (expandedSubfolderTab == tabId) GoldAccent else Color.Transparent),
                                                                    shape = RoundedCornerShape(4.dp),
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .clickable { expandedSubfolderTab = tabId }
                                                                ) {
                                                                    Text(
                                                                        text = label,
                                                                        color = if (expandedSubfolderTab == tabId) GoldAccent else TextMutedSlate,
                                                                        fontSize = 8.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        Spacer(modifier = Modifier.height(10.dp))

                                                        // Render list of items inside selected subfolder
                                                        val subDocsParsed = parseSubVaultItems(fact.associatedDocsJson)
                                                        val filteredSubdocs = subDocsParsed.filter { it.type == expandedSubfolderTab }

                                                        if (filteredSubdocs.isEmpty()) {
                                                            Text(
                                                                text = "No items saved inside this ${expandedSubfolderTab.uppercase()} sub-folder yet.",
                                                                color = TextMutedSlate,
                                                                fontSize = 9.sp,
                                                                fontStyle = FontStyle.Italic,
                                                                modifier = Modifier.padding(vertical = 4.dp)
                                                            )
                                                        } else {
                                                            filteredSubdocs.forEach { subItem ->
                                                                Card(
                                                                    colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.5f)),
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(vertical = 2.dp)
                                                                ) {
                                                                    Column(modifier = Modifier.padding(6.dp)) {
                                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                                            Icon(
                                                                                imageVector = when (expandedSubfolderTab) {
                                                                                    "evidence" -> Icons.Default.Attachment
                                                                                    "notes" -> Icons.Default.Comment
                                                                                    "drafts" -> Icons.Default.EditNote
                                                                                    else -> Icons.Default.CheckCircle
                                                                                },
                                                                                contentDescription = "Doc",
                                                                                tint = GoldAccent,
                                                                                modifier = Modifier.size(12.dp)
                                                                            )
                                                                            Spacer(modifier = Modifier.width(4.dp))
                                                                            Text(subItem.title, color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                                        }
                                                                        Spacer(modifier = Modifier.height(2.dp))
                                                                        Text(subItem.content, color = TextMutedSlate, fontSize = 9.sp, lineHeight = 12.sp)
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        // Add new item to this specific subfolder
                                                        Card(
                                                            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.2f)),
                                                            border = BorderStroke(0.5.dp, TextMutedSlate.copy(alpha = 0.2f)),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Column(modifier = Modifier.padding(6.dp)) {
                                                                Text("SAVE COPY TO THIS ${expandedSubfolderTab.uppercase()} SUBFOLDER:", color = GoldAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                    OutlinedTextField(
                                                                        value = newSubitemTitle,
                                                                        onValueChange = { newSubitemTitle = it },
                                                                        label = { Text("Title", fontSize = 8.sp) },
                                                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                                                        modifier = Modifier.weight(1f)
                                                                    )
                                                                    OutlinedTextField(
                                                                        value = newSubitemContent,
                                                                        onValueChange = { newSubitemContent = it },
                                                                        label = { Text("Content Descriptions", fontSize = 8.sp) },
                                                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                                                        modifier = Modifier.weight(2f)
                                                                    )
                                                                    IconButton(
                                                                        onClick = {
                                                                            if (newSubitemTitle.isNotBlank()) {
                                                                                val newItem = SubDocItem(expandedSubfolderTab, newSubitemTitle, newSubitemContent)
                                                                                val newList = subDocsParsed + newItem
                                                                                
                                                                                // Serialize back to JSON string manually to keep dependency simple and ultra-safe from Moshi adapter issues
                                                                                val jsonBuilder = StringBuilder("[")
                                                                                newList.forEachIndexed { idx, it ->
                                                                                    jsonBuilder.append("""{"type":"${it.type}","title":"${it.title.replace("\"", "\\\"")}","content":"${it.content.replace("\"", "\\\"")}"}""")
                                                                                    if (idx < newList.size - 1) jsonBuilder.append(",")
                                                                                }
                                                                                jsonBuilder.append("]")
                                                                                
                                                                                viewModel.updateChronologicalFactDocs(fact.id, jsonBuilder.toString())
                                                                                newSubitemTitle = ""
                                                                                newSubitemContent = ""
                                                                            }
                                                                        },
                                                                        modifier = Modifier
                                                                            .size(36.dp)
                                                                            .align(Alignment.CenterVertically)
                                                                    ) {
                                                                        Icon(Icons.Default.FileUpload, contentDescription = "Add subdoc", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // NEW COMPONENT: SIMULATED IN-APP SERVICE EMAIL BOX
            // ==========================================
            item {
                var isEmailTabExpanded by remember { mutableStateOf(false) }
                var selectedEmailThread by remember { mutableStateOf<String?>(null) }
                var composeTo by remember { mutableStateOf("") }
                var composeSubject by remember { mutableStateOf("") }
                var composeBody by remember { mutableStateOf("") }
                var personalEmailLinkInput by remember { mutableStateOf("rkahabka091117@gmail.com") }
                
                val emails = simulatedEmails

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = "Email Box", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("In-App Email Service Chamber (Proof of Service Link)", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            IconButton(onClick = { isEmailTabExpanded = !isEmailTabExpanded }) {
                                Icon(
                                    imageVector = if (isEmailTabExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand/Collapse Email Portal",
                                    tint = GoldAccent
                                )
                            }
                        }

                        Text(
                            text = "A dedicated secure email sandbox connected to SCAO e-Filing pathways. Starts fresh email strings for filings, links personal copy addresses, and auto-generates Proof of Service MC-11 Forms.",
                            color = TextMutedSlate,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        if (isEmailTabExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Link existing email configuration
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ObsidianBackground.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.SettingsCell, contentDescription = "Link", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                    OutlinedTextField(
                                        value = personalEmailLinkInput,
                                        onValueChange = { personalEmailLinkInput = it },
                                        label = { Text("Link Litigant Personal Email", fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("Double-Inbox Delivery Mode Active", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Compose & Serve Section
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text("COMPOSE OFFICIAL COURT SERVICE OUTREACH", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(
                                        value = composeTo,
                                        onValueChange = { composeTo = it },
                                        label = { Text("Designated OPPOSING Attn Recipients / Caseworker Group") },
                                        placeholder = { Text("E.g. opposition@agency.org, supervisor@mdhhs.gov") },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = composeSubject,
                                        onValueChange = { composeSubject = it },
                                        label = { Text("Filing Title (Starts New Clean Thread)") },
                                        placeholder = { Text("E.g. MOTION TO SHOW CAUSE RESPONSE") },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = composeBody,
                                        onValueChange = { composeBody = it },
                                        label = { Text("Transmissive Serving Notice content") },
                                        placeholder = { Text("Describe the attachments and court delivery parameters...") },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                        modifier = Modifier.fillMaxWidth().height(100.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            if (composeTo.isNotBlank() && composeSubject.isNotBlank()) {
                                                viewModel.sendFilingServiceEmail(
                                                    caseId = activeCase?.id ?: 1,
                                                    threadId = "tr_" + (1000..9999).random(),
                                                    subject = composeSubject,
                                                    body = composeBody,
                                                    recipientGroup = composeTo,
                                                    personalEmailToLink = personalEmailLinkInput
                                                )
                                                composeTo = ""
                                                composeSubject = ""
                                                composeBody = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                        modifier = Modifier.align(Alignment.End),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Serve", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Serve & Attach Proof of Service MC-11", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Interactive Sidebar Threads
                                Column(modifier = Modifier.weight(1.1f)) {
                                    Text("SERVICE THREADS INBOX", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (emails.isEmpty()) {
                                        Text("No email string threads established.", color = TextMutedSlate, fontSize = 9.sp, fontStyle = FontStyle.Italic)
                                    } else {
                                        emails.forEach { mail ->
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (selectedEmailThread == mail.id) GoldAccent.copy(alpha = 0.15f) else ObsidianBackground
                                                ),
                                                border = BorderStroke(0.5.dp, if (selectedEmailThread == mail.id) GoldAccent else TextMutedSlate.copy(alpha = 0.3f)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp)
                                                    .clickable { selectedEmailThread = mail.id }
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Text(mail.subject, color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text("Group: ${mail.recipientGroup.take(20)}...", color = TextMutedSlate, fontSize = 8.sp)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                        Text(mail.timestamp, color = GoldAccent, fontSize = 8.sp)
                                                        if (mail.hasProofOfService) {
                                                            Text("✓ MC-11 Attached", color = SuccessGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Email Detail Overlay panel when thread clicked
                            selectedEmailThread?.let { selectedId ->
                                val emailItem = emails.find { it.id == selectedId }
                                if (emailItem != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = ObsidianBackground),
                                        border = BorderStroke(1.dp, GoldAccent),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Clean Service Thread details", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                IconButton(onClick = { selectedEmailThread = null }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Close, contentDescription = "Close detail", tint = AlertCoral)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("SENDER: ${emailItem.sender}", color = TextWarmCream, fontSize = 10.sp)
                                            Text("SERVED RECIPIENTS: ${emailItem.recipientGroup}", color = TextWarmCream, fontSize = 10.sp)
                                            Text("SUBJECT: ${emailItem.subject}", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("TIMESTAMP: ${emailItem.timestamp}", color = TextMutedSlate, fontSize = 9.sp)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Divider(color = TextMutedSlate.copy(alpha = 0.2f))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = emailItem.body,
                                                color = TextWarmCream,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .heightIn(max = 200.dp)
                                                    .verticalScroll(rememberScrollState())
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // NEW COMPONENT: GOOGLE VOICE & SYNC GATEWAY
            // ==========================================
            item {
                var isCommsExpanded by remember { mutableStateOf(false) }
                var syncPartyInput by remember { mutableStateOf("") }
                var syncTypeInput by remember { mutableStateOf("Call") } // "Call", "Text"
                var syncBodyInput by remember { mutableStateOf("") }
                
                val records = commsRecords

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SettingsCell, contentDescription = "Cloud comms", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cloud Call & Text Record Sync Gateway", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            IconButton(onClick = { isCommsExpanded = !isCommsExpanded }) {
                                Icon(
                                    imageVector = if (isCommsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand Comms",
                                    tint = GoldAccent
                                )
                            }
                        }

                        Text(
                            text = "Keep direct tracking of official telephone calls and written text logs. Simulates server handshakes to pull conversations with caseworkers and sync them chronologically inside fact charts.",
                            color = TextMutedSlate,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        if (isCommsExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Input Sync Form
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1.2f)) {
                                    OutlinedTextField(
                                        value = syncPartyInput,
                                        onValueChange = { syncPartyInput = it },
                                        label = { Text("Remote Party Name", fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf("Call", "Text").forEach { type ->
                                            Surface(
                                                color = if (syncTypeInput == type) GoldAccent.copy(alpha = 0.2f) else ObsidianBackground,
                                                border = BorderStroke(0.5.dp, if (syncTypeInput == type) GoldAccent else TextMutedSlate.copy(alpha = 0.2f)),
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { syncTypeInput = type }
                                                    .padding(vertical = 4.dp)
                                            ) {
                                                Text(type, color = if (syncTypeInput == type) GoldAccent else TextMutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                            }
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = syncBodyInput,
                                    onValueChange = { syncBodyInput = it },
                                    label = { Text("Call Transcript Summary / Text Message content", fontSize = 10.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                    modifier = Modifier
                                        .weight(1.8f)
                                        .height(80.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (syncPartyInput.isNotBlank() && syncBodyInput.isNotBlank()) {
                                        viewModel.runVoiceCallCloudSync(syncPartyInput, syncTypeInput, syncBodyInput)
                                        syncPartyInput = ""
                                        syncBodyInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync & Commit to Statement of Facts", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("SYNCED COMMUNICATIONS RECORD HISTORY:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            records.forEach { record ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = ObsidianBackground),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (record.type == "Call") Icons.Default.Call else Icons.Default.Message,
                                            contentDescription = "comms",
                                            tint = if (record.type == "Call") GoldAccent else SuccessGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                Text(record.remoteParty, color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                Text(record.timestamp, color = TextMutedSlate, fontSize = 8.sp)
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(record.transcriptSummary, color = TextWarmCream, fontSize = 10.sp, lineHeight = 13.sp)
                                            if (record.type == "Call" && record.durationSeconds > 0) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text("Duration: ${record.durationSeconds} seconds • Audio Verification Anchored", color = GoldAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // NEW COMPONENT: AI SCREEN SHARE OCR ASSISTANT
            // ==========================================
            item {
                var isScreenExpanded by remember { mutableStateOf(false) }
                var screenDescriptionInput by remember { mutableStateOf("") }
                
                val screenOcrResult = screenShareCaptureResult

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Computer, contentDescription = "Screen Share", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI Screen Capture OCR & Document Formatter", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            IconButton(onClick = { isScreenExpanded = !isScreenExpanded }) {
                                Icon(
                                    imageVector = if (isScreenExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand Screen",
                                    tint = GoldAccent
                                )
                            }
                        }

                        Text(
                            text = "Take screen captures of administrative app records, messy medical sheets, or state notices. Vision AI executes instant OCR, structural correction, SCAO mapping, and outputs ready for e-File.",
                            color = TextMutedSlate,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        if (isScreenExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            OutlinedTextField(
                                value = screenDescriptionInput,
                                onValueChange = { screenDescriptionInput = it },
                                label = { Text("Simulate Screen Capture (Describe the visual contents & text to parse)") },
                                placeholder = { Text("E.g. Screenshot of SCAO custody recommendation form dated 5/10/26 stating sole custody is recommended but signs of parent stability are evident...") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (screenDescriptionInput.isNotBlank()) {
                                        viewModel.runScreenShareCaptureAndOCR(screenDescriptionInput)
                                        screenDescriptionInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Vision AI", modifier = Modifier.size(16.dp), tint = ObsidianBackground)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Perform OCR Capture Conversion", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            if (!screenOcrResult.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("CONVERTED OCR COURT FORMAT OUTPUT:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = ObsidianBackground),
                                    border = BorderStroke(0.5.dp, GoldAccent),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = screenOcrResult!!,
                                        color = TextWarmCream,
                                        fontSize = 11.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        lineHeight = 15.sp,
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .heightIn(max = 200.dp)
                                            .verticalScroll(rememberScrollState())
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Easy Voice Notes / Typing Events Recorder
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Real-Time Event Recorder", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            // Simulated Voice Speech To Text button
                            Button(
                                onClick = {
                                    isMicRecordingSim = !isMicRecordingSim
                                    if (isMicRecordingSim) {
                                        // Simulate recording a live event and appending after speech
                                        notesText += "\n[Live Event Voice Entry - 2026-05-30]: Witnessed CPS worker entering premises without presenting an official safety warrant. Retaliation was verbally threatened if barn was not opened immediately."
                                        onUpdateNotes(notesText)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMicRecordingSim) AlertCoral else GoldAccent.copy(alpha = 0.2f)
                                )
                            ) {
                                Icon(
                                    imageVector = if (isMicRecordingSim) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = "Simulate mic",
                                    tint = if (isMicRecordingSim) Color.White else GoldAccent
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isMicRecordingSim) "Recording Voice..." else "Tap to Speak Note",
                                    color = if (isMicRecordingSim) Color.White else GoldAccent,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Easily document incidents as they happen. Type or use simulated voice STT to lock evidence directly into Case Notes.", color = TextMutedSlate, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = notesText,
                            onValueChange = {
                                notesText = it
                                onUpdateNotes(it)
                            },
                            label = { Text("Consolidated Case Notes") },
                            placeholder = { Text("Log timelines, employee badges, dates, and event transcripts...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = TextMutedSlate.copy(alpha = 0.4f),
                                focusedTextColor = TextWarmCream,
                                unfocusedTextColor = TextWarmCream
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                    }
                }
            }

            // Consolidated External Chat/Doc Compiler Portal
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudSync, contentDescription = "Import", tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Outside AI assistant compilation engine", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connect outputs and conversations from Claude, ChatGPT, Gemini, or other assistants. Paste them here to synthesize all case knowledge under a singular secure repository.",
                            color = TextMutedSlate,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Claude", "ChatGPT", "Gemini", "Other App").forEach { app ->
                                FilterChip(
                                    selected = importSource == app,
                                    onClick = { importSource = app },
                                    label = { Text(app, color = TextWarmCream) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldAccent.copy(alpha = 0.3f),
                                        selectedLabelColor = GoldAccent
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = importText,
                            onValueChange = { importText = it },
                            placeholder = { Text("Paste entire chat histories, summaries, or research drafts retrieved from $importSource...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                focusedTextColor = TextWarmCream,
                                unfocusedTextColor = TextWarmCream
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (importText.isNotBlank()) {
                                    onImportChat(importSource, importText)
                                    importText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Import Button")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Compile with Active Case File", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                        }

                        if (activeCase?.externalIntegrations?.isNotBlank() == true) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("COMPILED CHATS & RETRIEVED CONTENT:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 120.dp)
                                    .background(ObsidianBackground)
                                    .border(0.5.dp, TextMutedSlate.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = activeCase?.externalIntegrations ?: "",
                                    color = TextWarmCream,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // ========================================================
            // 💭 NEW COMPONENT: CONTEXTUAL AI CHAT COUNSEL & SCHEDULING ALARMS
            // ========================================================
            item {
                var chatPrompt by remember { mutableStateOf("") }
                var customThreadTitleInput by remember { mutableStateOf("") }
                var selectedThreadId by remember { mutableStateOf<Int?>(null) }
                var showThreadSelectorDropdown by remember { mutableStateOf(false) }

                // Chat companion task alarms
                var isSchedulingChatAlarm by remember { mutableStateOf(false) }
                var chatAlarmDate by remember { mutableStateOf("") }
                var chatAlarmTitle by remember { mutableStateOf("") }

                val aiThreads = aiChatThreads
                val isChatting = isChattingWithAi

                val activeThreadCurrentSelected = aiThreads.find { it.id == selectedThreadId }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().testTag("context_ai_counsel_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudQueue, contentDescription = "AI Summons", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("💭 Summon Contextual AI Assistant", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            
                            Text(
                                text = "Connected Context",
                                color = SuccessGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                modifier = Modifier
                                    .background(SuccessGreen.copy(alpha = 0.12f))
                                    .border(0.5.dp, SuccessGreen, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Summon an AI companion trained to explain legal rules, draft outline checklists, and find strategies. Active threads compile with your local facts sequence automatically.",
                            color = TextMutedSlate,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // SESSION THREAD DRAWER SELECTORS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                Button(
                                    onClick = { showThreadSelectorDropdown = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianBackground),
                                    modifier = Modifier.fillMaxWidth().border(0.5.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = activeThreadCurrentSelected?.sessionName ?: "➕ (New Conversation Stream)",
                                            color = GoldAccent,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                    }
                                }

                                DropdownMenu(
                                    expanded = showThreadSelectorDropdown,
                                    onDismissRequest = { showThreadSelectorDropdown = false },
                                    modifier = Modifier.background(CardSlate).width(260.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("➕ Start New Conversation Stream", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                        onClick = {
                                            selectedThreadId = null
                                            customThreadTitleInput = ""
                                            showThreadSelectorDropdown = false
                                        }
                                    )
                                    Divider(color = TextMutedSlate.copy(alpha = 0.2f))
                                    aiThreads.forEach { thread ->
                                        DropdownMenuItem(
                                            text = { Text(thread.sessionName, color = TextWarmCream, fontSize = 11.sp) },
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = { viewModel.deleteChatThread(thread) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, "Delete Thread", tint = AlertCoral, modifier = Modifier.size(12.dp))
                                                }
                                            },
                                            onClick = {
                                                selectedThreadId = thread.id
                                                customThreadTitleInput = thread.sessionName
                                                showThreadSelectorDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Option to set custom title if creating a new stream
                        if (selectedThreadId == null) {
                            OutlinedTextField(
                                value = customThreadTitleInput,
                                onValueChange = { customThreadTitleInput = it },
                                placeholder = { Text("Thread Title (e.g. Zoning Research)") },
                                label = { Text("Stream Title") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldAccent,
                                    focusedTextColor = TextWarmCream,
                                    unfocusedTextColor = TextWarmCream
                                ),
                                textStyle = TextStyle(fontSize = 12.sp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // CHAT HISTORIC STREAM BUBBLES
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .background(ObsidianBackground)
                                .border(0.5.dp, TextMutedSlate.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            if (activeThreadCurrentSelected == null) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.QuestionAnswer, contentDescription = "Query Assistant", tint = TextMutedSlate.copy(alpha = 0.4f), modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Ask a question to start. Conversation will persist.", color = TextMutedSlate, fontSize = 11.sp)
                                }
                            } else {
                                // Parse messages manually and scroll them
                                val messagesList = mutableListOf<Map<String, String>>()
                                val regex = Regex("""\{"role":"([^"]*)","text":"([^"]*)","timestamp":"([^"]*)"\}""")
                                regex.findAll(activeThreadCurrentSelected.fullThreadJson).forEach { m ->
                                    val cleanedText = m.groupValues[2].replace("\\\"", "\"").replace("\\n", "\n")
                                    messagesList.add(mapOf("role" to m.groupValues[1], "text" to cleanedText, "timestamp" to m.groupValues[3]))
                                }

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(messagesList) { msg ->
                                        val isAi = msg["role"] == "assistant"
                                        val bubbleBg = if (isAi) CardSlate else GoldAccent.copy(alpha = 0.15f)
                                        val align = if (isAi) Alignment.Start else Alignment.End
                                        
                                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = bubbleBg),
                                                shape = RoundedCornerShape(
                                                    topStart = 8.dp,
                                                    topEnd = 8.dp,
                                                    bottomStart = if (isAi) 0.dp else 8.dp,
                                                    bottomEnd = if (isAi) 8.dp else 0.dp
                                                ),
                                                border = BorderStroke(0.5.dp, if (isAi) TextMutedSlate.copy(alpha = 0.15f) else GoldAccent.copy(alpha = 0.3f)),
                                                modifier = Modifier.widthIn(max = 240.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Text(
                                                        text = if (isAi) "🛡_ COMPANION" else "👤 LITIGANT",
                                                        color = if (isAi) GoldAccent else TextWarmCream,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = msg["text"] ?: "",
                                                        color = TextWarmCream,
                                                        fontSize = 11.sp,
                                                        lineHeight = 15.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = msg["timestamp"] ?: "",
                                                        color = TextMutedSlate,
                                                        fontSize = 8.sp,
                                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                        modifier = Modifier.align(Alignment.End)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Processing compilation alert indicators
                        if (isChatting) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = GoldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compiling timeline context & summoning Gemini Pro assistance...", color = GoldAccent, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // INPUT MESSAGE BOX BAR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = chatPrompt,
                                onValueChange = { chatPrompt = it },
                                placeholder = { Text("Ask legal standards, help verify files...") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldAccent,
                                    focusedTextColor = TextWarmCream,
                                    unfocusedTextColor = TextWarmCream
                                ),
                                textStyle = TextStyle(fontSize = 12.sp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (chatPrompt.isNotBlank()) {
                                        viewModel.sendAiChatPrompt(
                                            sessionName = customThreadTitleInput.ifBlank { "Query: " + chatPrompt.take(15) },
                                            promptText = chatPrompt,
                                            existingThreadId = selectedThreadId
                                        )
                                        chatPrompt = ""
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(GoldAccent)
                                    .size(44.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Query Stream", tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                            }
                        }

                        // CHAT-LEVEL REMINDER BUILD PANEL (FOR GETFUL USERS)
                        if (activeThreadCurrentSelected != null) {
                            Divider(color = TextMutedSlate.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 10.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "📅 Schedule Follow-up Alarm for this Research Thread", 
                                    color = GoldAccent, 
                                    fontWeight = FontWeight.SemiBold, 
                                    fontSize = 11.sp
                                )
                                IconButton(onClick = { isSchedulingChatAlarm = !isSchedulingChatAlarm }) {
                                    Icon(
                                        imageVector = if (isSchedulingChatAlarm) Icons.Default.Close else Icons.Default.CalendarToday,
                                        tint = GoldAccent,
                                        contentDescription = "Trigger Reminder Dialog",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (isSchedulingChatAlarm) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = ObsidianBackground.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedTextField(
                                                value = chatAlarmTitle,
                                                onValueChange = { chatAlarmTitle = it },
                                                placeholder = { Text("e.g. Read Case Law Thread") },
                                                label = { Text("Task Action Name") },
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                                                textStyle = TextStyle(fontSize = 11.sp),
                                                modifier = Modifier.weight(1.2f).height(46.dp)
                                            )

                                            OutlinedTextField(
                                                value = chatAlarmDate,
                                                onValueChange = { chatAlarmDate = it },
                                                placeholder = { Text("Date (e.g. 06/20)") },
                                                label = { Text("Remind Date") },
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                                                textStyle = TextStyle(fontSize = 11.sp),
                                                modifier = Modifier.weight(1f).height(46.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                if (chatAlarmDate.isNotBlank()) {
                                                    viewModel.addChatReminder(
                                                        threadId = activeThreadCurrentSelected.id,
                                                        alarmDate = chatAlarmDate
                                                    )
                                                    // Create equivalent task reminder automatically
                                                    viewModel.addReminderCustom(
                                                        title = "📚 REVIEW CHAT STREAM: " + (chatAlarmTitle.ifBlank { activeThreadCurrentSelected.sessionName }),
                                                        dueDate = chatAlarmDate,
                                                        reminderTiming = "1 Day Before",
                                                        repetition = "Once",
                                                        notificationSound = "Calm Chime",
                                                        customAction = "Focus Priority Focus Overlay",
                                                        associatedDeviceCalendars = "Google"
                                                    )
                                                    chatAlarmTitle = ""
                                                    chatAlarmDate = ""
                                                    isSchedulingChatAlarm = false
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                            modifier = Modifier.align(Alignment.End).height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp)
                                        ) {
                                            Text("Register Research Reminder", color = ObsidianBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                AffiliateAndAdSponsorshipSection(tabName = "Dashboard", viewModel = viewModel)
            }
        }
    }
}

// ==========================================
// TAB 1: FILINGS AND CHECKLIST / SERVICE
// ==========================================
@Composable
fun FilingsTab(
    documents: List<CaseDocument>,
    checklists: List<DocumentChecklist>,
    expenses: List<CaseExpense>,
    onToggleChecklist: (DocumentChecklist) -> Unit,
    onAddChecklist: (String, Boolean) -> Unit,
    onSaveDoc: (String, String, String, String) -> Unit,
    onDeleteDoc: (CaseDocument) -> Unit,
    onGenerateGuidanceDraft: (String, String) -> Unit,
    isGenerating: Boolean,
    onAddExpense: (String, String, Double, String, String) -> Unit,
    onDeleteExpense: (CaseExpense) -> Unit,
    onVerifyWaiver: (Double) -> Unit,
    isUnlimited: Boolean,
    viewModel: CaseViewModel
) {
    var selectedSubtab by remember { mutableStateOf(0) } // 0 = Folder Vault, 1 = Expense & Tortious Loss Ledger

    val isEfilingInProgress by viewModel.isEfilingInProgress.collectAsStateWithLifecycle()
    val isPortalPaying by viewModel.isPortalPaying.collectAsStateWithLifecycle()
    val paymentPortalResult by viewModel.paymentPortalResult.collectAsStateWithLifecycle()

    // Tab 0 States
    var checklistInput by remember { mutableStateOf("") }
    var expandedFilingGuide by remember { mutableStateOf<String?>(null) }
    var manualDocTitle by remember { mutableStateOf("") }
    var manualDocType by remember { mutableStateOf("SCAO Form") }
    var manualDocContent by remember { mutableStateOf("") }
    var manualDocService by remember { mutableStateOf("") }
    var selectedGuidanceFacts by remember { mutableStateOf("") }

    // Tab 1 States
    var expenseCategory by remember { mutableStateOf("Filing Fee") }
    var expenseAmount by remember { mutableStateOf("") }
    var expenseDate by remember { mutableStateOf("") }
    var expenseDesc by remember { mutableStateOf("") }
    var expenseReceipt by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedSubtab,
            containerColor = CardSlate,
            contentColor = GoldAccent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedSubtab == 0,
                onClick = { selectedSubtab = 0 },
                text = { Text("📂 Filings Index", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                selectedContentColor = GoldAccent,
                unselectedContentColor = TextMutedSlate
            )
            Tab(
                selected = selectedSubtab == 1,
                onClick = { selectedSubtab = 1 },
                text = { Text("💰 Damage Ledger", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                selectedContentColor = GoldAccent,
                unselectedContentColor = TextMutedSlate
            )
        }

        if (selectedSubtab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
        // Document Verification Checklist Index
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FactCheck, contentDescription = "Check", tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Required Attachment Checklist", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        // Progress Badge
                        val attached = checklists.count { it.isAttached }
                        val total = checklists.size
                        Text(
                            text = if (total > 0) "$attached/$total Complete" else "0/0",
                            color = if (attached == total && total > 0) SuccessGreen else GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Verify that all legally mandated exhibits, proof of service affidavits, SCAO structures, and poverty indices are compiled in this envelope prior to final submission:",
                        color = TextMutedSlate,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (checklists.isEmpty()) {
                        Text("No checklist items configured.", color = TextMutedSlate)
                    } else {
                        checklists.forEach { check ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Checkbox(
                                        checked = check.isAttached,
                                        onCheckedChange = { onToggleChecklist(check) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = SuccessGreen,
                                            uncheckedColor = TextMutedSlate
                                        )
                                    )
                                    Text(
                                        text = check.itemName,
                                        color = if (check.isAttached) TextMutedSlate else TextWarmCream,
                                        fontSize = 13.sp
                                    )
                                }
                                if (check.isRequired) {
                                    Text(
                                        text = "Req",
                                        color = AlertCoral.copy(alpha = 0.8f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .border(0.5.dp, AlertCoral.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = checklistInput,
                            onValueChange = { checklistInput = it },
                            placeholder = { Text("Add Custom Exhibit Check...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                focusedTextColor = TextWarmCream,
                                unfocusedTextColor = TextWarmCream
                            ),
                            textStyle = TextStyle(fontSize = 13.sp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (checklistInput.isNotBlank()) {
                                    onAddChecklist(checklistInput, false)
                                    checklistInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Add", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Response Guidance System ("How do I Respond?")
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HelpCenter, contentDescription = "Help Guide", tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Accurately Instructed Response Guide", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select any Motion, Order or Petition to reveal procedural guidelines, required SCAO filings, and use Gemini to pre-compile your response:", color = TextMutedSlate, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Guidance accordion items
                    val templates = listOf(
                        "SCAO Form FOC 44 (Response to motion regarding custody/parenting)" to
                                "Use this to contest custody/parenting actions. Requires submitting detailed answers to each allegation. Must assert children’s established custodial environment protection and lack of 'observable behavior' danger.",
                        "Show Cause Order Response (Contempt)" to
                                "Filed when cooperating with/facing a contempt accusation. Must detail legal justification (e.g. inability to pay due to verified extreme poverty or clerical mistake by contractor). Use SCAO Form MC 230.",
                        "Answer to CPS / Abuse and Neglect Petition" to
                                "A formal, line-by-line response to CPS state emergency petitions. Strictly raise Fourth Amendment unwarranted entry protection, absence of imminent risk, and demand proof of any 'observable behavioral change' protocols.",
                        "Michigan Right to Farm Act (RTFA) Protections Response" to
                                "Use this custom statutory filing template to respond to local municipality zoning violations, nuisance complaints, or property abatement enforcement actions. This incorporates PA 116, the 2026 Farm Bill, and GAAMPs (Generally Accepted Agricultural and Management Practices) to override local municipal zoning restrictions on family farming."
                    )

                    templates.forEach { (title, helpText) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ObsidianBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    expandedFilingGuide = if (expandedFilingGuide == title) null else title
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(title, color = GoldAccent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = if (expandedFilingGuide == title) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = GoldAccent
                                    )
                                }

                                if (expandedFilingGuide == title) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(helpText, color = TextWarmCream, fontSize = 12.sp, lineHeight = 16.sp)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = selectedGuidanceFacts,
                                        onValueChange = { selectedGuidanceFacts = it },
                                        placeholder = { Text("Specify your personal facts and timeline here to help Gemini draft your Answer framework automatically...") },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            if (selectedGuidanceFacts.isNotBlank()) {
                                                onGenerateGuidanceDraft(title, selectedGuidanceFacts)
                                                selectedGuidanceFacts = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                        enabled = !isGenerating,
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        if (isGenerating) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianBackground)
                                        } else {
                                            Text("AI Pre-Compile Response", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Core Document Creator & Service Register form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddHomeWork, contentDescription = "Editor", tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Interactive Document Repository & Safe Storage", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = manualDocTitle,
                        onValueChange = { manualDocTitle = it },
                        label = { Text("Document Title / Filing Name") },
                        placeholder = { Text("e.g. Defendant's Objections to CPS") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("SCAO Form", "Response", "Custom Filing").forEach { type ->
                            FilterChip(
                                selected = manualDocType == type,
                                onClick = { manualDocType = type },
                                label = { Text(type, color = TextWarmCream) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent.copy(alpha = 0.3f),
                                    selectedLabelColor = GoldAccent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = manualDocContent,
                        onValueChange = { manualDocContent = it },
                        label = { Text("Filing Content (Text Statement)") },
                        placeholder = { Text("Type full legal statements, references, or motions here...") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = manualDocService,
                        onValueChange = { manualDocService = it },
                        label = { Text("Proof of Service & Verification Details") },
                        placeholder = { Text("e.g. Served on worker Smithe by certified mail on 2026-05-30") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (manualDocTitle.isNotBlank() && manualDocContent.isNotBlank()) {
                                onSaveDoc(manualDocTitle, manualDocType, manualDocContent, manualDocService)
                                manualDocTitle = ""
                                manualDocContent = ""
                                manualDocService = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Archive Filing Document", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Archive Documents Drawer Lists
        item {
            Text("ARCHIVED COMPLETED FILINGS", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (documents.isEmpty()) {
            item {
                Text("No archived filings found in vault. Create one above or run AI compile.", color = TextMutedSlate, fontSize = 12.sp)
            }
        } else {
            items(documents) { doc ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FilePresent, contentDescription = "Doc", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(doc.title, color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            IconButton(onClick = { onDeleteDoc(doc) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertCoral, modifier = Modifier.size(18.dp))
                            }
                        }

                        Text(
                            text = "Category: ${doc.docType}",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 100.dp)
                                .background(ObsidianBackground)
                                .border(0.5.dp, TextMutedSlate.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(doc.content, color = TextWarmCream, fontSize = 11.sp, lineHeight = 15.sp)
                        }

                        var selectedCourt by remember { mutableStateOf("3rd Circuit Court (Wayne County) MiFILE") }
                        var showCourtDropdown by remember { mutableStateOf(false) }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Description, contentDescription = "PDF File Icon", tint = AlertCoral, modifier = Modifier.size(14.dp))
                            Text("Standard Court PDF format (Michigan SCAO margins mapping)", color = TextMutedSlate, fontSize = 11.sp, fontStyle = FontStyle.Italic)
                        }

                        if (!doc.isFilingComplete) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("SELECT STATE COURT GATEWAY SYSTEM", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                Button(
                                    onClick = { showCourtDropdown = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianBackground),
                                    border = BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Text(selectedCourt, color = TextWarmCream, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                }
                                DropdownMenu(
                                    expanded = showCourtDropdown,
                                    onDismissRequest = { showCourtDropdown = false },
                                    modifier = Modifier.background(CardSlate)
                                ) {
                                    listOf(
                                        "3rd Circuit Court (Wayne County) MiFILE",
                                        "6th Circuit Court (Oakland County) MiFILE",
                                        "17th Circuit Court (Kent County) MiFILE",
                                        "9th Circuit Court (Kalamazoo County) MiFILE",
                                        "Supreme Court of Michigan SCAO Gateway"
                                    ).forEach { court ->
                                        DropdownMenuItem(
                                            text = { Text(court, color = TextWarmCream, fontSize = 11.sp) },
                                            onClick = {
                                                selectedCourt = court
                                                showCourtDropdown = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.runEFileSubmission(doc, selectedCourt)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isEfilingInProgress
                            ) {
                                if (isEfilingInProgress) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianBackground)
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = "Submit", tint = ObsidianBackground, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("SUBMIT & E-FILE WITHIN APP", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }

                        if (doc.serviceDetails.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verification & Service: ${doc.serviceDetails}", color = SuccessGreen, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            item {
                AffiliateAndAdSponsorshipSection(tabName = "Filings", viewModel = viewModel)
            }
        }
    }
} else {
            val categoriesList = listOf(
                "Filing Fee", "Process Server", "Printing/Ink", "Lost Wages", "Gas/Travel", "Emotional/Health Cost", "Tortious Interference Loss"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header card
                item {
                    val totalExpenses = expenses.filter { it.category != "Tortious Interference Loss" && it.category != "Emotional/Health Cost" }.sumOf { it.amount }
                    val totalTortiousLosses = expenses.filter { it.category == "Tortious Interference Loss" || it.category == "Emotional/Health Cost" }.sumOf { it.amount }
                    val overallClaimable = expenses.sumOf { it.amount }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingDown, contentDescription = "Losses", tint = AlertCoral)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Damage, Tortious Loss & Expense Summary", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Keep track of every cent exacted from you due to bad-faith agency actions, non-compliant hearings, or retaliatory interference. Maintain this spreadsheet as supplementary evidence of compensable harms.",
                                color = TextMutedSlate, fontSize = 12.sp, lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f).background(ObsidianBackground.copy(alpha = 0.5f)).border(0.5.dp, TextMutedSlate.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(10.dp)) {
                                    Text("Out-Of-Pocket Fees", color = TextMutedSlate, fontSize = 10.sp)
                                    Text("$${String.format("%.2f", totalExpenses)}", color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column(modifier = Modifier.weight(1f).background(ObsidianBackground.copy(alpha = 0.5f)).border(0.5.dp, AlertCoral.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(10.dp)) {
                                    Text("Tortious Interference", color = AlertCoral, fontSize = 10.sp)
                                    Text("$${String.format("%.2f", totalTortiousLosses)}", color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column(modifier = Modifier.weight(1f).background(ObsidianBackground.copy(alpha = 0.5f)).border(0.5.dp, SuccessGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(10.dp)) {
                                    Text("Total Damages", color = SuccessGreen, fontSize = 10.sp)
                                    Text("$${String.format("%.2f", overallClaimable)}", color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            if (overallClaimable > 0 && !isUnlimited) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        onVerifyWaiver(overallClaimable)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = "Waiver", tint = ObsidianBackground)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Verify Losses to Waive AI Fees (Get Free Unlimited Access)", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            } else if (isUnlimited) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                                    border = BorderStroke(0.5.dp, SuccessGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                                        Icon(Icons.Default.DoneAll, contentDescription = "Active Waiver", tint = SuccessGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Active Fee Waiver: Unlimited AI Access Unlocked based on documented tortious losses.", color = SuccessGreen, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // ========================================================
                // 💳 SECURE SCAO DIRECT ELECTRONIC PAYMENT PORTAL GATEWAY
                // ========================================================
                item {
                    var gateCourtName by remember { mutableStateOf("Third Judicial Circuit Court of Michigan") }
                    var gateFeeType by remember { mutableStateOf("Filing Fee: Civil Motion") }
                    var gateFeeAmount by remember { mutableStateOf(150.00) }
                    var gateCardNum by remember { mutableStateOf("") }
                    var gateCardExpiry by remember { mutableStateOf("") }
                    var gateCardCvv by remember { mutableStateOf("") }
                    var gateApplyWaiverMC20 by remember { mutableStateOf(false) }

                    val isPaying = isPortalPaying
                    val paymentResult = paymentPortalResult

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        border = BorderStroke(1.5.dp, GoldAccent),
                        modifier = Modifier.fillMaxWidth().testTag("scao_payment_portal_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CreditCard, contentDescription = "Card System", tint = GoldAccent)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("SCAO Direct Electronic Payment Gateway", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Text(
                                    text = "SECURE EFT",
                                    color = SuccessGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(SuccessGreen.copy(alpha = 0.15f))
                                        .border(0.5.dp, SuccessGreen, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Direct integration simulating official state court financial portals. Complete filings payments or apply for official MCR 2.002 fee waivers inline.",
                                color = TextMutedSlate, fontSize = 11.sp, lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Court Selector Drops
                            Text("Target Court Jurisdiction:", color = TextMutedSlate, fontSize = 11.sp)
                            var showCourtMenu by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { showCourtMenu = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWarmCream),
                                    border = BorderStroke(1.dp, TextMutedSlate.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(gateCourtName, color = TextWarmCream, fontSize = 12.sp)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = TextMutedSlate)
                                    }
                                }
                                DropdownMenu(
                                    expanded = showCourtMenu,
                                    onDismissRequest = { showCourtMenu = false },
                                    modifier = Modifier.background(CardSlate).fillMaxWidth(0.9f)
                                ) {
                                    listOf(
                                        "Third Judicial Circuit Court of Michigan (Wayne County)",
                                        "Oakland County Probate Court",
                                        "Grand Rapids 61st District Court",
                                        "Court of Appeals - District 1",
                                        "Michigan Supreme Court Direct Financial Center"
                                    ).forEach { court ->
                                        DropdownMenuItem(
                                            text = { Text(court, color = TextWarmCream, fontSize = 11.sp) },
                                            onClick = {
                                                gateCourtName = court
                                                showCourtMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Fee Categories Selection
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text("Fee Category & Action:", color = TextMutedSlate, fontSize = 11.sp)
                                    var showFeeMenu by remember { mutableStateOf(false) }
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedButton(
                                            onClick = { showFeeMenu = true },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWarmCream),
                                            border = BorderStroke(1.dp, TextMutedSlate.copy(alpha = 0.5f)),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(gateFeeType, color = TextWarmCream, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = TextMutedSlate)
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = showFeeMenu,
                                            onDismissRequest = { showFeeMenu = false },
                                            modifier = Modifier.background(CardSlate)
                                        ) {
                                            listOf(
                                                "Filing Fee: Civil Contempt Motion" to 150.00,
                                                "Filing Fee: Custody Petition Modification" to 80.00,
                                                "Process Service Fees Verification" to 26.00,
                                                "Summons Issuing Fee" to 15.00,
                                                "SCAO Special Copy & Certify Rate" to 42.00
                                            ).forEach { (t, valF) ->
                                                DropdownMenuItem(
                                                    text = { Text("$t ($${String.format("%.2f", valF)})", color = TextWarmCream, fontSize = 11.sp) },
                                                    onClick = {
                                                        gateFeeType = t
                                                        gateFeeAmount = valF
                                                        showFeeMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Fee Amount Evaluated:", color = TextMutedSlate, fontSize = 11.sp)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .background(ObsidianBackground)
                                            .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = "  $${String.format("%.2f", if (gateApplyWaiverMC20) 0.00 else gateFeeAmount)}",
                                            color = if (gateApplyWaiverMC20) SuccessGreen else AlertCoral,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Waiver checkbox
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ObsidianBackground.copy(alpha = 0.3f))
                                    .border(0.5.dp, GoldAccent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Checkbox(
                                    checked = gateApplyWaiverMC20,
                                    onCheckedChange = { gateApplyWaiverMC20 = it },
                                    colors = CheckboxDefaults.colors(checkedColor = SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text("Apply SCAO MC-20 Waiver Affidavit", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("MCR 2.002 verification: waives payment for low-income/SNAP.", color = TextMutedSlate, fontSize = 9.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (!gateApplyWaiverMC20) {
                                // Card credentials inputs
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = gateCardNum,
                                        onValueChange = { gateCardNum = it },
                                        placeholder = { Text("4111 2222 3333 4444") },
                                        label = { Text("Card Number", fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                        textStyle = TextStyle(fontSize = 11.sp),
                                        modifier = Modifier.weight(2f).height(46.dp)
                                    )
                                    OutlinedTextField(
                                        value = gateCardExpiry,
                                        onValueChange = { gateCardExpiry = it },
                                        placeholder = { Text("12/28") },
                                        label = { Text("Expiry", fontSize = 10.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                        textStyle = TextStyle(fontSize = 11.sp),
                                        modifier = Modifier.weight(1f).height(46.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.runSimulatedCourtFilingFeePayment(
                                        courtName = gateCourtName,
                                        feeCategory = gateFeeType,
                                        amount = gateFeeAmount,
                                        cardNumberSimulated = gateCardNum.ifBlank { "SIMULATED-FEE-WAIVER-MC20-NULL" },
                                        routingNumberSimulated = "SECURE-EFT-PORT-ROUTER-SIM",
                                        applyForFeeWaiverMc20 = gateApplyWaiverMC20
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (gateApplyWaiverMC20) SuccessGreen else GoldAccent),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isPaying
                            ) {
                                if (isPaying) {
                                    CircularProgressIndicator(color = ObsidianBackground, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Securing Gateway Connection with SCAO Financial Router...", color = ObsidianBackground, fontSize = 11.sp)
                                } else {
                                    Icon(Icons.Default.CreditCard, contentDescription = "EFT Execute", tint = ObsidianBackground)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (gateApplyWaiverMC20) "Submit Fee Waiver Validation Protocol" else "Execute Direct Secure EFT Payment",
                                        color = ObsidianBackground,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Payment system log outputs
                            if (paymentResult != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = ObsidianBackground),
                                    border = BorderStroke(1.dp, if (paymentResult!!.contains("✅")) SuccessGreen.copy(alpha = 0.5f) else AlertCoral.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "PORTAL RECEIPT BUFFER:",
                                            color = GoldAccent,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = paymentResult!!,
                                            color = TextWarmCream,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                        }
                    }
                }

                // Logging form
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Paid, contentDescription = "Add Expense", tint = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Log New Damage or Expense", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            // Category chips
                            Text("Category:", color = TextMutedSlate, fontSize = 11.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categoriesList.forEach { category ->
                                    FilterChip(
                                        selected = expenseCategory == category,
                                        onClick = { expenseCategory = category },
                                        label = { Text(category, fontSize = 10.sp, color = if(expenseCategory == category) ObsidianBackground else TextWarmCream) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldAccent,
                                            containerColor = CardSlate
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = expenseAmount,
                                    onValueChange = { expenseAmount = it },
                                    label = { Text("Amount ($)", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. 150.00") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = expenseDate,
                                    onValueChange = { expenseDate = it },
                                    label = { Text("Date Incurred", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. 05/30") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = expenseDesc,
                                onValueChange = { expenseDesc = it },
                                label = { Text("How Opposing Party Caused This Injury / Loss Details") },
                                placeholder = { Text("e.g. CPS retaliator demanded a mandatory private visit on short notice, forcing unpaid leave.") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = expenseReceipt,
                                onValueChange = { expenseReceipt = it },
                                label = { Text("Evidence Code / Receipt Reference") },
                                placeholder = { Text("e.g. Exhibit C (Unpaid leave stub)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val amt = expenseAmount.toDoubleOrNull() ?: 0.0
                                    val dateVal = if(expenseDate.isNotBlank()) expenseDate else "05/30"
                                    if (expenseDesc.isNotBlank() && amt > 0.0) {
                                        onAddExpense(expenseCategory, dateVal, amt, expenseDesc, expenseReceipt)
                                        expenseAmount = ""
                                        expenseDate = ""
                                        expenseDesc = ""
                                        expenseReceipt = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Add to Damage Ledger", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // List of logged damages
                if (expenses.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth().padding(24.dp)
                            ) {
                                Text("No expenses or damages registered for this case file yet.", color = TextMutedSlate, fontSize = 11.sp, fontStyle = FontStyle.Italic)
                            }
                        }
                    }
                } else {
                    item {
                        Text("Logged Case Damages & Incurred Costs", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    items(expenses) { exp ->
                        val isTort = exp.category == "Tortious Interference Loss" || exp.category == "Emotional/Health Cost"
                        val badgeColor = if (isTort) AlertCoral else GoldAccent
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = if (isTort) Icons.Default.Gavel else Icons.Default.ReceiptLong,
                                            contentDescription = "Category",
                                            tint = badgeColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(exp.category, color = badgeColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(exp.date, color = TextMutedSlate, fontSize = 11.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("$${String.format("%.2f", exp.amount)}", color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { onDeleteExpense(exp) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertCoral.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                Text(exp.description, color = TextWarmCream, fontSize = 11.sp, modifier = Modifier.padding(vertical = 4.dp), lineHeight = 15.sp)
                                if (exp.receiptReference.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AttachFile, contentDescription = "Receipt", tint = SuccessGreen, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(exp.receiptReference, color = SuccessGreen, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    AffiliateAndAdSponsorshipSection(tabName = "Filings", viewModel = viewModel)
                }
            }
        }
    }
}

// ==========================================
// TAB 2: AI POLICY VS PRACTICE AUDITOR / ONE-PAGER
// ==========================================
@Composable
fun PolicyAuditorTab(
    viewModel: CaseViewModel,
    activeCase: LegalCase?,
    userCredits: Int,
    isUnlimited: Boolean
) {
    var selectedSubtab by remember { mutableStateOf(0) } // 0 = AI Auditor, 1 = State & US Law Library

    val policyAnalysisResult by viewModel.policyAnalysisResult.collectAsStateWithLifecycle()
    val isAnalyzingPolicy by viewModel.isAnalyzingPolicy.collectAsStateWithLifecycle()
    val caseMisconducts by viewModel.caseMisconducts.collectAsStateWithLifecycle()
    val generatedOnePagerResult by viewModel.generatedOnePagerResult.collectAsStateWithLifecycle()
    val isGeneratingDoc by viewModel.isGeneratingDoc.collectAsStateWithLifecycle()

    // AI Auditor Subtab States
    var incidentText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Constitutional (1st, 4th, 14th)") }

    // Library Subtab States
    var searchQuery by remember { mutableStateOf("") }
    var selectedStateFilter by remember { mutableStateOf("All") }

    val categories = listOf(
        "Constitutional (1st, 4th, 14th)",
        "MDHHS/CPS Agency Policies",
        "Michigan Right to Farm Act (Zoning Support)",
        "Private Contractor Regulations"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedSubtab,
            containerColor = CardSlate,
            contentColor = GoldAccent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedSubtab == 0,
                onClick = { selectedSubtab = 0 },
                text = { Text("⚖️ AI Auditor", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                selectedContentColor = GoldAccent,
                unselectedContentColor = TextMutedSlate
            )
            Tab(
                selected = selectedSubtab == 1,
                onClick = { selectedSubtab = 1 },
                text = { Text("📖 Laws Library", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                selectedContentColor = GoldAccent,
                unselectedContentColor = TextMutedSlate
            )
        }

        if (selectedSubtab == 0) {

            val policyAnalysis = policyAnalysisResult
            val isAnalyzing = isAnalyzingPolicy
            val misconducts = caseMisconducts

            val wordPagerResult = generatedOnePagerResult
            val isGeneratingOnePager = isGeneratingDoc

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // AI remaining credit indicator
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlimited) SuccessGreen.copy(alpha = 0.12f) else CardSlate
                        ),
                        border = BorderStroke(
                            0.5.dp, if(isUnlimited) SuccessGreen else TextMutedSlate.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(12.dp).fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (isUnlimited) Icons.Default.AllInclusive else Icons.Default.Toll,
                                    contentDescription = "Credits",
                                    tint = if (isUnlimited) SuccessGreen else GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isUnlimited) "Unlimited AI Pro Se License Active" else "Pro Se AI License Credits: $userCredits Remaining",
                                    color = if (isUnlimited) SuccessGreen else TextWarmCream,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            if (!isUnlimited && userCredits <= 5) {
                                Text(
                                    "Low credits!",
                                    color = AlertCoral,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
        // Constitutional & Agency Rules Library
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Library", tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Michigan Policy & Rights Source Library", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val libraryContents = listOf(
                        "Constitutional Protections" to "Asserts First Amendment protections against retaliatory state actions, Fourth Amendment protections forbidding non-consensual entry without judicial warrants, and Fourteenth Amendment Due Process rights regarding parents' rights to care and custody.",
                        "MDHHS & CPS-Specific Rules" to "Imposes SCAO regulations, specifically demanding MDHHS state caseworkers verify and report concrete 'observable behavior changes' establishing custody risks, rather than relying on hearsay or subjective reports.",
                        "Michigan Right to Farm Act (RTFA)" to "Guarantees agricultural operations are exempted from local municipal zoning bullying if they meet state GAAMPs standards (updated under 2026 Farm Bill & PA 116). Under state law, zoning preemption is enforced.",
                        "Private Contractor Intake Policies" to "Mandates clear background audits for private visit supervisors, private foster agencies, and healthcare contractors who violate private family boundaries while receiving public state funding."
                    )

                    libraryContents.forEach { (heading, summaryText) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(heading, color = GoldAccent, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(summaryText, color = TextWarmCream, fontSize = 12.sp, lineHeight = 16.sp)
                            Divider(color = TextMutedSlate.copy(alpha = 0.1f), modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }

        // Real-Time 'Policy vs. Practice' Comparison Engine
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = "AI Audit", tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Real-time Policy vs. Practice Auditor", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Paste any action, search, or incident experienced on court/property lines, and let Gemini audit it directly against active rules & preemption codes:",
                        color = TextMutedSlate,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("AUDIT CONTEXT / CATEGORY:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    // Category Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp, color = TextWarmCream) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent.copy(alpha = 0.3f),
                                    selectedLabelColor = GoldAccent
                                )
                            )
                        }
                    }
                    
                    if (activeCase != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("AUTO-IMPORT FROM ACTIVE CASE:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    val notes = activeCase.caseNotes
                                    if (notes.isNotBlank()) {
                                        incidentText = notes
                                    } else {
                                        incidentText = "No active case notes found. Please add notes on the Dashboard first."
                                    }
                                },
                                label = { Text("📄 Active Case Notes", fontSize = 11.sp, color = TextWarmCream) },
                                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CardSlate
                                )
                            )
                            SuggestionChip(
                                onClick = {
                                    val ext = activeCase.externalIntegrations
                                    if (ext.isNotBlank()) {
                                        incidentText = ext
                                    } else {
                                        incidentText = "No imported chat records found in this case."
                                    }
                                },
                                label = { Text("🎙️ Imported Chats", fontSize = 11.sp, color = TextWarmCream) },
                                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CardSlate
                                )
                            )
                            SuggestionChip(
                                onClick = {
                                    if (misconducts.isNotEmpty()) {
                                        incidentText = misconducts.joinToString("\n") { 
                                            "- Officer/Employee: ${it.employeeName} (${it.agencyOrContractor}). Violation Date: ${it.violationDate}. Right Violated: ${it.constitutionalRightViolated}. Details: ${it.evidenceDescription}"
                                        }
                                    } else {
                                        incidentText = "No misconduct event logs manually registered for this case. Log them under Misconduct tab first."
                                    }
                                },
                                label = { Text("💼 Evidentiary Misconduct Logs", fontSize = 11.sp, color = TextWarmCream) },
                                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CardSlate
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = incidentText,
                        onValueChange = { incidentText = it },
                        label = { Text("Incident / Witness Statement Details") },
                        placeholder = { Text("e.g. Local zoning officer threatened to confiscate our small flock of chickens despite our agricultural commercial preemption declaration...") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Legal Security Disclaimer indicator to prevent UPL
                        Text("Legal Assistant Mode: ACTIVE", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        Button(
                            onClick = {
                                if (incidentText.isNotBlank()) {
                                    viewModel.runPolicyVsPracticeComparison(incidentText, selectedCategory)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            enabled = !isAnalyzing
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianBackground)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ScreenSearchDesktop, contentDescription = "Audit Btn", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Run Policy Discrepancy Audit", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    if (!policyAnalysis.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("AI CONTRADICTION & CITATION AUDIT RESULTS:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground)
                                .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = policyAnalysis!!,
                                    color = TextWarmCream,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.clickable {
                                        viewModel.saveDocument(
                                            title = "Policy Contradiction Audit: ${selectedCategory.take(20)}...",
                                            docType = "Policy Discrepancy",
                                            content = policyAnalysis!!,
                                            isComplete = true,
                                            serviceDetails = "AI Generated practice-vs-policy matrix saved in repository."
                                        )
                                    },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Archive, contentDescription = "Save", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Archive Audit Matrix to Filings Vault", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Legislative One-Pager Summarizer Briefing Generator
        if (activeCase != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PostAdd, contentDescription = "OnePager", tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("House Judiciary Legislative One-Pager", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Generate a formal, high-impact legislative briefing summarizing your case violations, tailored specifically for the House Judiciary Committee on Civil Rights.",
                            color = TextMutedSlate,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.generateLegislativeOnePager() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            enabled = !isGeneratingOnePager,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isGeneratingOnePager) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ObsidianBackground)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Analytics, contentDescription = "Generate Report", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate House Judiciary One-Pager", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (!wordPagerResult.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("GENERATED ONE-PAGER:", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ObsidianBackground)
                                    .border(0.5.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = wordPagerResult!!,
                                    color = TextWarmCream,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("✓ Saved automatically to your Documents Repository as 'Legislative One-Pager Briefing'", color = SuccessGreen, fontSize = 11.sp)
                        }
                    }
                }
            }
            item {
                AffiliateAndAdSponsorshipSection(tabName = "Policy", viewModel = viewModel)
            }
        }
    }
} else {
            val lawsList = listOf(
                LawItem(
                    jurisdiction = "Federal / USA",
                    citation = "US Constitution, Amendment I (Free Speech & Associations)",
                    summary = "Protects citizens, including pro se parents, from state policy retaliation for expressing critical sentiment, recording officials in public spaces, or petitioning agencies."
                ),
                LawItem(
                    jurisdiction = "Federal / USA",
                    citation = "US Constitution, Amendment IV (Unreasonable Search & Warrants)",
                    summary = "Explicitly prohibits caseworker or officer entry into a private residence without clear judicial warrant or active emergency threat. Hearsay does not waive Fourth Amendment safety protections."
                ),
                LawItem(
                    jurisdiction = "Federal / USA",
                    citation = "US Constitution, Amendment XIV (Due Process & parental rights)",
                    summary = "Establishes that the right of parents to the companionship, care, custody, and management of their children is a fundamental liberty interest protected by the Constitution."
                ),
                LawItem(
                    jurisdiction = "Federal / USA",
                    citation = "42 U.S. Code § 1983 (Civil Action for Deprivation of Rights)",
                    summary = "Allows litigants to sue state agents, contractors, and visit supervisors in federal court for damages if they knowingly violate constitutional parental rights under color of state law."
                ),
                LawItem(
                    jurisdiction = "Michigan",
                    citation = "Michigan Court Rule (MCR) 3.963 (Custody Protective Checklist)",
                    summary = "Demands that non-custodial caseworker overrides are only permitted when there is an immediate risk of harm, establishing and verifying observable behavior changes clearly."
                ),
                LawItem(
                    jurisdiction = "Michigan",
                    citation = "Michigan Right to Farm Act (MCL 286.471 preemption protections)",
                    summary = "Broad state-wide preemption guarding pro se agriculturalists, small-holding farms, or urban keepers from municipal zoning harassment if they operate according to GAAMP standards."
                ),
                LawItem(
                    jurisdiction = "Ohio",
                    citation = "Ohio Revised Code Section 2151.414 (Relative Placement Priority)",
                    summary = "Requires caseworker agencies to verify and exhaust all potential relatives or certified kin placements before committing children to system foster pipelines."
                ),
                LawItem(
                    jurisdiction = "Florida",
                    citation = "Florida Statute 39.401 (Warrant Requirements for Custody Intake)",
                    summary = "Mandates Florida protective supervisors secure formal judicial search warrants prior to removing minors from households, except in immediate, documented medical danger."
                ),
                LawItem(
                    jurisdiction = "Texas",
                    citation = "Texas Family Code § 261.3027 (Right to Record Caseworkers)",
                    summary = "Grants parents explicit legal authorization to capture complete audio and video recordings of caseworker interviews, home checks, and visit intakes. Caseworkers cannot deny parent records."
                )
            )

            val filteredLaws = lawsList.filter {
                val matchesState = selectedStateFilter == "All" || it.jurisdiction == selectedStateFilter
                val matchesSearch = it.citation.contains(searchQuery, ignoreCase = true) || it.summary.contains(searchQuery, ignoreCase = true)
                matchesState && matchesSearch
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Intro text
                item {
                    Text(
                        "State & Federal SCAO & Agency Rules Library",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "A comprehensive pro se reference directory compiled to level the playing field against adversarial councils and agency legal departments.",
                        color = TextMutedSlate,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                // Filter & Search Box
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Search citation code or rule topic...", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp)) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, focusedTextColor = TextWarmCream),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // State Filters
                            Text("Filter Jurisdiction:", color = TextMutedSlate, fontSize = 11.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("All", "Federal / USA", "Michigan", "Ohio", "Florida", "Texas").forEach { filter ->
                                    FilterChip(
                                        selected = selectedStateFilter == filter,
                                        onClick = { selectedStateFilter = filter },
                                        label = { Text(filter, fontSize = 10.sp, color = if(selectedStateFilter == filter) ObsidianBackground else TextWarmCream) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldAccent,
                                            containerColor = CardSlate
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // List
                if (filteredLaws.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No matching rule or policy found. Try searching simple terms like 'warrant', 'parental', or select 'All'.", color = TextMutedSlate, fontSize = 11.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                } else {
                    items(filteredLaws) { law ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardSlate),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = law.jurisdiction.uppercase(),
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier
                                            .background(GoldAccent.copy(alpha = 0.12f))
                                            .border(0.5.dp, GoldAccent, RoundedCornerShape(3.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                    Icon(Icons.Default.MenuBook, contentDescription = "Citation", tint = TextMutedSlate, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = law.citation,
                                    color = TextWarmCream,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = law.summary,
                                    color = TextWarmCream.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                    item {
                        AffiliateAndAdSponsorshipSection(tabName = "Policy", viewModel = viewModel)
                    }
                }
            }
        }
    }
}

data class LawItem(
    val jurisdiction: String,
    val citation: String,
    val summary: String
)

// ==========================================
// TAB 3: MISCONDUCT PATTERN GRAPH & PITCHING
// ==========================================
@Composable
fun MisconductTab(
    localMisconduct: List<MisconductEvent>,
    allMisconduct: List<MisconductEvent>,
    onLogMisconduct: (String, String, String, String, String, String, Boolean) -> Unit,
    onDeleteMisconduct: (MisconductEvent) -> Unit,
    activeCase: LegalCase?,
    userCredits: Int,
    isUnlimited: Boolean,
    onAwardAdCredits: () -> Unit,
    onVerifyIncomeWaiver: (String, String) -> Unit,
    viewModel: CaseViewModel
) {
    var violatorName by remember { mutableStateOf("") }
    var violatorAgency by remember { mutableStateOf("CPS") }
    var violationDate by remember { mutableStateOf("") }
    var manualPolicy by remember { mutableStateOf("") }
    var selectedRight by remember { mutableStateOf("Fourth Amendment") }
    var descriptionReason by remember { mutableStateOf("") }

    var isAnonMode by remember { mutableStateOf(false) }
    var showReportSuccessBanner by remember { mutableStateOf(false) }

    // Waiver & Ad State
    var incomeWaiverType by remember { mutableStateOf("Medicaid / SNAP / WIC") }
    var incomeWaiverNotes by remember { mutableStateOf("") }
    var isWatchingAd by remember { mutableStateOf(false) }

    val agencyOptions = listOf("CPS", "MDHHS", "SCAO", "Visit Supervisor", "Private Foster Agency", "Medical Provider")
    val rightOptions = listOf("First Amendment", "Fourth Amendment", "Fourteenth Amendment", "Policy Non-compliance")

    // Compile Repeating offenders list for Misconduct Pattern Recognition logic!
    val repeatViolators = allMisconduct
        .groupBy { it.employeeName.trim().lowercase() }
        .filter { it.key.isNotBlank() && it.value.size >= 2 }
        .map { Pair(it.value.first().employeeName, it.value.size) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sponsorship, Waiver & Credit Rewards Center
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "Sponsorship", tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sponsorship, Waiver & AI Credits", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        if (isUnlimited) {
                            Text(
                                "UNLIMITED",
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .background(SuccessGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .border(0.5.dp, SuccessGreen, RoundedCornerShape(3.dp))
                            )
                        } else {
                            Text(
                                "$userCredits Credits Remaining",
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "To ensure CaseVault is fully accessible, you can use the software for free by entering verified low-income assistance, reporting damage expenses, or watching sponsored short ads.",
                        color = TextMutedSlate,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isUnlimited) {
                        // Watch Sponsored Ad to Earn Credits Option
                        Button(
                            onClick = {
                                isWatchingAd = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, GoldAccent),
                            enabled = !isWatchingAd,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isWatchingAd) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Streaming Sponsor Ad (Earns +3 AI Credits)...", color = GoldAccent, fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Watch Sponsored Ad (+3 AI Credits)", color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Completed simulation callback
                        if (isWatchingAd) {
                            androidx.compose.runtime.LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(3000)
                                onAwardAdCredits()
                                isWatchingAd = false
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Low-income Assistance Waiver Form
                    HorizontalDivider(color = CardSlate, modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Poverty Index & Income Benefit Verification Request", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Receive instant unlimited Access to Justice by selecting your eligibility agency benefits:", color = TextMutedSlate, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("SNAP Food Assistance", "Medicaid / WIC", "SSDI / Disability Benefit", "Public Case Expense Debt Waiver").forEach { option ->
                            FilterChip(
                                selected = incomeWaiverType == option,
                                onClick = { incomeWaiverType = option },
                                label = { Text(option, fontSize = 10.sp, color = TextWarmCream) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent.copy(alpha = 0.3f),
                                    selectedLabelColor = GoldAccent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = incomeWaiverNotes,
                        onValueChange = { incomeWaiverNotes = it },
                        placeholder = { Text("Agency case worker number or SSDI confirmation reference...", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                        textStyle = TextStyle(fontSize = 11.sp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (incomeWaiverNotes.isNotBlank()) {
                                onVerifyIncomeWaiver(incomeWaiverType, incomeWaiverNotes)
                                incomeWaiverNotes = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Verify Income Waiver", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    // Affiliate Directory Partner Section
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CardSlate, modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Affordable & Pro-Bono Resource Partners (Sponsor Ads)", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("⚖ Michigan Legal Help (MLH Portal)", color = TextWarmCream, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("FREE Self-Litigant Templates", color = SuccessGreen, fontSize = 10.sp)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("🛡 State Bar of Michigan (Modest Means)", color = TextWarmCream, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("1/3 Reduced Legal Rate Plan", color = SuccessGreen, fontSize = 10.sp)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("📖 SCAO Directory (Forms Index & Guide)", color = TextWarmCream, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Direct PDF Downloads", color = SuccessGreen, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Pattern Recognition Alert
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = "Recognition", tint = AlertCoral)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Misconduct Pattern Recognition Graph",
                            color = AlertCoral,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Systemic repeat auditing automatically triggers flags on agency personnel or private foster contractors who repeatedly accumulate policy discrepancy records:",
                        color = TextMutedSlate,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (repeatViolators.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            Text("No repeating violators logged under local clinic records yet.", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        repeatViolators.forEach { (name, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AlertCoral.copy(alpha = 0.1f))
                                    .border(1.dp, AlertCoral.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ReportProblem, contentDescription = "Danger", tint = AlertCoral, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("$name ($count separate violations logged)", color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Text("CRITICAL REPEAT OFFENDER", color = AlertCoral, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Private Contractor / Employee Intake form
        if (activeCase != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Log employee", tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Private Contractor & Employee Performance Logging", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = violatorName,
                            onValueChange = { violatorName = it },
                            label = { Text("Employee Name / Badge / Organization ID") },
                            placeholder = { Text("e.g. Case Worker Smithe") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                            modifier = Modifier.fillMaxWidth().testTag("violator_name_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Agency Group / Contractor Group:", color = GoldAccent, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            agencyOptions.forEach { opt ->
                                FilterChip(
                                    selected = violatorAgency == opt,
                                    onClick = { violatorAgency = opt },
                                    label = { Text(opt, fontSize = 11.sp, color = TextWarmCream) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldAccent.copy(alpha = 0.3f),
                                        selectedLabelColor = GoldAccent
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = violationDate,
                                onValueChange = { violationDate = it },
                                label = { Text("Violation Date") },
                                placeholder = { Text("e.g. 2026-05-30") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 6.dp)
                            )

                            OutlinedTextField(
                                value = manualPolicy,
                                onValueChange = { manualPolicy = it },
                                label = { Text("Violated Policy Code") },
                                placeholder = { Text("e.g. CPS Manual Vol 4") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Violated Constitutional Right Category:", color = GoldAccent, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rightOptions.forEach { opt ->
                                FilterChip(
                                    selected = selectedRight == opt,
                                    onClick = { selectedRight = opt },
                                    label = { Text(opt, fontSize = 11.sp, color = TextWarmCream) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldAccent.copy(alpha = 0.3f),
                                        selectedLabelColor = GoldAccent
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = descriptionReason,
                            onValueChange = { descriptionReason = it },
                            label = { Text("Brief factual statement / witness confirmation lines") },
                            placeholder = { Text("e.g. Witnessed supervisor Smithe denying child safety visit because mother brought agricultural food supplies...") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = isAnonMode,
                                    onCheckedChange = { isAnonMode = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AlertCoral)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Anonymize Submission", color = TextWarmCream, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Send to Victim's Voice clinical database", color = TextMutedSlate, fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    if (violatorName.isNotBlank() && descriptionReason.isNotBlank()) {
                                        onLogMisconduct(
                                            violatorName,
                                            violatorAgency,
                                            violationDate,
                                            manualPolicy,
                                            selectedRight,
                                            descriptionReason,
                                            isAnonMode
                                        )
                                        violatorName = ""
                                        violationDate = ""
                                        manualPolicy = ""
                                        descriptionReason = ""
                                        showReportSuccessBanner = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isAnonMode) AlertCoral else GoldAccent)
                            ) {
                                Text(
                                    text = if (isAnonMode) "Upload Anonymously" else "Log Audited Incident",
                                    color = ObsidianBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (showReportSuccessBanner) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SuccessGreen.copy(alpha = 0.2f))
                                    .padding(8.dp)
                                    .border(0.5.dp, SuccessGreen, RoundedCornerShape(4.dp)),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("✓ Misconduct successfully archived into local database", color = SuccessGreen, fontSize = 12.sp)
                                IconButton(onClick = { showReportSuccessBanner = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Anonymous Systemic Failure Database and Clinical Access Portal
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Analytics, contentDescription = "Database", tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Victim's Voice: Anonymous Database Access", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Information reported anonymously is safely transferred here to build proof of 'Systemic Failures' and guidelines violation ratios for University of Michigan clinical researchers & advocate oversight clinics:",
                        color = TextMutedSlate,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val anonSubmissions = allMisconduct.filter { it.isAnonymous }
                    if (anonSubmissions.isEmpty()) {
                        Text("No anonymous entries compiled in dynamic researchers directory yet.", color = TextMutedSlate, fontSize = 11.sp)
                    } else {
                        anonSubmissions.forEach { sub ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ObsidianBackground)
                                    .padding(10.dp)
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Agency: ${sub.agencyOrContractor}", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Date: ${sub.violationDate}", color = TextMutedSlate, fontSize = 10.sp)
                                }
                                Text("Violator Name Checked: [Redacted for Research Compliance]", color = AlertCoral, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Violation Category: ${sub.constitutionalRightViolated}", color = TextMutedSlate, fontSize = 11.sp)
                                Text("Incident: ${sub.evidenceDescription}", color = TextWarmCream, fontSize = 11.sp, lineHeight = 15.sp)
                                Divider(color = TextMutedSlate.copy(alpha = 0.1f), modifier = Modifier.padding(top = 6.dp))
                            }
                        }
                    }
                }
            }
        }

        // Contact Agency Pitch Drafts Launcher (Sen Chang, OCA, etc.)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = "Advocacy", tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clinical Outreach & Pitch Draft Engine", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Secure support quickly. Select an authority below to create structured summaries of logged misconduct to dispatch to advocacy councils:",
                        color = TextMutedSlate,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    var currentPitchDraft by remember { mutableStateOf<String?>(null) }
                    var recipientTitle by remember { mutableStateOf("") }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                recipientTitle = "Office of the Child Advocate (OCA)"
                                currentPitchDraft = "To the Office of the Child Advocate:\n\n" +
                                        "This transmission coordinates safe-keeping metrics detailing CPS administrative negligence regarding our active case${if (activeCase != null) " (Docket " + activeCase.docketNumber + ")" else ""}.\n\n" +
                                        "SUMMARY OF DETECTED MISCONDUCT:\n" +
                                        localMisconduct.joinToString("\n") { "- Flagged: ${it.employeeName} (${it.agencyOrContractor}) violated ${it.constitutionalRightViolated}. Action: ${it.evidenceDescription}" } + "\n\n" +
                                        "Please audit these records verifying conformity with SCAO 'observable behavior change' safety standards rather than arbitrary removal protocols. An investigative hearing is urgently requested."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("OCA Pitch", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                recipientTitle = "Michigan Law AI Advisory Council"
                                currentPitchDraft = "Greetings Council Researchers:\n\n" +
                                        "I am contacting your team regarding our pro se litigant experience leveraging automated document organizations and audit vaults to secure basic 'Access to Justice'.\n\n" +
                                        "We suggest evaluating our collected evidence timeline to help clinical researchers and the University of Michigan AI Law and Policy Clinic audit how automation tools safely offset representation barriers for low-income families faced with state agency oversight."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("U of M AI Council", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                recipientTitle = "State Senator Stephanie Chang (Civil Rights Council)"
                                currentPitchDraft = "Honorable Senator Stephanie Chang:\n\n" +
                                        "I am writing as a constituent regarding civil rights discrepancies and private foster contractor overreaches targeting local small agricultural properties.\n\n" +
                                        "Under state preemption (Michigan Right to Farm Act), municipal zoning harassment contradicts state PA 116 definitions. I request an official inquiry on guidelines failure and administrative overreaches.\n\n" +
                                        "TIMELINE COPIED FOR REPORTING:\n" +
                                        localMisconduct.joinToString("\n") { "- Officer: ${it.employeeName} violated ${it.constitutionalRightViolated}. Description: ${it.evidenceDescription}" }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sen Chang", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (currentPitchDraft != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("DRAFT INTENDED FOR: $recipientTitle", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground)
                                .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(currentPitchDraft!!, color = TextWarmCream, fontSize = 11.sp, lineHeight = 15.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextButton(onClick = { currentPitchDraft = null }) {
                                        Text("Clear Draft", color = AlertCoral)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            // Close preview / show copy confirmation
                                            currentPitchDraft = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                                    ) {
                                        Text("Accept & Copy Draft Address", color = ObsidianBackground, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active list of misconduct incidents compiled locally
        item {
            Text("LOGGED MISCONDUCT LOGS (ACTIVE CASE)", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (localMisconduct.isEmpty()) {
            item {
                Text("No audited misconduct reports found for active case file.", color = TextMutedSlate, fontSize = 12.sp)
            }
        } else {
            items(localMisconduct) { event ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = "Active Log", tint = AlertCoral)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(event.employeeName, color = TextWarmCream, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            IconButton(onClick = { onDeleteMisconduct(event) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertCoral, modifier = Modifier.size(18.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Agency/Contractor: ${event.agencyOrContractor}", color = GoldAccent, fontSize = 12.sp)
                            Text("Date: ${event.violationDate}", color = TextMutedSlate, fontSize = 11.sp)
                        }

                        if (event.policyViolated.isNotBlank()) {
                            Text("Violated Code: ${event.policyViolated}", color = TextWarmCream, fontSize = 12.sp)
                        }

                        Text("Right Violated: ${event.constitutionalRightViolated}", color = AlertCoral, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBackground)
                                .padding(8.dp)
                        ) {
                            Text(event.evidenceDescription, color = TextWarmCream, fontSize = 11.sp)
                        }

                        if (event.isAnonymous) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("★ Shared with Systemic Failure Database", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item {
                AffiliateAndAdSponsorshipSection(tabName = "Misconduct", viewModel = viewModel)
            }
        }
    }
}

// ========================================================
// 🎁 SPONSORED ADS & BENEFICIAL RESOURCE AFFILIATES DIRECTORY
// ========================================================

@Composable
fun AffiliateRecommendationCard(
    resource: AffiliateResource,
    viewModel: CaseViewModel
) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var hasClaimedCredits by remember { mutableStateOf(false) }
    var showSimulationLoader by remember { mutableStateOf(false) }

    // Simulated claim sequence
    LaunchedEffect(showSimulationLoader) {
        if (showSimulationLoader) {
            kotlinx.coroutines.delay(1800)
            showSimulationLoader = false
            hasClaimedCredits = true
            viewModel.awardAffiliateVisitCredits(resource.rewardCredits)
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("affiliate_card_${resource.id}")
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category Badge + Type
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = resource.category.uppercase(),
                    color = GoldAccent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier
                        .background(GoldAccent.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Text(
                    text = "🎁 REWARD: +${resource.rewardCredits} CREDITS",
                    color = SuccessGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when(resource.resourceType) {
                        "Hardware" -> Icons.Default.Computer
                        "Software" -> Icons.Default.CloudQueue
                        "Legal Service" -> Icons.Default.Gavel
                        else -> Icons.Default.MenuBook
                    },
                    contentDescription = resource.resourceType,
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = resource.title,
                    color = TextWarmCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Description
            Text(
                text = resource.description,
                color = TextWarmCream.copy(alpha = 0.85f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Benefit Statement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianBackground.copy(alpha = 0.4f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Verified, contentDescription = "Verified Benefit", tint = SuccessGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = resource.benefitStatement,
                    color = SuccessGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Promo Code & Coupon
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, TextMutedSlate.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text("PROMO COUPON CODE:", color = TextMutedSlate, fontSize = 8.sp)
                    Text(resource.promoCode, color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                }
                Text(
                    text = resource.discountText,
                    color = AlertCoral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Outlined Button to browse site
                OutlinedButton(
                    onClick = { uriHandler.openUri(resource.affiliateUrl) },
                    border = BorderStroke(1.dp, GoldAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Launch, contentDescription = "Launch", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Browse Partner Site", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                
                // Button to simulate & claim free AI credits (Sponsorship flow)
                Button(
                    onClick = { showSimulationLoader = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasClaimedCredits) SuccessGreen.copy(alpha = 0.2f) else SuccessGreen
                    ),
                    enabled = !showSimulationLoader && !hasClaimedCredits,
                    modifier = Modifier.weight(1.2f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (showSimulationLoader) {
                        CircularProgressIndicator(color = ObsidianBackground, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verifying...", color = ObsidianBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    } else if (hasClaimedCredits) {
                        Icon(Icons.Default.Check, contentDescription = "Claimed", tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Credits Earned!", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "Gift", tint = ObsidianBackground, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify Visit & Claim Cr.", color = ObsidianBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AffiliateAndAdSponsorshipSection(
    tabName: String,
    viewModel: CaseViewModel
) {
    var isWatchingAd by remember { mutableStateOf(false) }
    var adCountdown by remember { mutableStateOf(0) }
    var showAdRewardBanner by remember { mutableStateOf(false) }

    // Auto-countdown for simulated sponsor ad watch
    LaunchedEffect(isWatchingAd) {
        if (isWatchingAd) {
            adCountdown = 3
            while (adCountdown > 0) {
                kotlinx.coroutines.delay(1000)
                adCountdown--
            }
            isWatchingAd = false
            showAdRewardBanner = true
            viewModel.awardAdWatchCredits()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Divider
        Divider(
            color = GoldAccent.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Paid, contentDescription = "Sponsor", tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Beneficial Legal & Tech Resources",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        "Direct affiliates structured by relevance to boost your Pro Se capability.",
                        color = TextMutedSlate,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Simulated Ad Watch Card (Credit Exchange)
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            border = BorderStroke(1.5.dp, AlertCoral.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .testTag("ad_sponsorship_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "SPONSORED CREDIT INTEGRATION",
                        color = AlertCoral,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier
                            .background(AlertCoral.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Text(
                        "FREE AD EXCHANGE: +15 CREDITS",
                        color = GoldAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SCAO Counsel Sponsor: Watch a simulated, educational legal-rights short video clip to replenish your CaseVault AI generation credits instantly. Keep your defense costs at absolute zero.",
                    color = TextWarmCream,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isWatchingAd) {
                    // Ad Play Simulation Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(CardSlate)
                            .border(1.dp, GoldAccent, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = GoldAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Streaming Law Practice sponsor reel... $adCountdown s remaining",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { isWatchingAd = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertCoral),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = "Play ad", tint = ObsidianBackground)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Watch 3-Second Sponsor Clip",
                            color = ObsidianBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (showAdRewardBanner) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SuccessGreen.copy(alpha = 0.15f))
                            .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Success", tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Success! +15 AI License Credits added to your balance. Unlimited access waiver states unaffected.",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Get matching affiliate items from resource index file
        val matchingResources = AffiliateResourcesRegistry.INDEXED_RESOURCES
            .filter { it.relevanceTabName.equals(tabName, ignoreCase = true) }

        matchingResources.forEach { resource ->
            AffiliateRecommendationCard(resource = resource, viewModel = viewModel)
        }
    }
}
