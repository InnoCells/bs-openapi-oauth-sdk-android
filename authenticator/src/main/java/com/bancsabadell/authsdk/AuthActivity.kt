package com.bancsabadell.authsdk

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import com.bancsabadell.authsdk.databinding.WebViewBinding
import com.bancsabadell.authsdk.data.TokenResponse
import com.bancsabadell.authsdk.data.AuthData
import com.bancsabadell.authsdk.data.AuthResult

/**
 * Created by Kame on 19/10/2017.
 */
class AuthActivity : AppCompatActivity() {
    companion object {
        val AUTH_DATA = "EXTRA_DATA"
        private val AUTH_RESULT = "AUTH_RESULT"
        @JvmStatic fun getAuthResult(intent: Intent?): AuthResult? = intent?.getParcelableExtra<AuthResult>(AUTH_RESULT)
    }

    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: AuthData = intent.getParcelableExtra<AuthData>(AUTH_DATA) ?:
                throw IllegalArgumentException("You must provide a AuthData as argument calling #AuthActivity.startForResult()!")

        val binding = DataBindingUtil.setContentView<WebViewBinding>(this, R.layout.web_view)
        val viewModel = ViewModel(data)

        viewModel.callback = object : ViewModel.Callback {
            override fun onCompleted(tokenResponse: TokenResponse) {
                onFinish(Activity.RESULT_OK, prepareResult(AuthResult(tokenResponse = tokenResponse)))
            }

            override fun onError(description: String) {
                onFinish(Activity.RESULT_CANCELED, prepareResult(AuthResult(description)))
            }

        }

        this.webView = binding.webView
        viewModel.init(binding.webView)
        binding.model = viewModel
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            onFinish(Activity.RESULT_CANCELED)
        }
    }

    fun prepareResult(result: AuthResult): Intent = Intent().putExtra(AUTH_RESULT, result)

    fun onFinish(result: Int, data: Intent? = null) {
        setResult(result, data)
        finish()
    }
}

