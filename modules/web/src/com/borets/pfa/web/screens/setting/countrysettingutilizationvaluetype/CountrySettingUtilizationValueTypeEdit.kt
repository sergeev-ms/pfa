package com.borets.pfa.web.screens.setting.countrysettingutilizationvaluetype

import com.borets.pfa.entity.setting.CountrySettingUtilizationValueType
import com.haulmont.cuba.gui.screen.*

@UiController("pfa_CountrySettingUtilizationValueType.edit")
@UiDescriptor("country-setting-utilization-value-type-edit.xml")
@EditedEntityContainer("countrySettingUtilizationValueTypeDc")
@LoadDataBeforeShow
class CountrySettingUtilizationValueTypeEdit : StandardEditor<CountrySettingUtilizationValueType>()