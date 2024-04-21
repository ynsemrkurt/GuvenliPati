package com.example.guvenlipati.models

data class Offer(
    val offerId: String="",
    val offerJobId: String="",
    val offerUser: String="",
    val offerPrice: Int=0,
    val offerBackerId: String="",
    val offerDate: String="",
    val offerStatus: Boolean=false,
    var priceStatus: Boolean=false,
    var confirmUser: Boolean=false,
    var confirmBacker: Boolean=false
)
