package com.borets.pfa.report.equipext.dto

import java.math.BigDecimal

class EquipmentItem(
    val equipmentSystem: EquipmentSystem,
    val equipmentType: String,
    val partNumber: String,
    val productDescription: String,
    val qty: BigDecimal,
    val uom: String,
    val value1stRun: BigDecimal,
    val valueNextRuns: BigDecimal,
    val valueNextRunsCompetitor: BigDecimal,
    val valuePullFirstRun: BigDecimal,
    val valuePullNextRuns: BigDecimal,
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
        const val NEXT_RUNS_COMP = "valueNextRunsCompetitor"
        const val PULL_FIRST_RUN = "valuePullFirstRun"
        const val PULL_NEXT_RUNS = "valuePullNextRuns"
        const val EQUIPMENT_TYPE_ORDER = "equipmentTypeOrder"
        const val REVENUE_MODE = "revenueMode"
        const val EQUIPMENT_TYPE = "equipmentType"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EquipmentItem

        if (equipmentSystem != other.equipmentSystem) return false
        if (equipmentType != other.equipmentType) return false
        if (partNumber != other.partNumber) return false
        if (productDescription != other.productDescription) return false
        if (qty != other.qty) return false
        if (uom != other.uom) return false
        if (value1stRun != other.value1stRun) return false
        if (valueNextRuns != other.valueNextRuns) return false
        if (equipmentTypeOrder != other.equipmentTypeOrder) return false
        if (revenueMode != other.revenueMode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = equipmentSystem.hashCode()
        result = 31 * result + equipmentType.hashCode()
        result = 31 * result + partNumber.hashCode()
        result = 31 * result + productDescription.hashCode()
        result = 31 * result + qty.hashCode()
        result = 31 * result + uom.hashCode()
        result = 31 * result + value1stRun.hashCode()
        result = 31 * result + valueNextRuns.hashCode()
        result = 31 * result + equipmentTypeOrder
        result = 31 * result + revenueMode.hashCode()
        return result
    }


}