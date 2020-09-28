package com.xsolla.android.storesdkexample.ui.fragments.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.AvatarItem
import com.xsolla.android.storesdkexample.adapter.AvatarsItemDecoration
import com.xsolla.android.storesdkexample.adapter.ChooseAvatarAdapter
import com.xsolla.android.storesdkexample.data.local.PrefManager
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmChooseAvatar
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
import kotlinx.android.synthetic.main.activity_store.appbar
import kotlinx.android.synthetic.main.activity_store.lock
import kotlinx.android.synthetic.main.app_bar_main.view.mainToolbar
import kotlinx.android.synthetic.main.fragment_choose_avatar.avatarsRecycler
import kotlinx.android.synthetic.main.fragment_choose_avatar.close
import kotlinx.android.synthetic.main.fragment_choose_avatar.mainAvatar
import kotlinx.android.synthetic.main.fragment_choose_avatar.removeAvatarButton
import java.io.ByteArrayOutputStream
import java.io.File

class ChooseAvatarFragment : BaseFragment() {
    private val args: ChooseAvatarFragmentArgs by navArgs()
    private val viewModel: VmChooseAvatar by viewModels()
    private val profileViewModel: VmProfile by activityViewModels()

    private val avatars = listOf(
        AvatarItem(R.drawable.avatar_1),
        AvatarItem(R.drawable.avatar_2),
        AvatarItem(R.drawable.avatar_3),
        AvatarItem(R.drawable.avatar_4),
        AvatarItem(R.drawable.avatar_5),
        AvatarItem(R.drawable.avatar_6),
    )

    override fun getLayout() = R.layout.fragment_choose_avatar

    override fun initUI() {
        requireActivity().appbar.mainToolbar.isGone = true
        close.setOnClickListener { findNavController().navigateUp() }
        Glide.with(this)
            .load(args.currentAvatar)
            .apply(RequestOptions.circleCropTransform())
            .error(R.drawable.ic_default_avatar)
            .into(mainAvatar)

        removeAvatarButton.setOnClickListener {
            viewModel.removeAvatar {
                Glide.with(this).load(R.drawable.ic_default_avatar).circleCrop().into(mainAvatar)
                PrefManager.setAvatar(args.id, -1)
                avatarsRecycler.adapter?.notifyDataSetChanged()
                profileViewModel.updateAvatar(null)
            }
        }

        viewModel.uploadingResult.observe(viewLifecycleOwner) { showSnack(it) }
        viewModel.loading.observe(viewLifecycleOwner) { requireActivity().lock.isVisible = it }

        val adapter = ChooseAvatarAdapter(avatars, args.id, onAvatarClickListener = { avatarRes ->
            viewModel.loading.value = true

            val bitmap = BitmapFactory.decodeResource(resources, avatarRes)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)

            val bytes = bos.toByteArray()
            val file = File(requireContext().cacheDir, "avatar.jpeg")
            file.outputStream().use {
                it.write(bytes)
                it.flush()
            }

            viewModel.uploadAvatar(file) {
                profileViewModel.updateAvatar(it)
                Glide.with(this).load(avatarRes).apply(RequestOptions.circleCropTransform()).into(mainAvatar)

                PrefManager.setAvatar(args.id, avatarRes)
                avatarsRecycler.adapter?.notifyDataSetChanged()
            }
        })
        val layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        val decoration = AvatarsItemDecoration()
        avatarsRecycler.adapter = adapter
        avatarsRecycler.layoutManager = layoutManager
        avatarsRecycler.addItemDecoration(decoration)
        avatarsRecycler.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        requireActivity().appbar.mainToolbar.isVisible = true
        super.onDestroyView()
    }
}