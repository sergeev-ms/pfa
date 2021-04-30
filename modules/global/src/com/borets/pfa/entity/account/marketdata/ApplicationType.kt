package com.borets.pfa.entity.account.marketdata

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class ApplicationType(private val id: String) : EnumClass<String> {
    CONVENTIONAL("C"),
    UNCONVENTIONAL("U"),
    WATER_PRODUCTION("W"),
    CO2("CO2"),
    SAGD("S");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): ApplicationType? = ApplicationType.values().find { it.id == id }
    }
}