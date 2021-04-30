package com.borets.pfa.web.screens.account.marketdata

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.marketdata.MarketData
import com.borets.pfa.entity.account.marketdata.RunsNumber
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.HasValue
import com.haulmont.cuba.gui.components.TextField
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@UiController("pfa_MarketData.edit")
@UiDescriptor("market-data-edit.xml")
@EditedEntityContainer("marketDataDc")
@LoadDataBeforeShow
class MarketDataEdit : StandardEditor<MarketData>() {
    @Inject
    private lateinit var yearMonthField: DatePicker<Date>

    @Inject
    private lateinit var firstRunDurationField: TextField<Int>
    @Inject
    private lateinit var secondRunDurationField: TextField<Int>
    @Inject
    private lateinit var thirdRunDurationField: TextField<Int>
    @Inject
    private lateinit var thirdPlusRunDurationField: TextField<Int>

    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        editedEntity.getYearMonth()?.let {
            yearMonthField.value = Date.from(it
                .atDay(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        }
    }


    @Subscribe("yearMonthField")
    private fun onYearMonthFieldValueChange(event: HasValue.ValueChangeEvent<Date>) {
        event.value.let {
            var yearMonth: YearMonth? = null
            if (it != null)
                yearMonth = YearMonth.from(it.toInstant().atZone(ZoneId.systemDefault()))
            editedEntity.setYearMonth(yearMonth)
        }
    }

    @Subscribe("runsNumberField")
    private fun onRunsNumberFieldValueChange(event: HasValue.ValueChangeEvent<RunsNumber>) {
        secondRunDurationField.isVisible = event.value == RunsNumber.TWO || event.value == RunsNumber.THREE || event.value ==  RunsNumber.THREE_PLUS
        thirdRunDurationField.isVisible = event.value == RunsNumber.THREE || event.value ==  RunsNumber.THREE_PLUS
        thirdPlusRunDurationField.isVisible = event.value == RunsNumber.THREE_PLUS

    }
}