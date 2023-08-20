package com.romullodev.auto_otp_retriever.core

import android.app.Activity
import android.content.IntentFilter
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.romullodev.auto_otp_retriever.broadcast.AutoOtpReceiver

abstract class AutoOtpRetrieverActivity : AppCompatActivity() {
    private val requestLauncherPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { handleSmsRetrieved(it) }

    private val pickPhoneNumberLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        handlePickPhoneNumber(it)
    }

    private fun handlePickPhoneNumber(activityResult: ActivityResult?) {
        activityResult?.let { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    phoneNumberHintListener.invoke(
                        Identity.getSignInClient(this).getPhoneNumberFromIntent(result.data)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    abstract val otpCodeListener: (String) -> Unit
    abstract val phoneNumberHintListener: (String) -> Unit
    open val timeoutListener: () -> Unit = {}
    open val startListenSmsMessagesSuccessfully: () -> Unit = {}
    open val failureOnListenSmsMessages: () -> Unit = {}
    open val registerOtpReceiverListener: () -> Unit = {}
    open val unregisterOtpReceiverListener: () -> Unit = {}

    private val autoTopReceiver = AutoOtpReceiver().apply {
        initializeFields(
            onTimeoutListener = timeoutListener,
            permissionLauncher = { requestLauncherPermission.launch(it) }
        )
    }

    private fun handleSmsRetrieved(activityResult: ActivityResult?) {
        activityResult?.let { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE).extractToken()
                    .takeIf { it.isNotEmpty() }?.let { otpCodeListener.invoke(it) }
            }
        }
    }
    protected fun initializeAutoOtp() {
        registerReceiver(
            autoTopReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION,
            null
        )
        registerOtpReceiverListener.invoke()
        initAutoOtp()
    }
    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(autoTopReceiver)
            unregisterOtpReceiverListener.invoke()
        }catch (e: Exception){
            Log.e("AutoOtp", e.printStackTrace().toString())
        }
    }
    private fun initAutoOtp() {
        SmsRetriever.getClient(this)
            .startSmsUserConsent(null)
            .addOnSuccessListener {
                startListenSmsMessagesSuccessfully.invoke()
            }
            .addOnFailureListener {
                failureOnListenSmsMessages.invoke()
            }
    }

    protected fun requestPhoneNumberHint() {
        val request = GetPhoneNumberHintIntentRequest.builder().build()
        Identity.getSignInClient(this)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener { taskResult ->
                pickPhoneNumberLauncher.launch(
                    IntentSenderRequest.Builder(
                        taskResult.intentSender
                    ).build()
                )
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}