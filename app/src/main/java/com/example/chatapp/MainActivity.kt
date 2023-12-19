package com.example.chatapp

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
import com.example.chatapp.ui.theme.ChatAppTheme


sealed class Destination(var route: String) {

    object SignUp : Destination("signup")
    object LogIn : Destination("login")
    object Profile : Destination("profile")
    object ChatList : Destination("chatList")
    object SingleChat : Destination("singleChat/{chatId}") {
        fun createRoute(id: String) = "singleChat/$id"
    }

    object StatusList : Destination("statusList")
    object SingleStatus : Destination("singleStatus/{userID}") {
        fun createRoute(userID: String) = "singleStatus/$userID"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()
                }
            }


        }
    }

    @Composable
    fun ChatAppNavigation() {
        Text(text = "Ameen")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChatAppTheme {
        Greeting("Android")
    }
}