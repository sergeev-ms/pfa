package com.borets.pfa.entity.setting

import com.borets.addon.country.entity.Country
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import javax.persistence.*

@Table(name = "PFA_COUNTRY_SETTING")
@Entity(name = "pfa_CountrySetting")
open class CountrySetting : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    var country: Country? = null

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "countrySetting")
    var analyticSettings: MutableList<CountrySettingAnalyticDetail>? = mutableListOf()

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "countrySetting")
    var revenueTypeSettings: MutableList<CountrySettingRevenueType>? = mutableListOf()

    @OrderBy("order")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "countrySetting")
    var equipmentTypeSettings: MutableList<CountrySettingEquipmentType>? = mutableListOf()

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "countrySetting")
    var utilizationValueTypeSettings: MutableList<CountrySettingUtilizationValueType>? = mutableListOf()

    companion object {
        private const val serialVersionUID = -2129473630577786850L
    }
}