package com.xsolla.android.storesdkexample.ui.fragments.character

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.DeleteSwipeCallback
import com.xsolla.android.storesdkexample.adapter.UserAttributesAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.UserAttributeUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmCharacterPage
import com.xsolla.android.storesdkexample.util.extensions.openInBrowser
import com.xsolla.android.storesdkexample.util.extensions.setClickableSpan
import kotlinx.android.synthetic.main.fragment_character_page.addAttributeButton
import kotlinx.android.synthetic.main.fragment_character_page.attributesRecycler
import kotlinx.android.synthetic.main.fragment_character_page.noItemsLayout
import kotlinx.android.synthetic.main.fragment_character_page.noItemsPlaceholder

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
        adapter = UserAttributesAdapter(
            onEditOptionClick = {
                findNavController().navigate(R.id.fragment_edit_attribute, EditAttributeFragmentArgs(true, it.item).toBundle())
            },
            onDeleteOptionClick = { viewModel.deleteAttribute(it.item) },
            onDeleteOptionClickByPosition = { viewModel.deleteAttributeBySwipe(it) },
            onAddAttributeButtonClick = {
                findNavController().navigate(R.id.fragment_edit_attribute, EditAttributeFragmentArgs(false, null).toBundle())
            },
            onDocumentationClick = { openHowToForAttributes() }
        )
        attributesRecycler.adapter = adapter

        if (!readOnly) {
            val itemTouch = ItemTouchHelper(
                DeleteSwipeCallback(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_cart_delete)!!,
                    ColorDrawable(ContextCompat.getColor(requireContext(), R.color.red_color))
                )
            )
            itemTouch.attachToRecyclerView(attributesRecycler)
        }

        if (readOnly) {
            viewModel.readOnlyItems.observe(viewLifecycleOwner) {
                adapter.submitList(adapter.toAdapterEntitiesWithFooter(it, readOnly))
                configurePlaceholderAndVisibilities(it, readOnly)
            }
        } else {
            viewModel.editableItems.observe(viewLifecycleOwner) {
                adapter.submitList(adapter.toAdapterEntitiesWithFooter(it, readOnly))
                configurePlaceholderAndVisibilities(it, readOnly)
            }
        }
    }

    private fun configurePlaceholderAndVisibilities(items: List<UserAttributeUiEntity>, readOnly: Boolean) {
        attributesRecycler.isVisible = items.isNotEmpty()
        noItemsLayout.isVisible = items.isEmpty()
        noItemsPlaceholder.setText(if (readOnly) R.string.character_read_only_attributes_placeholder else R.string.character_editable_attributes_placeholder)
        addAttributeButton.isVisible = items.isEmpty() && !readOnly
        addAttributeButton.setOnClickListener {
            findNavController().navigate(R.id.fragment_edit_attribute, EditAttributeFragmentArgs(false, null).toBundle())
        }

        if (readOnly) {
            noItemsPlaceholder.setClickableSpan(
                isUnderlineText = true,
                startIndex = noItemsPlaceholder.text.indexOf("See"),
                endIndex = noItemsPlaceholder.text.lastIndexOf("documentation") + "documentation".length,
                onClick = { openHowToForAttributes() }
            )
        }
    }

    private fun openHowToForAttributes() = "https://developers.xsolla.com/login-api/methods/attributes".toUri().openInBrowser(requireContext())

}