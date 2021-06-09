package com.borets.pfa.web.screens.account.system.classification.pumpconfig

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.PumpConfig

@UiController("pfa_PumpConfig.browse")
@UiDescriptor("pump-config-browse.xml")
@LookupComponent("pumpConfigsTable")
@LoadDataBeforeShow
class PumpConfigBrowse : StandardLookup<PumpConfig>()