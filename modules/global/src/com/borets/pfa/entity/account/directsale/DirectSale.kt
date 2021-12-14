package com.borets.pfa.entity.account.directsale

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.activity.RecordType
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*

@Table(name = "PFA_DIRECT_SALE")
@Entity(name = "pfa_DirectSale")
open class DirectSale : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "DATE_")
    var date: LocalDate? = null

    @Column(name = "RECORD_TYPE")
    private var recordType: String? = RecordType.FORECAST.id

    @Column(name = "STATUS")
    private var status: String? = SaleStatus.ACTIVE.id

    @MetaProperty(datatype = "percentage")
    @Column(name = "PROBABILITY")
    var probability: BigDecimal? = BigDecimal.ZERO

    @MetaProperty(datatype = "percentage")
    @Column(name = "B_SHARE")
    var bShare: BigDecimal? = BigDecimal.ZERO

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "directSale")
    var details: MutableList<DirectSaleDetail>? = mutableListOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    var parent: DirectSale? = null

    fun getStatus(): SaleStatus? = status?.let { SaleStatus.fromId(it) }

    fun setStatus(status: SaleStatus?) {
        this.status = status?.id
    }

    fun getRecordType(): RecordType? = recordType?.let { RecordType.fromId(it) }

    fun setRecordType(recordType: RecordType?) {
        this.recordType = recordType?.id
    }

    companion object {
        private const val serialVersionUID = -2847401519511986927L
    }
}