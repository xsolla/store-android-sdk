package com.xsolla.android.storesdkexample.ui.fragments.character

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.xsolla.android.login.entity.common.UserAttributePermission
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.UserAttributeUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmCharacterPage
import kotlinx.android.synthetic.main.fragment_edit_attribute.attributeKeyInput
import kotlinx.android.synthetic.main.fragment_edit_attribute.attributeKeyLayout
import kotlinx.android.synthetic.main.fragment_edit_attribute.attributeValueInput
import kotlinx.android.synthetic.main.fragment_edit_attribute.close
import kotlinx.android.synthetic.main.fragment_edit_attribute.removeDiscardButton
import kotlinx.android.synthetic.main.fragment_edit_attribute.saveButton

class EditAttributeFragment : BaseFragment() {

    private val args: EditAttributeFragmentArgs by navArgs()
    private val viewModel: VmCharacterPage by activityViewModels()

    override fun getLayout() = R.layout.fragment_edit_attribute

    override fun initUI() {
        close.setOnClickListener { findNavController().navigateUp() }

        saveButton.isEnabled = !attributeKeyInput.text.isNullOrBlank() && !attributeValueInput.text.isNullOrBlank()
        attributeKeyInput.addTextChangedListener { text ->
            saveButton.isEnabled = !text.isNullOrBlank() && !attributeValueInput.text.isNullOrBlank()
        }
        attributeValueInput.addTextChangedListener { text ->
            saveButton.isEnabled = !text.isNullOrBlank() && !attributeKeyInput.text.isNullOrBlank()
        }

        if (args.isEdit) {
            val attribute = args.attribute ?: throw IllegalArgumentException("If isEdit is true attribute param must not be null")

            attributeKeyInput.setText(attribute.key)
            attributeValueInput.setText(attribute.value)

            saveButton.setOnClickListener {
                val key = attributeKeyInput.text?.toString() ?: return@setOnClickListener
                val value = attributeValueInput.text?.toString() ?: return@setOnClickListener

                if (attribute.key == key) {
                    viewModel.saveAttribute(attribute.copy(value = value), true) {
                        navigateUp()
                    }
                } else {
                    viewModel.renameAndUpdateAttribute(attribute, attribute.copy(key = key, value = value)) {
                        navigateUp()
                    }
                }
            }

            removeDiscardButton.setOnClickListener {
                navigateUp()
                viewModel.deleteAttribute(attribute)
            }
        } else {
            removeDiscardButton.setText(R.string.character_edit_attribute_discard_button)
            removeDiscardButton.setOnClickListener {
                navigateUp()
            }
            saveButton.setOnClickListener {
                val key = attributeKeyInput.text?.toString()
                val value = attributeValueInput.text?.toString()

                if (!key.isNullOrBlank() && !value.isNullOrBlank()) {
                    findNavController().navigateUp()
                    viewModel.saveAttribute(UserAttributeUiEntity(key, UserAttributePermission.PUBLIC, value), false)
                }
            }
        }
    }

    private fun navigateUp() = findNavController().navigateUp()
}