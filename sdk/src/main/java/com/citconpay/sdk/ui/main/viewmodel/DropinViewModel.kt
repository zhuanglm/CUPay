package com.citconpay.sdk.ui.main.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.braintreepayments.api.models.BinData
import com.braintreepayments.api.models.CardNonce
import com.braintreepayments.api.models.PayPalAccountNonce
import com.braintreepayments.api.models.PaymentMethodNonce
import com.citconpay.dropin.DropInRequest
import com.citconpay.dropin.DropInResult
import com.citconpay.sdk.data.api.response.CitconApiResponse
import com.citconpay.sdk.data.api.response.ConfirmChargePayment
import com.citconpay.sdk.data.api.response.LoadedConfig
import com.citconpay.sdk.data.api.response.PlacedOrder
import com.citconpay.sdk.data.model.CPayRequest
import com.citconpay.sdk.data.model.CPayResult
import com.citconpay.sdk.data.model.ErrorMessage
import com.citconpay.sdk.data.repository.ApiRepository
import com.citconpay.sdk.data.repository.CPayENV
import com.citconpay.sdk.data.repository.CPayENVMode
import com.citconpay.sdk.ui.main.view.CUPaySDKActivity
import com.citconpay.sdk.utils.Resource
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import retrofit2.HttpException
import sdk.CPayMode
import sdk.CPaySDK
import sdk.interfaces.OrderResponse
import sdk.models.CPayInquireResult
import sdk.models.CPayOrder
import upisdk.CPayUPISDK
import upisdk.models.CPayUPIInquireResult
import upisdk.models.CPayUPIOrder
import upisdk.models.CardInfo
import java.text.SimpleDateFormat
import java.util.*


class DropinViewModel(request: CPayRequest, application: Application) :
    AndroidViewModel(application) {
    val mTextViewMsg = MutableLiveData<String>()
    private val mRequest: CPayRequest by lazy { request }
    val mLoading = MutableLiveData<Boolean>()
    val mResult = MutableLiveData<DropInResult>()
    private val apiRepository = ApiRepository(CPayENV.getBaseURL(request.getENVMode()))
    private var mRunningFlag = false
    var mInitFlag = false

    fun getDropInRequest(): CPayRequest {
        return mRequest
    }

    fun setDropInResult(result: DropInResult) {
        mResult.postValue(result)
    }

    private fun <T> inquireResponse2CPayResult(inquireResponse: T): CPayResult? {
        if (inquireResponse is CPayUPIInquireResult) {
            return inquireResponse.run {
                if (mStatus == "authorized" || mStatus == "0" || mStatus == "success" || mStatus == "succeeded") {
                    CPayResult(
                        Activity.RESULT_OK,
                        mRequest.getPaymentMethod(),

                        PlacedOrder(
                            "", mId, mReference,
                            mAmount,
                            amount_captured = mCaptureAmount > 0,
                            amount_refunded = mRefundAmount > 0,
                            currency = mCurrency,
                            time_created = mTime,
                            time_captured = mCaptureTime,
                            status = mStatus ?: "",
                            country = mCountry,
                            payment = ConfirmChargePayment(
                                mRequest.getPaymentMethod().type, ""
                            )
                        )
                    )
                } else {
                    CPayResult(
                        Activity.RESULT_CANCELED,
                        mRequest.getPaymentMethod(),
                        ErrorMessage(
                            mStatus,
                            "error",
                            mNote
                        )
                    )
                }
            }
        } else if (inquireResponse is CPayInquireResult) {
            return inquireResponse.run {
                if (mStatus == "authorized" || mStatus == "0" || mStatus == "success") {
                    val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    val time = sdf.parse(mTime)?.time
                    CPayResult(
                        Activity.RESULT_OK,
                        mRequest.getPaymentMethod(),

                        PlacedOrder(
                            "", mId, mReference,
                            if (mAmount == "") 0 else mAmount.toInt(),
                            amount_captured = true,
                            amount_refunded = false,
                            currency = mCurrency,
                            time_created = time ?: 0,
                            time_captured = 0,
                            status = mStatus ?: "",
                            country = "",
                            payment = ConfirmChargePayment(mRequest.getPaymentMethod().type, "")
                        )

                    )
                } else {
                    CPayResult(
                        Activity.RESULT_CANCELED,
                        mRequest.getPaymentMethod(),
                        ErrorMessage(
                            mStatus,
                            "error",
                            mNote
                        )
                    )
                }
            }
        }

        return null
    }

    fun onlineInquire(activity: CUPaySDKActivity) {
        if (CPaySDK.getInstance() == null) {
            CPaySDK.initInstance(activity, null)
        } else {
            CPaySDK.setToken(mRequest.getToken())
        }

        when (mRequest.getENVMode()) {
            CPayENVMode.DEV -> CPaySDK.setMode(CPayMode.DEV)
            CPayENVMode.UAT -> CPaySDK.setMode(CPayMode.UAT)
            CPayENVMode.PROD -> CPaySDK.setMode(CPayMode.PROD)
            CPayENVMode.QA -> CPaySDK.setMode(CPayMode.QA)
        }

        CPaySDK.getInstance().inquireOrderByRef(
            mRequest.getReference(), mRequest.getCurrency(),
            mRequest.getPaymentMethod().type, false
        ) { inquireResponse ->
            inquireResponse?.run {
                if (mStatus != "success") {
                    activity.finish(
                        CPayResult(
                            Activity.RESULT_CANCELED,
                            mRequest.getPaymentMethod(),
                            ErrorMessage(
                                mStatus,
                                "error",
                                mNote
                            )
                        )
                    )
                } else {
                    activity.finish(
                        inquireResponse2CPayResult(inquireResponse)
                    )
                }

            } ?: activity.finish(
                CPayResult(
                    Activity.RESULT_CANCELED,
                    mRequest.getPaymentMethod(),
                    ErrorMessage(
                        "-1",
                        "error",
                        "error"
                    )
                )
            )

        }


    }

    fun upiInquire(activity: CUPaySDKActivity) {
        if (CPayUPISDK.getInstance() == null) {
            CPayUPISDK.initInstance(activity, mRequest.getAccessToken())
        } else {
            CPayUPISDK.setToken(mRequest.getAccessToken())
        }

        when (mRequest.getENVMode()) {
            CPayENVMode.DEV -> CPayUPISDK.setMode("DEV")
            CPayENVMode.UAT -> CPayUPISDK.setMode("UAT")
            CPayENVMode.QA -> CPayUPISDK.setMode("QA")
            CPayENVMode.PROD -> CPayUPISDK.setMode("PROD")
        }

        CPayUPISDK.getInstance().inquireOrderByRef(
            mRequest.getReference(),
            mRequest.getPaymentMethod().type
        ) { inquireResponse ->
            inquireResponse?.run {
                if (mStatus != "success") {
                    activity.finish(
                        CPayResult(
                            Activity.RESULT_CANCELED,
                            mRequest.getPaymentMethod(),
                            ErrorMessage(
                                mStatus,
                                "error",
                                mNote
                            )
                        )
                    )
                } else {
                    activity.finish(
                        inquireResponse2CPayResult(inquireResponse)
                    )
                }

            } ?: activity.finish(
                CPayResult(
                    Activity.RESULT_CANCELED,
                    mRequest.getPaymentMethod(),
                    ErrorMessage(
                        "-1",
                        "error",
                        "error"
                    )
                )
            )

        }
    }

    fun requestOnlineOrder(activity: CUPaySDKActivity, launchType: sdk.CPayLaunchType) {
        val consumer = mRequest.getConsumer()?.run {
            sdk.models.Consumer(firstName, lastName, phone, email, reference)
        }
        val order = CPayOrder.Builder()
            .setLaunchType(launchType)
            .setReferenceId(mRequest.getReference())
            .setSubject(mRequest.getSubject())
            .setBody(mRequest.getBody())
            .setAmount(mRequest.getAmount())
            .setCurrency(mRequest.getCurrency())
            .setCountry(mRequest.getCountry())
            .setVendor(mRequest.getPaymentMethod().type)
            .setIpnUrl(mRequest.getIpn())
            .setCallbackUrl(mRequest.getCallback())
            .setCallbackFailUrl(mRequest.getFailCallback())
            .setCallbackCancelUrl(mRequest.getCancelURL())
            .setConsumer(consumer)
            .setNote(mRequest.getNote())
            .setSource(mRequest.getSource())
            .setGoods(mRequest.getGoods())
            .setAllowDuplicate(mRequest.isAllowDuplicate())
            .enableCNPayAcceleration(false)
            .setInstallment(mRequest.getInstallmentPeriod())
            .cardIssuer(null)
            .receiptType(null)
            .build()

        CPaySDK.initInstance(activity, null)
        when (mRequest.getENVMode()) {
            CPayENVMode.DEV -> CPaySDK.setMode(CPayMode.DEV)
            CPayENVMode.UAT -> CPaySDK.setMode(CPayMode.UAT)
            CPayENVMode.PROD -> CPaySDK.setMode(CPayMode.PROD)
            CPayENVMode.QA -> CPaySDK.setMode(CPayMode.QA)
        }

        CPaySDK.mInquireResult.observe(activity) { inquireResponse ->
            inquireResponse?.run {
                if (mRunningFlag) {
                    activity.finish(
                        inquireResponse2CPayResult(inquireResponse)
                    )
                }
            }
        }

        CPaySDK.setToken(mRequest.getToken())
        CPaySDK.getInstance().requestOrder(activity, order, OrderResponse { orderResult ->

            if (orderResult == null || orderResult.mStatus != "0") {
                if(orderResult.mStatus == "initiated") {
                    //allow inquire to detect the transaction status
                    mRunningFlag = true
                } else {
                    activity.finish(
                        CPayResult(
                            Activity.RESULT_CANCELED,
                            mRequest.getPaymentMethod(),
                            orderResult?.run {
                                ErrorMessage(
                                    mStatus,
                                    mMessage,
                                    mOrderId
                                )
                            } ?: ErrorMessage(
                                "-1",
                                "error",
                                "error"
                            )
                        )
                    )

                    mRunningFlag = false
                }
                return@OrderResponse
            } else {
                mRunningFlag = true
            }
        })

    }

    fun requestUPIOrder(activity: CUPaySDKActivity, launchType: upisdk.CPayLaunchType) {
        val consumer = mRequest.getConsumer()
        val billingAddress = consumer?.billingAddress

        val order = CPayUPIOrder.Builder()
            .setLaunchType(launchType)
            .setReferenceId(mRequest.getReference())
            .setAmount(mRequest.getAmount())
            .setCurrency(mRequest.getCurrency())
            .setVendor(mRequest.getPaymentMethod().type)
            .setIpnUrl(mRequest.getIpn())
            .setCallbackUrl(mRequest.getCallback())
            .setMobileCallback(mRequest.getMobileCallback())
            .setCallbackFailUrl(mRequest.getFailCallback())
            .setCallbackCancelUrl(mRequest.getCancelURL())
            .setAllowDuplicate(mRequest.isAllowDuplicate())
            .setCountry(mRequest.getCountry())
            .setExpiry(mRequest.getExpiry())
            .enableCNPayAcceleration(false)
            .setInstallment(mRequest.getInstallmentPeriod())
            .setAutoCapture(true)
            .totalDiscountCode("code")
            .cardIssuer(mRequest.getCardInfo()?.issuer)
            .cardInfo(mRequest.getCardInfo()?.run { CardInfo(pan, firstName, lastName, cvv, expiry) })
            .receiptType(mRequest.getReceiptType())
            .billingAddress(billingAddress?.street, billingAddress?.street2, billingAddress?.city,
                billingAddress?.zip, billingAddress?.state, billingAddress?.country)
            .consumer(consumer?.reference, consumer?.firstName, consumer?.lastName, consumer?.phone, consumer?.email)
            .deviceIP(mRequest.getDeviceInfo()?.ipAddress)
            //.format(mRequest.getPaymentFormat())
            .build()

        CPayUPISDK.initInstance(activity, mRequest.getAccessToken())
        when (mRequest.getENVMode()) {
            CPayENVMode.DEV -> CPayUPISDK.setMode("DEV")
            CPayENVMode.UAT -> CPayUPISDK.setMode("UAT")
            CPayENVMode.QA -> CPayUPISDK.setMode("QA")
            CPayENVMode.PROD -> CPayUPISDK.setMode("PROD")
        }

        //CPayUPISDK.setToken(request.getAccessToken())
        CPayUPISDK.mInquireResult.observe(activity) { inquireResponse ->
            inquireResponse?.run {
                if (mRunningFlag) {
                    activity.finish(
                        inquireResponse2CPayResult(inquireResponse)
                    )
                }
            }
        }

        CPayUPISDK.getInstance().requestOrder(activity, order,
            upisdk.interfaces.OrderResponse { orderResult ->
                if (orderResult == null || orderResult.mStatus != "0") {
                    if(orderResult.mStatus == "initiated") {
                        //allow inquire to detect the transaction status
                        mRunningFlag = true
                    } else {
                        activity.finish(
                            CPayResult(
                                Activity.RESULT_CANCELED,
                                mRequest.getPaymentMethod(),
                                orderResult?.run {
                                    ErrorMessage(
                                        mStatus,
                                        mMessage,
                                        mOrder.mReferenceId
                                    )
                                } ?: ErrorMessage(
                                    "-1",
                                    "error",
                                    "error"
                                )
                            )
                        )

                        mRunningFlag = false
                    }
                    return@OrderResponse
                } else {
                    mRunningFlag = true
                }
            })
    }

    private fun getClientToken() = liveData(Dispatchers.IO) {
        mLoading.postValue(true)
        emit(Resource.loading(data = null))
        try {
            //emit(Resource.success(data = apiRepository.getClientToken()))
            emit(
                Resource.success(
                    data = apiRepository.loadConfig(
                        mRequest.getAccessToken(),
                        mRequest.getConsumerID(),
                        mRequest.getPaymentMethod().type
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
                        mRequest.getAccessToken(),
                        mRequest.getChargeToken(),
                        mRequest.getReference(),
                        mRequest.getPaymentMethod().type,
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

    internal fun loadClientToken(): LiveData<Resource<CitconApiResponse<LoadedConfig>>> {
        return getClientToken()
    }

    internal fun placeOrderByNonce(nonce: PaymentMethodNonce): LiveData<Resource<CitconApiResponse<PlacedOrder>>> {
        return sendNonceToServer(nonce)
    }

    fun getBTDropInRequest(): DropInRequest {
        return mRequest.getBrainTreeDropInRequest()
    }

    private fun handleErrorMsg(exception: Exception): ErrorMessage? {
        var errorMessage: ErrorMessage? = null

        if (exception is HttpException) {
            exception.response()?.let { response ->
                response.errorBody()?.let { errorMsg ->
                    JSONObject(errorMsg.string()).let {
                        errorMessage = GsonBuilder().create().fromJson(
                            it.getJSONObject("data").toString(),
                            ErrorMessage::class.java
                        )
                    }
                }
            }
        } else {
            errorMessage = ErrorMessage("-1", exception.localizedMessage, exception.toString())
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