package com.borets.pfa.web.screens.account.system.classification.motortype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.MotorType

@UiController("pfa_MotorType.browse")
@UiDescriptor("motor-type-browse.xml")
@LookupComponent("motorTypesTable")
@LoadDataBeforeShow
class MotorTypeBrowse : StandardLookup<MotorType>()