package com.borets.pfa.web.screens.account.system.systemstd

import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.View
import com.haulmont.cuba.core.global.ViewBuilder
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
        var copyFrom = systemStdsTable.singleSelected!!

        val view = ViewBuilder.of(SystemStd::class.java)
            .addView(View.LOCAL)
            .add("pumpModel", View.MINIMAL)
            .add("depth", View.MINIMAL)
            .add("motorType", View.MINIMAL)
            .add("intakeConfig", View.MINIMAL)
            .add("vaproConfig", View.MINIMAL)
            .add("sealConfig", View.MINIMAL)
            .add("pumpConfig", View.MINIMAL)
            .add("pumpMaterials", View.MINIMAL)
            .add("sealMaterials", View.MINIMAL)
            .add("motorMaterials", View.MINIMAL)
            .add("details") {
                it.addView(View.LOCAL)
                    .add("equipmentType", View.MINIMAL)
                    .add("partNumber", View.MINIMAL)
            }
            .build()

        copyFrom = dataManager.reload(copyFrom, view)

        val new = copyFrom.copy()
        screenBuilders.editor(systemStdsTable)
            .editEntity(new)
            .show()
    }


    private fun SystemStd.copy() : SystemStd {
        val newSystem = dataManager.create(SystemStd::class.java)

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
            dataManager.create(SystemDetail::class.java).apply {
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