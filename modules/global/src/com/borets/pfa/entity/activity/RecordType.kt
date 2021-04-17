package com.borets.pfa.entity.activity

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class RecordType(private val id: String) : EnumClass<String> {
    KPI("KPI"),
    Q1("Q1"),
    Q2("Q2"),
    Q3("Q3"),
    Q4("Q3"),
    FORECAST("FC");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): RecordType? = RecordType.values().find { it.id == id }
    }
}