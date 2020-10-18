package com.xsolla.android.storesdkexample.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FieldsForChanging
import com.xsolla.android.storesdkexample.ui.vm.Gender
import com.xsolla.android.storesdkexample.ui.vm.UserDetailsUi
import com.xsolla.android.storesdkexample.ui.vm.ValidateFieldResult
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
import com.xsolla.android.storesdkexample.ui.vm.base.ViewModelFactory
import com.xsolla.android.storesdkexample.util.extensions.BirthdayFormat
import com.xsolla.android.storesdkexample.util.extensions.formatBirthday
import kotlinx.android.synthetic.main.activity_store.appbar
import kotlinx.android.synthetic.main.app_bar_main.view.balanceLayout
import kotlinx.android.synthetic.main.app_bar_main.view.mainToolbar
import kotlinx.android.synthetic.main.fragment_profile.avatar
import kotlinx.android.synthetic.main.fragment_profile.birthdayInput
import kotlinx.android.synthetic.main.fragment_profile.birthdayLayout
import kotlinx.android.synthetic.main.fragment_profile.emailInput
import kotlinx.android.synthetic.main.fragment_profile.emailLayout
import kotlinx.android.synthetic.main.fragment_profile.firstnameInput
import kotlinx.android.synthetic.main.fragment_profile.firstnameLayout
import kotlinx.android.synthetic.main.fragment_profile.genderInput
import kotlinx.android.synthetic.main.fragment_profile.lastnameInput
import kotlinx.android.synthetic.main.fragment_profile.lastnameLayout
import kotlinx.android.synthetic.main.fragment_profile.nickname
import kotlinx.android.synthetic.main.fragment_profile.nicknameInput
import kotlinx.android.synthetic.main.fragment_profile.nicknameLayout
import kotlinx.android.synthetic.main.fragment_profile.phoneInput
import kotlinx.android.synthetic.main.fragment_profile.phoneLayout
import kotlinx.android.synthetic.main.fragment_profile.resetPasswordButton
import kotlinx.android.synthetic.main.fragment_profile.root
import kotlinx.android.synthetic.main.fragment_profile.saveChangesButton
import kotlinx.android.synthetic.main.fragment_profile.usernameInput
import kotlinx.android.synthetic.main.fragment_profile.usernameLayout
import kotlinx.android.synthetic.main.item_datepicker.view.cancelButton
import kotlinx.android.synthetic.main.item_datepicker.view.clearButton
import kotlinx.android.synthetic.main.item_datepicker.view.datePicker
import kotlinx.android.synthetic.main.item_datepicker.view.okButton
import java.util.Calendar

class ProfileFragment : BaseFragment() {
    private val viewModel: VmProfile by activityViewModels {
        ViewModelFactory(resources)
    }

    private val fieldsWithPossibleError by lazy {
        arrayOf(nicknameLayout, phoneLayout, firstnameLayout, lastnameLayout)
    }

    override fun getLayout() = R.layout.fragment_profile

    override fun initUI() {
        requireActivity().appbar.balanceLayout.isVisible = false
        requireActivity().appbar.mainToolbar.menu.clear()

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
                .into(avatar)

            // Nickname
            nickname.text = when {
                userData.nickname.isNotBlank() -> { userData.nickname }
                userData.username.isNotBlank() -> { userData.nickname }
                userData.firstName.isNotBlank() -> { userData.firstName }
                userData.lastName.isNotBlank() -> { userData.lastName }
                else -> { "Nickname" }
            }
            nicknameInput.setText(userData.nickname)

            // Email
            emailLayout.isVisible = userData.email.isNotBlank()
            emailInput.setText(userData.email)

            // Username
            usernameLayout.isVisible = userData.email.isNotBlank()
            usernameInput.setText(userData.username)

            // Phone
            phoneInput.setText(userData.phone)

            // Names
            firstnameInput.setText(userData.firstName)
            lastnameInput.setText(userData.lastName)

            // Birthday
            birthdayInput.setText(userData.birthday.formatBirthday(BirthdayFormat.FROM_BACKEND_TO_UI))
            if (userData.birthday.isNotBlank()) birthdayLayout.isEnabled = false

            // Gender
            // https://stackoverflow.com/questions/28184543/android-autocompletetextview-not-showing-after-settext-is-called
            genderInput.setText(userData.gender?.name, false)

            // Button
            resetPasswordButton.isVisible = userData.email.isNotBlank()
        }
        viewModel.stateForChanging.observe(viewLifecycleOwner) {
            if (it == null || it == viewModel.state.value) {
                saveChangesButton.isVisible = false
                clearValidations()
                return@observe
            }
            saveChangesButton.isVisible = true
        }

        saveChangesButton.setOnClickListener { updateFields(viewModel.stateForChanging.value) }

        resetPasswordButton.setOnClickListener { viewModel.resetPassword() }

        avatar.setOnClickListener {
            val id = viewModel.state.value?.id ?: return@setOnClickListener
            findNavController().navigate(ProfileFragmentDirections.actionNavProfileToFragmentChooseAvatar(id, viewModel.state.value?.avatar))
        }

        configureBirthday()
        configureFields()
    }

    private fun configureFields() {
        nicknameInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.NICKNAME, it?.toString())
        }
        phoneInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.PHONE, it?.toString())
        }
        firstnameInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.FIRST_NAME, it?.toString())
        }
        lastnameInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.LAST_NAME, it?.toString())
        }
        birthdayInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.BIRTHDAY, it?.toString())
        }
        genderInput.addTextChangedListener {
            textChangedListener(FieldsForChanging.GENDER, it?.toString())
        }
    }

    private fun textChangedListener(field: FieldsForChanging, value: String?) {
        if (field == FieldsForChanging.GENDER && value.isNullOrBlank()) return
        field.updateStateForChanging(value ?: "", viewModel.stateForChanging)
    }

    override fun onDestroyView() {
        requireActivity().appbar.balanceLayout.isVisible = true
        requireActivity().invalidateOptionsMenu()
        super.onDestroyView()
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
            .into(avatar)
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
            showErrorOnFieldOrClear(validateField, firstnameLayout)
            validateField
        }

        val lastnameValidation = if (newState.lastName == viewModel.state.value!!.lastName) {
            ValidateFieldResult(true)
        } else {
            val validateField = viewModel.validateField(FieldsForChanging.LAST_NAME, newState.lastName)
            showErrorOnFieldOrClear(validateField, lastnameLayout)
            validateField
        }

        val phoneValidation = if (newState.phone == viewModel.state.value!!.phone) {
            ValidateFieldResult(true)
        } else {
            val validateField = viewModel.validateField(FieldsForChanging.PHONE, newState.phone)
            showErrorOnFieldOrClear(validateField, phoneLayout)
            validateField
        }

        val nicknameValidation = if (newState.nickname == viewModel.state.value!!.nickname) {
            ValidateFieldResult(true)
        } else {
            val validateField = viewModel.validateField(FieldsForChanging.NICKNAME, newState.nickname)
            showErrorOnFieldOrClear(validateField, nicknameLayout)
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
        fieldsWithPossibleError.forEach { it?.isErrorEnabled = false }
    }

    // Not DatePickerDialog from Android SDK due to colors problems
    private fun configureBirthday() {
        birthdayInput.setOnClickListener {
            val pickerDialog = LayoutInflater.from(context).inflate(R.layout.item_datepicker, root, false)
            val picker = pickerDialog.datePicker
            picker.init(1990, 0, 1, null)
            picker.minDate = Calendar.getInstance().apply { set(1950, 0, 1) }.timeInMillis
            picker.maxDate = System.currentTimeMillis()

            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(pickerDialog)
                .setCancelable(true)
                .create()

            pickerDialog.okButton.setOnClickListener {
                val month = if (picker.month + 1 < 10) "0${picker.month + 1}" else "${picker.month + 1}"
                val day = if (picker.dayOfMonth < 10) "0${picker.dayOfMonth}" else "${picker.dayOfMonth}"
                val result = "$day/$month/${picker.year}"
                birthdayInput.setText(result)
                alertDialog.dismiss()
            }
            pickerDialog.clearButton.setOnClickListener {
                birthdayInput.setText("")
                alertDialog.dismiss()
            }
            pickerDialog.cancelButton.setOnClickListener { alertDialog.dismiss() }

            alertDialog.show()
        }
    }

    private fun configureGender() {
        val items = Gender.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_gender, items)
        genderInput.setAdapter(adapter)
    }
}