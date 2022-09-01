package com.borets.pfa.report.equipext.dto

import java.math.BigDecimal

class EquipmentItem(
    val equipmentSystem: EquipmentSystem,
    val applicationDataId: String,
    val equipmentTypeId: String,
    val equipmentType: String,
    val partNumber: String,
    val productDescription: String,
    val qty: BigDecimal,
    val uom: String,
    val value1stRun: BigDecimal,
    val valueNextRuns: BigDecimal,
    val equipmentTypeOrder: Int
) : Comparable<EquipmentItem> {

    val rowKey: RowKey
    var equipmentAllocations: List<EquipmentAllocation>
    val equipmentUtilizations: MutableList<EquipmentUtilizationItem>
    val equipmentDemands: MutableList<EquipmentDemandItem>

    init {
        this.rowKey = RowKey(equipmentSystem.accountId, equipmentTypeId)
        this.equipmentUtilizations =  mutableListOf()
        this.equipmentAllocations = mutableListOf()
        this.equipmentDemands = mutableListOf()
    }

    companion object {
        const val APPLICATION_DATA_ID = "APPLICATION_DATA_ID"
        const val PART_NUMBER = "partNumber"
        const val PRODUCT_DESCRIPTION = "productDescription"
        const val QTY = "qty"
        const val UOM = "uom"
        const val FIRST_RUN = "valueFirstRun"
        const val NEXT_RUNS = "valueNextRuns"
        const val EQUIPMENT_TYPE_ORDER = "equipmentTypeOrder"
        const val EQUIPMENT_TYPE = "equipmentType"
        const val EQUIPMENT_TYPE_ID_COLUMN = "EQUIPMENT_TYPE_ID"
    }

    override fun compareTo(other: EquipmentItem): Int {
        val thisOrder = this.equipmentSystem.customerOrder * 100 +
                this.equipmentTypeOrder
        val otherOrder = other.equipmentSystem.customerOrder * 100 +
                other.equipmentTypeOrder

        return if (thisOrder < otherOrder) -1 else if (thisOrder == otherOrder) 0 else 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EquipmentItem

        if (equipmentSystem != other.equipmentSystem) return false
        if (applicationDataId != other.applicationDataId) return false
        if (equipmentTypeId != other.equipmentTypeId) return false
        if (equipmentType != other.equipmentType) return false
        if (partNumber != other.partNumber) return false
        if (productDescription != other.productDescription) return false
        if (qty != other.qty) return false
        if (uom != other.uom) return false
        if (value1stRun != other.value1stRun) return false
        if (valueNextRuns != other.valueNextRuns) return false
        if (equipmentTypeOrder != other.equipmentTypeOrder) return false

        return true
    }

    override fun hashCode(): Int {
        var result = equipmentSystem.hashCode()
        result = 31 * result + applicationDataId.hashCode()
        result = 31 * result + equipmentType.hashCode()
        result = 31 * result + equipmentTypeId.hashCode()
        result = 31 * result + partNumber.hashCode()
        result = 31 * result + productDescription.hashCode()
        result = 31 * result + qty.hashCode()
        result = 31 * result + uom.hashCode()
        result = 31 * result + value1stRun.hashCode()
        result = 31 * result + valueNextRuns.hashCode()
        result = 31 * result + equipmentTypeOrder
        return result
    }


}