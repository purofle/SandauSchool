package com.github.purofle.sandauschool.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 校园一卡通余额数据
 */
@Serializable
data class YktData(
    val data: CardBalanceData,
    val code: String
)

@Serializable
data class CardBalanceData(
    @SerialName("YE") val balance: Double
)