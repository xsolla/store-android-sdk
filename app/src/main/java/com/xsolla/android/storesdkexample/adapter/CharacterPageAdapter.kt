package com.xsolla.android.storesdkexample.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xsolla.android.storesdkexample.ui.fragments.character.CharacterPageFragment

class CharacterPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private companion object {
        private const val CHARACTER_PAGES = 2
        private const val READ_ONLY_POSITION = 0
    }

    override fun getItemCount() = CHARACTER_PAGES

    override fun createFragment(position: Int): Fragment {
        val readOnly = (position == READ_ONLY_POSITION)
        return CharacterPageFragment.newInstance(readOnly)
    }
}