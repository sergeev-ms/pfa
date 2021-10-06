package com.borets.pfa.web.screens.setting.countrysetting

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.setting.CountrySetting

@UiController("pfa_CountrySetting.browse")
@UiDescriptor("country-setting-browse.xml")
@LookupComponent("countrySettingsTable")
@LoadDataBeforeShow
class CountrySettingBrowse : StandardLookup<CountrySetting>()