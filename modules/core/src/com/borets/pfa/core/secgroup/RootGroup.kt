package com.borets.pfa.core.secgroup

import com.haulmont.cuba.security.app.group.AnnotatedAccessGroupDefinition
import com.haulmont.cuba.security.app.group.annotation.AccessGroup
import com.haulmont.cuba.security.group.ConstraintsContainer
import java.io.Serializable

@AccessGroup(name = "Borets")
class RootGroup : AnnotatedAccessGroupDefinition() {
    override fun accessConstraints(): ConstraintsContainer {
        return super.accessConstraints()
    }

    override fun sessionAttributes(): MutableMap<String, Serializable> {
        return super.sessionAttributes()
    }
}