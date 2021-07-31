package com.borets.pfa.web.screens.account.marketdata

import com.borets.pfa.entity.account.marketdata.MarketData
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.HasValue
import com.haulmont.cuba.gui.screen.*
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import javax.inject.Named

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

}