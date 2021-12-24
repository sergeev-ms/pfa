package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.marketdata.RunsNumber
import com.borets.pfa.entity.account.system.System
import com.borets.pfa.entity.account.system.SystemStd
import com.borets.pfa.web.screens.account.system.copyToSystem
import com.borets.pfa.web.screens.account.system.reloadForCopy
import com.borets.pfa.web.screens.account.system.systemstd.SystemStdEdit
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.actions.list.CreateAction
import com.haulmont.cuba.gui.actions.list.EditAction
import com.haulmont.cuba.gui.actions.list.RemoveAction
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.DataGrid
import com.haulmont.cuba.gui.components.ValidationErrors
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import javax.inject.Inject
import javax.inject.Named


@UiController("pfa_ApplicationDataFragment")
@UiDescriptor("application-data-fragment.xml")
class ApplicationDataFragment : ScreenFragment() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var screenBuilders: ScreenBuilders
    @Inject
    private lateinit var messageBundle: MessageBundle

    @Inject
    private lateinit var applicationDataDc: InstanceContainer<ApplicationData>
    @Inject
    private lateinit var systemsAllocationDc: CollectionPropertyContainer<SystemAllocation>

    @Inject
    private lateinit var systemsAllocationGrid: DataGrid<SystemAllocation>

    @field:Named("systemsAllocationGrid.create")
    private lateinit var systemsAllocationGridCreate: CreateAction<SystemAllocation>
    @field:Named("systemsAllocationGrid.remove")
    private lateinit var systemsAllocationGridRemove: RemoveAction<SystemAllocation>

    @field:Named("systemsAllocationGrid.edit")
    private lateinit var systemsAllocationGridEdit: EditAction<SystemAllocation>

    @javax.inject.Inject
    private lateinit var entityStates: com.haulmont.cuba.core.global.EntityStates


    @Subscribe("systemsAllocationGrid.create")
    private fun onSystemsAllocationGridCreate(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.lookup(SystemStd::class.java, this)
            .withSelectHandler {
                it.map { selectedStdSystem ->
                    dataContext.create(SystemAllocation::class.java).apply {
                        this.applicationData = applicationDataDc.item
                        this.system = selectedStdSystem.reloadForCopy(dataManager)
                            .copyToSystem<System>(dataManager)
                            .also { newSystem -> dataContext.merge(newSystem) }
                    }
                }.let { selectedSystemsWrap ->
                    systemsAllocationDc.mutableItems.addAll(selectedSystemsWrap)
                }
            }
            .show()
    }


    fun setEditable(editable : Boolean) {
        systemsAllocationGridCreate.isVisible = editable
        systemsAllocationGridRemove.isVisible = editable
        systemsAllocationGridEdit.isVisible = editable

        systemsAllocationGrid.isEditorEnabled = editable
    }

    @Subscribe(target = Target.PARENT_CONTROLLER)
    private fun onValidation(event: StandardEditor.ValidationEvent) {
        val errors: ValidationErrors = performSystemAllocationValidation()
        event.addErrors(errors)
    }

    private fun performSystemAllocationValidation(): ValidationErrors {
        val validationErrors = ValidationErrors()
        if (applicationDataDc.itemOrNull != null && entityStates.isNew(applicationDataDc.item)) {
            val systemAllocations = applicationDataDc.itemOrNull?.systemAllocations
            if (!systemAllocations.isNullOrEmpty()) {
                val runsNumber = applicationDataDc.item.account?.actualMarketDetail?.getRunsNumber()

                val sumRun1 = systemAllocations.sumOf { it.run1!! }
                if (sumRun1.toInt() != 1) {
                    validationErrors.add(
                        systemsAllocationGrid as Component,
                        messageBundle.formatMessage("systemsAllocationGrid.validationMessage", "1st Run")
                    )
                }

                if (runsNumber == RunsNumber.TWO || runsNumber == RunsNumber.THREE || runsNumber == RunsNumber.THREE_PLUS) {
                    val sumRun2 = systemAllocations.sumOf { it.run2!! }
                    if (sumRun2.toInt() != 1) {
                        validationErrors.add(
                            systemsAllocationGrid as Component,
                            messageBundle.formatMessage("systemsAllocationGrid.validationMessage", "2st Run")
                        )
                    }
                }
            }
        }
        return validationErrors
    }

    @Subscribe("systemsAllocationGrid.view")
    private fun onSystemsAllocationGridView(event: Action.ActionPerformedEvent) {
        val screen = screenBuilders.editor(SystemStd::class.java, this)
            .withScreenClass(SystemStdEdit::class.java)
            .editEntity(systemsAllocationGrid.singleSelected!!.system!!)
            .build()
        (screen as ReadOnlyAwareScreen).isReadOnly = true
        screen.show()
    }

    @Subscribe("systemsAllocationGrid.edit")
    private fun onSystemsAllocationGridEdit(event: Action.ActionPerformedEvent) {
        screenBuilders.editor(SystemStd::class.java, this)
            .withParentDataContext(dataContext)
            .withScreenClass(SystemStdEdit::class.java)
            .editEntity(systemsAllocationGrid.singleSelected!!.system!!)
            .show()
    }
}