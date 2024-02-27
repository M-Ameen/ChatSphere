package com.example.chatapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.CHATS
import com.example.chatapp.data.ChatData
import com.example.chatapp.data.ChatUser
import com.example.chatapp.data.MESSAGE
import com.example.chatapp.data.Message
import com.example.chatapp.data.STATUS
import com.example.chatapp.data.Status
import com.example.chatapp.data.USER_NODE
import com.example.chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    var inProcess = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    val signInState = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    var inProcessChats = mutableStateOf(false)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProcessChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null

    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)

    init {
        val currentUser = auth.currentUser
        signInState.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun populateMessages(chatID: String) {
        inProcessChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatID).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error)
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.timeStamp }
                    inProcessChatMessage.value = false
                }

            }

    }

    fun dePopulateMessage() {
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }

    fun populateChat() {
        inProcessChats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)

            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChats.value = false
            }
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
            } else {
                handleException(customMessage = "number already exists")
            }
        }

    }

    fun LogIn(email: String, password: String) {
        if (email.isNullOrEmpty() or password.isNullOrEmpty()) {
            handleException(customMessage = "Please fill all Details")
            return
        } else {
            inProcess.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signInState.value = true
                    inProcess.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }

                } else {
                    handleException(exception = it.exception, customMessage = "LogIn Failed")
                }
            }
        }
    }

    fun createOrUpdateProfile(
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
                    db.collection(USER_NODE).document(uid).update(
                        "name",
                        userData.name,
                        "number",
                        userData.number,
                        "imageUrl",
                        userData.imageUrl
                    )
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
                populateChat()
                populateStatuses()
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

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }

    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProcess.value = true

        val storageRef = storage.reference
        val uuid = UUID.randomUUID()

        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener(onSuccess)
            inProcess.value = false
        }.addOnFailureListener {
            handleException(it)
        }


    }

    fun logout() {
        auth.signOut()
        signInState.value = false
        userData.value = null
        dePopulateMessage()
        currentChatMessageListener = null
        eventMutableState.value = Event("Logged Out")
    }

    fun onAddChat(number: String) {
        if (number.isNullOrEmpty() || !number.isDigitsOnly()) {
            handleException(customMessage = "Contain Image Only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )

                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "No not Found")
                            } else {
                                val chatPartners = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ),
                                    ChatUser(
                                        chatPartners.userId,
                                        chatPartners.name,
                                        chatPartners.imageUrl,
                                        chatPartners.number
                                    )
                                )
                                if (!number.equals(userData.value?.number)) {
                                    db.collection(CHATS).document(id).set(chat)
                                }
                            }
                        }
                        .addOnFailureListener {
                            handleException(it)
                        }
                } else {
                    handleException(customMessage = "Chat already exists")
                }
            }
        }
    }

    fun onSendReply(chatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message = message, timeStamp = time)
        db.collection(CHATS).document(chatID).collection(MESSAGE).document().set(msg)
    }

    fun uploadStatus(it: Uri) {
        uploadImage(it) {
            createStatus(it.toString())
        }

    }

    fun createStatus(imageUrl: String) {
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number
            ), imageUrl = imageUrl, System.currentTimeMillis()
        )
        db.collection(STATUS).document().set(newStatus)
    }

    fun populateStatuses() {
        val timeDelta = 24L * 60 * 60 * 1000
        val cutOff = System.currentTimeMillis() - timeDelta
        inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
            }
            if (value != null) {
                val currentConnections = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach { chat ->
                    if (chat.user1.userId == userData.value?.userId) {
                        currentConnections.add(chat.user2.userId)
                    } else {
                        currentConnections.add(chat.user1.userId)

                    }
                    db.collection(STATUS).whereGreaterThan("timestamp", cutOff)
                        .whereIn("user.userId", currentConnections)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                handleException(error)
                            }
                            if (value != null) {
                                status.value = value.toObjects()
                                inProgressStatus.value = false
                            }
                        }

                }

            }
        }
    }

}

