package com.xsolla.android.storesdkexample.ui.fragments.profile

import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.xsolla.android.login.entity.response.GenderResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
import kotlinx.android.synthetic.main.fragment_profile.avatar
import kotlinx.android.synthetic.main.fragment_profile.birthdayInput
import kotlinx.android.synthetic.main.fragment_profile.emailInput
import kotlinx.android.synthetic.main.fragment_profile.emailLayout
import kotlinx.android.synthetic.main.fragment_profile.firstnameInput
import kotlinx.android.synthetic.main.fragment_profile.genderInput
import kotlinx.android.synthetic.main.fragment_profile.genderLayout
import kotlinx.android.synthetic.main.fragment_profile.lastnameInput
import kotlinx.android.synthetic.main.fragment_profile.nickname
import kotlinx.android.synthetic.main.fragment_profile.nicknameInput
import kotlinx.android.synthetic.main.fragment_profile.phoneInput
import kotlinx.android.synthetic.main.fragment_profile.usernameInput
import kotlinx.android.synthetic.main.fragment_profile.usernameLayout

class ProfileFragment : BaseFragment() {
    private val viewModel: VmProfile by viewModels()

    override fun getLayout() = R.layout.fragment_profile

    override fun initUI() {
        viewModel.error.observe(viewLifecycleOwner) {
            showSnack(it)
        }
        viewModel.state.observe(viewLifecycleOwner) { userData ->
            if (userData == null) return@observe

            // avatar
            Glide.with(this)
                .load(userData.picture)
                .placeholder(R.drawable.ic_xsolla_logo)
                .error(R.drawable.ic_xsolla_logo)
                .circleCrop()
                .into(avatar)

            // Nickname
            nickname.text = userData.nickname ?: userData.name ?: userData.firstName ?: userData.lastName ?: "Nickname"
            userData.nickname?.let { nicknameInput.setText(it) }

            // Email
            emailLayout.isVisible = userData.email != null
            emailInput.setText(userData.email)

            // Username
            usernameLayout.isVisible = userData.name != null
            usernameInput.setText(userData.name)

            // Phone
            phoneInput.setText(userData.phone)

            // Names
            firstnameInput.setText(userData.firstName)
            lastnameInput.setText(userData.lastName)

            // Birthday
            birthdayInput.setText(userData.birthday)

            // Gender
            genderInput.setText(userData.gender?.name)
        }

        configureBirthday()
        configureGender()
    }

    private fun configureBirthday() {
        birthdayInput.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, calendarMonth, dayOfMonth ->
                    val month = calendarMonth + 1
                    val result = "$dayOfMonth/$month/$year"
                    birthdayInput.setText(result)

                },
                1990,
                0,
                0
            )
                .show()
        }
    }

    private fun configureGender() {
        val items = GenderResponse.values()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        (genderLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }
}