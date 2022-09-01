package com.borets.pfa.report.equipext.dto

import java.util.*

class ActivityStatItem(val accountId: String,
                       val analyticId: String,
                       val jobType: String,
                       val wellTag: String,
                       val variableName: String?,
                       val yearMonth: Date,
                       val value: Int) {
    companion object {
//        const val COUNTRY_ID_COLUMN = "countryId"
        const val ACCOUNT_ID_COLUMN = "ACCOUNT_ID"
        const val ANALYTIC_COLUMN = "ANALYTIC_ID"
        const val WELL_TAG_COLUMN = "WELL_TAG"
        const val JOB_TYPE_COLUMN = "JOB_TYPE"
        const val VARIABLE_NAME_COLUMN = "VARIABLE_NAME"
        const val YEAR_MONTH_COLUMN = "YEAR_MONTH"
        const val VALUE_COLUMN = "VALUE_"
    }
}