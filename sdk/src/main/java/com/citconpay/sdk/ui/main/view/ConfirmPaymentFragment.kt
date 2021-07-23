package com.citconpay.sdk.ui.main.view

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.citconpay.sdk.databinding.PaymentResultFragmentBinding
import com.citconpay.sdk.ui.base.BaseFragment
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel
import com.citconpay.sdk.utils.Constant.PAYMENT_ERROR
import com.citconpay.sdk.utils.Constant.PAYMENT_RESULT
import com.citconpay.sdk.utils.Status


class ConfirmPaymentFragment : BaseFragment() {
    private var _binding: PaymentResultFragmentBinding? = null

    private lateinit var sharedModel: DropinViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedModel = ViewModelProvider(requireActivity()).get(DropinViewModel::class.java)
        sharedModel.mResult.observe(viewLifecycleOwner) {
            _binding?.result = sharedModel.displayNonce(it)
            _binding?.frameLayout?.visibility = View.VISIBLE

            it.paymentMethodNonce?.let { nonce ->
                sharedModel.placeOrderByNonce(nonce).observe(viewLifecycleOwner, { result ->
                    result?.let { resource ->
                        val resultIntent = Intent()
                        when (resource.status) {
                            Status.SUCCESS -> {
                                result.data?.let { response ->
                                    resultIntent.putExtra(PAYMENT_RESULT,response.data)
                                }
                                Toast.makeText(context, "paid successful", Toast.LENGTH_LONG).show()
                                close(RESULT_OK,resultIntent)
                                activity as CUPaySDKActivity
                            }
                            Status.ERROR -> {
                                result.message?.let { errorMessage ->
                                    resultIntent.putExtra(PAYMENT_ERROR, errorMessage)
                                }
                                Toast.makeText(context, "paid fail", Toast.LENGTH_LONG).show()
                                close(RESULT_CANCELED,resultIntent)
                            }
                            Status.LOADING -> {

                            }
                        }
                    }
                })
            }
        }

    }

    private fun close(resultStatus: Int, intent: Intent) {
        val parent = activity as CUPaySDKActivity
        parent.finish(resultStatus,intent)
    }

}

