package com.tashrif.jarvis.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tashrif.jarvis.data.ChatMessage
import com.tashrif.jarvis.ui.theme.JarvisAccent
import com.tashrif.jarvis.ui.theme.JarvisSurface
import com.tashrif.jarvis.ui.theme.JarvisTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("JARVIS", fontWeight = FontWeight.Bold, color = JarvisAccent)
                        Text(
                            if (isThinking) "Anafikiri..." else "Tayari",
                            style = MaterialTheme.typography.bodySmall,
                            color = JarvisTextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearConversation() }) {
                        Text("Futa")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFF6B6B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mwambie Jarvis...") },
                    maxLines = 4
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = !isThinking
                ) {
                    Text("Tuma")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isUser) JarvisAccent.copy(alpha = 0.15f) else JarvisSurface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(text = message.content)
        }
    }
}
