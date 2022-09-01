package com.borets.pfa.report.equipext.dto

import com.borets.pfa.report.custom.Column
import com.borets.pfa.report.custom.IsColumn
import java.math.BigDecimal

class EquipmentUtilizationItem(
    val accountId: String,
    val equipmentTypeId: String,
    val revenueModeId: String,
    utilizationValueTypeId: String,
    utilizationValueTypeName: String,
    utilizationValueTypeVariableName: String,
    utilizationValueTypeOrder: Int,
    val value: BigDecimal
) : IsColumn {
    val utilizationValueTypeItem: UtilizationValueTypeItem
    val rowKey: RowKey
    var equipmentItem : EquipmentItem? = null

    init {
        this.utilizationValueTypeItem = UtilizationValueTypeItem(utilizationValueTypeId,
            utilizationValueTypeName,
            utilizationValueTypeVariableName,
            utilizationValueTypeOrder)
        this.rowKey = RowKey(accountId, equipmentTypeId)
    }

    companion object {
        const val UTIL_ACCOUNT_ID_COLUMN = "ACCOUNT_ID"
        const val UTIL_EQUIPMENT_TYPE_ID_COLUMN = "EQUIPMENT_TYPE_ID"
        const val UTIL_REVENUE_MODE_COLUMN = "REVENUE_MODE"
        const val UTIL_VALUE_TYPE_ID_COLUMN = "VALUE_TYPE_ID"
        const val UTIL_VALUE_TYPE_NAME_COLUMN = "VALUE_TYPE_NAME"
        const val UTIL_VALUE_TYPE_VARIABLE_NAME_COLUMN = "VARIABLE_NAME"
        const val UTIL_VALUE_TYPE_ORDER_COLUMN = "VALUE_TYPE_ORDER"
        const val UTIL_VALUE_COLUMN = "VALUE_"
        const val COLUMN_TYPE = "utilization"
        const val REVENUE_TYPE_COLUMN_TYPE = "revenueType"
    }

    override fun getColumn(): Column {
        return Column(COLUMN_TYPE, utilizationValueTypeItem.typeName, utilizationValueTypeItem.typeId)
    }
}