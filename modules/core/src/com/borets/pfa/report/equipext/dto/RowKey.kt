package com.borets.pfa.report.equipext.dto

class RowKey(val accountId: String, val equipmentTypeId: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RowKey

        if (accountId != other.accountId) return false
        if (equipmentTypeId != other.equipmentTypeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accountId.hashCode()
        result = 31 * result + equipmentTypeId.hashCode()
        return result
    }
}