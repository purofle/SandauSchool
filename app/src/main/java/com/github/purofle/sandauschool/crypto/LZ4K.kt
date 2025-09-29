package com.github.purofle.sandauschool.crypto


/**
 * from https://github.com/ZenLiuCN/lz-string4k
 */

import kotlin.math.pow


typealias call = () -> Unit

object LZ4K {
    private const val KEY_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
    private const val KEY_STR_URI = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-$"


    private data class Data(
        var value: Char = '0',
        var position: Int = 0,
        var index: Int = 1
    )

    private inline fun String.charLess256(yes: call, no: call) {
        if (this[0].code < 256) {
            yes.invoke()
        } else {
            no.invoke()
        }
    }

    private fun Int.power() = 1 shl this
    private fun compressInternal(source: String, bitsPerChar: Int, getCharFromInt: (code: Int) -> Char): String {
        var contextC: String
        var value: Int
        var contextW = ""
        var contextWC: String
        val contextDictionary = mutableMapOf<String, Int>()
        val contextDictionaryToCreate = mutableMapOf<String, Boolean>()
        var contextEnlargeIn = 2.0 // Compensate for the first entry which should not count
        var contextDictSize = 3
        val contextData = mutableListOf<Char>()
        var contextNumBits = 2
        var contextDataVal = 0
        var contextDataPosition = 0
        fun minusEnlargeIn() {
            contextEnlargeIn--
            if (contextEnlargeIn == 0.0) {
                contextEnlargeIn = 2.0.pow(contextNumBits.toDouble())
                contextNumBits++
            }
        }

        fun checkPostition() {
            if (contextDataPosition == bitsPerChar - 1) {
                contextDataPosition = 0
                contextData.add(getCharFromInt(contextDataVal))
                contextDataVal = 0
            } else {
                contextDataPosition++
            }
        }

        fun processContextWordInCreateDictionary() {
            contextW.charLess256({
                repeat(contextNumBits) {
                    contextDataVal = contextDataVal shl 1
                    checkPostition()
                }
                value = contextW[0].code
                repeat(8) {
                    contextDataVal = contextDataVal shl 1 or (value and 1)
                    checkPostition()
                    value = value shr 1

                }
            }) {
                value = 1
                repeat(contextNumBits) {
                    contextDataVal = contextDataVal shl 1 or value
                    checkPostition()
                    value = 0
                }
                value = contextW[0].code
                repeat(16) {
                    contextDataVal = contextDataVal shl 1 or (value and 1)
                    checkPostition()
                    value = value shr 1
                }
            }
            minusEnlargeIn()
            contextDictionaryToCreate.remove(contextW)
        }

        fun processContextWord() {
            if (contextDictionaryToCreate.containsKey(contextW)) {
                processContextWordInCreateDictionary()
            } else {
                value = contextDictionary[contextW]!! //not be empty?
                repeat(contextNumBits) {
                    contextDataVal = contextDataVal shl 1 or (value and 1)
                    checkPostition()
                    value = value shr 1
                }
            }
            minusEnlargeIn()
        }
        source.forEach {
            contextC = it.toString()
            //char in dictionary
            contextDictionary[contextC] ?: run {
                contextDictionary[contextC] = contextDictSize++
                contextDictionaryToCreate[contextC] = true
            }
            contextWC = contextW + contextC
            if (contextDictionary.contains(contextWC)) {
                contextW = contextWC
            } else {
                processContextWord()
                // Add wc to the dictionary.
                contextDictionary[contextWC] = contextDictSize++
                contextW = contextC
            }
        }
        // Output the code for w.
        if (contextW.isNotBlank()) {
            processContextWord()
        }
        // Mark the end of the stream
        value = 2
        repeat(contextNumBits) {
            contextDataVal = contextDataVal shl 1 or (value and 1)
            if (contextDataPosition == bitsPerChar - 1) {
                contextDataPosition = 0
                contextData.add(getCharFromInt(contextDataVal))
                contextDataVal = 0
            } else {
                contextDataPosition++
            }
            value = value shr 1
        }
        // Flush the last char
        while (true) {
            contextDataVal = contextDataVal shl 1
            if (contextDataPosition == bitsPerChar - 1) {
                contextData.add(getCharFromInt(contextDataVal))
                break
            } else
                contextDataPosition++
        }
        return contextData.joinToString("")
    }

    private val Int.string get() = this.toChar().toString()
    private fun decompressInternal(length: Int, resetValue: Int, getNextValue: (idx: Int) -> Char): String? {
        val builder = StringBuilder()
        val dictionary = mutableListOf(0.string, 1.string, 2.string)
        var bits = 0
        var maxPower: Int
        var power: Int
        val data = Data(getNextValue(0), resetValue, 1)
        var resb: Int
        var c = ""
        var w: String
        var entry: String
        var numBits = 3
        var enlargeIn = 4
        var dictSize = 4
        var next: Int
        fun doPower(initBits: Int, initPower: Int, initMaxPowerFactor: Int, mode: Int = 0) {
            bits = initBits
            maxPower = initMaxPowerFactor.power()
            power = initPower
            while (power != maxPower) {
                resb = data.value.code and data.position
                data.position = data.position shr 1
                if (data.position == 0) {
                    data.position = resetValue
                    data.value = getNextValue(data.index++)
                }
                bits = bits or (if (resb > 0) 1 else 0) * power
                power = power shl 1
            }
            when (mode) {
                0 -> Unit
                1 -> c = bits.string
                2 -> {
                    dictionary.add(dictSize++, bits.string)
                    next = (dictSize - 1)
                    enlargeIn--
                }
            }
        }

        fun checkEnlargeIn() {
            if (enlargeIn == 0) {
                enlargeIn = numBits.power()
                numBits++
            }
        }
        doPower(bits, 1, 2)
        next = bits
        when (next) {
            0 -> doPower(0, 1, 8, 1)
            1 -> doPower(0, 1, 16, 1)
            2 -> return ""
        }
        dictionary.add(3, c)
        w = c
        builder.append(w)
        while (true) {
            if (data.index > length) {
                return ""
            }
            doPower(0, 1, numBits)
            next = bits
            when (next) {
                0 -> doPower(0, 1, 8, 2)
                1 -> doPower(0, 1, 16, 2)
                2 -> return builder.toString()
            }
            checkEnlargeIn()
            entry = when {
                dictionary.size > next -> dictionary[next]
                next == dictSize -> w + w[0]
                else -> return null
            }
            builder.append(entry)
            // Add w+entry[0] to the dictionary.
            dictionary.add(dictSize++, w + entry[0])
            enlargeIn--
            w = entry
            checkEnlargeIn()
        }


    }


    fun decompressFromBase64(input: String) = when {
        input.isBlank() -> null
        else -> decompressInternal(input.length, 32) {
            KEY_STR.indexOf(input[it]).toChar()
        }
    }
}