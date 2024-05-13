package com.example.guvenlipati.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guvenlipati.R

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
}