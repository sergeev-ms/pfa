package com.borets.pfa.report.equipext.dto

import com.borets.pfa.report.custom.Column
import com.borets.pfa.report.custom.IsColumn

class CountrySettingsAllocationRemapItem : IsColumn {

    val countryId: String
    val utilizationValueTypeItem: UtilizationValueTypeItem
    val remapScript: String

    constructor(countryId: String,
                utilizationValueTypeId: String,
                utilizationValueTypeName: String,
                utilizationValueTypeOrder: Int,
                remapScript: String) {
        this.countryId = countryId
        this.utilizationValueTypeItem = UtilizationValueTypeItem(utilizationValueTypeId, utilizationValueTypeName, utilizationValueTypeOrder)
        this.remapScript = remapScript
    }

    companion object {
        const val COUNTRY_ID_COLUMN = "countryId"
        const val UTILIZATION_VALUE_TYPE_ID_COLUMN = "utilizationValueTypeId"
        const val UTILIZATION_VALUE_TYPE_NAME_COLUMN = "utilizationValueTypeName"
        const val UTILIZATION_VALUE_TYPE_ORDER_COLUMN = "utilizationValueTypeOrder"
        const val REMAP_SCRIPT_COLUMN = "remapScript"
        const val COLUMN_TYPE = "allocation"
    }

    override fun getColumn(): Column {
        return Column(COLUMN_TYPE, utilizationValueTypeItem.typeName, utilizationValueTypeItem.typeId)
    }
}