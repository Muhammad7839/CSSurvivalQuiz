package com.example.cssurvivalquiz.ui.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.cssurvivalquiz.R
import com.example.cssurvivalquiz.data.Prefs
import com.example.cssurvivalquiz.data.Questions
import androidx.compose.foundation.background

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // full-screen background image
                    Image(
                        painter = painterResource(id = R.drawable.background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // slight dark overlay helps readability (tweak alpha if you want)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp)
                            .background(Color.Black.copy(alpha = 0.25f))
                    )
                    GameRoot()
                }
            }
        }
    }
}

@Composable
private fun GameRoot() {
    var screen by remember { mutableStateOf("rules") }
    var lastScore by remember { mutableStateOf(0) }
    var lastTotal by remember { mutableStateOf(0) }
    var lastStreak by remember { mutableStateOf(0) }

    when (screen) {
        "rules" -> RulesScreen(onStart = { screen = "quiz" })
        "quiz" -> QuizScreen { score, total, streakMax ->
            lastScore = score
            lastTotal = total
            lastStreak = streakMax
            screen = "results"
        }
        "results" -> ResultsScreen(
            score = lastScore,
            total = lastTotal,
            streakMax = lastStreak,
            onPlayAgain = { screen = "rules" },
            onSeeHistory = { screen = "history" }
        )
        "history" -> HistoryScreen(onClose = { screen = "rules" })
    }
}

@Composable
fun RulesScreen(onStart: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Rules", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("This quiz busts beginner debugging myths and shares tips I wish I knew on day one.")
                Text("You have 20 seconds per question. One skip. Confirm before locking answers.")
                Spacer(Modifier.height(12.dp))
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Start") }
            }
        }
    }
}

@Composable
fun QuizScreen(onDone: (score: Int, total: Int, streakMax: Int) -> Unit) {
    val questions = remember { Questions.all }
    var idx by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var streakMax by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(20) }
    var showConfirm by remember { mutableStateOf(false) }
    var showFact by remember { mutableStateOf<String?>(null) }
    var factColor by remember { mutableStateOf(Color.Unspecified) }
    var skipsLeft by remember { mutableStateOf(1) }

    val q = questions[idx]
    val selectedSingle = remember(idx) { mutableStateOf(-1) }
    val selectedMulti = remember(idx) { mutableStateOf(mutableSetOf<Int>()) }

    // 20s timer
    LaunchedEffect(idx) {
        timeLeft = 20
        while (timeLeft > 0 && !showConfirm && showFact == null) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0 && showFact == null) {
            streak = 0
            val correctAnswers = q.correct.joinToString(", ") { q.options[it] }
            factColor = Color(0xFFC62828) // red
            showFact = "⏰ Time up.\n\nCorrect answer: $correctAnswers\n\n${q.fact}"
        }
    }

    fun proceed() {
        showFact = null
        factColor = Color.Unspecified
        if (idx == questions.lastIndex) onDone(score, questions.size, streakMax) else idx++
    }

    fun submit() {
        if (!q.multiple && selectedSingle.value == -1) return
        if (q.multiple && selectedMulti.value.isEmpty()) return
        showConfirm = true
    }

    fun confirm() {
        showConfirm = false
        val chosen = if (q.multiple) selectedMulti.value.toSet() else setOf(selectedSingle.value)
        val correct = chosen == q.correct
        if (correct) {
            score++
            streak++
            if (streak > streakMax) streakMax = streak
            factColor = Color(0xFF2E7D32) // green
            showFact = "✅ Correct!\n\n${q.fact}"
        } else {
            streak = 0
            val correctAnswers = q.correct.joinToString(", ") { q.options[it] }
            factColor = Color(0xFFC62828) // red
            showFact = "❌ Wrong.\n\nCorrect answer: $correctAnswers\n\n${q.fact}"
        }
    }

    fun skip() {
        if (skipsLeft <= 0) return
        skipsLeft--
        streak = 0
        val correctAnswers = q.correct.joinToString(", ") { q.options[it] }
        factColor = Color(0xFFEF6C00) // orange
        showFact = "⏭ Skipped.\n\nCorrect answer: $correctAnswers\n\n${q.fact}"
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            )
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text("CS Survival Quiz: Debugging Myths & Tips", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = { timeLeft / 20f })
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Q ${idx + 1}/${questions.size}")
                    Text("⏱ $timeLeft s   🔥 x$streak   ⏭ $skipsLeft")
                }
                Spacer(Modifier.height(12.dp))
                Text(q.text, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))

                if (!q.multiple) {
                    q.options.forEachIndexed { i, opt ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedSingle.value == i,
                                onClick = { selectedSingle.value = i }
                            )
                            Text(opt, Modifier.padding(start = 8.dp))
                        }
                    }
                } else {
                    q.options.forEachIndexed { i, opt ->
                        val checked = i in selectedMulti.value
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (checked) selectedMulti.value.remove(i) else selectedMulti.value.add(i)
                                }
                            )
                            Text(opt, Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { submit() }) { Text("Submit") }
                    OutlinedButton(onClick = { skip() }, enabled = skipsLeft > 0) { Text("Skip") }
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            confirmButton = { TextButton(onClick = { confirm() }) { Text("Confirm") } },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } },
            title = { Text("Lock your answer?") },
            text = {
                val chosen = if (q.multiple)
                    selectedMulti.value.sorted().joinToString { q.options[it] }
                else if (selectedSingle.value >= 0)
                    q.options[selectedSingle.value]
                else ""
                Text(chosen)
            }
        )
    }

    showFact?.let { fact ->
        AlertDialog(
            onDismissRequest = {},
            confirmButton = { TextButton(onClick = { proceed() }) { Text("Next") } },
            title = { Text("Why") },
            text = { Text(fact, color = factColor) }
        )
    }
}

@Composable
fun ResultsScreen(
    score: Int,
    total: Int,
    streakMax: Int,
    onPlayAgain: () -> Unit,
    onSeeHistory: () -> Unit
) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { Prefs.appendHistory(ctx, score, total, streakMax) }

    val title = when {
        score == total -> "Bug Whisperer"
        score >= total * 2 / 3 -> "Debugger in Training"
        else -> "Myth Buster Rookie"
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                Text("Score: $score / $total")
                Text("Best streak: $streakMax")
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onSeeHistory) { Text("See History") }
                    OutlinedButton(onClick = onPlayAgain) { Text("Play Again") }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(onClose: () -> Unit) {
    val ctx = LocalContext.current
    val items = remember { Prefs.readHistory(ctx) }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("History", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                if (items.isEmpty()) {
                    Text("No games yet.")
                } else {
                    items.forEach { line ->
                        Text("• $line")
                        Spacer(Modifier.height(4.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) { Text("Back") }
            }
        }
    }
}