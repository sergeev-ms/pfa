package com.borets.pfa.entity.activity

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class WellEquip(private val id: String) : EnumClass<String> {
    BORETS("B"),
    COMPETITOR("C"),
    NONE("N");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): WellEquip? = WellEquip.values().find { it.id == id }
    }
}