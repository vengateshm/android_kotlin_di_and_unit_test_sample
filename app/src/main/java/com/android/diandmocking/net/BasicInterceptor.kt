package com.android.diandmocking.net

import com.android.diandmocking.util.CONTENT_TYPE
import com.android.diandmocking.util.MEDIA_TYPE
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class BasicInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) = chain.run {
        proceed(request().newBuilder().addHeader(CONTENT_TYPE, MEDIA_TYPE).build())
    }
}