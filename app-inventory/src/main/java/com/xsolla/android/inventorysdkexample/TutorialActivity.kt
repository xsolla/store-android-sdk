package com.xsolla.android.inventorysdkexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.inventorysdkexample.data.local.PrefManager
import com.xsolla.android.inventorysdkexample.databinding.ActivityTutorialBinding
import com.xsolla.android.inventorysdkexample.ui.fragments.tutorial.TutorialPageFragment

class TutorialActivity : AppCompatActivity(R.layout.activity_tutorial) {

    companion object {
        const val EXTRA_MANUAL_RUN = "EXTRA_MANUAL_RUN"
    }

    private val binding: ActivityTutorialBinding by viewBinding(R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isManualRun = intent.getBooleanExtra(EXTRA_MANUAL_RUN, false)
        binding.buttonClose.setOnClickListener {
            finish()
        }
        binding.pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 4
            override fun createFragment(position: Int) = TutorialPageFragment.newInstance(position + 1)
        }
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setupButtons(position + 1, isManualRun)
                setupPageIndicator(position + 1)
            }
        })
        if (isManualRun) {
            binding.pager.setCurrentItem(1, false)
        }
        binding.pager.isUserInputEnabled = false
    }

    private fun setupButtons(page: Int, isManualRun: Boolean) {
        binding.buttonBack.apply {
            when (page) {
                1 -> {
                    isVisible = true
                    text = getString(R.string.tutorial_button_skip)
                    setOnClickListener { skip() }
                }
                2 -> {
                    isVisible = !isManualRun
                    text = getString(R.string.tutorial_button_back)
                    setOnClickListener { back() }
                }
                3, 4 -> {
                    isVisible = true
                    text = getString(R.string.tutorial_button_back)
                    setOnClickListener { back() }
                }
            }
        }
        binding.buttonNext.apply {
            when (page) {
                1, 2, 3 -> {
                    isVisible = true
                    text = getString(R.string.tutorial_button_next)
                    setOnClickListener { next() }
                }
                4 -> {
                    isVisible = true
                    text = getString(R.string.tutorial_button_done)
                    setOnClickListener { done() }
                }
            }
        }
    }

    private fun setupPageIndicator(page: Int) {
        binding.dot1.setImageResource(R.drawable.ic_tutorial_dot_inactive)
        binding.dot2.setImageResource(R.drawable.ic_tutorial_dot_inactive)
        binding.dot3.setImageResource(R.drawable.ic_tutorial_dot_inactive)
        binding.dot4.setImageResource(R.drawable.ic_tutorial_dot_inactive)
        when(page) {
            1 -> binding.dot1.setImageResource(R.drawable.ic_tutorial_dot_active)
            2 -> binding.dot2.setImageResource(R.drawable.ic_tutorial_dot_active)
            3 -> binding.dot3.setImageResource(R.drawable.ic_tutorial_dot_active)
            4 -> binding.dot4.setImageResource(R.drawable.ic_tutorial_dot_active)
        }
    }

    private fun next() {
        binding.pager.setCurrentItem(binding.pager.currentItem + 1, true)
    }

    private fun back() {
        binding.pager.setCurrentItem(binding.pager.currentItem - 1, true)
    }

    private fun done() {
        PrefManager.setHideTutorial(true)
        finish()
    }

    private fun skip() = done()

}