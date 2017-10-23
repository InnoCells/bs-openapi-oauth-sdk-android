package com.bancsabadell.authsdk

import android.databinding.ObservableBoolean
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.*
import com.bancsabadell.authsdk.data.TokenResponse
import com.bancsabadell.authsdk.data.AuthData
import com.bancsabadell.authsdk.utils.AuthUriUtils
import com.fasterxml.jackson.databind.ObjectMapper
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


/**
 * Created by Kame on 19/10/2017.
 */
class ViewModel(val data: AuthData) {

    companion object {
        val LOG_TAG = "AuthSdk"
    }

    interface Callback {
        fun onError(description: String)
        fun onCompleted(tokenResponse: TokenResponse)
    }

    var callback: Callback? = null

    val webClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            var finalUrl = url
            print("Overriding url...\n$finalUrl")

            val uri = Uri.parse(finalUrl)

            val error = uriUtils.findError(uri)
            val previousOfToken = uriUtils.isPreviousOfToken(uri)
            val secure = uriUtils.isSecure(uri)

            if (!error.isEmpty()) {
                complain(error, finalUrl)
                return super.shouldOverrideUrlLoading(view, url)
            } else if (previousOfToken && secure) {
                print("Is previous of token and secure, lets get token....")
                finalUrl = uriUtils.buildRequestTokenUrl(uri)
                print("Post url to get token: \n$finalUrl\nwith headers: ${uriUtils.baseAuthHeader}")

                showProgress.set(true)
                val observableToken = requestToken(finalUrl, uriUtils.baseAuthHeader)
                observableToken
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext({ tokenResponse ->
                            print("Request token successful!: ${tokenResponse.accessToken}")
                            showProgress.set(false)
                            callback?.onCompleted(tokenResponse)
                        })
                        .doOnError({
                            e ->
                            complain("Error requesting token...", t = e)
                            showProgress.set(false)
                        })
                        .subscribe()

                return true
            } else {
                view?.loadUrl(finalUrl)
                return true
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showProgress.set(true)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            showProgress.set(false)
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            complain("Error code: $errorCode, error desc: $description", failingUrl)
        }

    }

    val chromeClient = WebChromeClient()
    val uriUtils = AuthUriUtils(data)
    val showProgress = ObservableBoolean(false)

    fun init(webView: WebView) {
        with(webView) {
            settings.javaScriptEnabled = true
            webViewClient = webClient
            webChromeClient = chromeClient
            val baseUrl = uriUtils.baseAuthUrl
            loadUrl(baseUrl)
            print("Loading base url... \n$baseUrl")
        }
    }

    private fun print(text: String) {
        if (data.debug) {
            Log.d(LOG_TAG, text)
        }
    }

    private fun complain(text: String, url: String? = null, t: Throwable? = null) {
        if (data.debug) {
            Log.e(LOG_TAG, text + " Url: $url", t)
        }
        abort(text)
    }

    private fun abort(error: String) {
        callback?.onError(error)
    }

    fun requestToken(finalUrl: String, token: String): Observable<TokenResponse> {
        return Observable.unsafeCreate<TokenResponse> { subscriber ->
            try {
                val urlConnection = URL(finalUrl).openConnection()
                urlConnection.doOutput = true
                urlConnection.setRequestProperty("Authorization", "Basic " + token)

                val bf = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
                var inputLine: String?
                val response = StringBuffer()

                do {
                    inputLine = bf.readLine()
                    response.append(inputLine)
                } while (inputLine != null)

                val mapper = ObjectMapper()
                val tokenResponse = mapper.readValue(response.toString(), TokenResponse::class.java)
                if (!subscriber.isUnsubscribed) {
                    subscriber.onNext(tokenResponse)
                    subscriber.onCompleted()
                }
            } catch (e: Exception) {
                if (!subscriber.isUnsubscribed) {
                    subscriber.onError(e)
                }
            }
        }
    }


}

