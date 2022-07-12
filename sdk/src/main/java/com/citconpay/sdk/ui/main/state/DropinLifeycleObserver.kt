package com.citconpay.sdk.ui.main.state

import android.app.Activity.RESULT_CANCELED
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.citconpay.cardform.view.CardForm
import com.citconpay.sdk.data.model.CPayAPIType
import com.citconpay.sdk.data.model.CPayMethodType
import com.citconpay.sdk.data.model.CPayResult
import com.citconpay.sdk.ui.main.view.CUPaySDKActivity
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel
import com.citconpay.sdk.utils.Status
import sdk.CPaySDK
import upisdk.CPayUPISDK

class DropinLifecycleObserver(activity: CUPaySDKActivity, viewModel: DropinViewModel) : DefaultLifecycleObserver {
    private val mViewModel: DropinViewModel by lazy { viewModel }
    private val mLifecycleOwner: LifecycleOwner by lazy { activity }
    private val mActivity by lazy { activity }

    private var mGatewayType = "None"
    //private val SANDBOX_TOKENIZATION_KEY = "sandbox_tmxhyf7d_dcpspy2brwdjr3qn"

    //@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onCreate(owner: LifecycleOwner) {
        //mViewModel.mLoading.observe(mLifecycleOwner, LoadingObserver(mActivity))

        when (mViewModel.getDropInRequest().getApiType()) {
            CPayAPIType.ONLINE_ORDER -> {
                mGatewayType = mViewModel.getDropInRequest().getPaymentMethod().type
                when (mViewModel.getDropInRequest().getPaymentMethod()) {
                    CPayMethodType.ALI,CPayMethodType.WECHAT,CPayMethodType.UNIONPAY ->
                        mViewModel.requestOnlineOrder(mActivity,sdk.CPayLaunchType.OTHERS)

                    else -> {
                        mViewModel.requestOnlineOrder(mActivity,sdk.CPayLaunchType.URL)
                    }
                }

            }
            CPayAPIType.UPI_ORDER -> {
                //UPI SDK
                mViewModel.loadClientToken().observe(mLifecycleOwner) {
                    it?.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                it.data?.let { response ->
                                    mGatewayType = response.data.gateway
                                    if (mGatewayType == "braintree") {
                                        mViewModel.getDropInRequest().getBrainTreeDropInRequest()
                                            //.clientToken(SANDBOX_TOKENIZATION_KEY)
                                            .clientToken(response.data.config.client_token)
                                            .cardholderNameStatus(CardForm.FIELD_REQUIRED)

                                        mActivity.launchDropIn()
                                    } else if (mGatewayType == "toss" || mGatewayType == "sbps" || mGatewayType == "fomo" ) {
                                        mViewModel.requestUPIOrder(mActivity, upisdk.CPayLaunchType.URL)
                                    } else {
                                        // alipay,wechatpay,upop
                                        mViewModel.requestUPIOrder(mActivity, upisdk.CPayLaunchType.OTHERS)
                                    }
                                }

                            }
                            Status.ERROR -> {
                                it.message?.let { err ->
                                    mActivity.finish(
                                        CPayResult(
                                            RESULT_CANCELED,
                                            mViewModel.getDropInRequest().getPaymentMethod(),
                                            err
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

            CPayAPIType.UPI_INQUIRE -> {
                mViewModel.upiInquire(mActivity)
            }

            CPayAPIType.ONLINE_INQUIRE -> {
                mViewModel.onlineInquire(mActivity)
            }
        }



    }

    //@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume(owner: LifecycleOwner) {
        if (mViewModel.mInitFlag) {
            //alipay and wxpay have call inquire internally
            if (mGatewayType != "alipay" && mGatewayType != "wechatpay") {
                when (mViewModel.getDropInRequest().getApiType()) {
                    CPayAPIType.UPI_ORDER -> {
                        CPayUPISDK.getInstance()?.onResume()
                    }

                    CPayAPIType.ONLINE_ORDER -> {
                        CPaySDK.getInstance()?.onResume()
                    }

                    else -> {}
                }
            }
        } else {
            //1st onResume
            mViewModel.mInitFlag = true
        }
    }

}