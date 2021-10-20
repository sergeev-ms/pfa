package com.borets.pfa.entity.account.utilization

import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PFA_EQUIPMENT_UTILIZATION_DETAIL_VALUE")
@javax.persistence.Entity(name = "pfa_EquipmentUtilizationDetailValue")
open class EquipmentUtilizationDetailValue : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DETAIL_ID")
    var detail: EquipmentUtilizationDetail? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VALUE_TYPE_ID")
    var valueType: EquipmentUtilizationValueType? = null

    @NumberFormat(pattern = "#%")
    @Column(name = "VALUE_")
    var value: BigDecimal? = null

    companion object {
        private const val serialVersionUID = 9193351881471947422L
    }
}