package com.bancsabadell.authsdk

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bancsabadell.authsdk.data.RequestData
import com.bancsabadell.authsdk.data.ResultData
import com.bancsabadell.authsdk.databinding.SampleActivityBinding

/**
 * Created by Kame on 19/10/2017.
 */
class SampleActivity : AppCompatActivity() {
    companion object {
        private val REQUEST_AUTH_CODE = 1000
    }

    lateinit var binding: SampleActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<SampleActivityBinding>(this, R.layout.sample_activity)
        binding.initToken.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra(AuthActivity.EXTRA_REQUEST_DATA, RequestData("CLI1462287906482kIZMWo4CYyLqzVQAfIX4ftNmqBIcz6ZSwBNzgwXG74054H", "123456789", true))
            startActivityForResult(intent, REQUEST_AUTH_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTH_CODE) {
            val resultData: ResultData? = AuthActivity.getResultData(data)

            if (resultCode == Activity.RESULT_OK) {
                binding.success = true
                binding.text = resultData?.authData?.accessToken
            } else {
                binding.success = false
                binding.text = resultData?.error ?: "Cancelled"
            }
        }
    }
}