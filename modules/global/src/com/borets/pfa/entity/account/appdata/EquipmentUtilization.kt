package com.borets.pfa.entity.account.appdata

import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PFA_EQUIPMENT_UTILIZATION")
@Entity(name = "pfa_EquipmentUtilization")
open class EquipmentUtilization : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EQUIPMENT_TYPE_ID")
    var equipmentType: EquipmentType? = null

    @NumberFormat(pattern = "#%")
    @Column(name = "FIRST_RUN_VALUE")
    var firstRunValue: BigDecimal? = BigDecimal.ZERO

    @NumberFormat(pattern = "#%")
    @Column(name = "SEQUENT_RUN_VALUE")
    var sequentRunValue: BigDecimal? = BigDecimal.ZERO

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "APPLICATION_DATA_ID")
    var applicationData: ApplicationData? = null

    @Column(name = "REVENUE_MODE")
    private var revenueMode: String? = null

    @Column(name = "ORDER_")
    var order: Int? = null

    fun getRevenueMode(): RevenueMode? = revenueMode?.let { RevenueMode.fromId(it) }

    fun setRevenueMode(revenueMode: RevenueMode?) {
        this.revenueMode = revenueMode?.id
    }


    companion object {
        private const val serialVersionUID = -2509087691919503194L
    }

    @PrePersist
    open fun prePersist() {
        order = equipmentType?.order
    }
}