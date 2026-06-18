package com.tashrif.jarvis.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tashrif.jarvis.ui.theme.JarvisAccent
import com.tashrif.jarvis.ui.theme.JarvisSurface
import com.tashrif.jarvis.ui.theme.JarvisTextSecondary

private data class QuickAction(
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(onOpenChat: () -> Unit) {
    val actions = listOf(
        QuickAction("Ongea na Jarvis", "Mazungumzo ya maandishi - tayari", enabled = true, onClick = onOpenChat),
        QuickAction("Content Studio", "Picha, video, audio - Awamu 3", enabled = false, onClick = {}),
        QuickAction("Social Monitor", "Comments, views - Awamu 6", enabled = false, onClick = {}),
        QuickAction("Usalama & Ruhusa", "PIN, approval queue - Awamu 4", enabled = false, onClick = {}),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Text(
            text = "Habari",
            style = MaterialTheme.typography.bodyLarge,
            color = JarvisTextSecondary
        )
        Text(
            text = "Kahugu Engine",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = JarvisAccent
        )

        Spacer(modifier = Modifier.height(40.dp))

        KahuguCore(onClick = onOpenChat)

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Vitendo",
            style = MaterialTheme.typography.titleMedium,
            color = JarvisTextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(actions) { action -> ActionCard(action) }
        }
    }
}

@Composable
private fun KahuguCore(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "core_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(pulse)
                .border(width = 2.dp, color = JarvisAccent.copy(alpha = ringAlpha), shape = CircleShape)
                .padding(14.dp)
                .border(width = 1.dp, color = JarvisAccent.copy(alpha = 0.4f), shape = CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(JarvisAccent, CircleShape)
                    .alpha(ringAlpha)
            )
        }
    }
}

@Composable
private fun ActionCard(action: QuickAction) {
    Box(
        modifier = Modifier
            .background(JarvisSurface, RoundedCornerShape(16.dp))
            .clickable(enabled = action.enabled) { action.onClick() }
            .padding(16.dp)
            .height(110.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = action.title,
                fontWeight = FontWeight.SemiBold,
                color = if (action.enabled) Color.White else JarvisTextSecondary,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = action.subtitle,
                color = JarvisTextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
