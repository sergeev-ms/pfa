package com.borets.pfa.web.screens.setting.countrysetting

import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationValueType
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.price.RevenueType
import com.borets.pfa.entity.setting.*
import com.borets.pfa.web.screens.analytic.analyticset.AnalyticSetBrowse
import com.haulmont.cuba.core.global.MetadataTools
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
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
    private lateinit var metadataTools: MetadataTools

    @Inject
    private lateinit var analyticSettingsDc: CollectionPropertyContainer<CountrySettingAnalyticDetail>
    @Inject
    private lateinit var revenueTypeSettingsDc: CollectionPropertyContainer<CountrySettingRevenueType>
    @Inject
    private lateinit var equipmentTypeSettingsDc: CollectionPropertyContainer<CountrySettingEquipmentType>
    @Inject
    private lateinit var utilizationValueTypeSettingsDc: CollectionPropertyContainer<CountrySettingUtilizationValueType>

    @Subscribe("analyticSettingsTable.add")
    private fun onAnalyticSettingsTableAdd(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
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
    private fun onRevenueTypeSettingsTableAdd(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
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
    private fun onEquipmentTypeSettingsTableAdd(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
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
    private fun onUtilizationValueTypeSettingsTableAdd(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
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

    @Install(to = "revenueTypeSettingsTable.analyticSets", subject = "columnGenerator",
        target = Target.COMPONENT, type = Any::class, required = true)
    private fun revenueTypeSettingsTableAnalyticSetsColumnGenerator(countrySettingRevenueType: CountrySettingRevenueType?): Component {
        val analyticSetAsString = countrySettingRevenueType?.analyticSets
            ?.joinToString("; ") { metadataTools.getInstanceName(it) } ?: ""
        return Table.PlainTextCell(analyticSetAsString)
    }

    @Install(to = "utilizationValueTypeSettingsTable.analyticSets", subject = "columnGenerator")
    private fun utilizationValueTypeSettingsTableAnalyticSetsColumnGenerator(countrySettingUtilizationValueType: CountrySettingUtilizationValueType?): Component {
        val analyticSetAsString = countrySettingUtilizationValueType?.analyticSets
            ?.joinToString("; ") { metadataTools.getInstanceName(it) } ?: ""
        return Table.PlainTextCell(analyticSetAsString)
    }

    @Subscribe("revenueTypeSettingsTable.selectAnalytics")
    private fun onRevenueTypeSettingsTableSelectAnalytics(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        selectAnalytic(revenueTypeSettingsDc.item.analyticSets) {
            revenueTypeSettingsDc.item.analyticSets = it.toMutableList()
        }
    }

    private fun selectAnalytic(
        analyticSets: MutableList<AnalyticSet>?,
        selectHandler: (selected: MutableCollection<AnalyticSet>) -> Unit
    ) {
        val availableAnalytics = editedEntity.analyticSettings
            ?.map { it.analyticSet }
            ?.toList()
        screenBuilders.lookup(AnalyticSet::class.java, this)
            .withOpenMode(OpenMode.DIALOG)
            .withOptions(
                MapScreenOptions(
                    mapOf(
                        Pair(AnalyticSetBrowse.AVAILABLE_ANALYTICS_PARAM_NAME, availableAnalytics),
                        Pair(AnalyticSetBrowse.SELECTED_ANALYTICS_PARAM_NAME, analyticSets)
                    )
                )
            )
            .withSelectHandler(selectHandler)
            .show()
    }
}