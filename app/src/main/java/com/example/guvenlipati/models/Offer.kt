package com.example.guvenlipati.models

data class Offer(
    val offerJobId: String="",
    val offerUser: String="",
    val offerPrice: Int=0,
    val offerBackerId: String="",
    val offerDate: String="",
    val offerStatus: Boolean=true,
    val priceStatus: Boolean=true
)
