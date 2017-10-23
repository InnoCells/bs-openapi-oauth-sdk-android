package com.bancsabadell.authsdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bancsabadell.authsdk.data.AuthData
import com.bancsabadell.authsdk.data.AuthResult

/**
 * Created by Kame on 19/10/2017.
 */
class SampleActivity: AppCompatActivity() {
    companion object {
        private val REQUEST_AUTH_CODE = 1000
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(AuthActivity.AUTH_DATA, AuthData("http://localhost:3000/callback", true))
        startActivityForResult(intent, REQUEST_AUTH_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTH_CODE) {
            val authResult: AuthResult? = AuthActivity.getAuthResult(data)

            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, authResult?.tokenResponse?.accessToken, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, authResult?.error ?: "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}