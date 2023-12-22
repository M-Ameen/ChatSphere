package com.example.chatapp.Screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.chatapp.ChatViewModel

@Composable
fun ChatListScreen(navController: NavHostController, vm: ChatViewModel) {
    BottomNavigationMenu(
        selectedItem = BottomNavigationItem.CHATLIST,
        navController = navController
    )
}