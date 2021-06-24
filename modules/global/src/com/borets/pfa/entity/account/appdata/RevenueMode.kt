package com.borets.pfa.entity.account.appdata

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class RevenueMode(private val id: String) : EnumClass<String> {
    SALE("S"),
    RENTAL("R"),
    CO("CO");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): RevenueMode? = RevenueMode.values().find { it.id == id }
    }
}