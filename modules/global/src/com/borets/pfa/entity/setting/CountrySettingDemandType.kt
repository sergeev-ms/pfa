package com.borets.pfa.entity.setting

import com.borets.pfa.entity.demand.DemandType
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.*

@Table(name = "PFA_COUNTRY_SETTING_DEMAND_TYPE")
@javax.persistence.Entity(name = "pfa_CountrySettingDemandType")
open class CountrySettingDemandType : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COUNTRY_SETTING_ID")
    var countrySetting: CountrySetting? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    var type: DemandType? = null

    @Column(name = "ORDER_")
    var order: Int? = 0

    @Lob
    @Column(name = "SCRIPT")
    var script: String? = "return 0.0"

    companion object {
        private const val serialVersionUID = -6251473050489247468L
    }
}