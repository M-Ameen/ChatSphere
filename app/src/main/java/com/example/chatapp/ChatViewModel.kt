package com.example.chatapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    val auth: FirebaseAuth
) : ViewModel() {
    init {

    }

    var inProcess = mutableStateOf(false)
    val eventMutableState= mutableStateOf<Event<String>?>(null)

    fun signUp(name: String, number: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("loginstatus", "successful")
            }
        }
    }
}

fun handleException(exception: Exception? = null, customMessage: String = "") {
    Log.e("ChatApp", "live chat exception: ", exception)
    exception?.printStackTrace()
    val errorMsg = exception?.localizedMessage ?: ""

    val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage


}