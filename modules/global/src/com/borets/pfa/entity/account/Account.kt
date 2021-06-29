package com.borets.pfa.entity.account

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.marketdata.ApplicationType
import com.borets.pfa.entity.account.marketdata.MarketData
import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.borets.pfa.entity.activity.ContractType
import com.borets.pfa.entity.customer.DimCustomers
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.chile.core.annotations.MetaProperty
import com.haulmont.chile.core.annotations.NamePattern
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents
import com.haulmont.cuba.core.entity.annotation.SystemLevel
import com.haulmont.cuba.core.global.DeletePolicy
import java.math.BigDecimal
import java.time.YearMonth
import javax.persistence.*

@PublishEntityChangedEvents
@NamePattern(value = "%s|name")
@Table(name = "PFA_ACCOUNT")
@Entity(name = "pfa_Account")
open class Account : StandardEntity() {
    @Column(name = "NAME")
    var name: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    var parent: Account? = null

    @SystemLevel
    @Column(name = "CUSTOMER_ID", precision = 7, scale = 0)
    var customerId: BigDecimal? = null

    @Transient
    @MetaProperty(related = ["customerId"])
    var customer: DimCustomers? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "account")
    var revisions: MutableList<AccountRevision>? = mutableListOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTUAL_REVISION_ID")
    var actualRevision: AccountRevision? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "account")
    var marketDetails: MutableList<MarketData>? = mutableListOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTUAL_MARKET_DETAIL_ID")
    var actualMarketDetail: MarketData? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "account")
    var appDetails: MutableList<ApplicationData>? = mutableListOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTUAL_APP_DETAIL_ID")
    var actualAppDetail: ApplicationData? = null

    @Column(name = "CONTRACT_TYPE")
    private var contractType: String? = null

    @Column(name = "APPLICATION_TYPE")
    private var applicationType: String? = null

    @OneToMany(mappedBy = "account")
    var equipmentUtilizations: MutableList<EquipmentUtilization>? = mutableListOf()

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTUAL_EQUIPMENT_UTILIZATION_ID")
    var actualEquipmentUtilization: EquipmentUtilization? = null

    fun getType(): Type? = actualRevision?.getType()

    fun getYearMonth(): YearMonth? {
        return actualRevision?.getYearMonth()
    }

    fun getApplicationType(): ApplicationType? = applicationType?.let { ApplicationType.fromId(it) }

    fun setApplicationType(applicationType: ApplicationType?) {
        this.applicationType = applicationType?.id
    }

    fun getContractType(): ContractType? = contractType?.let { ContractType.fromId(it) }

    fun setContractType(contractType: ContractType?) {
        this.contractType = contractType?.id
    }

    companion object {
        private const val serialVersionUID = -5049196871514871532L
    }
}