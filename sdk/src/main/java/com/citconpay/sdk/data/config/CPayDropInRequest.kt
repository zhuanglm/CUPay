package com.citconpay.sdk.data.config

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import com.braintreepayments.api.DataCollector
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.utils.PaymentMethodType
import com.braintreepayments.api.models.GooglePaymentRequest
import com.braintreepayments.api.models.ThreeDSecureRequest
import com.citconpay.sdk.ui.main.view.CUPaySDKActivity
import com.citconpay.sdk.data.model.CitconPaymentMethodType
import com.cupay.cardform.view.CardForm

open class CPayDropInRequest() : Parcelable {
    private lateinit var mPaymentMethodType: CitconPaymentMethodType
    private lateinit var mAccessToken: String
    private lateinit var mChargeToken: String
    private var mConsumerID: String? = null

    //val EXTRA_CHECKOUT_REQUEST = "com.citconpay.sdk.data.api.EXTRA_CHECKOUT_REQUEST"
    private var mBrainTreeDropInRequest: DropInRequest =  DropInRequest()
    private var mGooglePaymentRequest: GooglePaymentRequest? = null

    private var mThemeColor: Int = Color.BLUE

    constructor(parcel: Parcel) : this() {
        mPaymentMethodType = parcel.readSerializable() as CitconPaymentMethodType
        mAccessToken = parcel.readString()!!
        mChargeToken = parcel.readString()!!
        mConsumerID = parcel.readString()
        mBrainTreeDropInRequest = parcel.readParcelable(DropInRequest::class.java.classLoader)!!
        mGooglePaymentRequest = parcel.readParcelable(GooglePaymentRequest::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(mPaymentMethodType)
        parcel.writeString(mAccessToken)
        parcel.writeString(mChargeToken)
        parcel.writeString(mConsumerID)
        parcel.writeParcelable(mBrainTreeDropInRequest, 0)
        parcel.writeParcelable(mGooglePaymentRequest, 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * Get an [Intent] that can be used in [androidx.appcompat.app.AppCompatActivity.startActivityForResult]
     * to launch [CUPaySDKActivity] and the Drop-in UI.
     *
     * @param context
     * @return [Intent] containing all of the options set in [CPayDropInRequest].
     */
    fun getIntent(context: Context?): Intent {
        return Intent(context, CUPaySDKActivity::class.java)
                .putExtra(EXTRA_CHECKOUT_REQUEST, this)
    }

    fun getBrainTreeDropInRequest(): DropInRequest {
        mBrainTreeDropInRequest.cardholderNameStatus(CardForm.FIELD_OPTIONAL)
        return mBrainTreeDropInRequest
    }

    fun citconPaymentRequest(citconPaymentRequest: CitconPaymentRequest): CPayDropInRequest {
        mGooglePaymentRequest = citconPaymentRequest.googlePaymentRequest
        //Todo: setup Payment request depends on Gateway type
        mBrainTreeDropInRequest.googlePaymentRequest(mGooglePaymentRequest)
        return this
    }

    /**
     * This method is mandatory. payment method type is used for bring up UI.
     *
     * @param type Type of the payment method.
     */
    fun paymentMethod(type: CitconPaymentMethodType): CPayDropInRequest {
        this.mPaymentMethodType = type
        when(type) {
            CitconPaymentMethodType.PAYPAL -> mBrainTreeDropInRequest.paymentMethodType(PaymentMethodType.PAYPAL)
            CitconPaymentMethodType.UNKNOWN -> mBrainTreeDropInRequest.paymentMethodType(PaymentMethodType.UNKNOWN)
            CitconPaymentMethodType.PAY_WITH_VENMO -> mBrainTreeDropInRequest.paymentMethodType(PaymentMethodType.PAY_WITH_VENMO)
            CitconPaymentMethodType.AMEX -> TODO()
            CitconPaymentMethodType.GOOGLE_PAYMENT -> mBrainTreeDropInRequest.paymentMethodType(PaymentMethodType.GOOGLE_PAYMENT)
            CitconPaymentMethodType.DINERS -> TODO()
            CitconPaymentMethodType.DISCOVER -> TODO()
            CitconPaymentMethodType.JCB -> TODO()
            CitconPaymentMethodType.MAESTRO -> TODO()
            CitconPaymentMethodType.MASTERCARD -> TODO()
            CitconPaymentMethodType.VISA -> TODO()
            CitconPaymentMethodType.UNIONPAY -> TODO()
            CitconPaymentMethodType.HIPER -> TODO()
            CitconPaymentMethodType.HIPERCARD -> TODO()
        }
        return this
    }

    /**
     * This method is mandatory. access token is used apply client token from Citcon server.
     *
     * @param token is applied from backend server.
     */
    fun accessToken(token: String): CPayDropInRequest {
        mAccessToken = token
        return this
    }

    /**
     * This method is mandatory. charge token is used to finish payment.
     *
     * @param token is applied from backend server.
     */
    fun chargeToken(token: String): CPayDropInRequest {
        mChargeToken = token
        return this
    }

    /**
     * This method is optional. customer id is used to vault payment methods.
     *
     * @param id is current customer id.
     */
    fun customerID(id: String): CPayDropInRequest {
        mConsumerID = id
        return this
    }

    /**
     * This method is optional.
     *
     * @param collectDeviceData `true` if Drop-in should collect and return device data for
     * fraud prevention.
     * @see DataCollector
     */
    fun collectDeviceData(collectDeviceData: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.collectDeviceData(collectDeviceData)
        return this
    }

    /**
     * @param vaultManager `true` to allow customers to manage their vaulted payment methods.
     * Defaults to `false`.
     */
    fun vaultManager(vaultManager: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.vaultManager(vaultManager)
        return this
    }

    /**
     * @param maskCardNumber `true` to mask the card number when the field is not focused.
     * See [com.cupay.cardform.view.CardEditText] for more details. Defaults to
     * `false`.
     */
    fun maskCardNumber(maskCardNumber: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.maskCardNumber(maskCardNumber)
        return this
    }

    /**
     * @param maskSecurityCode `true` to mask the security code during input. Defaults to `false`.
     */
    fun maskSecurityCode(maskSecurityCode: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.maskSecurityCode(maskSecurityCode)
        return this
    }

    /**
     * If 3D Secure has been enabled in the control panel and an amount is specified in
     * [DropInRequest.amount] or a [ThreeDSecureRequest] is provided,
     * Drop-In will request a 3D Secure verification for any new cards added by the user.
     *
     * @param requestThreeDSecure `true` to request a 3D Secure verification as part of Drop-In, `false` to not request a 3D Secure verification. Defaults to `false`.
     * @return the drop in request
     */
     fun request3DSecureVerification(requestThreeDSecure: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.requestThreeDSecureVerification(requestThreeDSecure)
        return this
    }

    /**
     * This method is optional.
     *
     * @param threeDSecureRequest [ThreeDSecureRequest] to specify options and additional information for 3D Secure. To encourage 3DS 2.0 flows, set [ThreeDSecureRequest.billingAddress], [ThreeDSecureRequest.email], and [ThreeDSecureRequest.mobilePhoneNumber] for best results. If no amount is set, the [DropInRequest.amount] will be used.
     * @return the drop in request
     */
    fun threeDSecureRequest(request: Citcon3DSecureRequest): CPayDropInRequest {
        //Todo: setup 3DS request depends on Gateway type
        mBrainTreeDropInRequest.threeDSecureRequest(request.threeDSecureRequest)
        return this
    }

    companion object CREATOR : Parcelable.Creator<CPayDropInRequest> {
        val EXTRA_CHECKOUT_REQUEST: String? = "com.citconpay.sdk.data.api.EXTRA_CHECKOUT_REQUEST"

        override fun createFromParcel(parcel: Parcel): CPayDropInRequest {
            return CPayDropInRequest(parcel)
        }

        override fun newArray(size: Int): Array<CPayDropInRequest?> {
            return arrayOfNulls(size)
        }
    }
}