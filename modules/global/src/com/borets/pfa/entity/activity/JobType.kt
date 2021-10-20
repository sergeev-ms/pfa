package com.borets.pfa.entity.activity

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class JobType(private val id: String) : EnumClass<String> {
    INSTALL("I"),
    PULL("P"),
    ACTIVE_WELLS("AW"),
    WELL_CHECK("WCH"),
    WELL_MONITORING("WM");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): JobType? = JobType.values().find { it.id == id }
    }
}