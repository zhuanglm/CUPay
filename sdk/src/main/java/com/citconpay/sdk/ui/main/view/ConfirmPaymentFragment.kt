package com.citconpay.sdk.ui.main.view

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.citconpay.sdk.data.model.CPayResult
import com.citconpay.sdk.databinding.PaymentResultFragmentBinding
import com.citconpay.sdk.ui.base.BaseFragment
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel
import com.citconpay.sdk.utils.Status


class ConfirmPaymentFragment : BaseFragment() {
    private var _binding: PaymentResultFragmentBinding? = null

    private lateinit var mSharedModel: DropinViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //return inflater.inflate(R.layout.payment_list_fragment, container, false)
        _binding = PaymentResultFragmentBinding.inflate(inflater, container, false)
        _binding?.buttonOrder?.setOnClickListener {
            //close()
        }
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mSharedModel = ViewModelProvider(requireActivity())[DropinViewModel::class.java]
        mSharedModel.mResult.observe(viewLifecycleOwner) {
            _binding?.result = mSharedModel.displayNonce(it)
            _binding?.frameLayout?.visibility = View.VISIBLE

            it.paymentMethodNonce?.let { nonce ->
                mSharedModel.placeOrderByNonce(nonce).observe(viewLifecycleOwner) { result ->
                    result?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                result.data?.let { response ->
                                    close(CPayResult(
                                            RESULT_OK,
                                            mSharedModel.getDropInRequest().getPaymentMethod(),
                                            response.data
                                        )
                                    )
                                }
                            }
                            Status.ERROR -> {
                                result.message?.let { errorMessage ->
                                    close(CPayResult(
                                            RESULT_CANCELED,
                                            mSharedModel.getDropInRequest().getPaymentMethod(),
                                            errorMessage
                                        )
                                    )
                                }
                            }
                            Status.LOADING -> {

                            }
                        }
                    }
                }
            }
        }

    }

    private fun close(result: CPayResult) {
        val parent = activity as CUPaySDKActivity
        parent.finish(result)
    }

}

