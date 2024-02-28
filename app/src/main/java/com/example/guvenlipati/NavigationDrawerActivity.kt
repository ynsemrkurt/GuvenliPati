package com.example.guvenlipati

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class NavigationDrawerActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_navigation_drawer)

        drawerLayout = findViewById(R.id.drawerLayout)
        imageView = findViewById(R.id.imageView2)

        imageView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            Toast.makeText(this,"Sex",Toast.LENGTH_SHORT).show()
        }
    }
}
