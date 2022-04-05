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
import com.citconpay.cardform.view.CardForm
import com.citconpay.dropin.DropInRequest
import com.citconpay.dropin.utils.PaymentMethodType
import com.citconpay.sdk.data.repository.CPayENVMode
import com.citconpay.sdk.ui.main.view.CUPaySDKActivity
import java.util.*

@Suppress("SameParameterValue")
open class CPayRequest() : Parcelable {
    private var mENVmode = CPayENVMode.UAT

    private var mPaymentMethod: CPayMethodType = CPayMethodType.NONE
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

    private var mMobileCallback: String? = null
    private var mCallbackFailUrl: String? = null
    private var mCancelUrl: String? = null
    private var mCountry: Locale = Locale.CANADA

    private var mTimeout:Long = 60000

    private var  mApiType = CPayAPIType.UPI_ORDER

    object ManagerBuilder {
        private lateinit var accessToken: String

        fun accessToken(token: String): ManagerBuilder {
            accessToken = token
            return this
        }

        fun build(mode: CPayENVMode): CPayRequest {
            return CPayRequest().accessToken(accessToken)
                .vaultManager(true)
                .paymentMethod(CPayMethodType.NONE)
                .setENVMode(mode)
        }

    }

    object CPayOrderBuilder {
        var referenceId: String = ""
        private var amount: String = "0"
        private lateinit var currency: String
        private var subject: String = "cupay test subject"
        private var body: String = "cupay test body"
        private var ipnUrl: String = "https://cupay.test.ipn"
        private var callbackUrl: String = "https://cupay.test.ipn"
        private var allowDuplicate = true
        private lateinit var type: CPayMethodType

        fun reference(id: String):  CPayOrderBuilder {
            referenceId = id
            return this
        }

        fun amount(amount: String):  CPayOrderBuilder {
            this.amount = amount
            return this
        }

        fun currency(currency: String):  CPayOrderBuilder {
            this.currency = currency
            return this
        }

        fun setAllowDuplicate(flag: Boolean): CPayOrderBuilder {
            this.allowDuplicate = flag
            return this
        }

        fun paymentMethod(type: CPayMethodType): CPayOrderBuilder {
            this.type = type
            return this
        }

        fun build(mode: CPayENVMode): CPayRequest {
            return CPayRequest().amount(amount)
                .reference(referenceId)
                .currency(this.currency)
                .paymentMethod(type)
                .subject(subject)
                .body(body)
                .ipnURL(ipnUrl)
                .callbackURL(callbackUrl)
                .setAllowDuplicate(allowDuplicate)
                .setENVMode(mode)
                .setApiType(CPayAPIType.ONLINE_ORDER)
        }
    }

    object UPIInquireBuilder {
        private lateinit var accessToken: String
        private lateinit var reference: String
        private lateinit var type: CPayMethodType

        fun accessToken(token: String): UPIInquireBuilder {
            this.accessToken = token
            return this
        }

        fun paymentMethod(type: CPayMethodType): UPIInquireBuilder {
            this.type = type
            return this
        }

        fun reference(reference: String): UPIInquireBuilder {
            this.reference = reference
            return this
        }

        fun build(mode: CPayENVMode): CPayRequest {
            return CPayRequest().reference(reference)
                .accessToken(accessToken)
                .paymentMethod(type)
                .setENVMode(mode)
                .setApiType(CPayAPIType.UPI_INQUIRE)
        }
    }

    object UPIOrderBuilder {
        private lateinit var accessToken: String
        private lateinit var customerID: String
        private lateinit var reference: String
        private var amount: String = "0"
        private lateinit var currency: String
        private var subject: String = "cupay test subject"
        private var body: String = "cupay test body"
        private var ipnUrl: String = "https://cupay.test.ipn"
        private var callbackUrl: String = "https://cupay.test.ipn"
        private var allowDuplicate = true
        private lateinit var type: CPayMethodType
        private var country: Locale = Locale.CANADA
        private var mobileCallback: String? = null
        private var callbackFailUrl: String? = null
        private var cancelUrl: String? = null
        private var timeout: Long = 60000

        fun setTimeout(t: Long): UPIOrderBuilder {
            this.timeout = t
            return this
        }

        fun accessToken(token: String): UPIOrderBuilder {
            this.accessToken = token
            return this
        }

        fun reference(reference: String): UPIOrderBuilder {
            this.reference = reference
            return this
        }

        fun customerID(id: String): UPIOrderBuilder {
            this.customerID = id
            return this
        }

        fun amount(amount: String):  UPIOrderBuilder {
            this.amount = amount
            return this
        }

        fun currency(currency: String):  UPIOrderBuilder {
            this.currency = currency
            return this
        }

        fun setAllowDuplicate(flag: Boolean): UPIOrderBuilder {
            this.allowDuplicate = flag
            return this
        }

        fun paymentMethod(type: CPayMethodType): UPIOrderBuilder {
            this.type = type
            return this
        }

        fun country(country: Locale): UPIOrderBuilder {
            this.country = country
            return this
        }

        fun callbackURL(url: String): UPIOrderBuilder {
            this.callbackUrl = url
            return this
        }

        fun ipnURL(url: String): UPIOrderBuilder {
            this.ipnUrl = url
            return this
        }

        fun mobileURL(url: String): UPIOrderBuilder {
            this.mobileCallback = url
            return this
        }

        fun failURL(url: String): UPIOrderBuilder {
            this.callbackFailUrl = url
            return this
        }

        fun cancelURL(url: String): UPIOrderBuilder {
            this.cancelUrl = url
            return this
        }

        fun build(mode: CPayENVMode): CPayRequest {
            return CPayRequest().amount(amount)
                .reference(reference)
                .accessToken(accessToken)
                .consumerID(customerID)
                .currency(this.currency)
                .paymentMethod(type)
                .subject(subject)
                .body(body)
                .ipnURL(ipnUrl)
                .callbackURL(callbackUrl)
                .setAllowDuplicate(allowDuplicate)
                .mobileCallbackURL(mobileCallback)
                .failCallbackURL(callbackFailUrl)
                .cancelURL(cancelUrl)
                .setCountry(country)
                .setENVMode(mode)
                .setTimeout(timeout)
        }
    }

    object BillingAdressBuilder {
        private var street: String? = null
        private var street2: String? = null
        private var city: String? = null
        private var state: String? = null
        private var zip: String? = null
        private var country: String? = null

        fun street(street: String): BillingAdressBuilder {
            this.street = street
            return this
        }

        fun street2(street: String): BillingAdressBuilder {
            this.street2 = street
            return this
        }

        fun city(city: String): BillingAdressBuilder {
            this.city = city
            return this
        }

        fun state(state: String): BillingAdressBuilder {
            this.state = state
            return this
        }

        fun postCode(zip: String): BillingAdressBuilder {
            this.zip = zip
            return this
        }

        fun country(country: String): BillingAdressBuilder {
            this.country = country
            return this
        }

        fun build(): CPayBillingAddr {
            return CPayBillingAddr(street, street2, city, state, zip, country)
        }
    }

    object ConsumerBuilder {
        private var reference: String? = null
        private var firstName: String? = null
        private var lastName: String? = null
        private var phone: String? = null
        private var email: String? = null
        private var billingAddress: CPayBillingAddr? = null

        fun firstName(name: String): ConsumerBuilder {
            this.firstName = name
            return this
        }

        fun lastName(name: String): ConsumerBuilder {
            this.lastName = name
            return this
        }

        fun phone(number: String): ConsumerBuilder {
            this.phone = number
            return this
        }

        fun email(email: String): ConsumerBuilder {
            this.email = email
            return this
        }

        fun billingAddress(billingAddr: CPayBillingAddr): ConsumerBuilder {
            this.billingAddress = billingAddr
            return this
        }

        fun build(): CPayConsumer {
            return CPayConsumer(reference, firstName, lastName, phone, email, billingAddress)
        }
    }

    object PaymentBuilder {
        private lateinit var accessToken: String
        private lateinit var chargeToken: String
        private lateinit var customerID: String
        private lateinit var reference: String
        private var paymentRequest: CitconPaymentRequest? = null
        //private var citcon3DSecureRequest: Citcon3DSecureRequest? = null
        private var isRequest3DSecure = false
        private lateinit var type: CPayMethodType
        private var consumer: CPayConsumer? = null
        private var amount: String = "0"

        fun paymentMethod(type: CPayMethodType): PaymentBuilder {
            this.type = type
            return this
        }

        fun amount(amount: String):  PaymentBuilder {
            this.amount = amount
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

        /*fun threeDSecureRequest(request: Citcon3DSecureRequest): PaymentBuilder {
            citcon3DSecureRequest = request
            return this
        }*/

        fun request3DSecureVerification(requestThreeDSecure: Boolean): PaymentBuilder {
            isRequest3DSecure = requestThreeDSecure
            return this
        }

        fun consumer(consumer: CPayConsumer): PaymentBuilder {
            this.consumer = consumer
            return this
        }

        fun build(mode: CPayENVMode): CPayRequest {
            val dropInRequest = CPayRequest()
                .amount(amount)
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

            if(isRequest3DSecure) {
                val billingAddress = consumer?.run {
                    CPay3DSecurePostalAddress()
                        .givenName(firstName)
                        .surname(lastName)
                        .phoneNumber(phone)
                        .streetAddress(billingAddress?.street)
                        .extendedAddress(billingAddress?.street2)
                        .locality(billingAddress?.city)
                        .region(billingAddress?.state)
                        .postalCode(billingAddress?.zip)
                        .countryCodeAlpha2(billingAddress?.country)
                }

                val additionalInformation = CPay3DSecureAdditionalInfo()
                    .accountId("account-id")

                dropInRequest.threeDSecureRequest(
                    Citcon3DSecureRequest()
                        .amount(amount)
                        .versionRequested(Citcon3DSecureRequest.VERSION_2)
                        .email(consumer?.email)
                        .mobilePhoneNumber(consumer?.phone)
                        .billingAddress(billingAddress)
                        .additionalInformation(additionalInformation)
                )
            }

            if(paymentRequest != null) {
                dropInRequest.citconPaymentRequest(paymentRequest!!)
            }

            return dropInRequest.paymentMethod(type)
        }

    }

    constructor(parcel: Parcel) : this() {
        mENVmode = parcel.readSerializable() as CPayENVMode
        mPaymentMethod = parcel.readSerializable() as CPayMethodType
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

        mMobileCallback = parcel.readString()
        mCallbackFailUrl = parcel.readString()
        mCancelUrl = parcel.readString()
        mCountry = parcel.readSerializable() as Locale
        mTimeout = parcel.readLong()

        mApiType = parcel.readSerializable() as CPayAPIType
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(mENVmode)
        parcel.writeSerializable(mPaymentMethod)
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

        parcel.writeString(mMobileCallback)
        parcel.writeString(mCallbackFailUrl)
        parcel.writeString(mCancelUrl)
        parcel.writeSerializable(mCountry)
        parcel.writeLong(mTimeout)
        parcel.writeSerializable(mApiType)
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
     * @return [Intent] containing all of the options set in [CPayRequest].
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
    private fun citconPaymentRequest(citconPaymentRequest: CitconPaymentRequest): CPayRequest {
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
    private fun setENVMode(mode: CPayENVMode): CPayRequest {
        mENVmode = mode
        return this
    }

    fun getENVMode(): CPayENVMode {
        return mENVmode
    }

    /**
     * This method is mandatory. payment method type is used for bring up UI.
     *
     * @param type Type of the payment method.
     */
    private fun paymentMethod(type: CPayMethodType): CPayRequest {
        this.mPaymentMethod = type
        when(type) {
            CPayMethodType.PAYPAL -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.PAYPAL)
            CPayMethodType.UNKNOWN -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.UNKNOWN)
            CPayMethodType.PAY_WITH_VENMO -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.PAY_WITH_VENMO)
            CPayMethodType.AMEX -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.AMEX)
            CPayMethodType.GOOGLE_PAYMENT -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.GOOGLE_PAYMENT)


            CPayMethodType.DINERS -> TODO()
            CPayMethodType.DISCOVER -> TODO()
            CPayMethodType.JCB -> TODO()
            CPayMethodType.MAESTRO -> TODO()
            CPayMethodType.MASTERCARD -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.MASTERCARD)
            CPayMethodType.VISA -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.VISA)

            CPayMethodType.HIPER -> TODO()
            CPayMethodType.HIPERCARD -> TODO()
            CPayMethodType.NONE -> mBrainTreeDropInRequest.paymentMethodType(
                PaymentMethodType.NONE)
            else -> {}
        }
        return this
    }

    fun getPaymentMethod(): CPayMethodType {
        return mPaymentMethod
    }

    private fun setApiType(type: CPayAPIType): CPayRequest {
        mApiType = type
        return this
    }

    fun getApiType(): CPayAPIType {
        return mApiType
    }

    /**
     * This method is mandatory. access token is used apply client token from Citcon server.
     *
     * @param token is applied from backend server.
     */
    private fun accessToken(token: String): CPayRequest {
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
    private fun chargeToken(token: String): CPayRequest {
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
    private fun reference(ref: String): CPayRequest {
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
    private fun consumerID(id: String): CPayRequest {
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
    private fun collectDeviceData(collectDeviceData: Boolean): CPayRequest {
        mBrainTreeDropInRequest.collectDeviceData(collectDeviceData)
        return this
    }

    /**
     * @param vaultManager `true` to allow customers to manage their vaulted payment methods.
     * Defaults to `false`.
     */
    private fun vaultManager(vaultManager: Boolean): CPayRequest {
        mBrainTreeDropInRequest.vaultManager(vaultManager)
        return this
    }

    /**
     * @param maskCardNumber `true` to mask the card number when the field is not focused.
     * See [com.citconpay.cardform.view.CardEditText] for more details. Defaults to
     * `false`.
     */
    private fun maskCardNumber(maskCardNumber: Boolean): CPayRequest {
        mBrainTreeDropInRequest.maskCardNumber(maskCardNumber)
        return this
    }

    /**
     * @param maskSecurityCode `true` to mask the security code during input. Defaults to `false`.
     */
    private fun maskSecurityCode(maskSecurityCode: Boolean): CPayRequest {
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
     private fun request3DSecureVerification(requestThreeDSecure: Boolean): CPayRequest {
        mBrainTreeDropInRequest.requestThreeDSecureVerification(requestThreeDSecure)
        return this
    }

    /**
     * This method is optional.
     *
     * @param request [ThreeDSecureRequest] to specify options and additional information for 3D Secure. To encourage 3DS 2.0 flows, set [ThreeDSecureRequest.billingAddress], [ThreeDSecureRequest.email], and [ThreeDSecureRequest.mobilePhoneNumber] for best results. If no amount is set, the [DropInRequest.amount] will be used.
     * @return the drop in request
     */
    private fun threeDSecureRequest(request: Citcon3DSecureRequest): CPayRequest {
        //Todo: setup 3DS request depends on Gateway type
        mBrainTreeDropInRequest.threeDSecureRequest(request.threeDSecureRequest)
        return this
    }

    //CPaySDK
    private fun amount(amount: String): CPayRequest {
        mAmount = try {
            amount.toInt()
            amount
        } catch (e: Exception) {
            "0"
        }
        return this
    }

    fun getAmount(): String {
        return mAmount
    }

    private fun currency(currency: String): CPayRequest {
        mCurrency = currency
        return this
    }

    fun getCurrency(): String {
        return mCurrency
    }

    private fun subject(subject: String): CPayRequest {
        mSubject = subject
        return this
    }

    fun getSubject(): String {
        return mSubject
    }

    private fun body(body: String): CPayRequest {
        mBody = body
        return this
    }

    fun getBody(): String {
        return mBody
    }

    private fun ipnURL(ipn: String): CPayRequest {
        mIpnUrl = ipn
        return this
    }

    fun getIpn(): String {
        return mIpnUrl
    }

    private fun callbackURL(callback: String): CPayRequest {
        mCallbackUrl = callback
        return this
    }

    fun getCallback(): String {
        return mCallbackUrl
    }

    private fun mobileCallbackURL(callback: String?): CPayRequest {
        mMobileCallback = callback
        return this
    }

    fun getMobileCallback(): String? {
        return mMobileCallback
    }

    private fun failCallbackURL(callback: String?): CPayRequest {
        mCallbackFailUrl = callback
        return this
    }

    fun getFailCallback(): String? {
        return mCallbackFailUrl
    }

    private fun cancelURL(callback: String?): CPayRequest {
        mCancelUrl = callback
        return this
    }

    fun getCancelURL(): String? {
        return mCancelUrl
    }

    private fun setCountry(country: Locale): CPayRequest {
        mCountry = country
        return this
    }

    fun getCountry(): Locale? {
        return mCountry
    }

    private fun setTimeout(t: Long): CPayRequest {
        mTimeout = t
        return this
    }

    fun getTimeout(): Long {
        return mTimeout
    }

    private fun setAllowDuplicate(flag: Boolean): CPayRequest {
        mAllowDuplicate = flag
        return this
    }

    fun isAllowDuplicate(): Boolean {
        return mAllowDuplicate
    }

    companion object CREATOR : Parcelable.Creator<CPayRequest> {
        const val EXTRA_CHECKOUT_REQUEST: String = "com.citconpay.sdk.data.api.EXTRA_CHECKOUT_REQUEST"

        override fun createFromParcel(parcel: Parcel): CPayRequest {
            return CPayRequest(parcel)
        }

        override fun newArray(size: Int): Array<CPayRequest?> {
            return arrayOfNulls(size)
        }
    }
}