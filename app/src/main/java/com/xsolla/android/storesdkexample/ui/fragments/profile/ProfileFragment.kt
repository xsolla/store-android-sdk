package com.xsolla.android.storesdkexample.ui.fragments.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
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
import kotlinx.android.synthetic.main.fragment_profile.usernameInput
import kotlinx.android.synthetic.main.fragment_profile.usernameLayout
import java.util.Calendar
import java.util.Date

class ProfileFragment : BaseFragment() {
    private val viewModel: VmProfile by activityViewModels()

    override fun getLayout() = R.layout.fragment_profile

    override fun initUI() {
        requireActivity().appbar.balanceLayout.isVisible = false
        requireActivity().appbar.mainToolbar.menu.clear()

        viewModel.message.observe(viewLifecycleOwner) {
            showSnack(it)
        }
        viewModel.state.observe(viewLifecycleOwner) { userData ->
            if (userData == null) return@observe

            // avatar
            Glide.with(this)
                .load(userData.avatar)
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(avatar)

            // Nickname
            nickname.text = userData.nickname ?: userData.firstName ?: userData.lastName ?: "Nickname"
            nicknameInput.setText(userData.nickname)

            // Email
            emailLayout.isVisible = userData.email != null
            emailInput.setText(userData.email)

            // Username
            usernameLayout.isVisible = userData.email != null
            usernameInput.setText(userData.username)

            // Phone
            phoneInput.setText(userData.phone)

            // Names
            firstnameInput.setText(userData.firstName)
            lastnameInput.setText(userData.lastName)

            // Birthday
            birthdayInput.setText(userData.birthday)
            if (userData.birthday != null) birthdayLayout.isEnabled = false

            // Gender
            // https://stackoverflow.com/questions/28184543/android-autocompletetextview-not-showing-after-settext-is-called
            genderInput.setText(userData.gender?.name, false)

            // Button
            resetPasswordButton.isVisible = userData.email != null
        }
        viewModel.stateForChanging.observe(viewLifecycleOwner) {
            if (it == null) {
                // block button
                return@observe
            }
            if (it == viewModel.state.value) {
                // block button
                return@observe
            }
            // unblock button
        }

        viewModel.fieldChangeResult.observe(viewLifecycleOwner) {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }

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
            viewModel.stateForChanging.value = viewModel.stateForChanging.value!!.copy(nickname = it?.toString())
        }
        phoneInput.addTextChangedListener {
            viewModel.stateForChanging.value = viewModel.stateForChanging.value!!.copy(phone = it?.toString())
        }
        firstnameInput.addTextChangedListener {
            viewModel.stateForChanging.value = viewModel.stateForChanging.value!!.copy(firstName = it?.toString())
        }
        lastnameInput.addTextChangedListener {
            viewModel.stateForChanging.value = viewModel.stateForChanging.value!!.copy(lastName = it?.toString())
        }
        birthdayInput.addTextChangedListener {
            viewModel.stateForChanging.value = viewModel.stateForChanging.value!!.copy(birthday = it?.toString())
        }
        genderInput.addTextChangedListener {
            val text = it?.toString() ?: return@addTextChangedListener
            viewModel.stateForChanging.value = viewModel.stateForChanging.value!!.copy(gender = Gender.valueOf(text))
        }
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

    private fun updateFields(newState: UserDetailsUi) {
        hideKeyboard()

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

    private fun configureBirthday() {
        birthdayInput.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                0,
                { _, year, calendarMonth, dayOfMonth ->
                    val month = if (calendarMonth + 1 < 10) "0${calendarMonth + 1}" else "${calendarMonth + 1}"
                    val day = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                    val result = "$year-$month-$day"
                    birthdayInput.setText(result)
                },
                1990,
                0,
                1
            )

            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.datePicker.minDate = Calendar.getInstance().apply { set(1950, 0, 1) }.timeInMillis
            dialog.show()
        }
    }

    private fun configureGender() {
        val items = Gender.values()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_gender, items)
        genderInput.setAdapter(adapter)
        /*genderInput.setOnItemClickListener { _, _, position, _ ->

        }*/
    }
}