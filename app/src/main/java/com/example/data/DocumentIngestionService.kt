package com.example.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

// ---------------------------------------------------------------------------
// DocumentIngestionService
//
// Handles reading and extracting raw text content from user-uploaded files.
// Supported formats: PDF, DOCX, TXT, MD (Markdown)
//
// Usage in ViewModel:
//   val result = DocumentIngestionService.ingest(context, uri)
//   if (result.success) { /* use result.text and result.metadata */ }
// ---------------------------------------------------------------------------

object DocumentIngestionService {

    // -----------------------------------------------------------------------
    // Public entry point — call this with any file URI from a file picker
    // -----------------------------------------------------------------------
    suspend fun ingest(context: Context, uri: Uri): IngestionResult = withContext(Dispatchers.IO) {
        val metadata = resolveMetadata(context, uri)
        return@withContext when {
            metadata.mimeType.contains("pdf", ignoreCase = true) ||
            metadata.fileName.endsWith(".pdf", ignoreCase = true) -> ingestPdf(context, uri, metadata)

            metadata.mimeType.contains("wordprocessingml", ignoreCase = true) ||
            metadata.fileName.endsWith(".docx", ignoreCase = true) -> ingestDocx(context, uri, metadata)

            metadata.mimeType.contains("text/plain", ignoreCase = true) ||
            metadata.fileName.endsWith(".txt", ignoreCase = true) ||
            metadata.fileName.endsWith(".md", ignoreCase = true) -> ingestPlainText(context, uri, metadata)

            else -> IngestionResult(
                success = false,
                text = "",
                metadata = metadata,
                errorMessage = "Unsupported file type: ${metadata.mimeType}. Supported: PDF, DOCX, TXT, MD"
            )
        }
    }

    // -----------------------------------------------------------------------
    // Resolve file name, MIME type, and size from a URI
    // -----------------------------------------------------------------------
    private fun resolveMetadata(context: Context, uri: Uri): FileMetadata {
        var fileName = "unknown"
        var fileSize = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex >= 0) fileName = cursor.getString(nameIndex) ?: "unknown"
                if (sizeIndex >= 0) fileSize = cursor.getLong(sizeIndex)
            }
        }
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        return FileMetadata(fileName = fileName, mimeType = mimeType, fileSizeBytes = fileSize)
    }

    // -----------------------------------------------------------------------
    // PDF ingestion
    // Requires: implementation("com.tom-roush:pdfbox-android:2.0.27.0") in build.gradle.kts
    // -----------------------------------------------------------------------
    private fun ingestPdf(context: Context, uri: Uri, metadata: FileMetadata): IngestionResult {
        return try {
            // Initialise PDFBox font resources once
            com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(context)
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return IngestionResult(false, "", metadata, "Could not open file stream")
            val document = com.tom_roush.pdfbox.pdmodel.PDDocument.load(inputStream)
            val stripper = com.tom_roush.pdfbox.text.PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            inputStream.close()
            IngestionResult(
                success = true,
                text = text.trim(),
                metadata = metadata.copy(pageCount = document.numberOfPages)
            )
        } catch (e: Exception) {
            IngestionResult(false, "", metadata, "PDF read error: ${e.message}")
        }
    }

    // -----------------------------------------------------------------------
    // DOCX ingestion
    // Requires: implementation("org.apache.poi:poi-ooxml:5.2.5") in build.gradle.kts
    // Note: poi-ooxml is large; consider poi-scratchpad for .doc (legacy Word)
    // -----------------------------------------------------------------------
    private fun ingestDocx(context: Context, uri: Uri, metadata: FileMetadata): IngestionResult {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return IngestionResult(false, "", metadata, "Could not open file stream")
            val document = org.apache.poi.xwpf.usermodel.XWPFDocument(inputStream)
            val extractor = org.apache.poi.xwpf.extractor.XWPFWordExtractor(document)
            val text = extractor.text
            extractor.close()
            inputStream.close()
            IngestionResult(success = true, text = text.trim(), metadata = metadata)
        } catch (e: Exception) {
            IngestionResult(false, "", metadata, "DOCX read error: ${e.message}")
        }
    }

    // -----------------------------------------------------------------------
    // Plain text / Markdown ingestion — no extra library needed
    // -----------------------------------------------------------------------
    private fun ingestPlainText(context: Context, uri: Uri, metadata: FileMetadata): IngestionResult {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return IngestionResult(false, "", metadata, "Could not open file stream")
            val text = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            inputStream.close()
            IngestionResult(success = true, text = text.trim(), metadata = metadata)
        } catch (e: Exception) {
            IngestionResult(false, "", metadata, "Text read error: ${e.message}")
        }
    }

    // -----------------------------------------------------------------------
    // Save ingested content to Room as a CaseDocument
    // Call this after a successful ingest() to persist the raw text
    // -----------------------------------------------------------------------
    suspend fun saveToCase(
        repository: CaseRepository,
        caseId: Int,
        result: IngestionResult,
        docType: String = "Uploaded Document"
    ): Long {
        if (!result.success) return -1L
        val doc = CaseDocument(
            caseId = caseId,
            title = result.metadata.fileName,
            docType = docType,
            content = result.text,
            isFilingComplete = false,
            serviceDetails = "Ingested on ${java.text.SimpleDateFormat("MM/dd/yyyy HH:mm", java.util.Locale.US).format(java.util.Date())}"
        )
        return repository.insertDocument(doc)
    }
}

// ---------------------------------------------------------------------------
// Data classes
// ---------------------------------------------------------------------------

data class FileMetadata(
    val fileName: String,
    val mimeType: String,
    val fileSizeBytes: Long = 0L,
    val pageCount: Int = 0
)

data class IngestionResult(
    val success: Boolean,
    val text: String,
    val metadata: FileMetadata,
    val errorMessage: String = ""
)
