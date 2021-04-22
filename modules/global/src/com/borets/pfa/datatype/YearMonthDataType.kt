package com.borets.pfa.datatype

import com.haulmont.chile.core.annotations.JavaClass
import com.haulmont.chile.core.datatypes.Datatype
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@JavaClass(String::class)
class YearMonthDataType : Datatype<YearMonth?> {
    private val pattern = "yyyy-MM"

    override fun format(value: Any?): String {
        return if (value == null) ""
        else YearMonth.parse(value.toString()).format(DateTimeFormatter.ofPattern(pattern))
    }

    override fun format(value: Any?, locale: Locale): String {
        return if (value == null) ""
        else YearMonth.parse(value.toString())
                .format(DateTimeFormatter.ofPattern(pattern, locale))
    }

    override fun parse(value: String?): YearMonth? {
        return if (!value.isNullOrEmpty()){
            YearMonth.parse(value)
        } else null
    }

    override fun parse(value: String?, locale: Locale): YearMonth? {
        return parse(value)
    }
}