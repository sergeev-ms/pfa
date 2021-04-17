package com.borets.pfa.entity.activity

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class JobType(private val id: String) : EnumClass<String> {
    INSTALL("I"),
    PULL("P");

    override fun getId() = id

    companion object {

        @JvmStatic
        fun fromId(id: String): JobType? = JobType.values().find { it.id == id }
    }
}