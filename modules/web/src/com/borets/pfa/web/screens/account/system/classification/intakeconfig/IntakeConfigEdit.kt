package com.borets.pfa.web.screens.account.system.classification.intakeconfig

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.IntakeConfig

@UiController("pfa_IntakeConfig.edit")
@UiDescriptor("intake-config-edit.xml")
@EditedEntityContainer("intakeConfigDc")
@LoadDataBeforeShow
class IntakeConfigEdit : StandardEditor<IntakeConfig>()