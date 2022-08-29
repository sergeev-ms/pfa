package com.borets.pfa.report.equipext.dto

import com.borets.pfa.report.custom.Column
import com.borets.pfa.report.custom.IsColumn
import java.math.BigDecimal

class EquipmentAllocation : IsColumn {
    var applicationDataId: String? = null
    var systemId: String? = null
    val utilizationValueTypeItem: UtilizationValueTypeItem
    val value: BigDecimal

    constructor(
        applicationDataId: String,
        systemId: String,
        utilizationValueTypeId: String,
        utilizationValueTypeName: String,
        utilizationValueTypeOrder: Int,
        value: BigDecimal
    ) {
        this.applicationDataId = applicationDataId
        this.systemId = systemId
        this.utilizationValueTypeItem = UtilizationValueTypeItem(utilizationValueTypeId, utilizationValueTypeName, utilizationValueTypeOrder)
        this.value = value
    }

    constructor(
        applicationDataId: String,
        systemId: String,
        utilizationValueTypeItem: UtilizationValueTypeItem,
        value: BigDecimal
    ) {
        this.applicationDataId = applicationDataId
        this.systemId = systemId
        this.utilizationValueTypeItem = utilizationValueTypeItem
        this.value = value
    }

    constructor(
        utilizationValueTypeItem: UtilizationValueTypeItem,
        value: BigDecimal
    ) {
        this.utilizationValueTypeItem = utilizationValueTypeItem
        this.value = value
    }

    companion object {
        const val APPLICATION_DATA_ID_COLUMN = "applicationDataId"
        const val SYSTEM_ID_COLUMN = "systemId"
        const val UTILIZATION_VALUE_TYPE_ID_COLUMN = "utilizationValueTypeId"
        const val UTILIZATION_VALUE_TYPE_NAME_COLUMN = "utilizationValueTypeName"
        const val UTILIZATION_VALUE_TYPE_ORDER_COLUMN = "utilizationValueTypeOrder"
        const val VALUE_COLUMN = "value"
        const val COLUMN_TYPE = "allocation"
    }

    override fun getColumn(): Column {
        return Column(COLUMN_TYPE, utilizationValueTypeItem.typeName, utilizationValueTypeItem.typeId)
    }
}