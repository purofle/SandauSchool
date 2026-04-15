package com.github.sandauschool.crypt

expect fun desEncrypt(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray

expect fun rsaEncrypt(data: ByteArray, publicKey: ByteArray): ByteArray

expect fun sumMD5(data: ByteArray): ByteArray