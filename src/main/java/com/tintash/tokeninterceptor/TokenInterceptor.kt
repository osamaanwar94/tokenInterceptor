package com.tintash.tokeninterceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor for adding auth token in requests
 *
 * Requires the methods to be annotated with @HEADER(AUTH_REQUIRED) annotation
 */
class TokenInterceptor(
    private val HEADER_AUTHORIZATION_KEY: String,
    private val tokenProvider: TokenProvider
) : Interceptor {

    /**
     * Intercepts the network call
     *
     * Header annotations are part of the OkHttp Api
     * Uses a custom header for identifying which requests require an auth token
     *
     * Only adds the token if the method was annotated and token is not null
     *
     * This solutions is based on an answer here
     * {@link <a href="https://stackoverflow.com/questions/37757520/retrofit-2-elegant-way-of-adding-headers-in-the-api-level">SO link</a>}
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        val customAnnotations = request.headers().values(AUTH_REQUIRED_KEY)

        if (customAnnotations.isNotEmpty()) {
            requestBuilder.removeHeader(AUTH_REQUIRED_KEY)
            requestBuilder.addHeader(HEADER_AUTHORIZATION_KEY, tokenProvider.getToken())
        }

        return chain.proceed(requestBuilder.build())
    }

    /**
     *
     */
    interface TokenProvider {
        fun getToken(): String
    }

    companion object {
        /**
         *
         */
        const val AUTH_REQUIRED_KEY = "Auth"
    }
}