package com.borets.pfa.web.screens.account.system.classification.othermaterials

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.OtherMaterials

@UiController("pfa_OtherMaterials.edit")
@UiDescriptor("other-materials-edit.xml")
@EditedEntityContainer("otherMaterialsDc")
@LoadDataBeforeShow
class OtherMaterialsEdit : StandardEditor<OtherMaterials>()