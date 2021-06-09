package com.borets.pfa.web.screens.account.system.classification.pumptype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.PumpType

@UiController("pfa_PumpType.edit")
@UiDescriptor("pump-type-edit.xml")
@EditedEntityContainer("pumpTypeDc")
@LoadDataBeforeShow
class PumpTypeEdit : StandardEditor<PumpType>()