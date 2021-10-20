package com.borets.pfa.datatype

import com.haulmont.chile.core.annotations.JavaClass
import com.haulmont.chile.core.datatypes.impl.BigDecimalDatatype
import org.dom4j.Element
import java.math.BigDecimal
import java.util.*


@JavaClass(BigDecimal::class)
class PercentageDatatype(element: Element) : BigDecimalDatatype(element) {
    override fun parse(value: String?): BigDecimal? {
        val result =
            if (!value.isNullOrEmpty()) value.replace("%", "")
            else value

        return super.parse(result)?.divide(BigDecimal(100))
    }

    override fun parse(value: String?, locale: Locale): BigDecimal? {
        val result =
            if (!value.isNullOrEmpty()) value.replace("%", "")
            else value
        return super.parse(result, locale)?.divide(BigDecimal(100))
    }

    override fun format(value: Any?): String {
        val format =
            if (value is BigDecimal) {
                super.format(value.multiply(BigDecimal(100)))
            } else super.format(value)

        return if (format.isNullOrEmpty()) format
        else "${format}%"
    }

    override fun format(value: Any?, locale: Locale): String {
        val format =
            if (value is BigDecimal) {
                super.format(value.multiply(BigDecimal(100)), locale)
            } else super.format(value)
        return if (format.isNullOrEmpty()) format
        else "$format%"
    }
}