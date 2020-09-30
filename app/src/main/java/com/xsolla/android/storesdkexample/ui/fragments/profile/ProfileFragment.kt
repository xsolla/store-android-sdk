package com.xsolla.android.storesdkexample.ui.fragments.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.fragments.login.ResetPasswordFragmentDirections
import com.xsolla.android.storesdkexample.ui.vm.FieldsForChanging
import com.xsolla.android.storesdkexample.ui.vm.Gender
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
import kotlinx.android.synthetic.main.activity_store.appbar
import kotlinx.android.synthetic.main.app_bar_main.view.balanceLayout
import kotlinx.android.synthetic.main.app_bar_main.view.mainToolbar
import kotlinx.android.synthetic.main.fragment_profile.avatar
import kotlinx.android.synthetic.main.fragment_profile.birthdayInput
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

class ProfileFragment : BaseFragment() {
    private val viewModel: VmProfile by activityViewModels()

    override fun getLayout() = R.layout.fragment_profile

    override fun initUI() {
        requireActivity().appbar.balanceLayout.isVisible = false
        requireActivity().appbar.mainToolbar.menu.clear()

        viewModel.error.observe(viewLifecycleOwner) {
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
            userData.nickname?.let { nicknameInput.setText(it) }

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
            if (userData.birthday != null) birthdayInput.isEnabled = false

            // Gender
            // https://stackoverflow.com/questions/28184543/android-autocompletetextview-not-showing-after-settext-is-called
            genderInput.setText(userData.gender?.name, false)

            // Button
            resetPasswordButton.isVisible = userData.email != null
        }

        viewModel.fieldChangeResult.observe(viewLifecycleOwner) {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }

        resetPasswordButton.setOnClickListener {
            findNavController().navigate(ResetPasswordFragmentDirections.toResetPasswordFragment())
        }

        avatar.setOnClickListener {
            val id = viewModel.state.value?.id ?: return@setOnClickListener
            findNavController().navigate(ProfileFragmentDirections.actionNavProfileToFragmentChooseAvatar(id, viewModel.state.value?.avatar))
        }

        configureBirthday()
        configureFields()
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

    private fun configureFields() {
        firstnameInput.setOnFocusChangeListener { v, hasFocus -> focusListener(v, hasFocus, firstnameLayout, FieldsForChanging.FIRST_NAME) }
        nicknameInput.setOnFocusChangeListener { v, hasFocus -> focusListener(v, hasFocus, nicknameLayout, FieldsForChanging.NICKNAME) }
        phoneInput.setOnFocusChangeListener { v, hasFocus -> focusListener(v, hasFocus, phoneLayout, FieldsForChanging.PHONE) }
        lastnameInput.setOnFocusChangeListener { v, hasFocus -> focusListener(v, hasFocus, lastnameLayout, FieldsForChanging.LAST_NAME) }
        lastnameInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateAndUpdate(lastnameLayout, FieldsForChanging.LAST_NAME, (v as? EditText)?.text?.toString())
                true
            }
            false
        }
    }

    private fun focusListener(view: View, hasFocus: Boolean, layout: TextInputLayout, field: FieldsForChanging) {
        if (hasFocus) return
        view as EditText

        validateAndUpdate(layout, field, view.text?.toString())
    }

    private fun validateAndUpdate(layout: TextInputLayout, field: FieldsForChanging, value: String?) {
        val validateResult = viewModel.validateField(field, value)
        if (validateResult.isSuccess) {
            viewModel.updateField(field, value!!)
            layout.isErrorEnabled = false
        } else {
            layout.error = validateResult.errorMessage
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

                    viewModel.updateField(FieldsForChanging.BIRTHDAY, result)
                },
                1990,
                0,
                1
            )

            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.show()
        }
    }

    private fun configureGender() {
        val items = Gender.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.item_gender, items)
        genderInput.setAdapter(adapter)
        genderInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateField(FieldsForChanging.GENDER, adapter.getItem(position)!!)
        }
    }
}