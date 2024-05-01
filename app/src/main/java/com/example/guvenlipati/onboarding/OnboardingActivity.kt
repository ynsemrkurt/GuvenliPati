package com.example.guvenlipati.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.guvenlipati.R

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val fragments = listOf(
            OnboardingFragment.newInstance(),
            Onboarding1Fragment.newInstance(),
            Onboarding2Fragment.newInstance(),
            Onboarding3Fragment.newInstance()
        )

        val adapter = OnboardingAdapter(this, fragments)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

    }
}
