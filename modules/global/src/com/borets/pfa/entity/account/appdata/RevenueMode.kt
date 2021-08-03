package com.borets.pfa.entity.account.appdata

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class RevenueMode(private val id: String) : EnumClass<String> {
    SALE("S"),
    RENTAL("R"),
    CO("CO"),
    LTP3("LTP3"),
    LTP6("LTP6"),
    LTP12("LTP12");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): RevenueMode? = values().find { it.id == id }
    }
}