package com.borets.pfa.report.equipext.dto

class DemandRuleItem(val id: String, val name: String, val script: String) {
    companion object {
        const val DEMAND_TYPE_ID_COLUMN = "demandTypeId"
        const val DEMAND_TYPE_NAME_COLUMN = "demandTypeName"
        const val DEMAND_SCRIPT_COLUMN = "script"
    }
}