package com.borets.pfa.datatype

import com.haulmont.chile.core.annotations.JavaClass
import com.haulmont.chile.core.datatypes.Datatype
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.UserSessionSource
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@JavaClass(YearMonth::class)
class YearMonthDataType : Datatype<YearMonth?> {
    override fun format(value: Any?): String {
        return if (value == null) ""
        else YearMonth.parse(value.toString()).format(DateTimeFormatter.ofPattern("MMM yy"))
    }

    override fun format(value: Any?, locale: Locale): String {
        return if (value == null) ""
        else YearMonth.parse(value.toString())
                .format(DateTimeFormatter.ofPattern("MMM yy", locale))
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