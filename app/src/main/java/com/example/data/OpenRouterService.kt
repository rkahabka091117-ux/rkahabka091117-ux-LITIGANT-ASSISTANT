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
// OpenRouter AI Provider — REST API client
// Docs: https://openrouter.ai/docs
// OpenRouter is an OpenAI-compatible gateway that routes to many models
// (GPT-4o, Claude 3.5, Mistral, Llama, Gemini, etc.).
// API key is injected via BuildConfig.OPENROUTER_API_KEY (from .env / .env.example).
// ---------------------------------------------------------------------------

// --- Request Models ---

@JsonClass(generateAdapter = true)
data class OpenRouterMessage(
    val role: String,       // "system", "user", or "assistant"
    val content: String
)

@JsonClass(generateAdapter = true)
data class OpenRouterRequest(
    val model: String = "mistralai/mistral-7b-instruct",  // Default: free Mistral 7B
    val messages: List<OpenRouterMessage>,
    val temperature: Float = 0.4f,
    val max_tokens: Int = 4096,
    val stream: Boolean = false
)

// --- Response Models ---

@JsonClass(generateAdapter = true)
data class OpenRouterResponse(
    val choices: List<OpenRouterChoice>? = null
)

@JsonClass(generateAdapter = true)
data class OpenRouterChoice(
    val message: OpenRouterMessage? = null
)

// --- Retrofit Interface ---

interface OpenRouterApiService {
    @POST("api/v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") bearerToken: String,
        @Header("HTTP-Referer") referer: String = "https://github.com/rkahabka091117-ux/LITIGANT-ASSISTANT",
        @Header("X-Title") appTitle: String = "LITIGANT-ASSISTANT",
        @Body request: OpenRouterRequest
    ): OpenRouterResponse
}

// --- Retrofit Client ---

object OpenRouterRetrofitClient {
    private const val BASE_URL = "https://openrouter.ai/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: OpenRouterApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(OpenRouterApiService::class.java)
    }
}

// --- Convenience helper: extract text from OpenRouterResponse ---
fun OpenRouterResponse.extractText(): String? =
    choices?.firstOrNull()?.message?.content
