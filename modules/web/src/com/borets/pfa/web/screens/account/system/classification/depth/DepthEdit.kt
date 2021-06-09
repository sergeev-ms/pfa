package com.borets.pfa.web.screens.account.system.classification.depth

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.Depth

@UiController("pfa_Depth.edit")
@UiDescriptor("depth-edit.xml")
@EditedEntityContainer("depthDc")
@LoadDataBeforeShow
class DepthEdit : StandardEditor<Depth>()