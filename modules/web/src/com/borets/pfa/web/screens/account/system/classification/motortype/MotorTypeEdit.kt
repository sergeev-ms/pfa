package com.borets.pfa.web.screens.account.system.classification.motortype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.MotorType

@UiController("pfa_MotorType.edit")
@UiDescriptor("motor-type-edit.xml")
@EditedEntityContainer("motorTypeDc")
@LoadDataBeforeShow
class MotorTypeEdit : StandardEditor<MotorType>()