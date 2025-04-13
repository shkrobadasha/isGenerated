package org.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Serializable
data class TextAnalysisRequest(
    val text: String
)

@Serializable
data class TextAnalysisResponse(
    val isGenerated: Boolean,
    val confidence: Double
)

class ApiService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    // Ссылка на ngrok
    private val baseUrl = "https://01d8-35-237-184-155.ngrok-free.app"

    suspend fun analyzeText(text: String): TextAnalysisResponse = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/analyze") {
                contentType(ContentType.Application.Json)
                setBody(TextAnalysisRequest(text))
            }
            
            if (response.status.isSuccess()) {
                Json.decodeFromString(response.bodyAsText())
            } else {
                throw Exception("API вернул ошибку: ${response.status}")
            }
        } catch (e: Exception) {
            throw Exception("Ошибка при анализе текста: ${e.message}")
        }
    }
} 