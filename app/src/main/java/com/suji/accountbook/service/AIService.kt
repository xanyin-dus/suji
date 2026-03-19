package com.suji.accountbook.service

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class AIAnalysisRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

data class AIAnalysisResponse(
    @SerializedName("choices")
    val choices: List<Choice>?,
    @SerializedName("error")
    val error: AIError?
)

data class Choice(
    @SerializedName("message")
    val message: Message
)

data class AIError(
    @SerializedName("message")
    val message: String?,
    @SerializedName("type")
    val type: String?
)

@Singleton
class AIService @Inject constructor(
    private val gson: Gson
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeExpenses(
        apiKey: String,
        apiEndpoint: String,
        expenseData: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """
                你是一个专业的财务分析师。请根据用户提供的消费数据，分析用户的消费习惯，并给出合理的建议。
                分析内容应包括：
                1. 消费概况总结
                2. 主要消费类别分析
                3. 消费趋势分析
                4. 节省开支的建议
                5. 理财建议
                
                请用简洁、友好的语言回答，避免过于专业的术语。
            """.trimIndent()

            val request = AIAnalysisRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    Message(role = "system", content = systemPrompt),
                    Message(role = "user", content = "以下是我的消费数据：\n$expenseData")
                )
            )

            val requestBody = gson.toJson(request)
                .toRequestBody("application/json".toMediaType())

            val httpRequest = Request.Builder()
                .url("$apiEndpoint/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            val response = client.newCall(httpRequest).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val aiResponse = gson.fromJson(responseBody, AIAnalysisResponse::class.java)
                
                if (aiResponse.error != null) {
                    Result.failure(Exception(aiResponse.error.message ?: "AI服务错误"))
                } else {
                    val content = aiResponse.choices?.firstOrNull()?.message?.content
                    if (content != null) {
                        Result.success(content)
                    } else {
                        Result.failure(Exception("AI返回内容为空"))
                    }
                }
            } else {
                Result.failure(Exception("请求失败: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeWithCustomPrompt(
        apiKey: String,
        apiEndpoint: String,
        prompt: String,
        data: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = AIAnalysisRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    Message(role = "user", content = "$prompt\n\n数据：\n$data")
                )
            )

            val requestBody = gson.toJson(request)
                .toRequestBody("application/json".toMediaType())

            val httpRequest = Request.Builder()
                .url("$apiEndpoint/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            val response = client.newCall(httpRequest).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val aiResponse = gson.fromJson(responseBody, AIAnalysisResponse::class.java)
                
                if (aiResponse.error != null) {
                    Result.failure(Exception(aiResponse.error.message ?: "AI服务错误"))
                } else {
                    val content = aiResponse.choices?.firstOrNull()?.message?.content
                    if (content != null) {
                        Result.success(content)
                    } else {
                        Result.failure(Exception("AI返回内容为空"))
                    }
                }
            } else {
                Result.failure(Exception("请求失败: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
