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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naveen.sideeffectscoroutine.ui.PostUiState
import com.naveen.sideeffectscoroutine.ui.PostViewModel
import com.naveen.sideeffectscoroutine.ui.theme.SideEffectsCoroutineTheme

class PostDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SideEffectsCoroutineTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PostDemoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PostDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = viewModel()
) {
    val uiState by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "POST Demo",
            style = MaterialTheme.typography.headlineMedium
        )

        when (val currentState = uiState) {
            is PostUiState.Idle -> {
                Text("Ready to create a post")
            }
            is PostUiState.Loading -> {
                CircularProgressIndicator()
                Text("Creating post...")
            }
            is PostUiState.Success -> {
                Text("Post created successfully!")
                Text("ID: ${currentState.id}") // This will now show the actual ID value
                
                // Additional debugging info
                Text("ID type: ${currentState.id::class.simpleName}")
                Text("ID value: ${currentState.id}")
                
                // If you were accidentally displaying the entire object, it would look like this:
                // Text("State: $currentState") // This would show: Success(id=123)
            }
            is PostUiState.Error -> {
                Text("Error: ${currentState.message}")
            }
        }

        Button(
            onClick = {
                viewModel.createOnce(
                    title = "Test Post",
                    body = "This is a test post body",
                    userId = 1
                )
            },
            enabled = uiState !is PostUiState.Loading
        ) {
            Text("Send one-time POST")
        }

        Button(
            onClick = {
                viewModel.schedulePeriodicPost()
            }
        ) {
            Text("Schedule periodic POST (15 min)")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostDemoScreenPreview() {
    SideEffectsCoroutineTheme {
        PostDemoScreen()
    }
}
