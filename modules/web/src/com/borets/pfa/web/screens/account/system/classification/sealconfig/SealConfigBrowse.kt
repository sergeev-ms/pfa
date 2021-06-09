package com.borets.pfa.web.screens.account.system.classification.sealconfig

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.SealConfig

@UiController("pfa_SealConfig.browse")
@UiDescriptor("seal-config-browse.xml")
@LookupComponent("sealConfigsTable")
@LoadDataBeforeShow
class SealConfigBrowse : StandardLookup<SealConfig>()