package com.github.purofle.sandauschool.network

import kotlinx.serialization.Serializable

@Serializable
data class SerializableCookie(
    val name: String,
    val value: String,
    val domain: String,
    val path: String,
    val expiresAt: Long,
    val secure: Boolean,
    val httpOnly: Boolean,
    val hostOnly: Boolean
) {
    companion object {
        fun fromOkHttp(cookie: okhttp3.Cookie): SerializableCookie =
            SerializableCookie(
                name = cookie.name,
                value = cookie.value,
                domain = cookie.domain,
                path = cookie.path,
                expiresAt = cookie.expiresAt,
                secure = cookie.secure,
                httpOnly = cookie.httpOnly,
                hostOnly = cookie.hostOnly
            )

        fun toOkHttp(serializable: SerializableCookie): okhttp3.Cookie {
            val builder = okhttp3.Cookie.Builder()
                .name(serializable.name)
                .value(serializable.value)
                .path(serializable.path)
                .expiresAt(serializable.expiresAt)

            if (serializable.hostOnly) {
                builder.hostOnlyDomain(serializable.domain)
            } else {
                builder.domain(serializable.domain)
            }
            if (serializable.secure) builder.secure()
            if (serializable.httpOnly) builder.httpOnly()

            return builder.build()
        }
    }
}