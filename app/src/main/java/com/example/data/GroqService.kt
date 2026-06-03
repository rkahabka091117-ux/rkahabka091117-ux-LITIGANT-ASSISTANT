package com.example.data

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// ---------------------------------------------------------------------------
// Groq AI Provider — REST API client
// Docs: https://console.groq.com/docs/openai
// Groq uses an OpenAI-compatible chat completions API.
// API key is injected via BuildConfig.GROQ_API_KEY (from .env / .env.example).
// ---------------------------------------------------------------------------

// --- Request Models ---

@JsonClass(generateAdapter = true)
data class GroqMessage(
    val role: String,       // "system", "user", or "assistant"
    val content: String
)

@JsonClass(generateAdapter = true)
data class GroqRequest(
    val model: String = "llama3-70b-8192",   // Default: Llama 3 70B (fast, free tier)
    val messages: List<GroqMessage>,
    val temperature: Float = 0.4f,
    val max_tokens: Int = 4096,
    val stream: Boolean = false
)

// --- Response Models ---

@JsonClass(generateAdapter = true)
data class GroqResponse(
    val choices: List<GroqChoice>? = null
)

@JsonClass(generateAdapter = true)
data class GroqChoice(
    val message: GroqMessage? = null
)

// --- Retrofit Interface ---

interface GroqApiService {
    @POST("openai/v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") bearerToken: String,
        @Body request: GroqRequest
    ): GroqResponse
}

// --- Retrofit Client ---

object GroqRetrofitClient {
    private const val BASE_URL = "https://api.groq.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GroqApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GroqApiService::class.java)
    }
}

// --- Convenience helper: extract text from GroqResponse ---
fun GroqResponse.extractText(): String? =
    choices?.firstOrNull()?.message?.content
