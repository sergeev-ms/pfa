package com.borets.pfa.web.screens.account.system

import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.View
import com.haulmont.cuba.core.global.ViewBuilder

inline fun<reified T : SystemStd> SystemStd.copyToSystem(dataManager: DataManager) : T {
    val newSystem = dataManager.create(T::class.java)

    arrayOf(
        "country",
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


fun SystemStd.reloadForCopy(dataManager: DataManager) : SystemStd {
    return dataManager.reload(
        this, ViewBuilder.of(SystemStd::class.java)
            .addView(View.LOCAL)
            .add("country", View.MINIMAL)
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
            .add("details") { it.addAll("length")
                .add("partNumber", View.MINIMAL)
                .add("equipmentType") { vb -> vb.addView(View.MINIMAL).add("order") }
            }
            .build()
    )
}