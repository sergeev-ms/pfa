package com.borets.pfa.entity.setting

import com.borets.pfa.entity.account.utilization.EquipmentUtilizationValueType
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "PFA_COUNTRY_SETTING_UTILIZATION_VALUE_TYPE")
@javax.persistence.Entity(name = "pfa_CountrySettingUtilizationValueType")
open class CountrySettingUtilizationValueType : StandardEntity() {
    companion object {
        private const val serialVersionUID = 4124986516853379859L
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COUNTRY_SETTING_ID")
    var countrySetting: CountrySetting? = null


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UTILIZATION_VALUE_TYPE_ID")
    var utilizationValueType: EquipmentUtilizationValueType? = null
}