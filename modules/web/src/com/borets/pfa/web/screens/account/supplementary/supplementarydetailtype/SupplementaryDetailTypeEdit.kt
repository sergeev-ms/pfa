package com.borets.pfa.web.screens.account.supplementary.supplementarydetailtype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.supplementary.SupplementaryDetailType

@UiController("pfa_SupplementaryDetailType.edit")
@UiDescriptor("supplementary-detail-type-edit.xml")
@EditedEntityContainer("supplementaryDetailTypeDc")
@LoadDataBeforeShow
class SupplementaryDetailTypeEdit : StandardEditor<SupplementaryDetailType>()