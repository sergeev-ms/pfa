package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.EquipmentUtilization
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.ButtonsPanel
import com.haulmont.cuba.gui.components.DataGrid
import com.haulmont.cuba.gui.components.Table
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
//    @Inject
//    private lateinit var uiComponents: UiComponents

    @Inject
    private lateinit var applicationDataDc: InstanceContainer<ApplicationData>
    @Inject
    private lateinit var systemsAllocationDc: CollectionPropertyContainer<SystemAllocation>

    @Inject
    private lateinit var systemsAllocationGrid: DataGrid<SystemAllocation>
    @Inject
    private lateinit var systemsAllocationGridPanel: ButtonsPanel
    @Inject
    private lateinit var utilizationTable: Table<EquipmentUtilization>

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

        systemsAllocationGrid.isEditorEnabled = false

        utilizationTable.isEditable = editable
    }

//    @Subscribe
//    private fun onInit(event: InitEvent) {
//        systemsAllocationGrid.addGeneratedColumn("run1", object : DataGrid.ColumnGenerator<SystemAllocation, Component> {
//            override fun getValue(event: DataGrid.ColumnGeneratorEvent<SystemAllocation>): Component {
//                @Suppress("UnstableApiUsage")
//                return uiComponents.create(TextField.TYPE_BIGDECIMAL).apply {
//                    this.setWidthFull()
//                    this.setFormatter(NumberFormatter())
//                    this.value = event.item.run1
//                    this.addValueChangeListener { changeEvent ->
//                        event.item.run1 = changeEvent.value
//                    }
//                }
//            }
//
//            override fun getType(): Class<Component> {
//                return Component::class.java
//            }
//        })
//    }
}