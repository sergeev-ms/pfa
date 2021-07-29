package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.appdata.SystemAllocation
import com.borets.pfa.entity.account.marketdata.RunsNumber
import com.borets.pfa.entity.account.system.System
import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.borets.pfa.web.screens.account.system.systemstd.SystemStdEdit
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.View
import com.haulmont.cuba.core.global.ViewBuilder
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.actions.list.CreateAction
import com.haulmont.cuba.gui.actions.list.RemoveAction
import com.haulmont.cuba.gui.components.*
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

    @Inject
    private lateinit var dataManager: DataManager


    @Subscribe("systemsAllocationGrid.create")
    private fun onSystemsAllocationGridCreate(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        screenBuilders.lookup(SystemStd::class.java, this)
            .withSelectHandler {
                it.map { selectedStdSystem ->
                    dataContext.create(SystemAllocation::class.java).apply {
                        this.applicationData = applicationDataDc.item
                        this.system = reloadStdSystem(selectedStdSystem).copyToSystem()
                    }
                }.let { selectedSystemsWrap ->
                    systemsAllocationDc.mutableItems.addAll(selectedSystemsWrap)
                }
            }
            .show()
    }

    private fun reloadStdSystem(stdSystem: SystemStd) : SystemStd {
        return dataManager.reload(
            stdSystem, ViewBuilder.of(SystemStd::class.java)
                .addView(View.LOCAL)
                .add("pumpModel", View.MINIMAL)
                .add("depth", View.MINIMAL)
                .add("sealConfig", View.MINIMAL)
                .add("pumpConfig", View.MINIMAL)
                .add("pumpMaterials", View.MINIMAL)
                .add("sealMaterials", View.MINIMAL)
                .add("motorMaterials", View.MINIMAL)
                .add("motorType", View.MINIMAL)
                .add("intakeConfig", View.MINIMAL)
                .add("vaproConfig", View.MINIMAL)
                .add("details") {it.addAll("equipmentType", "partNumber", "length")}
                .build()
        )
    }


    fun setEditable(editable : Boolean) {
        systemsAllocationGridCreate.isVisible = editable
        systemsAllocationGridRemove.isVisible = editable

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

    @Subscribe("systemsAllocationGrid.view")
    private fun onSystemsAllocationGridView(event: Action.ActionPerformedEvent) {
        val screen = screenBuilders.editor(SystemStd::class.java, this)
            .withScreenClass(SystemStdEdit::class.java)
            .editEntity(systemsAllocationGrid.singleSelected!!.system!!)
            .build()
        (screen as ReadOnlyAwareScreen).isReadOnly = true
        screen.show()
    }

    private fun SystemStd.copyToSystem() : System {
        val newSystem = dataContext.create(System::class.java)

        arrayOf(
            "casingSize",
            "casingWeight",
            "pumpModel",
            "depth",
            "motorType",
            "intakeConfig",
            "vaproConfig",
            "sealConfig",
            "pumpConfig",
            "pumpMaterials",
            "sealMaterials",
            "motorMaterials",
            "comment"
        ).forEach { property -> newSystem.setValue(property, this.getValue(property)) }

        val newDetailList = this.details?.map { detailFrom ->
            dataContext.create(SystemDetail::class.java).apply {
                arrayOf("equipmentType", "length", "partNumber")
                    .forEach { property ->
                        this.setValue(property, detailFrom.getValue(property))
                        this.system = newSystem
                    }
            }
        }?.toMutableList()
        newSystem.details = newDetailList

        return newSystem
    }
}