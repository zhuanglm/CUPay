package com.citconpay.sdk.ui.main.state

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.citconpay.cardform.view.CardForm
import com.citconpay.sdk.data.api.response.ConfirmChargePayment
import com.citconpay.sdk.data.api.response.PlacedOrder
import com.citconpay.sdk.data.model.CPayAPIType
import com.citconpay.sdk.data.model.CPayMethodType
import com.citconpay.sdk.data.model.CPayOrderResult
import com.citconpay.sdk.data.model.ErrorMessage
import com.citconpay.sdk.data.repository.CPayENVMode
import com.citconpay.sdk.ui.main.view.CUPaySDKActivity
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel
import com.citconpay.sdk.utils.Constant
import com.citconpay.sdk.utils.Status
import sdk.CPayMode
import sdk.CPaySDK
import sdk.interfaces.OrderResponse
import sdk.models.CPayInquireResult
import sdk.models.CPayOrder
import upisdk.CPayUPISDK
import upisdk.models.CPayUPIOrder

class DropinLifecycleObserver(activity: CUPaySDKActivity, viewModel: DropinViewModel) : DefaultLifecycleObserver {
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
                        if (mAmount == "")  0 else mAmount.toInt(),
                    amount_captured = true,
                    amount_refunded = false,
                    currency = mCurrency,
                    time_created = 0,
                    time_captured = 0,
                    status = mStatus?:"",
                    country = "",
                    payment = ConfirmChargePayment(mViewModel.getDropInRequest().getPaymentMethod().type, "")
                )
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
        if (mViewModel.getDropInRequest().getApiType() == CPayAPIType.ONLINE) {
            CPaySDK.getInstance().unregisterReceiver(mInquireReceiver)
        }
    }

    //@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onCreate(owner: LifecycleOwner) {
        //mViewModel.mLoading.observe(mLifecycleOwner, LoadingObserver(mActivity))
        when (mViewModel.getDropInRequest().getApiType()) {
            CPayAPIType.ONLINE -> {
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

                CPaySDK.initInstance(mActivity, null)
                when (dropInRequest.getENVMode()) {
                    CPayENVMode.DEV -> CPaySDK.setMode(CPayMode.DEV)
                    CPayENVMode.UAT -> CPaySDK.setMode(CPayMode.UAT)
                    CPayENVMode.PROD -> CPaySDK.setMode(CPayMode.PROD)
                    else -> CPaySDK.setMode(CPayMode.UAT)
                }

                CPaySDK.setToken("XYIL2W9BCQSTNN1CXUQ6WEH9JQYZ3VLM")
                CPaySDK.getInstance().requestOrder(mActivity, order, OrderResponse { orderResult ->
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
                })
            }
            else -> {
                mViewModel.loadClientToken().observe(mLifecycleOwner) {
                    it?.let {
                        when (it.status) {
                            //Todo: load config to fill up related Request by payment method type
                            Status.SUCCESS -> {
                                it.data?.let { response ->
                                    //todo: setup and launch dropin according to gateway
                                    if (response.data.gateway == "braintree") {
                                        mViewModel.getDropInRequest().getBrainTreeDropInRequest()
                                            //.clientToken(SANDBOX_TOKENIZATION_KEY)
                                            //.clientToken(response.data?.token)
                                            .clientToken(response.data.client_token)
                                            .cardholderNameStatus(CardForm.FIELD_REQUIRED)

                                        mActivity.launchDropIn()
                                    } else {
                                        // alipay,wechatpay,upop
                                        val dropInRequest = mViewModel.getDropInRequest()
                                        val order = CPayUPIOrder(
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

                                        CPayUPISDK.initInstance(mActivity, null)
                                        when (dropInRequest.getENVMode()) {
                                            CPayENVMode.DEV -> CPayUPISDK.setMode("DEV")
                                            CPayENVMode.UAT -> CPayUPISDK.setMode("UAT")
                                            CPayENVMode.QA -> CPayUPISDK.setMode("QA")
                                            CPayENVMode.PROD -> CPayUPISDK.setMode("PROD")
                                        }

                                        CPayUPISDK.setToken("XYIL2W9BCQSTNN1CXUQ6WEH9JQYZ3VLM")
                                        CPayUPISDK.getInstance().requestOrder(
                                            mActivity,
                                            order,
                                            upisdk.interfaces.OrderResponse { orderResult ->
                                                if (orderResult == null || orderResult.mStatus != "0") {
                                                    mActivity.finish(RESULT_CANCELED,
                                                        Intent().putExtra(
                                                            Constant.PAYMENT_RESULT,
                                                            CPayOrderResult(
                                                                RESULT_CANCELED,
                                                                mViewModel.getDropInRequest()
                                                                    .getPaymentMethod(),
                                                                orderResult?.run {
                                                                    ErrorMessage(
                                                                        mStatus,
                                                                        mMessage,
                                                                        mOrder.mReferenceId
                                                                    )
                                                                } ?: ErrorMessage(
                                                                    "error",
                                                                    "error",
                                                                    "error"
                                                                )
                                                            )
                                                        ))

                                                    mRunningFlag = false
                                                    return@OrderResponse
                                                } else {
                                                    mRunningFlag = true
                                                }
                                            })
                                    }
                                }

                            }
                            Status.ERROR -> {
                                it.message?.let { err ->
                                    mActivity.finish(
                                        RESULT_CANCELED, Intent().putExtra(
                                            Constant.PAYMENT_RESULT, CPayOrderResult(
                                                RESULT_CANCELED,
                                                mViewModel.getDropInRequest().getPaymentMethod(),
                                                err
                                            )
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

    //@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume(owner: LifecycleOwner) {
        if (mViewModel.getDropInRequest().getApiType() == CPayAPIType.ONLINE) {
            registerInquireReceiver()

            CPaySDK.getInstance().onResume()
        }
    }

    //@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause(owner: LifecycleOwner) {
            unregisterInquireReceiver()
    }
}