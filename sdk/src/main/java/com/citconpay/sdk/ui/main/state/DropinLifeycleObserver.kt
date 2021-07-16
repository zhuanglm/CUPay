package com.citconpay.sdk.ui.main.state

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.citconpay.sdk.data.api.response.CitconApiResponse
import com.citconpay.sdk.data.api.response.ResponseLoadConfig
import com.citconpay.sdk.ui.main.adapter.LoadingObserver
import com.citconpay.sdk.ui.main.view.CUPaySDKActivity
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel
import com.citconpay.sdk.utils.Status
import com.cupay.cardform.view.CardForm

class DropinLifecycleObserver(activity: CUPaySDKActivity, viewModel: DropinViewModel) : LifecycleObserver {
    private val mViewModel : DropinViewModel by lazy { viewModel }
    private val mLifecycleOwner : LifecycleOwner by lazy { activity }
    private val mContext : Context by lazy { activity }
    private val mActivity by lazy { activity }
    private val SANDBOX_TOKENIZATION_KEY = "sandbox_tmxhyf7d_dcpspy2brwdjr3qn"

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        //mViewModel.mLoading.observe(mLifecycleOwner, LoadingObserver(mActivity))

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
                            mActivity.finish(RESULT_CANCELED, "${err.message}(${err.debug} ${err.code})")
                        }
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        //mViewModel.getTextView("CitconPay")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {

    }
}