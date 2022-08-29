package com.borets.pfa.report.equipext.dto

import java.util.*

class ActivityStat(val accountId: String, val analyticId: String, val wellTag: String, val yearMonth: Date, val value: Int) {
    companion object {
//        const val COUNTRY_ID_COLUMN = "countryId"
        const val ACCOUNT_ID_COLUMN = "ACCOUNT_ID"
        const val ANALYTIC_COLUMN = "ANALYTIC_ID"
        const val WELL_TAG_COLUMN = "WELL_TAG"
        const val YEAR_MONTH_COLUMN = "YEAR_MONTH"
        const val VALUE_COLUMN = "VALUE_"
    }
}