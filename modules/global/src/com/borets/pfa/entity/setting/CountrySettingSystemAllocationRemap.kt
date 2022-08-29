package com.borets.pfa.entity.setting

import com.borets.pfa.entity.account.utilization.EquipmentUtilizationValueType
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.*

@Table(name = "PFA_COUNTRY_SETTING_SYSTEM_ALLOCATION_REMAP")
@javax.persistence.Entity(name = "pfa_CountrySettingSystemAllocationRemap")
open class CountrySettingSystemAllocationRemap : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_SETTING_ID")
    var countrySetting: CountrySetting? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UTILIZATION_VALUE_TYPE_ID")
    var utilizationValueType: EquipmentUtilizationValueType? = null

    @Lob
    @Column(name = "REMAP_SCRIPT")
    var remapScript: String? = null

    companion object {
        private const val serialVersionUID = -1852965735653494262L
    }
}