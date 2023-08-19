package com.romullodev.auto_otp_retriever.core

import android.app.Activity
import android.content.IntentFilter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.romullodev.auto_otp_retriever.broadcast.AutoOtpReceiver

abstract class AutoOtpRetrieverActivity : AppCompatActivity() {
    private val requestLauncherPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { handleSmsRetrieved(it) }

    abstract val otpCodeListener: (String) -> Unit
    open val timeoutListener: () -> Unit = {}
    open val startListenSmsMessagesSuccessfully: () -> Unit = {}
    open val failureOnListenSmsMessages: () -> Unit = {}

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
        initAutoOtp()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(autoTopReceiver)
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
}