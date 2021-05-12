package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.cuba.gui.components.ButtonsPanel
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.screen.ScreenFragment
import com.haulmont.cuba.gui.screen.UiController
import com.haulmont.cuba.gui.screen.UiDescriptor
import javax.inject.Inject

@UiController("pfa_ApplicationDataFragment")
@UiDescriptor("application-data-fragment.xml")
class ApplicationDataFragment : ScreenFragment() {
    @Inject
    private lateinit var systemsTable: Table<SystemStd>

    @Inject
    private lateinit var systemsTableBtnPanel: ButtonsPanel

    fun setEditable(editable : Boolean) {
        systemsTableBtnPanel.isVisible = editable
        systemsTable.actions.forEach {
            it.isVisible = editable
        }
    }
}