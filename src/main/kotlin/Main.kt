package org.example

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

private val DarkBlue = Color(0xFF1A237E)
private val LightGray = Color(0xFFE0E0E0)

@Composable
fun App() {
    var text by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val apiService = remember { ApiService() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                TextField(
                    value = text,
                    onValueChange = { 
                        text = it
                        error = null
                        result = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Введите текст для анализа") },
                    enabled = !isLoading,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = DarkBlue,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )
                if (text.isNotEmpty() && !isLoading) {
                    IconButton(
                        onClick = { 
                            text = ""
                            result = null
                            error = null
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, "Очистить")
                    }
                }
            }

            Button(
                onClick = { 
                    scope.launch {
                        isLoading = true
                        error = null
                        try {
                            val response = apiService.analyzeText(text)
                            result = if (response.isGenerated) {
                                "Текст сгенерирован\nУверенность: ${String.format("%.1f", response.confidence * 100)}%"
                            } else {
                                "Текст написан человеком\nУверенность: ${String.format("%.1f", response.confidence * 100)}%"
                            }
                        } catch (e: Exception) {
                            error = e.message
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp),
                enabled = text.isNotEmpty() && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = DarkBlue,
                    disabledBackgroundColor = DarkBlue.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    if (isLoading) "Анализ..." else "Анализировать",
                    color = Color.White
                )
            }

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            result?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    elevation = 4.dp,
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = it,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = DarkBlue
                        )
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Text Generator Analyzer",
        state = rememberWindowState()
    ) {
        App()
    }
}