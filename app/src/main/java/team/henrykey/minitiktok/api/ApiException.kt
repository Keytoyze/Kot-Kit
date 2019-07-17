/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.api

import android.util.AndroidRuntimeException

class ApiException: AndroidRuntimeException {
    constructor() : super()
    constructor(name: String?) : super(name)
    constructor(name: String?, cause: Throwable?) : super(name, cause)
    constructor(cause: Exception?) : super(cause)
}