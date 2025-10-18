package com.example.cssurvivalquiz.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cssurvivalquiz.R
import com.example.cssurvivalquiz.data.Prefs
import com.example.cssurvivalquiz.ui.game.GameActivity

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Box(Modifier.fillMaxSize()) {
                    // Background image
                    Image(
                        painter = painterResource(id = R.drawable.background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dark overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f))
                    )
                    // Main UI
                    AuthRoot()
                }
            }
        }
    }
}

@Composable
private fun AuthRoot() {
    var screen by remember { mutableStateOf("menu") }
    val ctx = LocalContext.current

    when (screen) {
        "menu" -> CenteredCard {
            Text("Welcome to CS Survival Quiz", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { screen = "login" }, modifier = Modifier.fillMaxWidth()) { Text("Login") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { screen = "register" }, modifier = Modifier.fillMaxWidth()) { Text("Register") }
        }
        "register" -> CenteredCard { RegisterForm(onBack = { screen = "menu" }) }
        "login" -> CenteredCard {
            LoginForm(
                onBack = { screen = "menu" },
                onSuccess = { ctx.startActivity(Intent(ctx, GameActivity::class.java)) }
            )
        }
    }
}

/* Centered, scrollable Card. No animations (stable). */
@Composable
private fun CenteredCard(content: @Composable ColumnScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .imePadding(),              // lifts above keyboard
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.85f),   // cap height to avoid overflow
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            )
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // scroll if content is tall
            ) {
                content()
            }
        }
    }
}

@Composable
private fun RegisterForm(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Text("Register", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(first, { first = it }, label = { Text("First name") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(last, { last = it }, label = { Text("Family name") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(dob, { dob = it }, label = { Text("Date of birth") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(pass, { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
    error?.let { Spacer(Modifier.height(8.dp)); Text(it, color = MaterialTheme.colorScheme.error) }
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Back") }
        Button(
            onClick = {
                val ok = first.trim().length in 3..30 &&
                        last.trim().isNotEmpty() &&
                        dob.trim().isNotEmpty() &&
                        Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
                        pass.length >= 4
                if (!ok) {
                    error = "Please check your inputs"
                } else {
                    if (Prefs.saveUser(ctx, first, last, dob, email, pass)) onBack()
                    else error = "Could not save user"
                }
            },
            modifier = Modifier.weight(1f)
        ) { Text("Save") }
    }
}

@Composable
private fun LoginForm(onBack: () -> Unit, onSuccess: () -> Unit) {
    val ctx = LocalContext.current
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Text("Login", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(pass, { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
    error?.let { Spacer(Modifier.height(8.dp)); Text(it, color = MaterialTheme.colorScheme.error) }
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Back") }
        Button(
            onClick = {
                if (Prefs.checkLogin(ctx, email, pass)) onSuccess()
                else error = "Invalid email or password"
            },
            modifier = Modifier.weight(1f)
        ) { Text("Enter") }
    }
}