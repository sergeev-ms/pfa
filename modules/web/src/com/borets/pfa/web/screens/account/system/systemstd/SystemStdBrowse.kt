package com.borets.pfa.web.screens.account.system.systemstd

import com.borets.pfa.entity.account.system.SystemStd
import com.borets.pfa.web.screens.account.system.copyToSystem
import com.borets.pfa.web.screens.account.system.reloadForCopy
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.GroupTable
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_SystemStd.browse")
@UiDescriptor("system-std-browse.xml")
@LookupComponent("systemStdsTable")
@LoadDataBeforeShow
class SystemStdBrowse : StandardLookup<SystemStd>() {
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var screenBuilders: ScreenBuilders

    @Inject
    private lateinit var systemStdsTable: GroupTable<SystemStd>

    @Subscribe("systemStdsTable.copy")
    private fun onSystemStdsTableCopy(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        var newStdSystem = systemStdsTable.singleSelected!!
            .reloadForCopy(dataManager)
            .copyToSystem<SystemStd>(dataManager)

        screenBuilders.editor(systemStdsTable)
            .editEntity(newStdSystem)
            .show()
    }
}