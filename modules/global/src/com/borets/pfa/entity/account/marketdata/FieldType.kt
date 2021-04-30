package com.borets.pfa.entity.account.marketdata

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class FieldType(private val id: String) : EnumClass<String> {
    LAND("L"),
    OFFSHORE("O");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): FieldType? = FieldType.values().find { it.id == id }
    }
}