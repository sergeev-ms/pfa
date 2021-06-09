package com.borets.pfa.entity.account.system.classification

import com.haulmont.chile.core.datatypes.impl.EnumClass

enum class WellCasingWeight(private val id: String) : EnumClass<String> {
    NINE("9"),
    NINE_POINT_FIVE("9.5"),
    TEN("10"),
    TEN_POINT_FIVE("10.5"),
    ELEVEN_POINT_FIVE("11.5"),
    ELEVEN_POINT_SIX("11.6"),
    TWELVE_POINT_SEVENTY_FIVE("12.75"),
    THIRTEEN("13"),
    FOURTEEN("14"),
    FIFTEEN("15"),
    FIFTEEN_POINT_FIVE("15.5"),
    SEVENTEEN("17"), TWENTY("20"),
    TWENTY_POINT_THREE("20.3"),
    TWENTY_THREE("23"),
    TWENTY_FOUR("24"),
    TWENTY_SIX("26"),
    TWENTY_SIX_POINT_FOUR("26.4"),
    TWENTY_SEVEN("27"),
    TWENTY_EIGHT("28"),
    TWENTY_NINE("29"),
    THIRTY_TWO("32"),
    THIRTY_THREE_POINT_SEVEN("33.7"),
    THIRTY_SIX("36"),
    FORTY("40"),
    FORTY_THREE("43"),
    FORTY_THREE_POINT_FIVE("43.5"),
    FORTY_FOUR("44"),
    FORTY_SEVEN("47"),
    FIFTY_THREE("53"),
    FIFTY_FOUR_POINT_FIVE("54.5");

    override fun getId() = id

    companion object {
        @JvmStatic
        fun fromId(id: String): WellCasingWeight? = WellCasingWeight.values().find { it.id == id }
    }
}