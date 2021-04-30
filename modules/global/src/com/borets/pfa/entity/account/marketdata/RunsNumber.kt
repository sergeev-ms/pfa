package com.borets.pfa.entity.account.marketdata

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class RunsNumber(private val id: String) : EnumClass<String> {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    THREE_PLUS("3+");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): RunsNumber? = RunsNumber.values().find { it.id == id }
    }
}