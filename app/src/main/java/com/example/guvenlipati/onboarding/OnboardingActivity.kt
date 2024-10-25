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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater).apply { setContentView(root) }

        setupViewPager()
        setupButtonListeners()
    }

    private fun setupViewPager() = with(binding.viewPager) {
        adapter = OnboardingAdapter(this@OnboardingActivity, createFragments())
        offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonVisibility(position)
            }
        })
    }

    private fun setupButtonListeners() = with(binding) {
        btnNextStep.setOnClickListener { navigateToNextPage() }
        btnFinish.setOnClickListener { navigateToHomeActivity() }
    }

    private fun navigateToNextPage() {
        val currentItem = binding.viewPager.currentItem
        if (currentItem < createFragments().size - 1) {
            binding.viewPager.setCurrentItem(currentItem + 1, true)
        } else {
            updateButtonVisibility(currentItem + 1)
        }
    }

    private fun updateButtonVisibility(position: Int) = with(binding) {
        if (position >= createFragments().size - 1) {
            btnFinish.visibility = View.VISIBLE
            btnNextStep.visibility = View.INVISIBLE
        } else {
            btnFinish.visibility = View.INVISIBLE
            btnNextStep.visibility = View.VISIBLE
        }
    }

    private fun navigateToHomeActivity() {
        Intent(this, HomeActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun createFragments() = listOf(
        OnboardingFragment.newInstance(),
        Onboarding1Fragment.newInstance(),
        Onboarding2Fragment.newInstance(),
        Onboarding3Fragment.newInstance()
    )
}
