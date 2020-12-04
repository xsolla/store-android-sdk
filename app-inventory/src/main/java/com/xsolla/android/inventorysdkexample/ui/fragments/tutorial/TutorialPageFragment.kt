package com.xsolla.android.inventorysdkexample.ui.fragments.tutorial

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xsolla.android.inventorysdkexample.R
import kotlinx.android.synthetic.main.item_tutorial.*

class TutorialPageFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.item_tutorial, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val page = requireArguments()[ARG_PAGE] as Int
        tutorialImage.setImageResource(
                when (page) {
                    1 -> R.drawable.page1
                    2 -> R.drawable.page2
                    3 -> R.drawable.page3
                    4 -> R.drawable.page4
                    else -> throw IllegalArgumentException()
                }
        )
        tutorialTitle.setText(
                when (page) {
                    1 -> R.string.tutorial_1_title
                    2 -> R.string.tutorial_2_title
                    3 -> R.string.tutorial_3_title
                    4 -> R.string.tutorial_4_title
                    else -> throw IllegalArgumentException()
                }
        )
        tutorialText.movementMethod = LinkMovementMethod.getInstance()
        tutorialText.setText(
                when (page) {
                    1 -> R.string.tutorial_1_text
                    2 -> R.string.tutorial_2_text
                    3 -> R.string.tutorial_3_text
                    4 -> R.string.tutorial_4_text
                    else -> throw IllegalArgumentException()
                }
        )
    }

    companion object {
        private const val ARG_PAGE = "ARG_PAGE"

        @JvmStatic
        fun newInstance(page: Int) = TutorialPageFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE, page)
            }
        }
    }

}