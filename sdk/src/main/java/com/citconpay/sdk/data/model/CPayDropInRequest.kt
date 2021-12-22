package com.citconpay.sdk.data.model

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.result.ActivityResultLauncher
import com.braintreepayments.api.DataCollector
import com.braintreepayments.api.models.GooglePaymentRequest
import com.braintreepayments.api.models.ThreeDSecureRequest
import com.citconpay.sdk.data.repository.CPayENVMode
import com.citconpay.sdk.ui.main.view.CUPaySDKActivity
import com.cupay.api.dropin.DropInRequest
import com.cupay.api.dropin.utils.PaymentMethodType
import com.cupay.cardform.view.CardForm

@Suppress("SameParameterValue")
open class CPayDropInRequest() : Parcelable {
    private var mENVmode = CPayENVMode.UAT

    private var mPaymentMethodType: CitconPaymentMethodType = CitconPaymentMethodType.NONE
    private var mAccessToken: String = ""
    private var mChargeToken: String = ""
    private var mReference: String = ""
    private var mConsumerID: String = ""
    private var mBrainTreeDropInRequest: DropInRequest =
        DropInRequest()
    private var mGooglePaymentRequest: GooglePaymentRequest? = null

    //CPaySDK CPayOrder
    private var  mAmount: String = ""
    private var  mCurrency: String = ""
    private var  mSubject: String = ""
    private var  mBody: String = ""
    private var  mIpnUrl: String = ""
    private var  mCallbackUrl: String = ""
    private var  mAllowDuplicate = true

    object ManagerBuilder {
        private lateinit var accessToken: String

        fun accessToken(token: String): ManagerBuilder {
            accessToken = token
            return this
        }

        fun build(mode: CPayENVMode): CPayDropInRequest {
            return CPayDropInRequest().accessToken(accessToken)
                .vaultManager(true)
                .paymentMethod(CitconPaymentMethodType.NONE)
                .setENVMode(mode)
        }

    }

    object CPayBuilder {
        var referenceId: String = ""
        private var amount: String = "0"
        private lateinit var currency: String
        private var subject: String = "cupay test subject"
        private var body: String = "cupay test body"
        private var ipnUrl: String = "https://cupay.test.ipn"
        private var callbackUrl: String = "https://cupay.test.ipn"
        private var allowDuplicate = true
        private lateinit var type: CitconPaymentMethodType

        fun reference(id: String):  CPayBuilder {
            referenceId = id
            return this
        }

        fun amount(amount: String):  CPayBuilder {
            this.amount = amount
            return this
        }

        fun currency(currency: String):  CPayBuilder {
            this.currency = currency
            return this
        }

        fun setAllowDuplicate(flag: Boolean): CPayBuilder {
            this.allowDuplicate = flag
            return this
        }

        fun paymentMethod(type: CitconPaymentMethodType): CPayBuilder {
            this.type = type
            return this
        }

        fun build(mode: CPayENVMode): CPayDropInRequest {
            return CPayDropInRequest().amount(amount)
                .reference(referenceId)
                .currency(this.currency)
                .paymentMethod(type)
                .subject(subject)
                .body(body)
                .ipnURL(ipnUrl)
                .callbackURL(callbackUrl)
                .setAllowDuplicate(allowDuplicate)
                .setENVMode(mode)
        }
    }

    object PaymentBuilder {
        private lateinit var accessToken: String
        private lateinit var chargeToken: String
        private lateinit var customerID: String
        private lateinit var reference: String
        private var paymentRequest: CitconPaymentRequest? = null
        private var citcon3DSecureRequest: Citcon3DSecureRequest? = null
        private var isRequest3DSecure = false
        private lateinit var type: CitconPaymentMethodType

        fun paymentMethod(type: CitconPaymentMethodType): PaymentBuilder {
            this.type = type
            return this
        }

        fun accessToken(token: String): PaymentBuilder {
            accessToken = token
            return this
        }

        fun chargeToken(token: String): PaymentBuilder {
            chargeToken = token
            return this
        }

        fun reference(reference: String): PaymentBuilder {
            PaymentBuilder.reference = reference
            return this
        }

        fun customerID(id: String): PaymentBuilder {
            customerID = id
            return this
        }

        fun citconPaymentRequest(citconPaymentRequest: CitconPaymentRequest): PaymentBuilder {
            paymentRequest = citconPaymentRequest
            return this
        }

        fun threeDSecureRequest(request: Citcon3DSecureRequest): PaymentBuilder {
            citcon3DSecureRequest = request
            return this
        }

        fun request3DSecureVerification(requestThreeDSecure: Boolean): PaymentBuilder {
            isRequest3DSecure = requestThreeDSecure
            return this
        }

        fun build(mode: CPayENVMode): CPayDropInRequest {
            val dropInRequest = CPayDropInRequest()
                .collectDeviceData(true)
                .vaultManager(false)
                .maskCardNumber(true)
                .maskSecurityCode(true)
                .request3DSecureVerification(isRequest3DSecure)
                .accessToken(accessToken)
                .chargeToken(chargeToken)
                .consumerID(customerID)
                .reference(reference)
                .paymentMethod(type)
                .setENVMode(mode)

            if(isRequest3DSecure && citcon3DSecureRequest != null)
                dropInRequest.threeDSecureRequest(citcon3DSecureRequest!!)

            if(paymentRequest != null) {
                dropInRequest.citconPaymentRequest(paymentRequest!!)
            }

            return dropInRequest.paymentMethod(type)
        }

    }

    constructor(parcel: Parcel) : this() {
        mENVmode = parcel.readSerializable() as CPayENVMode
        mPaymentMethodType = parcel.readSerializable() as CitconPaymentMethodType
        mAccessToken = parcel.readString()!!
        mChargeToken = parcel.readString()!!
        mReference = parcel.readString()!!
        mConsumerID = parcel.readString()!!
        mBrainTreeDropInRequest = parcel.readParcelable(DropInRequest::class.java.classLoader)!!
        mGooglePaymentRequest = parcel.readParcelable(GooglePaymentRequest::class.java.classLoader)

        mAmount = parcel.readString()!!
        mCurrency = parcel.readString()!!
        mSubject = parcel.readString()!!
        mBody = parcel.readString()!!
        mIpnUrl = parcel.readString()!!
        mCallbackUrl = parcel.readString()!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mAllowDuplicate = parcel.readBoolean()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(mENVmode)
        parcel.writeSerializable(mPaymentMethodType)
        parcel.writeString(mAccessToken)
        parcel.writeString(mChargeToken)
        parcel.writeString(mReference)
        parcel.writeString(mConsumerID)
        parcel.writeParcelable(mBrainTreeDropInRequest, 0)
        parcel.writeParcelable(mGooglePaymentRequest, 0)

        parcel.writeString(mAmount)
        parcel.writeString(mCurrency)
        parcel.writeString(mSubject)
        parcel.writeString(mBody)
        parcel.writeString(mIpnUrl)
        parcel.writeString(mCallbackUrl)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(mAllowDuplicate)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    fun start(context: Context, launcher: ActivityResultLauncher<Intent>) {
        launcher.launch(this.getIntent(context))
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

    /**
     * This method is optional. customer id is used to set PaymentRequest.
     *
     * @param citconPaymentRequest [CitconPaymentRequest] is specify citcon PaymentRequest.
     */
    private fun citconPaymentRequest(citconPaymentRequest: CitconPaymentRequest): CPayDropInRequest {
        mGooglePaymentRequest = citconPaymentRequest.googlePaymentRequest
        //Todo: setup Payment request depends on Gateway type
        mBrainTreeDropInRequest.googlePaymentRequest(mGooglePaymentRequest)
        return this
    }

    /**
     * This method is mandatory. base url and endpoint
     *
     * @param mode [CPayENVMode] is DEV/UAT/PRD.
     */
    private fun setENVMode(mode: CPayENVMode): CPayDropInRequest {
        mENVmode = mode
        return this
    }

    public fun getENVMode(): CPayENVMode {
        return mENVmode
    }

    /**
     * This method is mandatory. payment method type is used for bring up UI.
     *
     * @param type Type of the payment method.
     */
    private fun paymentMethod(type: CitconPaymentMethodType): CPayDropInRequest {
        this.mPaymentMethodType = type
        when(type) {
            CitconPaymentMethodType.PAYPAL -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.PAYPAL)
            CitconPaymentMethodType.UNKNOWN -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.UNKNOWN)
            CitconPaymentMethodType.PAY_WITH_VENMO -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.PAY_WITH_VENMO)
            CitconPaymentMethodType.AMEX -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.AMEX)
            CitconPaymentMethodType.GOOGLE_PAYMENT -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.GOOGLE_PAYMENT)


            CitconPaymentMethodType.DINERS -> TODO()
            CitconPaymentMethodType.DISCOVER -> TODO()
            CitconPaymentMethodType.JCB -> TODO()
            CitconPaymentMethodType.MAESTRO -> TODO()
            CitconPaymentMethodType.MASTERCARD -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.MASTERCARD)
            CitconPaymentMethodType.VISA -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.VISA)

            CitconPaymentMethodType.HIPER -> TODO()
            CitconPaymentMethodType.HIPERCARD -> TODO()
            CitconPaymentMethodType.NONE -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.NONE)
            else -> {}
        }
        return this
    }

    fun getPaymentMethod(): CitconPaymentMethodType {
        return mPaymentMethodType
    }

    /**
     * This method is mandatory. access token is used apply client token from Citcon server.
     *
     * @param token is applied from backend server.
     */
    private fun accessToken(token: String): CPayDropInRequest {
        mAccessToken = token
        return this
    }

    fun getAccessToken() : String {
        return mAccessToken
    }

    /**
     * This method is mandatory. charge token is used to finish payment.
     *
     * @param token is applied from backend server.
     */
    private fun chargeToken(token: String): CPayDropInRequest {
        mChargeToken = token
        return this
    }

    fun getChargeToken() : String {
        return mChargeToken
    }

    /**
     * This method is mandatory. reference is used to finish payment.
     *
     * @param ref is applied from backend server.
     */
    private fun reference(ref: String): CPayDropInRequest {
        mReference = ref
        return this
    }

    fun getReference() : String {
        return mReference
    }

    /**
     * This method is optional. consumer id is used to vault payment methods.
     *
     * @param id is current customer id.
     */
    private fun consumerID(id: String): CPayDropInRequest {
        mConsumerID = id
        return this
    }

    fun getConsumerID(): String {
        return mConsumerID
    }


    /**
     * This method is optional.
     *
     * @param collectDeviceData `true` if Drop-in should collect and return device data for
     * fraud prevention.
     * @see DataCollector
     */
    private fun collectDeviceData(collectDeviceData: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.collectDeviceData(collectDeviceData)
        return this
    }

    /**
     * @param vaultManager `true` to allow customers to manage their vaulted payment methods.
     * Defaults to `false`.
     */
    private fun vaultManager(vaultManager: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.vaultManager(vaultManager)
        return this
    }

    /**
     * @param maskCardNumber `true` to mask the card number when the field is not focused.
     * See [com.cupay.cardform.view.CardEditText] for more details. Defaults to
     * `false`.
     */
    private fun maskCardNumber(maskCardNumber: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.maskCardNumber(maskCardNumber)
        return this
    }

    /**
     * @param maskSecurityCode `true` to mask the security code during input. Defaults to `false`.
     */
    private fun maskSecurityCode(maskSecurityCode: Boolean): CPayDropInRequest {
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
     private fun request3DSecureVerification(requestThreeDSecure: Boolean): CPayDropInRequest {
        mBrainTreeDropInRequest.requestThreeDSecureVerification(requestThreeDSecure)
        return this
    }

    /**
     * This method is optional.
     *
     * @param request [ThreeDSecureRequest] to specify options and additional information for 3D Secure. To encourage 3DS 2.0 flows, set [ThreeDSecureRequest.billingAddress], [ThreeDSecureRequest.email], and [ThreeDSecureRequest.mobilePhoneNumber] for best results. If no amount is set, the [DropInRequest.amount] will be used.
     * @return the drop in request
     */
    private fun threeDSecureRequest(request: Citcon3DSecureRequest): CPayDropInRequest {
        //Todo: setup 3DS request depends on Gateway type
        mBrainTreeDropInRequest.threeDSecureRequest(request.threeDSecureRequest)
        return this
    }

    //CPaySDK
    private fun amount(amount: String): CPayDropInRequest {
        mAmount = amount
        return this
    }

    fun getAmount(): String {
        return mAmount
    }

    private fun currency(currency: String): CPayDropInRequest {
        mCurrency = currency
        return this
    }

    fun getCurrency(): String {
        return mCurrency
    }

    private fun subject(subject: String): CPayDropInRequest {
        mSubject = subject
        return this
    }

    fun getSubject(): String {
        return mSubject
    }

    private fun body(body: String): CPayDropInRequest {
        mBody = body
        return this
    }

    fun getBody(): String {
        return mBody
    }

    private fun ipnURL(ipn: String): CPayDropInRequest {
        mIpnUrl = ipn
        return this
    }

    fun getIpn(): String {
        return mIpnUrl
    }

    private fun callbackURL(callback: String): CPayDropInRequest {
        mCallbackUrl = callback
        return this
    }

    fun getCallback(): String {
        return mCallbackUrl
    }

    private fun setAllowDuplicate(flag: Boolean): CPayDropInRequest {
        mAllowDuplicate = flag
        return this
    }

    fun isAllowDuplicate(): Boolean {
        return mAllowDuplicate
    }

    companion object CREATOR : Parcelable.Creator<CPayDropInRequest> {
        const val EXTRA_CHECKOUT_REQUEST: String = "com.citconpay.sdk.data.api.EXTRA_CHECKOUT_REQUEST"

        override fun createFromParcel(parcel: Parcel): CPayDropInRequest {
            return CPayDropInRequest(parcel)
        }

        override fun newArray(size: Int): Array<CPayDropInRequest?> {
            return arrayOfNulls(size)
        }
    }
}