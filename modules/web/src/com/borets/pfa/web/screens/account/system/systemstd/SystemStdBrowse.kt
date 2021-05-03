package com.borets.pfa.web.screens.account.system.systemstd

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.SystemStd

@UiController("pfa_SystemStd.browse")
@UiDescriptor("system-std-browse.xml")
@LookupComponent("systemStdsTable")
@LoadDataBeforeShow
class SystemStdBrowse : StandardLookup<SystemStd>()