package com.borets.pfa.entity.account.system

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class WellCasingSize(private val id: String) : EnumClass<String> {
    FOUR_AND_HALF("4 1/2"),
    FIVE("5"),
    FIVE_AND_HALF("5 1/2"),
    SIX_AND_FIVE_EIGHTS("6 5/8"),
    SEVEN("7"),
    SEVEN_AND_FIVE_EIGHTS("7 5/8"),
    EIGHT_AND_FIVE_EIGHTS("8 5/8"),
    NINE_AND_FIVE_EIGHTS("9 5/8"),
    NINE_AND_SEVEN_EIGHTS("9 7/8"),
    THIRTEEN_AND_THREE_EIGHTS("13 3/8");

    override fun getId() = id

    companion object {
        @JvmStatic
        fun fromId(id: String): WellCasingSize? = WellCasingSize.values().find { it.id == id }
    }
}