package com.github.sandauschool.crypt

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.value
import platform.CoreCrypto.CCCrypt
import platform.CoreCrypto.kCCAlgorithmDES
import platform.CoreCrypto.kCCBlockSizeDES
import platform.CoreCrypto.kCCEncrypt
import platform.CoreCrypto.kCCOptionPKCS7Padding
import platform.CoreCrypto.kCCSuccess
import platform.posix.size_tVar

@OptIn(ExperimentalForeignApi::class)
actual fun desEncrypt(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
    require(key.size == 8) { "DES key size must be 8 bytes." }
    require(iv.size == 8) { "DES IV size must be 8 bytes." }

    val output = ByteArray(data.size + kCCBlockSizeDES.toInt())

    return memScoped {
        val outMoved = alloc<size_tVar>()

        val status = CCCrypt(
            op = kCCEncrypt,
            alg = kCCAlgorithmDES,
            options = kCCOptionPKCS7Padding,
            key = key.refTo(0),
            keyLength = key.size.toULong(),
            iv = iv.refTo(0),
            dataIn = data.refTo(0),
            dataInLength = data.size.toULong(),
            dataOut = output.refTo(0),
            dataOutAvailable = output.size.toULong(),
            dataOutMoved = outMoved.ptr
        )

        if (status != kCCSuccess) {
            error("CCCrypt failed with status=$status")
        }

        // Ensure the output is the correct size
        output.copyOf(outMoved.value.toInt())
    }
}