package com.citconpay.sdk.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.citconpay.sdk.databinding.PaymentResultFragmentBinding
import com.citconpay.sdk.ui.base.BaseFragment
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel


class ConfirmPaymentFragment : BaseFragment() {
    private var _binding: PaymentResultFragmentBinding? = null

    companion object {
        fun newInstance() = ConfirmPaymentFragment()
    }

    private lateinit var sharedModel: DropinViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //return inflater.inflate(R.layout.payment_list_fragment, container, false)
        _binding = PaymentResultFragmentBinding.inflate(inflater, container, false)
        _binding?.buttonOrder?.setOnClickListener {
            close()
        }
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedModel = ViewModelProvider(requireActivity()).get(DropinViewModel::class.java)
        sharedModel.mResultString.observe(viewLifecycleOwner) {
            _binding?.result = it
            _binding?.frameLayout?.visibility = View.VISIBLE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun close() {
        activity?.finish()
    }

}

