package com.borets.pfa.web.screens.setting.countrysetting

import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.price.RevenueType
import com.borets.pfa.entity.setting.CountrySetting
import com.borets.pfa.entity.setting.CountrySettingAnalyticDetail
import com.borets.pfa.entity.setting.CountrySettingEquipmentType
import com.borets.pfa.entity.setting.CountrySettingRevenueType
import com.haulmont.cuba.core.global.Sort
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_CountrySetting.edit")
@UiDescriptor("country-setting-edit.xml")
@EditedEntityContainer("countrySettingDc")
@LoadDataBeforeShow
class CountrySettingEdit : StandardEditor<CountrySetting>() {
    @Inject
    private lateinit var screenBuilders: ScreenBuilders
    @Inject
    private lateinit var dataContext: DataContext

    @Inject
    private lateinit var analyticSettingsDc: CollectionPropertyContainer<CountrySettingAnalyticDetail>
    @Inject
    private lateinit var revenueTypeSettingsDc: CollectionPropertyContainer<CountrySettingRevenueType>

    @javax.inject.Inject
    private lateinit var equipmentTypeSettingsDc: com.haulmont.cuba.gui.model.CollectionPropertyContainer<com.borets.pfa.entity.setting.CountrySettingEquipmentType>

    @Subscribe("analyticSettingsTable.add")
    private fun onAnalyticSettingsTableAdd(event: Action.ActionPerformedEvent) {
        screenBuilders.lookup(AnalyticSet::class.java, this)
            .withOpenMode(OpenMode.NEW_TAB)
            .withSelectHandler {
                it.map {
                    dataContext.create(CountrySettingAnalyticDetail::class.java).apply {
                        this.analyticSet = it
                        this.countrySetting = editedEntity
                    }
                }.toList().run {
                    analyticSettingsDc.mutableItems.addAll(this)
                }
            }.show()
    }

    @Subscribe("revenueTypeSettingsTable.add")
    private fun onRevenueTypeSettingsTableAdd(event: Action.ActionPerformedEvent) {
        screenBuilders.lookup(RevenueType::class.java, this)
            .withOpenMode(OpenMode.NEW_TAB)
            .withSelectHandler {
                it.onEach {
                    dataContext.create(CountrySettingRevenueType::class.java).apply {
                        countrySetting = editedEntity
                        revenueType = it
                    }.run {
                        revenueTypeSettingsDc.mutableItems.add(this)
                    }
                }
            }.show()
    }

    @Subscribe("equipmentTypeSettingsTable.add")
    private fun onEquipmentTypeSettingsTableAdd(event: Action.ActionPerformedEvent) {
        screenBuilders.lookup(EquipmentType::class.java, this)
            .withOpenMode(OpenMode.NEW_TAB)
            .withSelectHandler {
                it.onEach {
                    dataContext.create(CountrySettingEquipmentType::class.java).apply {
                        countrySetting = editedEntity
                        equipmentType = it
                        order = it.order
                        mandatory = it.mandatory
                    }.run {
                        equipmentTypeSettingsDc.mutableItems.add(this)
                    }
                }
            }.show()
    }
}