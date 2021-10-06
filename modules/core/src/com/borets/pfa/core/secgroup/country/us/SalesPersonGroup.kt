package com.borets.pfa.core.secgroup.country.us

import com.borets.pfa.entity.account.Account
import com.haulmont.cuba.security.app.group.AnnotatedAccessGroupDefinition
import com.haulmont.cuba.security.app.group.annotation.AccessGroup
import com.haulmont.cuba.security.app.group.annotation.JpqlConstraint
import com.haulmont.cuba.security.group.ConstraintsContainer
import java.io.Serializable

@AccessGroup(name = "sales-person", parent = UsGroup::class)
class SalesPersonGroup : AnnotatedAccessGroupDefinition() {

    @JpqlConstraint(target = Account::class, where = "{E}.actualRevision.manager.user.id = :session\$userId")
    override fun accessConstraints(): ConstraintsContainer {
        return super.accessConstraints()
    }

    override fun sessionAttributes(): MutableMap<String, Serializable> {
        return super.sessionAttributes()
    }

}