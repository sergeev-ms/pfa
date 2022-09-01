package com.borets.pfa.report.equipext.dto

import com.borets.pfa.report.custom.Column
import com.borets.pfa.report.custom.IsColumn
import java.math.BigDecimal
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class EquipmentDemandItem (val demandTypeId: String,
                           val demandTypeName: String,
                           val date: Date,
                           val value: BigDecimal) : IsColumn {


    override fun getColumn(): Column {
        return Column(DEMAND_COLUMN_TYPE, formatDate(date), formatColumnId(demandTypeId, date))
    }

    companion object {
        const val DEMAND_COLUMN_TYPE = "demand"

        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM")
            .withZone(ZoneId.systemDefault())

        fun formatDate(date: Date): String {
            return formatter.format(date.toInstant())
        }

        fun formatColumnId(id: String, date: Date) : String {
            return id + ":" + formatDate(date)
        }

//        fun formatColumnType(id: String, date: Date) : String {
//            return id + ":" + formatDate(date)
//        }
    }
}