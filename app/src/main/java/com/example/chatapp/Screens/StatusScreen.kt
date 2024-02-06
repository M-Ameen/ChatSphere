package com.example.chatapp.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.chatapp.ChatViewModel

@Composable
fun StatusScreen(navController: NavHostController, vm: ChatViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.STATUSLIST,
            navController = navController
        )
    }
}