package com.example.guvenlipati.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.createEmailIntent(email: String): Intent {
    return Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
}

fun String.isValidPassword(): Boolean {
    return length >= 8 && !contains(" ")
}

fun String.isMatching(other: String): Boolean {
    return this == other
}
