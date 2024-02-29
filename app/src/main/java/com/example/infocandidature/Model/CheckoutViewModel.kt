package com.example.infocandidature.Model


import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.infocandidature.outils.PaiementsUtil
import com.google.android.gms.pay.Pay
import com.google.android.gms.pay.PayClient
import com.google.android.gms.pay.PayApiAvailabilityStatus
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import org.json.JSONObject

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    // A client for interacting with the Google Pay API.
    private val paymentsClient: PaymentsClient = PaiementsUtil.createPaymentsClient(application)

    // A client to interact with the Google Wallet API
    private val walletClient: PayClient = Pay.getClient(application)

    // LiveData with the result of whether the user can pay using Google Pay
    private val _canUseGooglePay = MutableLiveData<Boolean>()

    // LiveData with the result of whether the user can save passes with Google Wallet
    private val _canAddPasses = MutableLiveData<Boolean>()

    val canUseGooglePay: LiveData<Boolean> = _canUseGooglePay
    val canAddPasses: LiveData<Boolean> = _canAddPasses

    init {
        fetchCanUseGooglePay()
        fetchCanAddPassesToGoogleWallet()
    }

    /**
     * Determine the user's ability to pay with a payment method supported by your app and display
     * a Google Pay payment button.
     */
    private fun fetchCanUseGooglePay() {
        val isReadyToPayJson: JSONObject? = PaiementsUtil.getIsReadyToPayRequest()
        if (isReadyToPayJson == null) {
            _canUseGooglePay.value = false
            return
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task: Task<Boolean> = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                _canUseGooglePay.value = completedTask.result
            } else {
                Log.w("isReadyToPay failed", completedTask.exception)
                _canUseGooglePay.value = false
            }
        }
    }

    /**
     * Creates a Task that starts the payment process with the transaction details included.
     *
     * @param priceCents the price to show on the payment sheet.
     * @return a Task with the payment information.
     */
    fun getLoadPaymentDataTask(priceCents: Long): Task<PaymentData>? {
        val paymentDataRequestJson: JSONObject? = PaiementsUtil.getPaymentDataRequest(priceCents)
        if (paymentDataRequestJson == null) {
            return null
        }

        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    /**
     * Determine whether the API to save passes to Google Pay is available on the device.
     */
    private fun fetchCanAddPassesToGoogleWallet() {
        walletClient.getPayApiAvailabilityStatus(PayClient.RequestType.SAVE_PASSES)
            .addOnSuccessListener { status ->
                _canAddPasses.value = status == PayApiAvailabilityStatus.AVAILABLE
            }
            .addOnFailureListener { exception ->
                _canAddPasses.value = false
                // If the API is not available, we recommend to either:
                // 1) Hide the save button
                // 2) Fall back to a different Save Passes integration (e.g. JWT link)
                // Note that a user might become eligible in the future.
                // Google Play Services is too old. API availability can't be verified.
                Log.e("Pay API availability", "Error", exception)
            }
    }

    /**
     * Exposes the `savePassesJwt` method in the wallet client
     */
    fun savePassesJwt(jwtString: String, activity: Activity, requestCode: Int) {
        walletClient.savePassesJwt(jwtString, activity, requestCode)
    }

    /**
     * Exposes the `savePasses` method in the wallet client
     */
    fun savePasses(objectString: String, activity: Activity, requestCode: Int) {
        walletClient.savePasses(objectString, activity, requestCode)
    }

    // Test generic object used to be created against the API
    // See https://developers.google.com/wallet/tickets/boarding-passes/web#json_web_token_jwt for more details
    val genericObjectJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJnb29nbGUiLCJwYXlsb2FkIjp7ImdlbmVyaWNPYmplY3RzIjpbeyJpZCI6IjMzODgwMDAwMDAwMjIwOTUxNzcuZjUyZDRhZjYtMjQxMS00ZDU5LWFlNDktNzg2ZDY3N2FkOTJiIn1dfSwiaXNzIjoid2FsbGV0LWxhYi10b29sc0BhcHBzcG90LmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ0eXAiOiJzYXZldG93YWxsZXQiLCJpYXQiOjE2NTA1MzI2MjN9.ZURFHaSiVe3DfgXghYKBrkPhnQy21wMR9vNp84azBSjJxENxbRBjqh3F1D9agKLOhrrflNtIicShLkH4LrFOYdnP6bvHm6IMFjqpUur0JK17ZQ3KUwQpejCgzuH4u7VJOP_LcBEnRtzZm0PyIvL3j5-eMRyRAo5Z3thGOsKjqCPotCAk4Z622XHPq5iMNVTvcQJaBVhmpmjRLGJs7qRp87sLIpYOYOkK8BD7OxLmBw9geqDJX-Y1zwxmQbzNjd9z2fuwXX66zMm7pn6GAEBmJiqollFBussu-QFEopml51_5nf4JQgSdXmlfPrVrwa6zjksctIXmJSiVpxL7awKN2w"
}
