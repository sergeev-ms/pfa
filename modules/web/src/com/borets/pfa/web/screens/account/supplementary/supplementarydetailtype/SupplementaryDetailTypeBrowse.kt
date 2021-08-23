package com.borets.pfa.web.screens.account.supplementary.supplementarydetailtype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.supplementary.SupplementaryDetailType

@UiController("pfa_SupplementaryDetailType.browse")
@UiDescriptor("supplementary-detail-type-browse.xml")
@LookupComponent("supplementaryDetailTypesTable")
@LoadDataBeforeShow
class SupplementaryDetailTypeBrowse : StandardLookup<SupplementaryDetailType>()