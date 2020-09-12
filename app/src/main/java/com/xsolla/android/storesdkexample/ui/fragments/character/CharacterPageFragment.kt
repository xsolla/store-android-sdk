package com.xsolla.android.storesdkexample.ui.fragments.character

import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.UserAttributesAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmCharacterPage
import com.xsolla.android.storesdkexample.util.extensions.openInBrowser
import kotlinx.android.synthetic.main.fragment_character_page.attributesRecycler

class CharacterPageFragment : BaseFragment() {
    companion object {
        private const val EXTRA_READ_ONLY = "ExtraReadOnly"
        private const val ARG_IS_EDIT = "isEdit"

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
        adapter = UserAttributesAdapter(
            onEditOptionClick = {
                findNavController().navigate(R.id.fragment_edit_attribute, EditAttributeFragmentArgs(true, it.item).toBundle())
            },
            onDeleteOptionClick = { viewModel.deleteAttribute(it.item) },
            onAddAttributeButtonClick = {
                findNavController().navigate(R.id.fragment_edit_attribute, EditAttributeFragmentArgs(false, null).toBundle())
            },
            onDocumentationClick = { "https://google.com".toUri().openInBrowser(requireContext()) }
        )
        attributesRecycler.adapter = adapter

        if (readOnly) {
            viewModel.readOnlyItems.observe(viewLifecycleOwner) {
                adapter.submitList(adapter.toAdapterEntitiesWithFooter(it, readOnly)) {
                    attributesRecycler.scrollToPosition(0)
                }
            }
        } else {
            viewModel.editableItems.observe(viewLifecycleOwner) {
                adapter.submitList(adapter.toAdapterEntitiesWithFooter(it, readOnly)) {
                    attributesRecycler.scrollToPosition(0)
                }
            }
        }
    }

}