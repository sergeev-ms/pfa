package com.borets.pfa.report.equip.dto

import java.math.BigDecimal

class EquipmentItem(
    val equipmentSystem: EquipmentSystem,
    val partNumber: String,
    val productDescription: String,
    val qty: BigDecimal,
    val uom: String,
    val value1stRun: BigDecimal,
    val valueNextRuns: BigDecimal,
    val equipmentTypeOrder: Int,
    val revenueMode: String,
    val activityValues: MutableList<ActivityValue> = mutableListOf(),
    val periodValues: MutableList<PeriodValue> = mutableListOf(),
) {

    companion object {
        const val PART_NUMBER = "partNumber"
        const val PRODUCT_DESCRIPTION = "productDescription"
        const val QTY = "qty"
        const val UOM = "uom"
        const val FIRST_RUN = "valueFirstRun"
        const val NEXT_RUNS = "valueNextRuns"
        const val EQUIPMENT_TYPE_ORDER = "equipmentTypeOrder"
        const val REVENUE_MODE = "revenueMode"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EquipmentItem

        if (equipmentSystem != other.equipmentSystem) return false
        if (partNumber != other.partNumber) return false
        if (productDescription != other.productDescription) return false
        if (uom != other.uom) return false

        return true
    }

    override fun hashCode(): Int {
        var result = equipmentSystem.hashCode()
        result = 31 * result + partNumber.hashCode()
        result = 31 * result + productDescription.hashCode()
        result = 31 * result + uom.hashCode()
        return result
    }


}