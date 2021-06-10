package com.borets.pfa.web.screens.account.system.classification.pumpmaterials

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.PumpMaterials

@UiController("pfa_PumpMaterials.edit")
@UiDescriptor("pump-materials-edit.xml")
@EditedEntityContainer("pumpMaterialsDc")
@LoadDataBeforeShow
class PumpMaterialsEdit : StandardEditor<PumpMaterials>()