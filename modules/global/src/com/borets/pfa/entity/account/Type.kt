package com.borets.pfa.entity.account

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class Type(private val id: String) : EnumClass<String> {
    STRATEGIC("S"),
    KEY("K"),
    OTHER("O");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): Type? = Type.values().find { it.id == id }
    }
}