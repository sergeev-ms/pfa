package com.borets.pfa.web.screens.account.system.classification.vaproconfig

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.VaproConfig

@UiController("pfa_VaproConfig.browse")
@UiDescriptor("vapro-config-browse.xml")
@LookupComponent("vaproConfigsTable")
@LoadDataBeforeShow
class VaproConfigBrowse : StandardLookup<VaproConfig>()