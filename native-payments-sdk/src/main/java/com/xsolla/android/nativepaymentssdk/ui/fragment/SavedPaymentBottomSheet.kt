package com.xsolla.android.nativepaymentssdk.ui.fragment

import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xsolla.android.nativepaymentssdk.R
import com.xsolla.android.nativepaymentssdk.ui.adapter.SavedCardsAdapter
import com.xsolla.android.nativepaymentssdk.vm.VmPayment

class SavedPaymentBottomSheet : BottomSheetDialogFragment() {

    private val vmPayment: VmPayment by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.xsolla_native_payments_saved_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<RecyclerView>(R.id.xsolla_native_payments_recycler).addItemDecoration(DividerItemDecoration(
            context, DividerItemDecoration.VERTICAL))
        view.findViewById<View>(R.id.xsolla_native_payments_button_cancel).setOnClickListener {
            dismiss()
        }
        view.findViewById<View>(R.id.xsolla_native_payments_card_new_card).setOnClickListener {
            (activity as NewCardListener).onPayWithNewCard()
        }
        vmPayment.savedMethodsProgress.observe(viewLifecycleOwner) {
            it?.let {
                view.findViewById<View>(R.id.xsolla_native_payments_progress_get_saved).isVisible = it
            }
        }
        vmPayment.paymentProgress.observe(viewLifecycleOwner) {
            it?.let {
                view.findViewById<View>(R.id.xsolla_native_payments_progress_payment).isVisible = it
                if (it) {
                    view.findViewById<View>(R.id.xsolla_native_payments_button_cancel).visibility = View.INVISIBLE
                } else {
                    view.findViewById<View>(R.id.xsolla_native_payments_button_cancel).visibility = View.VISIBLE
                }
            }
        }
        vmPayment.savedCards.observe(viewLifecycleOwner) {
            it?.let {
                view.findViewById<RecyclerView>(R.id.xsolla_native_payments_recycler)
                    .adapter = SavedCardsAdapter(it, activity as SavedCardsAdapter.CardClickListener)
            }
        }
        vmPayment.loadSavedMethods()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        (activity as CancelListener).onCancel()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SavedPaymentBottomSheet()
    }

    interface CancelListener {
        fun onCancel()
    }

    interface NewCardListener {
        fun onPayWithNewCard()
    }
}