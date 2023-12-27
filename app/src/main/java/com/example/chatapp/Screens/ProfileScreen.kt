package com.example.chatapp.Screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chatapp.ChatViewModel
import com.example.chatapp.CommonDivider

@Composable
fun ProfileScreen(navController: NavHostController, vm: ChatViewModel) {

}

@Composable
fun ProfileContent(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", modifier = Modifier.clickable {
                onBack.invoke()
            })
            Text(text = "Save", modifier = Modifier.clickable {
                onSave.invoke()
            })
            CommonDivider()
            ProfileImage()
        }
    }

}

@Composable
fun ProfileImage(imageUrl: String?, vm: ChatViewModel) {

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                vm.uploadProfileImage(it)
            }
        }


}