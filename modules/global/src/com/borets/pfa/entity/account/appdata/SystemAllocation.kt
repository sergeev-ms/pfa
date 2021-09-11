package com.borets.pfa.entity.account.appdata

import com.borets.pfa.entity.account.system.System
import com.haulmont.chile.core.annotations.Composition
import com.haulmont.chile.core.annotations.NumberFormat
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PFA_SYSTEM_ALLOCATION")
@Entity(name = "pfa_SystemAllocation")
open class SystemAllocation : StandardEntity() {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "APPLICATION_DATA_ID")
    var applicationData: ApplicationData? = null

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SYSTEM_ID")
    var system: System? = null

    @NumberFormat(pattern = "#%")
    @Column(name = "RUN1")
    var run1: BigDecimal? = BigDecimal.ZERO

    @NumberFormat(pattern = "#%")
    @Column(name = "RUN2")
    var run2: BigDecimal? = BigDecimal.ZERO

    @NumberFormat(pattern = "#%")
    @Column(name = "RUN3")
    var run3: BigDecimal? = BigDecimal.ZERO

    @NumberFormat(pattern = "#%")
    @Column(name = "RUN3_PLUS")
    var run3plus: BigDecimal? = BigDecimal.ZERO

    companion object {
        private const val serialVersionUID = 5378292270482296206L
    }
}