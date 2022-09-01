package com.borets.pfa.report.equipext.dto

class UtilizationValueTypeItem (val typeId :String,
                                val typeName: String,
                                val variableName: String?,
                                val order : Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UtilizationValueTypeItem

        if (typeId != other.typeId) return false
        if (typeName != other.typeName) return false
        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int {
        var result = typeId.hashCode()
        result = 31 * result + typeName.hashCode()
        result = 31 * result + order
        return result
    }

    override fun toString(): String {
        return buildString {
            append("UtilizationValueType(utilizationValueTypeId='")
            append(typeId)
            append("', utilizationValueTypeName='")
            append(typeName)
            append("', utilizationValueTypeVariableName=")
            append(variableName)
            append("', utilizationValueTypeOrder=")
            append(order)
            append(")")
        }
    }


}