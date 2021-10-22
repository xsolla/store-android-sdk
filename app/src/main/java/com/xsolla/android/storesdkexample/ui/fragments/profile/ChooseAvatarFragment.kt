package com.xsolla.android.storesdkexample.ui.fragments.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.databinding.FragmentChooseAvatarBinding
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.AvatarItem
import com.xsolla.android.storesdkexample.adapter.AvatarsItemDecoration
import com.xsolla.android.storesdkexample.adapter.ChooseAvatarAdapter
import com.xsolla.android.storesdkexample.data.local.PrefManager
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmChooseAvatar
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
import com.xsolla.android.storesdkexample.ui.vm.base.ViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.File

class ChooseAvatarFragment : BaseFragment() {
    private val binding: FragmentChooseAvatarBinding by viewBinding()

    private val args: ChooseAvatarFragmentArgs by navArgs()
    private val viewModel: VmChooseAvatar by viewModels()
    private val profileViewModel: VmProfile by activityViewModels {
        ViewModelFactory(resources)
    }

    private val avatars = listOf(
        AvatarItem(R.drawable.avatar_1),
        AvatarItem(R.drawable.avatar_2),
        AvatarItem(R.drawable.avatar_3),
        AvatarItem(R.drawable.avatar_4),
        AvatarItem(R.drawable.avatar_5),
        AvatarItem(R.drawable.avatar_6),
    )

    private lateinit var adapter: ChooseAvatarAdapter

    override fun getLayout() = R.layout.fragment_choose_avatar

    override fun initUI() {
        mainToolbar?.isGone = true
        binding.close.setOnClickListener { findNavController().navigateUp() }
        Glide.with(this).load(args.currentAvatar).error(R.drawable.ic_default_avatar).circleCrop().into(binding.mainAvatar)

        viewModel.uploadingResult.observe(viewLifecycleOwner) { showSnack(it) }
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.lockForeground.isVisible = it
            binding.progress.isInvisible = !it
        }

        adapter = ChooseAvatarAdapter(avatars, args.id, onAvatarClickListener = { avatarRes ->
            viewModel.loading.value = true

            val file = prepareFile(avatarRes)
            viewModel.uploadAvatar(file) {
                profileViewModel.updateAvatar(it)
                Glide.with(this).load(avatarRes).circleCrop().into(binding.mainAvatar)

                PrefManager.setAvatar(args.id, avatarRes)
                adapter.notifyDataSetChanged()
            }
        })
        binding.avatarsRecycler.adapter = adapter
        binding.avatarsRecycler.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        binding.avatarsRecycler.addItemDecoration(AvatarsItemDecoration())
        binding.avatarsRecycler.setHasFixedSize(true)

        binding.removeAvatarButton.setOnClickListener {
            viewModel.removeAvatar {
                Glide.with(this).load(R.drawable.ic_default_avatar).circleCrop().into(binding.mainAvatar)

                PrefManager.setAvatar(args.id, -1)
                adapter.notifyDataSetChanged()
                profileViewModel.updateAvatar(null)
            }
        }
    }

    override fun onDestroyView() {
        mainToolbar?.isVisible = true
        super.onDestroyView()
    }

    private fun prepareFile(resourceId: Int): File {
        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)

        val bytes = bos.toByteArray()
        val file = File(requireContext().cacheDir, "avatar.jpeg")
        file.outputStream().use {
            it.write(bytes)
            it.flush()
        }
        return file
    }
}