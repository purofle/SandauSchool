package com.github.purofle.sandauschool.service

import com.github.purofle.sandauschool.crypto.LZ4K
import com.github.purofle.sandauschool.network.authService
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

data class AuthInfo(val salt: String, val execution: String)

object LoginService {
    private suspend fun getAuthServerHtml(): String {
        val rawHtml = authService.getLoginPage().string()
        return "var o='(.*?)'"
            .toRegex()
            .find(rawHtml)?.groupValues[1] ?:
            throw Exception("No o found")
    }

    private fun decompressHtml(compressedHtml: String): String {
        return LZ4K.decompressFromBase64(compressedHtml) ?: throw Exception("failed to decompress")
    }

    private fun getPwdEncryptSalt(html: String): String {
        return "id=\"pwdEncryptSalt\"\\s+value=\"([^\"]*)\""
            .toRegex()
            .find(html)?.groupValues[1] ?:
            throw Exception("failed to get pwdEncryptSalt")
    }

    private fun getExecution(html: String): String {
        return "name=\"execution\"\\s+value=\"([^\"]*)\""
            .toRegex()
            .find(html)?.groupValues[1] ?:
            throw Exception("no execution found")
    }

    suspend fun login(username: String, password: String): Response<ResponseBody> {
        val authInfo = getAuthInfo()
        val resp = authService.login(
            username,
            encryptPassword(password, authInfo.salt),
            execution = authInfo.execution
        )
        return resp
    }

    private fun randomString(length: Int): String {
        val aesChars = ('A'..'Z') + ('a'..'z') + ('1'..'8')
        return (1..length)
            .map { aesChars.random() }
            .joinToString("")
    }

    fun encryptPassword(password: String, key: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            ENCRYPT_MODE,
            SecretKeySpec(key.encodeToByteArray(), "AES"),
            IvParameterSpec(randomString(16).encodeToByteArray())
        )

        val encrypted = cipher.doFinal((randomString(64) + password).encodeToByteArray())

        return Base64.encodeToByteArray(encrypted).decodeToString()
    }

    suspend fun getAuthInfo(): AuthInfo {
        val compressedHtml = getAuthServerHtml()
        val html = decompressHtml(compressedHtml)
        val salt = getPwdEncryptSalt(html)
        val execution = getExecution(html)
        return AuthInfo(salt, execution)
    }

    private fun String.encodeUtf8(): String =
        URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

    const val AUTH_PAGE_URL =
        "https://authserver.sandau.edu.cn/authserver/login?service=https%3A%2F%2Fnewehall.sandau.edu.cn%2Fywtb-portal%2Fofficial%2Findex.html%3Fbrowser%3Dno%23%2Fhome%2Fofficial_home"

    const val TAG = "LoginService"
}