/*
 * Copyright 2021-present CitconPay UPI SDK
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.citconpay.sdk.ui.main.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.braintreepayments.api.dropin.DropInResult
import com.citconpay.sdk.R
import com.citconpay.sdk.data.config.CPayDropInRequest
import com.citconpay.sdk.data.model.ErrorMessage
import com.citconpay.sdk.data.repository.ApiRepository
import com.citconpay.sdk.databinding.ActivitySdkMainBinding
import com.citconpay.sdk.ui.base.BaseActivity
import com.citconpay.sdk.ui.base.ViewModelFactory
import com.citconpay.sdk.ui.main.adapter.LoadingObserver
import com.citconpay.sdk.ui.main.state.DropinLifecycleObserver
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel
import com.citconpay.sdk.utils.Constant.PAYMENT_ERROR

class CUPaySDKActivity : BaseActivity() {
    private lateinit var mDropInViewModel: DropinViewModel
    private lateinit var mLifecycleObserver: DropinLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivitySdkMainBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_sdk_main)

        mDropInViewModel = ViewModelProvider(this, ViewModelFactory(ApiRepository(),application))
                .get(DropinViewModel::class.java)

        mDropInViewModel.mLoading.observe(this,LoadingObserver(binding.pbLoadingParameter, binding.tvLoadingMessage))

        mDropInViewModel.mTextViewMsg.observe(this) {
            binding.tvLoadingMessage.text = getString(R.string.loading_message,it)
        }

        mDropInViewModel.setDropInRequest(intent.getParcelableExtra(CPayDropInRequest.EXTRA_CHECKOUT_REQUEST)!!)

        mLifecycleObserver = DropinLifecycleObserver(this,mDropInViewModel)
        lifecycle.addObserver(mLifecycleObserver)

        binding.lifecycleOwner = this

    }

    override fun finish() {
        lifecycle.removeObserver(mLifecycleObserver)
        super.finish()
    }

    fun finish(resultCode: Int, intent: Intent) {
        setResult(resultCode, intent)
        finish()
    }

    fun launchDropIn() {
        //Todo: start by Gateway type in future
        startActivityForResult(mDropInViewModel.getBTDropInRequest().getIntent(this), DROP_IN_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == DROP_IN_REQUEST) {
                val result: DropInResult? = data!!.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
                result?.let { it -> mDropInViewModel.setDropInResult(it) }
            }
        } else if (resultCode != RESULT_CANCELED) {
            setResult(resultCode, Intent())
            finish()
        } else {
            finish(RESULT_CANCELED, Intent().putExtra(PAYMENT_ERROR,ErrorMessage("","","")))
        }

    }


}