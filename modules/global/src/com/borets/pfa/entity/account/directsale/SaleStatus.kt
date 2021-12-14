package com.borets.pfa.entity.account.directsale

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class SaleStatus(private val id: String) : EnumClass<String> {
    ACTIVE("A"),
    WON("W"),
    VOID("V"),
    LOST("L");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): SaleStatus? = SaleStatus.values().find { it.id == id }
    }
}