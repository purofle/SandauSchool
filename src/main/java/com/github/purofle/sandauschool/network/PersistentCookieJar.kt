package com.github.purofle.sandauschool.network


import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar : CookieJar {

    private val cookieManager: CookieManager = CookieManager.getInstance()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val urlString = url.toString()
        cookies.forEach { cookie ->
            cookieManager.setCookie(urlString, cookie.toString())
        }
        cookieManager.flush()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val urlString = url.toString()
        val cookieString: String? = cookieManager.getCookie(urlString)

        return if (cookieString != null && cookieString.isNotEmpty()) {
            val cookieHeaders = cookieString.split(";")
            val cookies = mutableListOf<Cookie>()

            cookieHeaders.forEach { header ->
                Cookie.parse(url, header.trim())?.let { cookie ->
                    cookies.add(cookie)
                }
            }
            cookies
        } else {
            emptyList()
        }
    }
}
