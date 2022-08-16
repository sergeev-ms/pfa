package com.borets.pfa.report.equipext.dto

/**
 * Equipment System.
 */
class EquipmentSystem(
    val referenceCode: String,
    val customer: String,
    val customerOrder: Int,
    val rentalOrSale: String,
    val systemNumber: Long,
    val equipmentItems: MutableList<EquipmentItem> = mutableListOf()
) {
    companion object {
        const val REFERENCE_CODE = "referenceCode"
        const val CUSTOMER = "customer"
        const val CUSTOMER_ORDER = "customerOrder"
        const val RENTAL_OR_SALE = "rentalOrSale"
        const val SYSTEM_NUMBER = "systemNumber"
    }

    constructor(referenceCode: String, customer: String, customerOrder: Int, rentalOrSale: String, systemNumber: Long) :
            this(referenceCode, customer, customerOrder, rentalOrSale, systemNumber, mutableListOf())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EquipmentSystem

        if (referenceCode != other.referenceCode) return false
        if (customer != other.customer) return false
        if (rentalOrSale != other.rentalOrSale) return false
        if (systemNumber != other.systemNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = referenceCode.hashCode()
        result = 31 * result + customer.hashCode()
        result = 31 * result + rentalOrSale.hashCode()
        result = 31 * result + systemNumber.hashCode()
        return result
    }


}