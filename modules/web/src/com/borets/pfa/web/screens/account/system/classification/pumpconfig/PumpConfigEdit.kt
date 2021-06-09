package com.borets.pfa.web.screens.account.system.classification.pumpconfig

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.PumpConfig

@UiController("pfa_PumpConfig.edit")
@UiDescriptor("pump-config-edit.xml")
@EditedEntityContainer("pumpConfigDc")
@LoadDataBeforeShow
class PumpConfigEdit : StandardEditor<PumpConfig>()