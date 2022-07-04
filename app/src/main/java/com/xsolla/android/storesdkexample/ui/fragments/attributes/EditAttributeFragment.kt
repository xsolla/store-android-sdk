package com.xsolla.android.storesdkexample.ui.fragments.attributes

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.login.entity.common.UserAttributePermission
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.appcore.databinding.FragmentEditAttributeBinding
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.UserAttributeUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmAttributesPage

class EditAttributeFragment : BaseFragment() {

    private val binding: FragmentEditAttributeBinding by viewBinding()

    private val args: EditAttributeFragmentArgs by navArgs()
    private val viewModel: VmAttributesPage by activityViewModels()

    override fun getLayout() = R.layout.fragment_edit_attribute

    override fun initUI() {
        mainToolbar?.isGone = true

        binding.close.setOnClickListener { navigateUp() }

        binding.saveButton.isEnabled = !binding.attributeKeyInput.text.isNullOrBlank() && !binding.attributeValueInput.text.isNullOrBlank()
        binding.attributeKeyInput.addTextChangedListener { text ->
            binding.saveButton.isEnabled = !text.isNullOrBlank() && !binding.attributeValueInput.text.isNullOrBlank()
        }
        binding.attributeValueInput.addTextChangedListener { text ->
            binding.saveButton.isEnabled = !text.isNullOrBlank() && !binding.attributeKeyInput.text.isNullOrBlank()
        }

        if (args.isEdit) {
            val attribute = args.attribute ?: throw IllegalArgumentException("If isEdit is true attribute param must not be null")

            binding.attributeKeyInput.setText(attribute.key)
            binding.attributeValueInput.setText(attribute.value)

            binding.saveButton.setOnClickListener {
                val key = binding.attributeKeyInput.text?.toString() ?: return@setOnClickListener
                val value = binding.attributeValueInput.text?.toString() ?: return@setOnClickListener

                if (attribute.key == key) {
                    viewModel.saveAttribute(attribute.copy(value = value), true, ::navigateUp)
                } else {
                    viewModel.renameAndUpdateAttribute(attribute, attribute.copy(key = key, value = value), ::navigateUp)
                }
            }

            binding.removeDiscardButton.setOnClickListener {
                viewModel.deleteAttribute(attribute, ::navigateUp)
            }
        } else {
            binding.removeDiscardButton.setText(R.string.attributes_edit_attribute_discard_button)
            binding.removeDiscardButton.setOnClickListener { navigateUp() }
            binding.saveButton.setOnClickListener {
                val key = binding.attributeKeyInput.text?.toString()
                val value = binding.attributeValueInput.text?.toString()

                if (!key.isNullOrBlank() && !value.isNullOrBlank()) {
                    viewModel.saveAttribute(UserAttributeUiEntity(key, UserAttributePermission.PUBLIC, value), false, ::navigateUp)
                }
            }
        }
    }

    override fun onDestroyView() {
        mainToolbar?.isVisible = true
        super.onDestroyView()
    }

    private fun navigateUp() {
        hideKeyboard()
        findNavController().navigateUp()
    }
}