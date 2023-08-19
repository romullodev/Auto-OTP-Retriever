package com.romullodev.auto_otp_retriever

import android.os.Bundle
import android.util.Log
import com.romullodev.auto_otp_retriever.core.AutoOtpRetrieverActivity
import com.romullodev.auto_otp_retriever.databinding.ActivityMainBinding

class MainActivity : AutoOtpRetrieverActivity() {
    private lateinit var binding: ActivityMainBinding

    override val otpCodeListener: (String) -> Unit = { setupOtpListener(it) }
    override val phoneNumberHintListener: (String) -> Unit = { setupPhoneNumberListener(it) }

    private fun setupOtpListener(token: String) {
        binding.run {
            editTextOtp.setText(token)
            editTextOtp.setSelection(token.length)
        }
    }
    private fun setupPhoneNumberListener(phoneNumber: String) {
        binding.run {
            phoneNumberEditText.setText(phoneNumber)
            phoneNumberEditText.setSelection(phoneNumber.length)
        }
    }

    override val timeoutListener: () -> Unit get() = { Log.d("MainActivity", "timeout - otp retriever") }
    override val startListenSmsMessagesSuccessfully: () -> Unit get() = { Log.d("MainActivity", "listening for sms messages") }
    override val failureOnListenSmsMessages: () -> Unit get() = { Log.d("MainActivity", "failure on listening for sms messages") }
    override val registerOtpReceiverListener: () -> Unit get() = { Log.d("MainActivity", "opt receiver was registered") }
    override val unregisterOtpReceiverListener: () -> Unit get() = { Log.d("MainActivity", "opt receiver was unregistered") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.run {
            buttonStartListening.setOnClickListener {
                buttonStartListening.text = "listening ..."
                initializeAutoOtp()
            }
            buttonPickePhone.setOnClickListener {
                requestPhoneNumberHint()
            }
        }
    }
}