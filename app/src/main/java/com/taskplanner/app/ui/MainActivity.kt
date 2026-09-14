package com.taskplanner.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskPlannerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskPlannerScreen()
                }
            }
        }
    }
}

@Composable
fun TaskPlannerScreen() {
    Text(text = "Welcome to Task Planner App")
}

@Preview(showBackground = true)
@Composable
fun TaskPlannerScreenPreview() {
    TaskPlannerAppTheme {
        TaskPlannerScreen()
    }
}

@Composable
fun TaskPlannerAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}
