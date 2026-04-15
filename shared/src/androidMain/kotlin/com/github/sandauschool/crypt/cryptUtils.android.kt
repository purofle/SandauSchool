package com.github.sandauschool.crypt

import java.security.KeyFactory
import java.security.MessageDigest
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

actual fun desEncrypt(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
    val key = SecretKeySpec(key, "DES")
    val iv = IvParameterSpec(iv)

    val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key, iv)

    return cipher.doFinal(data)
}

actual fun sumMD5(data: ByteArray): ByteArray {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(data)
    return digest
}

actual fun rsaEncrypt(data: ByteArray, publicKey: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    val spec = X509EncodedKeySpec(publicKey)
    val publicKey = KeyFactory.getInstance("RSA").generatePublic(spec)
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)

    return cipher.doFinal(data)
}