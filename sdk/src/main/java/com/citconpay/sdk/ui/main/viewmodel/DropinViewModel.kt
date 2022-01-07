package com.citconpay.sdk.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.citconpay.dropin.DropInRequest
import com.citconpay.dropin.DropInResult
import com.braintreepayments.api.models.BinData
import com.braintreepayments.api.models.CardNonce
import com.braintreepayments.api.models.PayPalAccountNonce
import com.braintreepayments.api.models.PaymentMethodNonce
import com.citconpay.sdk.data.api.response.CitconApiResponse
import com.citconpay.sdk.data.api.response.PlacedOrder
import com.citconpay.sdk.data.model.ErrorMessage
import com.citconpay.sdk.data.api.response.LoadedConfig
import com.citconpay.sdk.data.model.CPayDropInRequest
import com.citconpay.sdk.data.repository.ApiRepository
import com.citconpay.sdk.data.repository.CPayENV
import com.citconpay.sdk.utils.Resource
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import retrofit2.HttpException


class DropinViewModel(dropInRequest: CPayDropInRequest, application: Application) :
    AndroidViewModel(application) {
    val mTextViewMsg = MutableLiveData<String>()
    private val mDropInRequest: CPayDropInRequest by lazy { dropInRequest }
    val mLoading = MutableLiveData<Boolean>()
    val mResult = MutableLiveData<DropInResult>()
    private val apiRepository: ApiRepository by lazy {
        ApiRepository(CPayENV.getBaseURL(dropInRequest.getENVMode()))
    }

    fun getDropInRequest(): CPayDropInRequest {
        return mDropInRequest
    }

    fun setDropInResult(result: DropInResult) {
        mResult.postValue(result)
    }

    private fun getClientToken() = liveData(Dispatchers.IO) {
        mLoading.postValue(true)
        emit(Resource.loading(data = null))
        try {
            //emit(Resource.success(data = apiRepository.getClientToken()))
            emit(
                Resource.success(
                    data = apiRepository.loadConfig(
                        mDropInRequest.getAccessToken(),
                        mDropInRequest.getConsumerID()
                    )
                )
            )
            mLoading.postValue(false)
        } catch (exception: Exception) {
            mLoading.postValue(false)
            emit(Resource.error(data = null, message = handleErrorMsg(exception)))
        }
    }

    private fun sendNonceToServer(nonce: PaymentMethodNonce) = liveData(Dispatchers.IO) {
        mLoading.postValue(true)
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiRepository.confirmCharge(
                        mDropInRequest.getAccessToken(),
                        mDropInRequest.getChargeToken(),
                        mDropInRequest.getReference(),
                        mDropInRequest.getPaymentMethod().type,
                        nonce.nonce
                    )
                )
            )
            mLoading.postValue(false)
        } catch (exception: Exception) {
            mLoading.postValue(false)
            emit(Resource.error(data = null, message = handleErrorMsg(exception)))
        }
    }

    /*fun loadClientToken() : LiveData<Resource<CPayApiResponse<BrainTreeClientToken>>> {
        return getClientToken()
    }*/

    internal fun loadClientToken(): LiveData<Resource<CitconApiResponse<LoadedConfig>>> {
        return getClientToken()
    }

    internal fun placeOrderByNonce(nonce: PaymentMethodNonce): LiveData<Resource<CitconApiResponse<PlacedOrder>>> {
        return sendNonceToServer(nonce)
    }

    fun getBTDropInRequest(): DropInRequest {
        return mDropInRequest.getBrainTreeDropInRequest()
    }

    private fun handleErrorMsg(exception: Exception): ErrorMessage {
        lateinit var errorMessage: ErrorMessage
        (exception as HttpException).response()?.let { response ->
            response.errorBody()?.let { errorMsg ->
                JSONObject(errorMsg.string()).let {
                    errorMessage = GsonBuilder().create().fromJson(
                        it.getJSONObject("data").toString(),
                        ErrorMessage::class.java
                    )
                }
            }
        }
        return errorMessage
    }

    private lateinit var mNonce: PaymentMethodNonce

    fun displayNonce(result: DropInResult): String {
        mNonce = result.paymentMethodNonce!!
//        mNonceIcon!!.setImageResource(PaymentMethodType.forType(mNonce).drawable)
//        mNonceIcon.visibility = View.VISIBLE
//        mNonceString!!.text = "Nonce: ${mNonce!!.nonce}"
//        mNonceString.visibility = View.VISIBLE
        var details: String
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
        nonce?.let {
            return "Card Last Two: ${nonce.lastTwo}\n" +
                    "${getDisplayString(nonce.binData)}\n" +
                    "3DS:\n" +
                    "         - isLiabilityShifted: ${nonce.threeDSecureInfo.isLiabilityShifted}\n" +
                    "         - isLiabilityShiftPossible: ${nonce.threeDSecureInfo.isLiabilityShiftPossible}\n" +
                    "         - wasVerified: ${nonce.threeDSecureInfo.wasVerified()}\n"
        }
        return "null"
    }

    private fun getDisplayString(binData: BinData): String {
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