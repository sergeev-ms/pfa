package com.borets.pfa.web.screens.account.system.classification.materials

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.Materials

@UiController("pfa_Materials.edit")
@UiDescriptor("materials-edit.xml")
@EditedEntityContainer("materialsDc")
@LoadDataBeforeShow
class MaterialsEdit : StandardEditor<Materials>()