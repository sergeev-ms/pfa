package com.borets.pfa.web.screens.setting.countrysetting

import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.setting.CountrySetting
import com.borets.pfa.entity.setting.CountrySettingAnalyticDetail
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
}