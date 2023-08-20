package com.github.romullodev.auto_otp_retriever.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class AutoOtpReceiver : BroadcastReceiver() {
    private var permissionLauncher: (Intent) -> Unit = {}
    private var onTimeoutListener: () -> Unit = {}

    fun initializeFields(
        onTimeoutListener: () -> Unit,
        permissionLauncher: (Intent) -> Unit
    ) {
        this.permissionLauncher = permissionLauncher
        this.onTimeoutListener = onTimeoutListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val statusMsg = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status
                when (statusMsg?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                        } else {
                            extras.getParcelable(
                                SmsRetriever.EXTRA_CONSENT_INTENT,
                                Intent::class.java
                            )
                        })?.let {
                            permissionLauncher.invoke(it)
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        onTimeoutListener.invoke()
                    }
                }
            }
        }
    }
}