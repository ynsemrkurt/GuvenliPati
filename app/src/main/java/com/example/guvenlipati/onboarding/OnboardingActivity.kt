package com.example.guvenlipati.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.guvenlipati.databinding.ActivityOnBoardingBinding
import com.example.guvenlipati.home.HomeActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private val fragments = listOf(
        OnboardingFragment.newInstance(),
        Onboarding1Fragment.newInstance(),
        Onboarding2Fragment.newInstance(),
        Onboarding3Fragment.newInstance()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = OnboardingAdapter(this, fragments)
        binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        binding.btnNextStep.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem >= fragments.size - 1) {
                binding.btnFinish.visibility = View.VISIBLE
                binding.btnNextStep.visibility = View.INVISIBLE
            } else {
                binding.viewPager.setCurrentItem(currentItem + 1, true)
            }
        }

        binding.btnFinish.setOnClickListener {
            navigateToHomeActivity()
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == fragments.size - 1) {
                    binding.btnFinish.visibility = View.VISIBLE
                    binding.btnNextStep.visibility = View.INVISIBLE
                } else {
                    binding.btnFinish.visibility = View.INVISIBLE
                    binding.btnNextStep.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
