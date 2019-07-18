/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import team.henrykey.minitiktok.R
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


    }
}
