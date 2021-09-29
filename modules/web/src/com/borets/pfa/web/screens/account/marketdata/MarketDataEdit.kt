package com.borets.pfa.web.screens.account.marketdata

import com.borets.pfa.entity.account.marketdata.MarketData
import com.haulmont.cuba.gui.components.Button
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.HasValue
import com.haulmont.cuba.gui.screen.*
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import javax.inject.Named
import kotlin.math.roundToInt

@UiController("pfa_MarketData.edit")
@UiDescriptor("market-data-edit.xml")
@EditedEntityContainer("marketDataDc")
@LoadDataBeforeShow
class MarketDataEdit : StandardEditor<MarketData>() {
    @field:Named("marketDateFragment.yearMonthField")
    private lateinit var yearMonthField: DatePicker<Date>

    @Subscribe
    private fun onAfterShow(@Suppress("UNUSED_PARAMETER") event: AfterShowEvent) {
        editedEntity.getYearMonth()?.let {
            yearMonthField.value = Date.from(it
                .atDay(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        }
    }

    @Subscribe("marketDateFragment.yearMonthField")
    private fun onYearMonthFieldValueChange(event: HasValue.ValueChangeEvent<Date>) {
        event.value.let {
            var yearMonth: YearMonth? = null
            if (it != null)
                yearMonth = YearMonth.from(it.toInstant().atZone(ZoneId.systemDefault()))
            editedEntity.setYearMonth(yearMonth)
        }
    }

    @Subscribe("marketDateFragment.calculateCustomerDataBtn")
    private fun onCalculateCustomerDataBtnClick(event: Button.ClickEvent) {
        calcCustomersPullsInYear()
        calcCustomerRunbackInYear()
        calcCustomerInstallInYear()
        calcCustomerWellsClosing()
    }

    private fun calcCustomerWellsClosing() {
        val wellCount = editedEntity.wellCount!!
        val customerPullsInYear = editedEntity.customerPullsInYear!!
        val customerInstallInYear = editedEntity.customerInstallInYear!!
        editedEntity.customerWellsClosingInYear = wellCount - customerPullsInYear + customerInstallInYear
    }

    private fun calcCustomerInstallInYear() {
        val newWellYear = editedEntity.newWellYear!!
        val customerRunbackInYear = editedEntity.customerRunbackInYear!!
        editedEntity.customerInstallInYear = newWellYear + customerRunbackInYear
    }

    private fun calcCustomerRunbackInYear() {
        val wellCount = editedEntity.wellCount!!.toFloat()
        val conversionRate = editedEntity.conversionRate!!.toFloat()
        val arl = editedEntity.arl!!.toFloat()
        val delayFactor = editedEntity.delayFactor!!.toFloat()
        val result =
            if (arl / 365 < 1) {
                (wellCount - (wellCount * conversionRate)) * 365 / arl *
                        (1 - (1 - delayFactor) * arl / 365)
            } else {
                (wellCount - (wellCount * conversionRate)) * 365 / arl *
                        delayFactor
            }
        editedEntity.customerRunbackInYear = result.roundToInt()
    }

    private fun calcCustomersPullsInYear() {
        val wellCount = editedEntity.wellCount!!.toFloat()
        val conversionRate = editedEntity.conversionRate!!.toFloat()
        val arl = editedEntity.arl!!.toFloat()
        val result = (wellCount - (wellCount * conversionRate)) * 365 / arl +
                (wellCount * conversionRate)
        editedEntity.customerPullsInYear = result.roundToInt()

    }
}