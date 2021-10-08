package com.borets.pfa.entity.setting

import com.borets.pfa.entity.account.appdata.EquipmentType
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.*

@Table(name = "PFA_COUNTRY_SETTING_EQUIPMENT_TYPE")
@Entity(name = "pfa_CountrySettingEquipmentType")
open class CountrySettingEquipmentType : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COUNTRY_SETTING_ID")
    var countrySetting: CountrySetting? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EQUIPMENT_TYPE_ID")
    var equipmentType: EquipmentType? = null

    @Column(name = "ORDER_")
    var order: Int? = null

    @Column(name = "MANDATORY")
    var mandatory: Boolean? = null

    @Column(name = "SHOW_IN_UTIL_MODEL")
    var showInUtilModel: Boolean? = true

    companion object {
        private const val serialVersionUID = 2198386593538067560L
    }
}