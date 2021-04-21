package com.borets.pfa.entity.activity

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class WellTag(private val id: String) : EnumClass<String> {
    NEW("N"),
    SECOND_RUN("SR");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): WellTag? = WellTag.values().find { it.id == id }
    }
}