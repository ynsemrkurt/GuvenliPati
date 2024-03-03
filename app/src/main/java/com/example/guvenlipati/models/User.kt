package com.example.guvenlipati.models

data class User(
    val userGender: Boolean = false,
    val userId: String = "",
    val userName: String = "",
    var userPhoto: String = "",
    val userProvince: String = "",
    val userRegisterDate: String = "",
    val userSurname: String = "",
    val userTown: String = ""
)
