package com.borets.pfa.web.screens.demand.demandtype

import com.borets.pfa.entity.demand.DemandType
import com.haulmont.cuba.gui.screen.*

@UiController("pfa_DemandType.edit")
@UiDescriptor("demand-type-edit.xml")
@EditedEntityContainer("demandTypeDc")
@LoadDataBeforeShow
class DemandTypeEdit : StandardEditor<DemandType>()