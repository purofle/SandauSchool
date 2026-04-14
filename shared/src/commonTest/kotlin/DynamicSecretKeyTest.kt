package com.github.sandauschool.crypt

import com.github.sandauschool.data.CpdailyInfo
import com.github.sandauschool.data.DynamicSecretKeyRequest
import com.github.sandauschool.utils.StringUtils.toBase64
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json
import network.api.createCampusDailyAPI
import kotlin.test.*

class DynamicSecretKeyTest {
    @Test
    fun `test getDynamicSecretKey`() {
        val ktorfit = Ktorfit.Builder().baseUrl("https://mobile.campushoy.com/").build()

        val api = ktorfit.createCampusDailyAPI()

    }
}