package com.example.chatapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.USER_NODE
import com.example.chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore
) : ViewModel() {

    var inProcess = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    val signInState = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)

    init {
        val currentUser = auth.currentUser
        signInState.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }


    fun signUp(name: String, number: String, email: String, password: String) {
        inProcess.value = true
        if (name.isNullOrEmpty() or number.isNullOrEmpty() or email.isNullOrEmpty() or password.isNullOrEmpty()) {
            handleException(customMessage = "Please Fill All Fields")
            return
        }

        inProcess.value = true

        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        signInState.value = true
                        createOrUpdateProfile(name, number)
                    } else {
                        handleException(it.exception, "SignUp Failed")
                    }
                }
            }else{
                handleException(customMessage = "number already exists")
            }
        }

    }

    private fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )

        uid?.let {
            inProcess.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {

                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProcess.value = false
                }
            }.addOnFailureListener {
                handleException(it, "Cannot Retrieve User")
                getUserData(uid)
            }

        }

    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot Retrieve User")
            }

            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProcess.value = false
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("ChatApp", "live chat exception: ", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""

        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage
        eventMutableState.value = Event(message)
        inProcess.value = false
    }

}

