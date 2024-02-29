package com.example.infocandidature.outils


import android.content.Context
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

object PaiementsUtil {

    val CENTS_IN_A_UNIT = BigDecimal(100)

    private fun getBaseRequest(): JSONObject {
        return JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
        }
    }

    fun createPaymentsClient(context: Context): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder().setEnvironment(Constants.PAYMENTS_ENVIRONMENT).build()
        return Wallet.getPaymentsClient(context, walletOptions)
    }

    @Throws(JSONException::class)
    private fun getGatewayTokenizationSpecification(): JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters", JSONObject().apply {
                put("gateway", "example")
                put("gatewayMerchantId", "exampleGatewayMerchantId")
            })
        }
    }

    @Throws(JSONException::class, RuntimeException::class)
    private fun getDirectTokenizationSpecification(): JSONObject {
        val tokenizationSpecification = JSONObject()
        tokenizationSpecification.put("type", "DIRECT")
        val parameters = JSONObject(Constants.DIRECT_TOKENIZATION_PARAMETERS as Map<*, *>?)
        tokenizationSpecification.put("parameters", parameters)
        return tokenizationSpecification
    }

    private fun getAllowedCardNetworks(): JSONArray {
        return JSONArray(Constants.SUPPORTED_NETWORKS)
    }

    private fun getAllowedCardAuthMethods(): JSONArray {
        return JSONArray(Constants.SUPPORTED_METHODS)
    }

    @Throws(JSONException::class)
    private fun getBaseCardPaymentMethod(): JSONObject {
        val cardPaymentMethod = JSONObject()
        cardPaymentMethod.put("type", "CARD")
        val parameters = JSONObject()
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods())
        parameters.put("allowedCardNetworks", getAllowedCardNetworks())
        parameters.put("billingAddressRequired", true)
        val billingAddressParameters = JSONObject()
        billingAddressParameters.put("format", "FULL")
        parameters.put("billingAddressParameters", billingAddressParameters)
        cardPaymentMethod.put("parameters", parameters)
        return cardPaymentMethod
    }

    @Throws(JSONException::class)
    private fun getCardPaymentMethod(): JSONObject {
        val cardPaymentMethod = getBaseCardPaymentMethod()
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification())
        return cardPaymentMethod
    }

    @Throws(JSONException::class)
    fun getAllowedPaymentMethods(): JSONArray {
        return JSONArray().apply {
            put(getCardPaymentMethod())
        }
    }

    fun getIsReadyToPayRequest(): JSONObject? {
        return try {
            val isReadyToPayRequest = getBaseRequest()
            isReadyToPayRequest.put("allowedPaymentMethods", JSONArray().put(getBaseCardPaymentMethod()))
            isReadyToPayRequest
        } catch (e: JSONException) {
            null
        }
    }

    @Throws(JSONException::class)
    private fun getTransactionInfo(price: String): JSONObject {
        val transactionInfo = JSONObject()
        transactionInfo.put("totalPrice", price)
        transactionInfo.put("totalPriceStatus", "FINAL")
        transactionInfo.put("countryCode", Constants.COUNTRY_CODE)
        transactionInfo.put("currencyCode", Constants.CURRENCY_CODE)
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE")
        return transactionInfo
    }

    @Throws(JSONException::class)
    private fun getMerchantInfo(): JSONObject {
        return JSONObject().put("merchantName", "Example Merchant")
    }

    fun getPaymentDataRequest(priceCents: Long): JSONObject? {
        val price = centsToString(priceCents)
        return try {
            val paymentDataRequest = getBaseRequest()
            paymentDataRequest.put("allowedPaymentMethods", JSONArray().put(getCardPaymentMethod()))
            paymentDataRequest.put("transactionInfo", getTransactionInfo(price))
            paymentDataRequest.put("merchantInfo", getMerchantInfo())
            paymentDataRequest.put("shippingAddressRequired", true)
            val shippingAddressParameters = JSONObject()
            shippingAddressParameters.put("phoneNumberRequired", false)
            val allowedCountryCodes = JSONArray(Constants.SHIPPING_SUPPORTED_COUNTRIES)
            shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes)
            paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters)
            paymentDataRequest
        } catch (e: JSONException) {
            null
        }
    }

    fun centsToString(cents: Long): String {
        return BigDecimal(cents)
            .divide(CENTS_IN_A_UNIT, RoundingMode.HALF_EVEN)
            .setScale(2, RoundingMode.HALF_EVEN)
            .toString()
    }
}
