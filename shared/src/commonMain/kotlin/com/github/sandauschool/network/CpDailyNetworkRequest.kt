package com.github.sandauschool.network

import com.github.sandauschool.crypt.desEncrypt
import com.github.sandauschool.data.CpdailyInfo
import com.github.sandauschool.data.DynamicSecretKeyRequest
import com.github.sandauschool.network.api.createCampusDailyAPI
import com.github.sandauschool.utils.StringUtils.toBase64
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.headers
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object CpDailyNetworkRequest {

    val cpdailyInfo: String by lazy {
        val campusDailyInfo = CpdailyInfo(
            deviceId = "26991875-B37A-4CB5-92E6-5228C89EE566"
        )

        desEncrypt(
            data = Json.encodeToString(campusDailyInfo).toByteArray(),
            key = "XCE927==".toByteArray(),
            iv = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08),
        ).toBase64()
    }

    val myClient = HttpClient {
        defaultRequest {
            headers {
                set("CpdailyClientType", "CPDAILY")
                set("CpdailyStandAlone", "0")
                set("CpdailyInfo", cpdailyInfo)
            }
        }
    }

    val ktorfit = Ktorfit.Builder().httpClient(myClient).build()


    @OptIn(ExperimentalUuidApi::class)
    suspend fun getDynamicSecretKey() {
        val api = ktorfit.createCampusDailyAPI()
        val randomUUID = Uuid.random()
        api.getDynamicSecretKey(
            DynamicSecretKeyRequest(
                private = "${randomUUID}|first_v4",
                sign = "",
            )
        )
    }
}