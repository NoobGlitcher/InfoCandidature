package com.example.infocandidature.View


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.WalletConstants
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.button.PayButton
import com.example.infocandidature.R
import com.example.infocandidature.databinding.ActivityCheckoutBinding
import com.example.infocandidature.outils.PaiementsUtil
import com.example.infocandidature.Model.CheckoutViewModel
import com.google.android.gms.pay.PayClient
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var model: CheckoutViewModel
    private lateinit var googlePayButton: PayButton
    private lateinit var addToGoogleWalletButtonContainer: View
    private lateinit var addToGoogleWalletButton: View
    private val ADD_TO_GOOGLE_WALLET_REQUEST_CODE = 999


    private val resolvePaymentForResult: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val resultData = result.data
                    resultData?.let {
                        val paymentData = PaymentData.getFromIntent(resultData)
                        paymentData?.let { handlePaymentSuccess(paymentData) }
                    }
                }
                RESULT_CANCELED -> {
                    // User canceled the payment attempt
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUi()
        model = ViewModelProvider(this).get(CheckoutViewModel::class.java)
        model.canUseGooglePay.observe(this, { setGooglePayAvailable(it) })
        model.canAddPasses.observe(this, { setAddToGoogleWalletAvailable(it) })
    }

    private fun initializeUi() {
        val layoutBinding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(layoutBinding.root)

        googlePayButton = layoutBinding.googlePayButton
        try {
            googlePayButton.initialize(
                ButtonOptions.newBuilder()
                    .setAllowedPaymentMethods(PaiementsUtil.getAllowedPaymentMethods().toString())
                    .build()
            )
            googlePayButton.setOnClickListener { requestPayment(it) }
        } catch (e: JSONException) {
            // Keep Google Pay button hidden
        }

        addToGoogleWalletButton = layoutBinding.addToGoogleWalletButton.root
        addToGoogleWalletButtonContainer = layoutBinding.passContainer
        addToGoogleWalletButton.setOnClickListener {
            addToGoogleWalletButton.isClickable = false
            model.savePassesJwt(model.genericObjectJwt, this, ADD_TO_GOOGLE_WALLET_REQUEST_CODE)
        }
    }

    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, R.string.google_pay_status_unavailable, Toast.LENGTH_LONG).show()
        }
    }

    private fun requestPayment(view: View) {
        googlePayButton.isClickable = false
        val dummyPriceCents: Long = 100
        val shippingCostCents: Long = 900
        val totalPriceCents = dummyPriceCents + shippingCostCents
        val task = model.getLoadPaymentDataTask(totalPriceCents)
        task?.addOnCompleteListener { completedTask ->
            completedTask?.let {
                if (it.isSuccessful) {
                    handlePaymentSuccess(it.result)
                } else {
                    val exception = it.exception
                    if (exception is ResolvableApiException) {
                        val resolution = exception.resolution
                        resolution?.let {
                            resolvePaymentForResult.launch(
                                IntentSenderRequest.Builder(it).build()
                            )
                        }
                    } else if (exception is ApiException) {
                        handleError(exception.statusCode, exception.message)
                    } else {
                        handleError(CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API exception")
                    }
                    googlePayButton.isClickable = true
                }
            }
        }

    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInfo = paymentData.toJson()
        try {
            val paymentMethodData = JSONObject(paymentInfo).getJSONObject("paymentMethodData")
            val info = paymentMethodData.getJSONObject("info")
            val billingName = info.getJSONObject("billingAddress").getString("name")
            Toast.makeText(
                this, getString(R.string.payments_show_name, billingName),
                Toast.LENGTH_LONG
            ).show()
            Log.d("Google Pay token", paymentMethodData.getJSONObject("tokenizationData")
                .getString("token"))
            startActivity(Intent(this, CheckoutSuccessActivity::class.java))
        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $e")
        }
    }

    private fun handleError(statusCode: Int, message: String?) {
        Log.e("loadPaymentData failed",
            String.format(Locale.getDefault(), "Error code: %d, Message: %s", statusCode, message))
    }

    private fun setAddToGoogleWalletAvailable(available: Boolean) {
        if (available) {
            addToGoogleWalletButtonContainer.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                this,
                R.string.google_wallet_status_unavailable,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TO_GOOGLE_WALLET_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    Toast.makeText(
                        this, getString(R.string.add_google_wallet_success), Toast.LENGTH_LONG
                    ).show()
                }
                WalletConstants.RESULT_ERROR -> {
                    val status = AutoResolveHelper.getStatusFromIntent(data)
                    // Handle error
                }
                RESULT_CANCELED -> {
                    // Save canceled
                }
                PayClient.SavePassesResult.SAVE_ERROR -> {
                    if (data != null) {
                        val apiErrorMessage = data.getStringExtra(PayClient.EXTRA_API_ERROR_MESSAGE)
                        handleError(resultCode, apiErrorMessage)
                    }
                }
                else -> handleError(
                    CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API exception"
                )
            }
            addToGoogleWalletButton.isClickable = true
        }
    }
}
