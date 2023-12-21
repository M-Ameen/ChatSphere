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