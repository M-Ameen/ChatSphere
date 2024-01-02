package com.example.chatapp.Screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)) {

        Column(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                }, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(100.dp)) {

            }
        }
    }


}