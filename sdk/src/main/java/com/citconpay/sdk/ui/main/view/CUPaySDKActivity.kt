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
import com.citconpay.sdk.data.model.PaymentMethod
import com.citconpay.sdk.data.repository.ApiRepository
import com.citconpay.sdk.databinding.ActivitySdkMainBinding
import com.citconpay.sdk.ui.base.BaseActivity
import com.citconpay.sdk.ui.base.ViewModelFactory
import com.citconpay.sdk.ui.main.state.DropinLifecycleObserver
import com.citconpay.sdk.ui.main.viewmodel.DropinViewModel

class CUPaySDKActivity : BaseActivity() {
    private lateinit var mDropInViewModel: DropinViewModel
    private lateinit var mLifecycleObserver: DropinLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivitySdkMainBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_sdk_main)

        mDropInViewModel = ViewModelProvider(this, ViewModelFactory(ApiRepository(),application))
                .get(DropinViewModel::class.java)
        mDropInViewModel.mTextViewMsg.observe(this) {
            binding.payment = PaymentMethod(it)
        }

        mDropInViewModel.mDropInRequest = intent.getParcelableExtra(CPayDropInRequest.EXTRA_CHECKOUT_REQUEST)!!

        mLifecycleObserver = DropinLifecycleObserver(this,mDropInViewModel)
        lifecycle.addObserver(mLifecycleObserver)

        binding.lifecycleOwner = this

    }

    override fun finish() {
        super.finish()
        lifecycle.removeObserver(mLifecycleObserver)
        setResult(RESULT_OK, Intent())
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
                result?.let { it -> mDropInViewModel.getDropInResult(it) }
                //result?.let{ result.paymentMethodNonce?.let { it1 -> displayNonce(it1, result.deviceData) } }
            } /*else {
                val returnedData = data!!.getParcelableExtra<Parcelable>(EXTRA_PAYMENT_RESULT)
                val deviceData = data!!.getStringExtra(EXTRA_DEVICE_DATA)
                if (returnedData is PaymentMethodNonce) {
                    displayNonce(returnedData as PaymentMethodNonce?, deviceData)
                }
                //mCreateTransactionButton.setEnabled(true)
            }*/
        } else if (resultCode != RESULT_CANCELED) {
            //showDialog((data!!.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception?)!!.message)
        }
    }


}