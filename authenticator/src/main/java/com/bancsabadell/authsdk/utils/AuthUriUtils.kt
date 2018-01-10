package com.bancsabadell.authsdk.utils

import android.net.Uri
import android.util.Base64
import com.bancsabadell.authsdk.data.RequestData
import java.security.SecureRandom
import java.util.*

/**
 * Created by Kame on 20/10/2017.
 */
class AuthUriUtils(val data: RequestData, uniqueStringLength: Int = 16) {

    companion object {
        private val STATE = "state"
        private val ERROR = "error"
        private val CODE = "code"
    }

    val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lower = upper.toLowerCase(Locale.ROOT)
    val digits = "0123456789"
    val alphaNum = upper + lower + digits

    private val random = SecureRandom()
    private var buf = CharArray(uniqueStringLength)
    private var symbols = alphaNum.toCharArray()

    private val uniqueString by lazy {
        for (idx in buf.indices) {
            buf[idx] = symbols[random.nextInt(symbols.size)]
        }
        String(buf)
    }

    val baseAuthUrl = "${data.basePath}/authorize?response_type=code&state=$uniqueString&redirect_uri=${data.redirectUri}" +
            "&client_id=${data.clientId}&scope=read"

    val baseAuthHeader: String = Base64.encodeToString("${data.clientId}:${data.clientSecret}".toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

    init {
        if (uniqueStringLength < 16) throw IllegalArgumentException("String size must be at least 16 chars")
    }

    fun findError(uri: Uri?) = uri?.getQueryParameter(ERROR) ?: ""

    fun isSecure(uri: Uri?) = uri?.getQueryParameter(STATE) == uniqueString

    fun isPreviousOfToken(uri: Uri?) = uri.toString().startsWith(data.redirectUri)

    fun buildRequestTokenUrl(uri: Uri?): String {
        if (uri == null) {
            return ""
        }
        val code = uri.getQueryParameter(CODE)
        return "${data.basePath}/token?grant_type=authorization_code&code=$code&redirect_uri=${data.redirectUri}&scope=read"
    }


}