/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.extension.showToast
import team.henrykey.minitiktok.util.SharedPrefsUtils

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_CODE = "code"

        fun hasLogin(context: Context): Boolean = getUserName(context) != null

        fun getUserName(context: Context): String? = SharedPrefsUtils.getString(
            KEY_USER_NAME, null, context)

        fun getCode(context: Context): String? = SharedPrefsUtils.getString(
            KEY_CODE, null, context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            when {
                codeEdit.text.isNullOrEmpty() -> {
                    showToast(getString(R.string.code_input_error))
                    codeEdit.requestFocus()
                }
                nameEdit.text.isNullOrEmpty() -> {
                    showToast(getString(R.string.user_input_error))
                    nameEdit.requestFocus()
                }
                else -> {
                    SharedPrefsUtils.putString(KEY_USER_NAME, nameEdit.text.toString(), this)
                    SharedPrefsUtils.putString(KEY_CODE, codeEdit.text.toString(), this)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}
