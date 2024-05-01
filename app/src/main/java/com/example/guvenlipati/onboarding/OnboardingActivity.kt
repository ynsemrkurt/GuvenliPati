package com.example.guvenlipati.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.guvenlipati.R
import com.example.guvenlipati.home.HomeActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var btnNextStep: Button
    private lateinit var btnFinish: Button
    private val fragments = listOf(
        OnboardingFragment.newInstance(),
        Onboarding1Fragment.newInstance(),
        Onboarding2Fragment.newInstance(),
        Onboarding3Fragment.newInstance()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        viewPager = findViewById(R.id.viewPager)
        btnNextStep = findViewById(R.id.btn_next_step)
        btnFinish = findViewById(R.id.btn_finish)

        btnNextStep.setOnClickListener {
            if (getItem() >= fragments.size - 1) {
                // Son fragmenta gelindiğinde "Bitir" butonu görünür olacak
                btnFinish.visibility = View.VISIBLE
                btnNextStep.visibility = View.INVISIBLE
            } else {
                // Son fragmenta gelinmediğinde "İleri" butonu gösterilecek
                viewPager.setCurrentItem(getItem() + 1, true)
            }
        }


        btnFinish.setOnClickListener {
            // "Bitir" butonuna tıklandığında HomeActivity'e geçiş yapacak
            navigateToHomeActivity()
        }

        val adapter = OnboardingAdapter(this, fragments)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == fragments.size - 1) {
                    btnFinish.visibility = View.VISIBLE
                    btnNextStep.visibility = View.INVISIBLE
                } else {
                    btnFinish.visibility = View.INVISIBLE
                    btnNextStep.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun getItem(): Int {
        return viewPager.currentItem
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
