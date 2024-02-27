package com.example.chatapp.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.chatapp.ChatViewModel


enum class State {
    INITIAL, ACTIVE, COMPLETED
}

@Composable
fun SingleStatusScreen(navController: NavHostController, vm: ChatViewModel, userId: String?) {
    val statuses = vm.status.value.filter {
        it.user.userId==userId
    }

}

@Composable
fun CustomProgressIndicator(modifier: Modifier, state: State, onComplete: () -> Unit) {
    var progress = if (state == State.INITIAL) 0f else 1f

    if (state == State.ACTIVE) {
        val toggleState = remember {
            mutableStateOf(false)
        }
        LaunchedEffect(key1 = toggleState) {
            toggleState.value = true
        }

        val p: Float by animateFloatAsState(
            targetValue = if (toggleState.value) 1f else 0f,
            animationSpec = tween(5000),
            finishedListener = {
                onComplete.invoke()
            }
        )

        progress = p

    }
    LinearProgressIndicator(modifier = modifier, color = Color.Red, progress = progress)

}