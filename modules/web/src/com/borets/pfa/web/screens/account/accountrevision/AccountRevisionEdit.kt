package com.borets.pfa.web.screens.account.accountrevision

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.AccountRevision
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.HasValue
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@UiController("pfa_AccountRevision.edit")
@UiDescriptor("account-revision-edit.xml")
@EditedEntityContainer("accountRevisionDc")
@LoadDataBeforeShow
class AccountRevisionEdit : StandardEditor<AccountRevision>() {
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