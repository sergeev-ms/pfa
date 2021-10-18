package com.borets.pfa.service

import org.apache.commons.lang3.StringUtils
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DiscoveryProjectsImportStatistics (
    val projectEntities: Int,
    val deletedProjectEntities: Int,
    val discoveryProjectEntities: Int,

    val newEntities: MutableList<String?> = ArrayList(),
    val updatedEntities: MutableList<String?> = ArrayList(),
    val deletedEntities: MutableList<String?> = ArrayList(),

    var startTime: LocalDateTime = LocalDateTime.now(),
    var elapsedTime: Duration = Duration.ZERO
) {

    fun addCreatedEntity(wellId: String?) {
        newEntities.add(wellId)
    }

    fun addUpdatedEntity(wellId: String?) {
        updatedEntities.add(wellId)
    }

    fun addDeletedEntity(wellId: String?) {
        deletedEntities.add(wellId)
    }

    override fun toString(): String {
        return  "=============== STATE BEFORE IMPORT ==================\n" +
                "    local:            Project entities count: $projectEntities\n" +
                "    local:           among them soft-deleted: $deletedProjectEntities\n" +
                " external:  Discovery Project entities count: $discoveryProjectEntities\n" +
                "================== IMPORT DURATION ===================\n" +
                "             Start time: ${startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}\n" +
                "           Elapsed time: ${elapsedTime.seconds} seconds\n" +
                "================== IMPORT RESULTS ====================\n" +
                "       Created entities: ${newEntities.size}\n" +
                "       Updated entities: ${updatedEntities.size}\n" +
                "  Soft deleted entities: ${deletedEntities.size}\n" +
                "===================== DETAILS ========================\n" +
                " Created WELL IDs: ${StringUtils.abbreviate(newEntities.toString(), 140)}\n" +
                " Updated WELL IDs: ${StringUtils.abbreviate(updatedEntities.toString(), 140)}\n" +
                " Deleted WELL IDs: ${StringUtils.abbreviate(deletedEntities.toString(), 140)}\n" +
                "======================================================\n"
    }
}
