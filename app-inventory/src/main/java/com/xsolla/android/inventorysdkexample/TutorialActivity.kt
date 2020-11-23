package com.xsolla.android.inventorysdkexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.xsolla.android.inventorysdkexample.data.local.PrefManager
import com.xsolla.android.inventorysdkexample.ui.fragments.tutorial.TutorialPageFragment
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MANUAL_RUN = "EXTRA_MANUAL_RUN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        val isManualRun = intent.getBooleanExtra(EXTRA_MANUAL_RUN, false)
        buttonClose.setOnClickListener {
            finish()
        }
        pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 4
            override fun createFragment(position: Int) = TutorialPageFragment.newInstance(position + 1)
        }
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setupButtons(position + 1, isManualRun)
                setupPageIndicator(position + 1)
            }
        })
        if (isManualRun) {
            pager.setCurrentItem(1, false)
        }
        pager.isUserInputEnabled = false
    }

    private fun setupButtons(page: Int, isManualRun: Boolean) {
        buttonBack.apply {
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
        buttonNext.apply {
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
        dot1.setImageResource(R.drawable.ic_tutorial_dot_inactive)
        dot2.setImageResource(R.drawable.ic_tutorial_dot_inactive)
        dot3.setImageResource(R.drawable.ic_tutorial_dot_inactive)
        dot4.setImageResource(R.drawable.ic_tutorial_dot_inactive)
        when(page) {
            1 -> dot1.setImageResource(R.drawable.ic_tutorial_dot_active)
            2 -> dot2.setImageResource(R.drawable.ic_tutorial_dot_active)
            3 -> dot3.setImageResource(R.drawable.ic_tutorial_dot_active)
            4 -> dot4.setImageResource(R.drawable.ic_tutorial_dot_active)
        }
    }

    private fun next() {
        pager.setCurrentItem(pager.currentItem + 1, true)
    }

    private fun back() {
        pager.setCurrentItem(pager.currentItem - 1, true)
    }

    private fun done() {
        PrefManager.setHideTutorial(true)
        finish()
    }

    private fun skip() = done()

}