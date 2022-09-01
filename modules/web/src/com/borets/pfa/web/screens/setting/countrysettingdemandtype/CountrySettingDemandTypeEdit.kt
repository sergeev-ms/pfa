package com.borets.pfa.web.screens.setting.countrysettingdemandtype

import com.borets.pfa.entity.setting.CountrySettingDemandType
import com.haulmont.cuba.gui.screen.*

@UiController("pfa_CountrySettingDemandType.edit")
@UiDescriptor("country-setting-demand-type-edit.xml")
@EditedEntityContainer("countrySettingDemandTypeDc")
@LoadDataBeforeShow
class CountrySettingDemandTypeEdit : StandardEditor<CountrySettingDemandType>()