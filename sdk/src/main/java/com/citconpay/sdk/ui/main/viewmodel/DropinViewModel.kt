package com.citconpay.sdk.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.braintreepayments.api.models.BinData
import com.braintreepayments.api.models.CardNonce
import com.braintreepayments.api.models.PayPalAccountNonce
import com.braintreepayments.api.models.PaymentMethodNonce
import com.citconpay.sdk.data.config.CPayDropInRequest
import com.citconpay.sdk.data.model.BrainTreeClientToken
import com.citconpay.sdk.data.model.CPayApiResponse
import com.citconpay.sdk.data.repository.ApiRepository
import com.citconpay.sdk.utils.Resource
import kotlinx.coroutines.Dispatchers

class DropinViewModel(private val apiRepository: ApiRepository, application: Application) : AndroidViewModel(application) {
    //val mTextViewMsg = MutableLiveData<String>()
    lateinit var mDropInRequest: CPayDropInRequest
    val mLoading = MutableLiveData<Boolean>()
    val mResultString = MutableLiveData<String>()

    /*fun getTextView(message: String) {
        mTextViewMsg.postValue(message)
    }*/

    fun getDropInResult(result: DropInResult) {
        mResultString.postValue(displayNonce(result))
    }

    private fun getClientToken() = liveData(Dispatchers.IO) {
        mLoading.postValue(true)
        emit(Resource.loading(data = null))
        try {
            mLoading.postValue(false)
            emit(Resource.success(data = apiRepository.getClientToken()))
        } catch (exception: Exception) {
            mLoading.postValue(false)
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun loadClientToken() : LiveData<Resource<CPayApiResponse<BrainTreeClientToken>>> {
        //mLoading.value = true
        return getClientToken()
    }

    fun getBTDropInRequest(): DropInRequest {
        return mDropInRequest.getBrainTreeDropInRequest()
    }

    /*private val refreshTrigger = MutableLiveData<Boolean>()
    val mLoading = MutableLiveData<Boolean>()
    private val api = WanApi.get()
    private val bannerList: LiveData<ApiResponse<List<BannerVO>>> =
            Transformations.switchMap(refreshTrigger) {
                api.bannerList()
            }

    val banners: LiveData<List<BannerVO>> = Transformations.map(bannerList) {
        mLoading.value = false
        it.data ?: ArrayList()
    }

    fun loadData() {
        refreshTrigger.postValue(true)
        mLoading.value = true
    }*/

    private lateinit var mNonce: PaymentMethodNonce

    fun displayNonce(result: DropInResult): String {
        mNonce = result.paymentMethodNonce!!
//        mNonceIcon!!.setImageResource(PaymentMethodType.forType(mNonce).drawable)
//        mNonceIcon.visibility = View.VISIBLE
//        mNonceString!!.text = "Nonce: ${mNonce!!.nonce}"
//        mNonceString.visibility = View.VISIBLE
        var details = ""
        if (mNonce is CardNonce) {
            details = getDisplayString(mNonce as CardNonce?)
            return details
            //_binding!!.tvNonceDetails.text = details
        } else if (mNonce is PayPalAccountNonce) {
            val paypalAccountNonce = mNonce as PayPalAccountNonce

            details = """
                First name: ${paypalAccountNonce.firstName}
                
                """.trimIndent()
            details += """
                Last name: ${paypalAccountNonce.lastName}
                
                """.trimIndent()
            details += """
                Email: ${paypalAccountNonce.email}
                
                """.trimIndent()
            details += """
                Phone: ${paypalAccountNonce.phone}
                
                """.trimIndent()
            details += """
                Payer id: ${paypalAccountNonce.payerId}
                
                """.trimIndent()
            details += """
                Client metadata id: ${paypalAccountNonce.clientMetadataId}
                
                """.trimIndent()

            return details
        } /*else if (mNonce is GooglePaymentCardNonce) {
            details = GooglePaymentActivity.getDisplayString(mNonce as GooglePaymentCardNonce?)
        } else if (mNonce is VisaCheckoutNonce) {
            details = VisaCheckoutActivity.getDisplayString(mNonce as VisaCheckoutNonce?)
        } else if (mNonce is VenmoAccountNonce) {
            details = VenmoActivity.getDisplayString(mNonce as VenmoAccountNonce?)
        } else if (mNonce is LocalPaymentResult) {
            details = LocalPaymentsActivity.getDisplayString(mNonce as LocalPaymentResult?)
        }
        mNonceDetails!!.text = details
        mNonceDetails.setVisibility(View.VISIBLE)
        mDeviceData!!.setText(getString(R.string.device_data_placeholder, deviceData))
        mDeviceData!!.setVisibility(View.VISIBLE)*/
        //mCreateTransactionButton.setEnabled(true)
        return ""
    }

    private fun getDisplayString(nonce: CardNonce?): String {
        nonce?.let{return "Card Last Two: ${nonce.lastTwo}\n" +
                "${getDisplayString(nonce.binData)}\n" +
                "3DS:\n" +
                "         - isLiabilityShifted: ${nonce.threeDSecureInfo.isLiabilityShifted}\n" +
                "         - isLiabilityShiftPossible: ${nonce.threeDSecureInfo.isLiabilityShiftPossible}\n" +
                "         - wasVerified: ${nonce.threeDSecureInfo.wasVerified()}\n"}
        return "null"
    }

    fun getDisplayString(binData: BinData): String? {
        return """Bin Data: 
         - Prepaid: ${binData.healthcare}
         - Healthcare: ${binData.healthcare}
         - Debit: ${binData.debit}
         - Durbin Regulated: ${binData.durbinRegulated}
         - Commercial: ${binData.commercial}
         - Payroll: ${binData.payroll}
         - Issuing Bank: ${binData.issuingBank}
         - Country of Issuance: ${binData.countryOfIssuance}
         - Product Id: ${binData.productId}"""
    }

}