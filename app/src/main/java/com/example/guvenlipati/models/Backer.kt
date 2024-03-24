package com.example.guvenlipati.models

data class Backer(
    val TC: String = "",
    val about: String = "",
    val address: String = "",
    val backerBirthYear: String = "",
    val birdBacker: Boolean = false,
    val catBacker: Boolean = false,
    val dogBacker: Boolean = false,
    val experience: String = "",
    val feedingJob: Boolean = false,
    val feedingMoney: Int = 0,
    val homeJob: Boolean = false,
    val homeMoney: Int = 0,
    val legalName: String = "",
    val legalSurname: String = "",
    val petNumber:  String = "",
    val userAvailability: Int = 0,
    val userID: String = "",
    val walkingJob: Boolean = false,
    val walkingMoney: Int = 0
)
