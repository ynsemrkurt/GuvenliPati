package com.example.guvenlipati.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guvenlipati.R
import com.example.guvenlipati.home.ChatListFragment
import com.example.guvenlipati.job.JobDetailsFragment
import com.example.guvenlipati.payment.PaymentFragment
import com.example.guvenlipati.splash.LoginFragment

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userId = intent.getStringExtra("userId")
        val fragment = ChatingFragment()
        val args = Bundle()
        args.putString("userId", userId)
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

     fun goPaymentFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainerView, PaymentFragment()
            )
            .commit()
    }
}