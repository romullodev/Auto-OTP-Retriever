package com.romullodev.auto_otp_retriever

import android.os.Bundle
import android.util.Log
import com.romullodev.auto_otp_retriever.core.AutoOtpRetrieverActivity
import com.romullodev.auto_otp_retriever.databinding.ActivityMainBinding

class MainActivity : AutoOtpRetrieverActivity() {
    private lateinit var binding: ActivityMainBinding

    override val otpCodeListener: (String) -> Unit
        get() = {
            setupOtpListeners(it)
        }
    override val timeoutListener: () -> Unit
        get() = {
            Log.d("MainActivity", "timeout - otp retriever")
        }

    override val startListenSmsMessagesSuccessfully: () -> Unit
        get() = {
            Log.d("MainActivity", "listening for sms messages")
        }

    override val failureOnListenSmsMessages: () -> Unit
        get() = {
            Log.d("MainActivity", "failure on listening for sms messages")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            binding.button.text = "listening ..."
            initializeAutoOtp()
        }
    }

    private fun setupOtpListeners(token: String) {
        binding.editText.setText(token)
        binding.editText.setSelection(token.length)
    }
}