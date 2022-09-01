package com.borets.pfa.report.equipext.dto

import com.borets.pfa.report.custom.Column
import com.borets.pfa.report.custom.IsColumn

class CountrySettingsAllocationRemapItem(
    countryId: String,
    utilizationValueTypeId: String,
    utilizationValueTypeName: String,
    utilizationValueTypeVariableName: String?,
    utilizationValueTypeOrder: Int,
    val remapScript: String
) : IsColumn {

    val utilizationValueTypeItem: UtilizationValueTypeItem

    init {
        this.utilizationValueTypeItem = UtilizationValueTypeItem(
            utilizationValueTypeId,
            utilizationValueTypeName,
            utilizationValueTypeVariableName,
            utilizationValueTypeOrder)
    }

    companion object {
        const val COUNTRY_ID_COLUMN = "countryId"
        const val UTILIZATION_VALUE_TYPE_ID_COLUMN = "utilizationValueTypeId"
        const val UTILIZATION_VALUE_TYPE_NAME_COLUMN = "utilizationValueTypeName"
        const val UTILIZATION_VALUE_TYPE_VARIABLE_NAME_COLUMN = "variableName"
        const val UTILIZATION_VALUE_TYPE_ORDER_COLUMN = "utilizationValueTypeOrder"
        const val REMAP_SCRIPT_COLUMN = "remapScript"
        const val COLUMN_TYPE = "allocation"
    }

    override fun getColumn(): Column {
        return Column(COLUMN_TYPE, utilizationValueTypeItem.typeName, utilizationValueTypeItem.typeId)
    }
}