package com.xsolla.android.storesdkexample.ui.fragments.profile

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.xsolla.android.appcore.databinding.FragmentProfileBinding
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.*
import com.xsolla.android.storesdkexample.ui.vm.base.ViewModelFactory

class ProfileFragment : BaseFragment() {
    private val binding: FragmentProfileBinding by viewBinding()

    private val viewModel: VmProfile by activityViewModels {
        ViewModelFactory(resources)
    }

    private val fieldsWithPossibleError by lazy {
        arrayOf(binding.nicknameLayout, binding.phoneLayout, binding.firstnameLayout, binding.lastnameLayout)
    }

    override fun getLayout() = R.layout.fragment_profile

    override val toolbarOption = ToolbarOptions(showBalance = false, showCart = false)

    override fun initUI() {
        viewModel.message.observe(viewLifecycleOwner) {
            showSnack(it)
        }
        viewModel.state.observe(viewLifecycleOwner) { userData ->
            // avatar
            Glide.with(this)
                .load(userData.avatar)
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(binding.avatar)

            // Nickname
            binding.nickname.text = when {
                userData.nickname.isNotBlank() -> { userData.nickname }
                userData.username.isNotBlank() -> { userData.nickname }
                userData.firstName.isNotBlank() -> { userData.firstName }
                userData.lastName.isNotBlank() -> { userData.lastName }
                else -> { "Nickname" }
            }
            binding.nicknameInput.setText(userData.nickname)

            // Email
            binding.emailLayout.isVisible = userData.email.isNotBlank()
            binding.emailInput.setText(userData.email)

            // Username
            binding.usernameLayout.isVisible = userData.email.isNotBlank()
            binding.usernameInput.setText(userData.username)

            // Phone
            binding.phoneInput.setText(userData.phone)

            // Names
            binding.firstnameInput.setText(userData.firstName)
            binding.lastnameInput.setText(userData.lastName)

            // Gender
            // https://stackoverflow.com/questions/28184543/android-autocompletetextview-not-showing-after-settext-is-called
            binding.genderInput.setText(userData.gender?.name, false)

            // Button
            binding.resetPasswordButton.isVisible = userData.email.isNotBlank()
        }
        viewModel.stateForChanging.observe(viewLifecycleOwner) {
            if (it == null || it == viewModel.state.value) {
                binding.saveChangesButton.isVisible = false
                clearValidations()
                return@observe
            }
            binding.saveChangesButton.isVisible = true
        }

        binding.saveChangesButton.setOnClickListener {
            updateFields(viewModel.stateForChanging.value)
        }

        binding.resetPasswordButton.setOnClickListener { viewModel.resetPassword() }

        binding.avatar.setOnClickListener {
            val id = viewModel.state.value?.id ?: return@setOnClickListener
            findNavController().navigate(ProfileFragmentDirections.actionNavProfileToFragmentChooseAvatar(id, viewModel.state.value?.avatar))
        }

        configureFields()
    }

    private fun configureFields() {
        binding.nicknameInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.NICKNAME, it?.toString())
        }
        binding.phoneInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.PHONE, it?.toString())
        }
        binding.firstnameInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.FIRST_NAME, it?.toString())
        }
        binding.lastnameInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.LAST_NAME, it?.toString())
        }
        binding.genderInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.GENDER, it?.toString())
        }
    }

    private fun textChangedListener(field: FieldsForChanging, value: String?) {
        if (field == FieldsForChanging.GENDER && value.isNullOrBlank()) return
        field.updateStateForChanging(value ?: "", viewModel.stateForChanging)
    }

    override fun onResume() {
        super.onResume()
        configureGender()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Glide.with(this)
            .load(viewModel.state.value?.avatar)
            .placeholder(R.drawable.ic_default_avatar)
            .error(R.drawable.ic_default_avatar)
            .circleCrop()
            .into(binding.avatar)
    }

    private fun updateFields(newState: UserDetailsUi?) {
        hideKeyboard()

        if (newState == null) {
            return
        }

        val firstnameValidation = if (newState.firstName == viewModel.state.value!!.firstName) {
            ValidateFieldResult(true)
        } else {
            val validateField = viewModel.validateField(FieldsForChanging.FIRST_NAME, newState.firstName)
            showErrorOnFieldOrClear(validateField, binding.firstnameLayout)
            validateField
        }

        val lastnameValidation = if (newState.lastName == viewModel.state.value!!.lastName) {
            ValidateFieldResult(true)
        } else {
            val validateField = viewModel.validateField(FieldsForChanging.LAST_NAME, newState.lastName)
            showErrorOnFieldOrClear(validateField, binding.lastnameLayout)
            validateField
        }

        val phoneValidation = if (newState.phone == viewModel.state.value!!.phone) {
            ValidateFieldResult(true)
        } else {
            val validateField = viewModel.validateField(FieldsForChanging.PHONE, newState.phone)
            showErrorOnFieldOrClear(validateField, binding.phoneLayout)
            validateField
        }

        val nicknameValidation = if (newState.nickname == viewModel.state.value!!.nickname) {
            ValidateFieldResult(true)
        } else {
            val validateField = viewModel.validateField(FieldsForChanging.NICKNAME, newState.nickname)
            showErrorOnFieldOrClear(validateField, binding.nicknameLayout)
            validateField
        }

        if (firstnameValidation.isSuccess && lastnameValidation.isSuccess && phoneValidation.isSuccess && nicknameValidation.isSuccess) {
            viewModel.updateFields(newState)
        }
    }

    private fun showErrorOnFieldOrClear(validation: ValidateFieldResult, layout: TextInputLayout) {
        if (validation.isSuccess) {
            layout.isErrorEnabled = false
        } else {
            layout.error = validation.errorMessage
        }
    }

    private fun clearValidations() {
        fieldsWithPossibleError.forEach { it.isErrorEnabled = false }
    }

    private fun configureGender() {
        val items = Gender.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_gender, items)
        binding.genderInput.setAdapter(adapter)
    }
}