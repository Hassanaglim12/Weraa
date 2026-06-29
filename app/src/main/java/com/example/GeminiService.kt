package com.example

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini API Request/Response Models ---
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

// --- Retrofit API Interface ---
interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- Retrofit Client ---
object GeminiRetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            
        retrofit.create(GeminiApiService::class.java)
    }
}

// --- High-Level Service Class ---
object GeminiService {
    
    suspend fun generateAdvice(prompt: String, apiToken: String, playerName: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // Check if API key is invalid or placeholder
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", ignoreCase = true)) {
            return simulateLocalAdvice(prompt, playerName)
        }

        val systemPrompt = """
            You are "Vanguard Advisor", the official combat strategist AI for the MMORPG WarEra.io.
            Your user is a player named $playerName who has the API token: $apiToken.
            Provide tactical intelligence, military strategies, trade arbitrage advice, and resource balance guidance.
            Use a tactical terminal HUD tone (highly objective, futuristic, military, and tech-savvy). Keep responses highly structured with clear sections like [SITUATION REPORT], [STRATEGIC INITIATIVES], and [TACTICAL DIRECTION]. Limit responses to 250 words.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        return try {
            val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "TACTICAL ADVISOR DIAGNOSTIC: No direct data response received. Re-engage transmission."
        } catch (e: Exception) {
            "COMMAND INTERRUPT: Network packet drop. Fallback to offline terminal calculations...\n\n" +
            simulateLocalAdvice(prompt, playerName)
        }
    }

    private fun simulateLocalAdvice(prompt: String, playerName: String): String {
        val normalized = prompt.lowercase()
        return when {
            normalized.contains("market") || normalized.contains("price") || normalized.contains("gold") || normalized.contains("iron") || normalized.contains("food") -> {
                """
                [OFFLINE LOG: TRADE HUB ADVISORY]
                User: $playerName
                System: Local Simulation Mode Active
                
                - Iron market currently experiences localized supply surpluses in the outer quadrants. Advise hoarding stockpiles until Imperial trade caravans arrive.
                - Food rates are stable; recommend keeping a 3-day upkeep buffer for active troop columns.
                - Gold-to-resource exchange rates show moderate arbitrage potential (approx. 4.2% yield) when trading Food for Gold in the Eastern Outposts.
                
                ACTIONABLE PLAN:
                1. Buy iron in Sector 4 when index dips below 120/unit.
                2. Avoid liquidating Gold reserves; secure for Castle Grade-IV fortification.
                """.trimIndent()
            }
            normalized.contains("combat") || normalized.contains("attack") || normalized.contains("military") || normalized.contains("war") -> {
                """
                [OFFLINE LOG: BATTLEFIELD TACTICAL SUITE]
                User: $playerName
                System: Local Simulation Mode Active
                
                - Combat vectors indicate strong defense buffs for players holding Citadel points in Sector 8.
                - Defensive setups should employ Vanguard Legions in the vanguard line, supported by cybernetic long-range drone batteries in the rear.
                
                RECOMMENDED SQUAD:
                - 40% Shield Vanguard
                - 35% Shock Phalanx
                - 25% Siege Artillery
                
                Keep scout probes active within 10 units of your outpost to spot ambush formations.
                """.trimIndent()
            }
            else -> {
                """
                [OFFLINE LOG: VANGUARD INTELLIGENCE TERMINAL]
                User: $playerName
                System: Gemini Advisor Offline (Simulated Fallback)
                
                Greetings, Commander. I am ready to calculate your WarEra MMORPG strategy.
                
                QUICK RECOGNITION TIPS:
                - Use "market" or "price" for resource arbitrage reports.
                - Use "combat" or "military" for tactical battalion composition.
                
                SYSTEM REPORT: 
                To unlock full real-time cosmic AI computations, please add a valid GEMINI_API_KEY to your AI Studio Secrets panel.
                """.trimIndent()
            }
        }
    }
}
