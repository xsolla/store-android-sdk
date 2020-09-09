package com.xsolla.android.storesdkexample.ui.fragments.login.character

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.UserAttributeItem
import com.xsolla.android.storesdkexample.adapter.UserAttributesAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.UserAttributeUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmCharacterPage

class CharacterPageFragment : BaseFragment() {
    companion object {
        private const val EXTRA_READ_ONLY = "ExtraReadOnly"

        fun newInstance(readOnly: Boolean): CharacterPageFragment {
            return CharacterPageFragment().apply {
                arguments = bundleOf(EXTRA_READ_ONLY to readOnly)
            }
        }
    }

    private lateinit var adapter: UserAttributesAdapter

    private var readOnly: Boolean = false

    private val viewModel: VmCharacterPage by activityViewModels()

    override fun getLayout() = R.layout.fragment_character_page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readOnly = requireArguments().getBoolean(EXTRA_READ_ONLY)
    }

    override fun initUI() {
        val footer = UserAttributeItem.Footer(readOnly) // TODO: move to adapter
        
        adapter = UserAttributesAdapter()

        if (readOnly) {
            viewModel.readOnlyItems.observe(viewLifecycleOwner) {
                adapter.submitList(toAdapterEntities(it, footer))
            }
        } else {
            viewModel.editableItems.observe(viewLifecycleOwner) {
                adapter.submitList(toAdapterEntities(it, footer))
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            showSnack(it.message)
        }
    }

    // TODO: move to adapter
    private fun toAdapterEntities(items: List<UserAttributeUiEntity>, footer: UserAttributeItem.Footer): List<UserAttributeItem> {
        val list = mutableListOf<UserAttributeItem>()
        items.forEach { list.add(UserAttributeItem.Item(it)) }
        list.add(footer)
        return list
    }
}