package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.Screens.ChatListScreen
import com.example.chatapp.Screens.LoginScreen
import com.example.chatapp.Screens.ProfileScreen
import com.example.chatapp.Screens.SignUpScreen
import com.example.chatapp.Screens.SingleChatScreen
import com.example.chatapp.Screens.SingleStatusScreen
import com.example.chatapp.Screens.StatusScreen
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

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


@AndroidEntryPoint
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
        val navController = rememberNavController()
        var vm = hiltViewModel<ChatViewModel>()

        NavHost(navController = navController, startDestination = Destination.SignUp.route) {

            composable(Destination.SignUp.route) {
                SignUpScreen(navController = navController, vm = vm)
            }
            composable(Destination.LogIn.route) {
                LoginScreen(navController = navController, vm = vm)
            }
            composable(Destination.ChatList.route) {
                ChatListScreen(navController = navController, vm = vm)
            }
            composable(Destination.SingleChat.route) {
                val chatId = it.arguments?.getString("chatId")
                chatId?.let {

                    SingleChatScreen(navController = navController, vm = vm, chatId = chatId)
                }
            }
            composable(Destination.StatusList.route) {
                StatusScreen(navController = navController, vm = vm)
            }
            composable(Destination.Profile.route) {
                ProfileScreen(navController = navController, vm = vm)
            }
            composable(Destination.SingleStatus.route) {
                val userId = it.arguments?.getString("userId")
                userId.let {
                    SingleStatusScreen(
                        navController = navController,
                        vm = vm,
                        userId=it
                    )
                }
            }
        }

    }
}
