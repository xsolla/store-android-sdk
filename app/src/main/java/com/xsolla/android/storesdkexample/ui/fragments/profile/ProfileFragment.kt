package com.xsolla.android.storesdkexample.ui.fragments.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import com.xsolla.android.login.entity.response.GenderResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.fragments.login.ResetPasswordFragmentDirections
import com.xsolla.android.storesdkexample.ui.vm.FieldsForChanging
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
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
        viewModel.error.observe(viewLifecycleOwner) {
            showSnack(it)
        }
        viewModel.state.observe(viewLifecycleOwner) { userData ->
            if (userData == null) return@observe

            // avatar
            Glide.with(this)
                .load(userData.avatar)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(avatar)

            avatar.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionNavProfileToFragmentChooseAvatar(userData.id, userData.avatar))
            }

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
            userData.phone?.let { phoneInput.setText(it) }

            // Names
            firstnameInput.setText(userData.firstName)
            lastnameInput.setText(userData.lastName)

            // Birthday
            birthdayInput.setText(userData.birthday)
            if (userData.birthday != null) birthdayInput.isEnabled = false

            // Gender
            genderInput.setText(userData.gender?.name)

            // Button
            resetPasswordButton.isVisible = userData.email != null
        }

        viewModel.fieldChangeResult.observe(viewLifecycleOwner) {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }

        resetPasswordButton.setOnClickListener {
            findNavController().navigate(ResetPasswordFragmentDirections.toResetPasswordFragment())
        }

        configureBirthday()
        configureGender()
        configureFields()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Glide.with(this)
            .load(viewModel.state.value?.avatar)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_default_avatar)
            .error(R.drawable.ic_default_avatar)
            .into(avatar)
    }

    private fun configureFields() {
        firstnameInput.setOnFocusChangeListener { v, hasFocus -> focusListener(v, hasFocus, firstnameLayout, FieldsForChanging.FIRST_NAME) }
        lastnameInput.setOnFocusChangeListener { v, hasFocus -> focusListener(v, hasFocus, lastnameLayout, FieldsForChanging.LAST_NAME) }
        nicknameInput.setOnFocusChangeListener { v, hasFocus -> focusListener(v, hasFocus, nicknameLayout, FieldsForChanging.NICKNAME) }
        phoneInput.setOnFocusChangeListener { v, hasFocus -> focusListener(v, hasFocus, phoneLayout, FieldsForChanging.PHONE) }
    }

    private fun focusListener(view: View, hasFocus: Boolean, layout: TextInputLayout, field: FieldsForChanging) {
        if (hasFocus) return
        view as EditText

        val validateResult = viewModel.validateField(field, view.text?.toString())
        if (validateResult.isSuccess) {
            viewModel.updateField(field, view.text.toString())
            layout.isErrorEnabled = false
        } else {
            layout.error = validateResult.errorMessage
        }
    }

    private fun configureBirthday() {
        birthdayInput.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, calendarMonth, dayOfMonth ->
                    val month = if (calendarMonth + 1 < 10) "0$calendarMonth" else "$calendarMonth"
                    val day = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                    val result = "$year-$month-$day"
                    birthdayInput.setText(result)

                    viewModel.updateField(FieldsForChanging.BIRTHDAY, result)
                },
                1990,
                -1,
                -1
            ).show()
        }
    }

    private fun configureGender() {
        val items = GenderResponse.values()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        genderInput.setAdapter(adapter)
        genderInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateField(FieldsForChanging.GENDER, adapter.getItem(position)!!.name)
        }
    }
}