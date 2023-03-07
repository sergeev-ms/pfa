package com.borets.pfa.web.screens.dataadmin

import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.borets.pfa.web.screens.account.system.systemstd.SystemStdEdit
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.GroupTable
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_SystemDetail.browse")
@UiDescriptor("system-detail-browse.xml")
@LookupComponent("systemDetailsTable")
@LoadDataBeforeShow
class SystemDetailBrowse : StandardLookup<SystemDetail>() {

    @Inject
    private lateinit var systemDetailsTable: GroupTable<SystemDetail>

    @Inject
    private lateinit var screenBuilders: ScreenBuilders

    @Subscribe("systemDetailsTable.view")
    private fun onSystemDetailsTableView(event: Action.ActionPerformedEvent) {
        systemDetailsTable.singleSelected?.let {
            screenBuilders.editor(SystemStd::class.java, this)
                .withScreenClass(SystemStdEdit::class.java)
                .withOpenMode(OpenMode.NEW_TAB)
                .editEntity(it.system!!)
                .show()
        }
    }
}