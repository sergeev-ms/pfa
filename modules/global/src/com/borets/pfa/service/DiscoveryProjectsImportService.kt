package com.borets.pfa.service

interface DiscoveryProjectsImportService {

    /**
     * Imports DiscoveryProject entities from external data source to Project entities.
     */
    fun import()

    companion object {
        const val NAME = "pfa_DiscoveryProjectsImportService"
    }
}