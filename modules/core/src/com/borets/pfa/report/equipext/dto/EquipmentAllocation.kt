package com.borets.pfa.report.equipext.dto

import com.borets.pfa.report.custom.Column
import com.borets.pfa.report.custom.IsColumn
import java.math.BigDecimal

class EquipmentAllocation : IsColumn {
    var applicationDataId: String? = null
    var systemId: String? = null
    val allocationValueTypeItem: UtilizationValueTypeItem
    val value: BigDecimal

    constructor(
        applicationDataId: String,
        systemId: String,
        utilizationValueTypeId: String,
        utilizationValueTypeName: String,
        utilizationValueTypeVariableName: String,
        utilizationValueTypeOrder: Int,
        value: BigDecimal
    ) {
        this.applicationDataId = applicationDataId
        this.systemId = systemId
        this.allocationValueTypeItem = UtilizationValueTypeItem(utilizationValueTypeId,
            utilizationValueTypeName,
            utilizationValueTypeVariableName,
            utilizationValueTypeOrder)
        this.value = value
    }

    constructor(
        utilizationValueTypeItem: UtilizationValueTypeItem,
        value: BigDecimal
    ) {
        this.allocationValueTypeItem = utilizationValueTypeItem
        this.value = value
    }

    companion object {
        const val APPLICATION_DATA_ID_COLUMN = "applicationDataId"
        const val SYSTEM_ID_COLUMN = "systemId"
        const val UTILIZATION_VALUE_TYPE_ID_COLUMN = "utilizationValueTypeId"
        const val UTILIZATION_VALUE_TYPE_NAME_COLUMN = "utilizationValueTypeName"
        const val UTILIZATION_VALUE_TYPE_VARIABLE_COLUMN = "utilizationValueTypeName"
        const val UTILIZATION_VALUE_TYPE_ORDER_COLUMN = "utilizationValueTypeOrder"
        const val VALUE_COLUMN = "value"
        const val COLUMN_TYPE = "allocation"
    }

    override fun getColumn(): Column {
        return Column(COLUMN_TYPE, allocationValueTypeItem.typeName, allocationValueTypeItem.typeId)
    }
}