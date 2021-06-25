package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.EquipmentUtilization
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.EntityStates
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.HasValue
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
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
    private lateinit var entityStates: EntityStates
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var dataManager: DataManager

    @Inject
    private lateinit var breakdownsDc: CollectionPropertyContainer<EquipmentUtilization>

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
        if (entityStates.isNew(editedEntity)) {
            createBreakdowns()
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


    private fun createBreakdowns() {
        val breakdowns = dataManager.load(EquipmentType::class.java)
            .query("order by e.order")
            .list()
            .map {
                dataContext.create(EquipmentUtilization::class.java).apply {
                    this.applicationData = editedEntity
                    this.equipmentType = it
                }
            }
        breakdownsDc.mutableItems.addAll(breakdowns)
    }
}