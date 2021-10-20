package com.borets.pfa.web.screens.setting.countrysetting

import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationValueType
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.price.RevenueType
import com.borets.pfa.entity.setting.*
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
    @Inject
    private lateinit var equipmentTypeSettingsDc: CollectionPropertyContainer<CountrySettingEquipmentType>
    @Inject
    private lateinit var utilizationValueTypeSettingsDc: CollectionPropertyContainer<CountrySettingUtilizationValueType>

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

    @Subscribe("utilizationValueTypeSettingsTable.add")
    private fun onUtilizationValueTypeSettingsTableAdd(event: Action.ActionPerformedEvent) {
        screenBuilders.lookup(EquipmentUtilizationValueType::class.java, this)
            .withOpenMode(OpenMode.NEW_TAB)
            .withSelectHandler {
                it.onEach {
                    dataContext.create(CountrySettingUtilizationValueType::class.java).apply {
                        countrySetting = editedEntity
                        utilizationValueType = it
                    }.run {
                        utilizationValueTypeSettingsDc.mutableItems.add(this)
                    }
                }
            }.show()
    }
}