package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.HasValue
import com.haulmont.cuba.gui.screen.*
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@UiController("pfa_ApplicationData.edit")
@UiDescriptor("application-data-edit.xml")
@EditedEntityContainer("applicationDataDc")
@LoadDataBeforeShow
class ApplicationDataEdit : StandardEditor<ApplicationData>() {
    @Inject
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

    @Subscribe("yearMonthField")
    private fun onYearMonthFieldValueChange(event: HasValue.ValueChangeEvent<Date>) {
        event.value.let {
            var yearMonth: YearMonth? = null
            if (it != null)
                yearMonth = YearMonth.from(it.toInstant().atZone(ZoneId.systemDefault()))
            editedEntity.setYearMonth(yearMonth)
        }
    }

}