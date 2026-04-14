package com.github.sandauschool.crypt

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

actual fun desEncrypt(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
    val key = SecretKeySpec(key, "DES")
    val iv = IvParameterSpec(iv)

    val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key, iv)

    val encrypted = cipher.doFinal(data)

    return encrypted
}