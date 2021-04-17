package com.borets.pfa.entity.activity

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class ContractType(private val id: String) : EnumClass<String> {
    SALE("S"),
    RENTAL("R");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): ContractType? = ContractType.values().find { it.id == id }
    }
}