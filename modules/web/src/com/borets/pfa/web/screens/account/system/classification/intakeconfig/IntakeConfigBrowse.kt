package com.borets.pfa.web.screens.account.system.classification.intakeconfig

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.IntakeConfig

@UiController("pfa_IntakeConfig.browse")
@UiDescriptor("intake-config-browse.xml")
@LookupComponent("intakeConfigsTable")
@LoadDataBeforeShow
class IntakeConfigBrowse : StandardLookup<IntakeConfig>()