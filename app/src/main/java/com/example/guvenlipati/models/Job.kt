package com.example.guvenlipati.models

data class Job(
    val jobType: String = "",
    val jobStartDate: String = "",
    val jobEndDate: String = "",
    val petID: String = "",
    val userID: String = "",
    val jobId: String = "",
    val jobAbout: String = "",
    val jobProvince: String = "",
    val jobTown: String = "",
    val petSpecies: String = "",
    val jobStatus: Boolean = false
)
