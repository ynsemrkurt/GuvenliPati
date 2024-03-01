package com.example.guvenlipati.models

data class Pet(
    val petAbout: String = "",
    val petAdoptionStatus: Boolean,
    val petAge: String = "",
    val petBreed: String = "",
    val petGender: String = "",
    val petName: String = "",
    val petPhoto: String = "",
    val petSpecies: String = "",
    val petVaccinate: String = "",
    val petWeight: String = "",
    val userId: String = ""
){
    constructor() : this("", false, "", "", "", "", "", "", "", "", "")
}
