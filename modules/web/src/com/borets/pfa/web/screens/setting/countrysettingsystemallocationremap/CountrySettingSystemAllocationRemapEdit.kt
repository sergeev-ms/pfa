package com.borets.pfa.web.screens.setting.countrysettingsystemallocationremap

import com.borets.pfa.entity.setting.CountrySettingSystemAllocationRemap
import com.haulmont.cuba.gui.screen.*

@UiController("pfa_CountrySettingSystemAllocationRemap.edit")
@UiDescriptor("country-setting-system-allocation-remap-edit.xml")
@EditedEntityContainer("countrySettingSystemAllocationRemapDc")
@LoadDataBeforeShow
class CountrySettingSystemAllocationRemapEdit : StandardEditor<CountrySettingSystemAllocationRemap>()