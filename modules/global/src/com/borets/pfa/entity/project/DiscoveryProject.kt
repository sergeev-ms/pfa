package com.borets.pfa.entity.project

import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.BaseStringIdEntity
import com.haulmont.cuba.core.global.DbView
import com.haulmont.cuba.core.global.DdlGeneration
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.Table

@DbView
@DdlGeneration(value = DdlGeneration.DbScriptGenerationMode.DISABLED)
@Table(name = "DS_FIELDSERVICEPROJECTS_VW")
@javax.persistence.Entity(name = "pfa_DiscoveryProject")
@NamePattern(value = "%s - %s (%s)|wellId,well,region")
open class DiscoveryProject : BaseStringIdEntity() {
    @Id
    @Column(name = "WELLID", nullable = false)
    var wellId: String? = null

    @Column(name = "CUSTOMER_NO")
    var customerNo: Int? = null

    @Column(name = "POWER")
    var power: String? = null

    @Column(name = "REGION")
    var region: String? = null

    @Column(name = "WELL")
    var well: String? = null

    override fun setId(id: String?) {
        this.wellId = id
    }

    override fun getId(): String? = wellId

    companion object {
        private const val serialVersionUID = -7036771858816069310L
    }
}