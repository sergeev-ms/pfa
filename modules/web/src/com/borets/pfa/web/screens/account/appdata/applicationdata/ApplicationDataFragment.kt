package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.EquipmentUtilization
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.ButtonsPanel
import com.haulmont.cuba.gui.components.DataGrid
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.screen.ScreenFragment
import com.haulmont.cuba.gui.screen.Subscribe
import com.haulmont.cuba.gui.screen.UiController
import com.haulmont.cuba.gui.screen.UiDescriptor
import javax.inject.Inject

@UiController("pfa_ApplicationDataFragment")
@UiDescriptor("application-data-fragment.xml")
class ApplicationDataFragment : ScreenFragment() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var screenBuilders: ScreenBuilders


    @Inject
    private lateinit var applicationDataDc: InstanceContainer<ApplicationData>
    @Inject
    private lateinit var systemsAllocationDc: CollectionPropertyContainer<SystemAllocation>

    @Inject
    private lateinit var systemsAllocationGrid: DataGrid<SystemAllocation>
    @Inject
    private lateinit var systemsAllocationGridPanel: ButtonsPanel
    @Inject
    private lateinit var utilizationDg: DataGrid<EquipmentUtilization>


    @Subscribe("systemsAllocationGrid.create")
    private fun onSystemsAllocationGridCreate(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.lookup(SystemStd::class.java, this)
            .withSelectHandler {
                dataContext.create(SystemAllocation::class.java).apply {
                    this.applicationData = applicationDataDc.item
                    this.system = it.first()
                }.also {
                    systemsAllocationDc.mutableItems.add(it)
                }}
            .show()
    }


    fun setEditable(editable : Boolean) {
        systemsAllocationGridPanel.isVisible = editable
        systemsAllocationGrid.actions.forEach {
            it.isVisible = editable
        }

        systemsAllocationGrid.isEditorEnabled = editable
        utilizationDg.isEditorEnabled = editable
    }
}