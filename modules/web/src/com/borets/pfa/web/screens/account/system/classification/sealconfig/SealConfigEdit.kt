package com.borets.pfa.web.screens.account.system.classification.sealconfig

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.SealConfig

@UiController("pfa_SealConfig.edit")
@UiDescriptor("seal-config-edit.xml")
@EditedEntityContainer("sealConfigDc")
@LoadDataBeforeShow
class SealConfigEdit : StandardEditor<SealConfig>()