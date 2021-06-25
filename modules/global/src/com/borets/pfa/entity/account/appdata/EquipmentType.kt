package com.borets.pfa.entity.account.appdata

import com.borets.pfa.entity.price.RevenueType
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import javax.persistence.*

@NamePattern(value = "%s|name")
@Table(name = "PFA_EQUIPMENT_TYPE")
@Entity(name = "pfa_EquipmentType")
open class EquipmentType : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    var category: EquipmentCategory? = null

    @Column(name = "MANDATORY")
    var mandatory: Boolean? = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVENUE_TYPE_ID")
    var revenueType: RevenueType? = null

    @Column(name = "ORDER_")
    var order: Int? = null

    companion object {
        private const val serialVersionUID = 526520663372571176L
    }
}