package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.marketdata.RunsNumber
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
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
    private lateinit var messageBundle: MessageBundle


    @Subscribe("systemsAllocationGrid.create")
    private fun onSystemsAllocationGridCreate(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.lookup(SystemStd::class.java, this)
            .withSelectHandler {
                it.map { selectedSystem ->
                    dataContext.create(SystemAllocation::class.java).apply {
                        this.applicationData = applicationDataDc.item
                        this.system = selectedSystem
                    }
                }.let { selectedSystemsWrap ->
                    systemsAllocationDc.mutableItems.addAll(selectedSystemsWrap)
                }
            }
            .show()
    }


    fun setEditable(editable : Boolean) {
        systemsAllocationGridPanel.isVisible = editable
        systemsAllocationGrid.actions.forEach {
            it.isVisible = editable
        }

        systemsAllocationGrid.isEditorEnabled = editable
    }

    @Subscribe(target = Target.PARENT_CONTROLLER)
    private fun onValidation(event: StandardEditor.ValidationEvent) {
        val errors: ValidationErrors = performSystemAllocationValidation()
        event.addErrors(errors)
    }

    private fun performSystemAllocationValidation(): ValidationErrors {
        val validationErrors = ValidationErrors()

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

            if (runsNumber == RunsNumber.THREE || runsNumber == RunsNumber.THREE_PLUS) {
                val sumRun3 = systemAllocations.sumOf { it.run3!! }
                if (sumRun3.toInt() != 1) {
                    validationErrors.add(
                        systemsAllocationGrid as Component,
                        messageBundle.formatMessage("systemsAllocationGrid.validationMessage", "3st Run")
                    )
                }
            }

            if (runsNumber == RunsNumber.THREE_PLUS) {
                val sumRun3plus = systemAllocations.sumOf { it.run3plus!! }
                if (sumRun3plus.toInt() != 1) {
                    validationErrors.add(
                        systemsAllocationGrid as Component,
                        messageBundle.formatMessage("systemsAllocationGrid.validationMessage", "3+ Run")
                    )
                }
            }
        }
        return validationErrors
    }


}