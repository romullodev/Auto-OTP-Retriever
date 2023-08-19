package com.romullodev.auto_otp_retriever.core

import java.util.regex.Pattern

private val TOKEN_PATTERN by lazy { "(\\d{6})" }

fun String?.extractToken(): String = this?.takeIf { it.isNotEmpty() }?.let {
    Pattern.compile(TOKEN_PATTERN).matcher(it).let { matcher ->
        if (matcher.find())
            matcher.group(0) ?: String()
        else
            String()
    }
} ?: String()