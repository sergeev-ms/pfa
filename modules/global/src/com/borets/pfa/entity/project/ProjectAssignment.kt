package com.borets.pfa.entity.project

import com.borets.pfa.entity.account.Account
import com.haulmont.cuba.core.entity.StandardEntity
import com.haulmont.cuba.core.entity.annotation.OnDelete
import com.haulmont.cuba.core.global.DeletePolicy
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "PFA_PROJECT_ASSIGNMENT")
@Entity(name = "pfa_ProjectAssignment")
open class ProjectAssignment : StandardEntity() {
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    var project: Project? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACCOUNT_ID")
    var account: Account? = null

    @Column(name = "DATE_START")
    var dateStart: LocalDateTime? = null

    @Column(name = "DATE_END")
    var dateEnd: LocalDateTime? = null

    companion object {
        private const val serialVersionUID = 3025750245706904908L
    }
}