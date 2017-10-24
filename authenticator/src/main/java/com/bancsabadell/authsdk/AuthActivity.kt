package com.bancsabadell.authsdk

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import com.bancsabadell.authsdk.databinding.WebViewBinding
import com.bancsabadell.authsdk.data.AuthData
import com.bancsabadell.authsdk.data.RequestData
import com.bancsabadell.authsdk.data.ResultData

/**
 * Created by Kame on 19/10/2017.
 */
class AuthActivity : AppCompatActivity() {
    companion object {
        val EXTRA_REQUEST_DATA = "EXTRA_DATA"
        private val AUTH_RESULT = "AUTH_RESULT"
        @JvmStatic fun getAuthResult(intent: Intent?): ResultData? = intent?.getParcelableExtra<ResultData>(AUTH_RESULT)
    }

    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: RequestData = intent.getParcelableExtra<RequestData>(EXTRA_REQUEST_DATA) ?:
                throw IllegalArgumentException("You must provide a RequestData as argument calling #AuthActivity.startForResult()!")

        val binding = DataBindingUtil.setContentView<WebViewBinding>(this, R.layout.web_view)
        val viewModel = ViewModel(data)

        viewModel.callback = object : ViewModel.Callback {
            override fun onCompleted(tokenResponse: AuthData) {
                onFinish(Activity.RESULT_OK, prepareResult(ResultData(authData = tokenResponse)))
            }

            override fun onError(description: String) {
                onFinish(Activity.RESULT_CANCELED, prepareResult(ResultData(description)))
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

    fun prepareResult(result: ResultData): Intent = Intent().putExtra(AUTH_RESULT, result)

    fun onFinish(result: Int, data: Intent? = null) {
        setResult(result, data)
        finish()
    }
}

