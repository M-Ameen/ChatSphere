package com.example.chatapp.data

data class UserData(

    var userId:String?=null,
    var name:String?=null,
    var number:String?=null,
    var imageUrl:String?=null
){

    fun toMap()= mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "imageUrl" to imageUrl
    )
}

data class ChatData(
    var chatId:String?=null,
    var user1:ChatUser=ChatUser(),
    var user2:ChatUser=ChatUser()
)
data class ChatUser(
    var userId:String?=null,
    var name:String?=null,
    var imageUrl:String?=null,
    var number:String?=null,

)

data class Message(
    var sendBy:String?="",
    var message:String?="",
    var timeStamp:String?=""
)