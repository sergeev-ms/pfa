package com.borets.pfa.web.screens.account.system.classification.vaproconfig

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.VaproConfig

@UiController("pfa_VaproConfig.edit")
@UiDescriptor("vapro-config-edit.xml")
@EditedEntityContainer("vaproConfigDc")
@LoadDataBeforeShow
class VaproConfigEdit : StandardEditor<VaproConfig>()