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
// Blackbox AI Provider — REST API client
// Docs: https://www.blackbox.ai/api-docs
// Blackbox uses an OpenAI-compatible chat completions endpoint.
// API key is injected via BuildConfig.BLACKBOX_API_KEY (from .env / .env.example).
// ---------------------------------------------------------------------------

// --- Request Models ---

@JsonClass(generateAdapter = true)
data class BlackboxMessage(
    val role: String,       // "system", "user", or "assistant"
    val content: String
)

@JsonClass(generateAdapter = true)
data class BlackboxRequest(
    val model: String = "blackboxai",   // Default: Blackbox flagship model
    val messages: List<BlackboxMessage>,
    val temperature: Float = 0.4f,
    val max_tokens: Int = 4096,
    val stream: Boolean = false
)

// --- Response Models ---

@JsonClass(generateAdapter = true)
data class BlackboxResponse(
    val choices: List<BlackboxChoice>? = null
)

@JsonClass(generateAdapter = true)
data class BlackboxChoice(
    val message: BlackboxMessage? = null
)

// --- Retrofit Interface ---

interface BlackboxApiService {
    @POST("api/chat")
    suspend fun chatCompletion(
        @Header("Authorization") bearerToken: String,
        @Body request: BlackboxRequest
    ): BlackboxResponse
}

// --- Retrofit Client ---

object BlackboxRetrofitClient {
    private const val BASE_URL = "https://api.blackbox.ai/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: BlackboxApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BlackboxApiService::class.java)
    }
}

// --- Convenience helper: extract text from BlackboxResponse ---
fun BlackboxResponse.extractText(): String? =
    choices?.firstOrNull()?.message?.content
