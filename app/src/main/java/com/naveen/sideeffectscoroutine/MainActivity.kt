package com.naveen.sideeffectscoroutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.naveen.sideeffectscoroutine.ui.theme.SideEffectsCoroutineTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SideEffectsCoroutineTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SideEffectsDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SideEffectsDemo(modifier: Modifier = Modifier) {
    var tickCount by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Idle") }
    var runningJob by remember { mutableStateOf<Job?>(null) }
    var launchedJob by remember { mutableStateOf<Job?>(null) }
    val context = LocalContext.current

    // LaunchedEffect: runs when tickCount changes (including first composition)
    LaunchedEffect(tickCount) {
        // Track this LaunchedEffect's Job so it can be cancelled externally
        launchedJob = coroutineContext.job
        try {
            // Simulate a short task tied to composition lifecycle
            delay(1000)
            message = "LaunchedEffect observed tick=$tickCount"
        } catch (e: CancellationException) {
            message = "LaunchedEffect cancelled"
            throw e
        } finally {
            launchedJob = null
        }
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Tick: $tickCount")
        Text(text = message)

        // Button 1: Triggers recomposition key change → LaunchedEffect runs
        Button(onClick = { tickCount++ }) {
            Text(text = "Increment (triggers LaunchedEffect)")
        }

        // Button 2: Launches an ad-hoc coroutine via rememberCoroutineScope
        Button(onClick = {
            // Cancel previous job if still running before starting a new one
            runningJob?.cancel()
            runningJob = scope.launch {
                try {
                    message = "Working..."
                    // Simulate multi-step work to make cancellation visible
                    repeat(5) {
                        delay(300)
                    }
                    message = "Completed via rememberCoroutineScope"
                } catch (e: CancellationException) {
                    message = "Cancelled"
                    throw e
                } finally {
                    runningJob = null
                }
            }
        }) {
            Text(text = "Do async work (rememberCoroutineScope)")
        }

        // Button 3: Cancels the currently running scope job, if any
        Button(onClick = {
            if (runningJob?.isActive == true) {
                runningJob?.cancel()
            }
        }) {
            Text(text = "Cancel async work")
        }

        // Button 4: Cancels the current LaunchedEffect job, if any
        Button(onClick = {
            if (launchedJob?.isActive == true) {
                launchedJob?.cancel()
            }
        }) {
            Text(text = "Cancel LaunchedEffect")
        }

        // Button 5: Open POST demo Activity
        Button(onClick = {
            context.startActivity(Intent(context, PostDemoActivity::class.java))
        }) {
            Text(text = "Open POST demo")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SideEffectsDemoPreview() {
    SideEffectsCoroutineTheme {
        SideEffectsDemo()
    }
}