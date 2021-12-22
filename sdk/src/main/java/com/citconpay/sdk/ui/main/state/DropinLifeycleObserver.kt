package com.citconpay.sdk.ui.main.state

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.*
import com.citconpay.sdk.data.api.response.ConfirmChargePayment
import com.citconpay.sdk.data.api.response.PlacedOrder
import com.citconpay.sdk.data.repository.CPayENVMode
import com.citconpay.sdk.data.model.CPayOrderResult
import com.citconpay.sdk.data.model.CitconPaymentMethodType
import com.citconpay.sdk.data.model.ErrorMessage
import com.citconpay.sdk.ui.main.view.CUPaySDKActivity
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel
import com.citconpay.sdk.utils.Constant
import com.citconpay.sdk.utils.Status
import com.cupay.cardform.view.CardForm
import sdk.CPayMode
import sdk.CPaySDK
import sdk.interfaces.OrderResponse
import sdk.models.CPayInquireResult
import sdk.models.CPayOrder

class DropinLifecycleObserver(activity: CUPaySDKActivity, viewModel: DropinViewModel) : LifecycleObserver {
    private val mViewModel: DropinViewModel by lazy { viewModel }
    private val mLifecycleOwner: LifecycleOwner by lazy { activity }
    private val mActivity by lazy { activity }
    private var mRunningFlag = false
    //private val SANDBOX_TOKENIZATION_KEY = "sandbox_tmxhyf7d_dcpspy2brwdjr3qn"
    private val mInquireReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val response = intent.getSerializableExtra("inquire_result") as CPayInquireResult?

            val result: PlacedOrder? = response?.run {
                PlacedOrder("", mId, mReference,
                        if (mAmount == "")  0 else mAmount.toInt(), true, false,
                        mCurrency, 0, 0, mStatus?:"", "",
                        ConfirmChargePayment(mViewModel.getDropInRequest().getPaymentMethod().type, ""))
            }

            if (mRunningFlag) {
                val resultIntent = Intent()
                resultIntent.putExtra(Constant.PAYMENT_RESULT, result?.let {
                    CPayOrderResult(RESULT_OK, mViewModel.getDropInRequest().getPaymentMethod(), it)
                })
                mActivity.finish(RESULT_OK, resultIntent)
            }
        }
    }

    /**
     * Register BroadcastReceiver.
     */
    private fun registerInquireReceiver() {
        val filter = IntentFilter()
        filter.addAction("CPAY_INQUIRE_ORDER")
        CPaySDK.getInstance().registerReceiver(mInquireReceiver, filter)
    }

    /**
     *
     * unregister BroadcastReceiver.
     */
    private fun unregisterInquireReceiver() {
        if (mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.ALI
                || mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.ALI_HK
                || mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.KAKAO
                || mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.WECHAT
                || mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.UNIONPAY) {
                    CPaySDK.getInstance().unregisterReceiver(mInquireReceiver)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        //mViewModel.mLoading.observe(mLifecycleOwner, LoadingObserver(mActivity))
        when (mViewModel.getDropInRequest().getPaymentMethod()) {
            CitconPaymentMethodType.ALI, CitconPaymentMethodType.ALI_HK,
            CitconPaymentMethodType.WECHAT, CitconPaymentMethodType.KAKAO,
            CitconPaymentMethodType.UNIONPAY -> {
                val dropInRequest = mViewModel.getDropInRequest()
                val order = CPayOrder(
                        dropInRequest.getReference(),
                        dropInRequest.getSubject(),
                        dropInRequest.getBody(),
                        dropInRequest.getAmount(),
                        dropInRequest.getCurrency(),
                        dropInRequest.getPaymentMethod().type,
                        dropInRequest.getIpn(),
                        dropInRequest.getCallback(),
                        dropInRequest.isAllowDuplicate()
                )

                CPaySDK.getInstance(mActivity, null)
                when (dropInRequest.getENVMode()) {
                    CPayENVMode.DEV -> CPaySDK.setMode(CPayMode.DEV)
                    CPayENVMode.UAT -> CPaySDK.setMode(CPayMode.UAT)
                    CPayENVMode.PROD -> CPaySDK.setMode(CPayMode.PROD)
                    else -> CPaySDK.setMode(CPayMode.UAT)
                }

                CPaySDK.setToken("XYIL2W9BCQSTNN1CXUQ6WEH9JQYZ3VLM")
                CPaySDK.getInstance().requestOrder(order, OrderResponse { orderResult ->
                    if (orderResult == null || orderResult.mStatus != "0") {
                        mActivity.finish(RESULT_CANCELED, Intent().putExtra(
                                Constant.PAYMENT_RESULT, CPayOrderResult(
                                RESULT_CANCELED,
                                mViewModel.getDropInRequest().getPaymentMethod(),
                                orderResult?.run {
                                    ErrorMessage(mStatus, mMessage, mOrder.mReferenceId)
                                } ?: ErrorMessage("error", "error", "error")
                        )
                        ))

                        mRunningFlag = false
                        return@OrderResponse
                    } else {
                        mRunningFlag = true
                    }

                    Log.d("zlm", "CPay requestOrder success")
                })
            }
            else -> {
                mViewModel.loadClientToken().observe(mLifecycleOwner, Observer {
                    it?.let {
                        when (it.status) {
                            //Todo: load config to fill up related Request by payment method type
                            Status.SUCCESS -> {
                                it.data?.let { response ->
                                    mViewModel.getDropInRequest().getBrainTreeDropInRequest()
                                            //.clientToken(SANDBOX_TOKENIZATION_KEY)
                                            //.clientToken(response.data?.token)
                                            .clientToken(response.data.payment.client_token)
                                            .cardholderNameStatus(CardForm.FIELD_REQUIRED)
                                }
                                mActivity.launchDropIn()

                            }
                            Status.ERROR -> {
                                it.message?.let { err ->
                                    mActivity.finish(RESULT_CANCELED, Intent().putExtra(
                                            Constant.PAYMENT_RESULT, CPayOrderResult(
                                            RESULT_CANCELED,
                                            mViewModel.getDropInRequest().getPaymentMethod(),
                                            err
                                    )
                                    ))
                                }
                            }
                            Status.LOADING -> {
                            }
                        }
                    }
                })
            }
        }



    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.ALI
                || mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.ALI_HK
                || mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.KAKAO
                || mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.WECHAT
                || mViewModel.getDropInRequest().getPaymentMethod() == CitconPaymentMethodType.UNIONPAY) {
            registerInquireReceiver()

            CPaySDK.getInstance().onResume()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
            unregisterInquireReceiver()
    }
}