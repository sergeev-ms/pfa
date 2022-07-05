package com.borets.pfa.core.secgroup.country.region.us

import com.borets.pfa.core.secgroup.country.region.RegionGroup
import com.haulmont.cuba.security.app.group.AnnotatedAccessGroupDefinition
import com.haulmont.cuba.security.app.group.annotation.AccessGroup
import com.haulmont.cuba.security.app.group.annotation.SessionAttribute
import java.io.Serializable

@AccessGroup(name = "US", parent = RegionGroup::class)
class UsGroup : AnnotatedAccessGroupDefinition() {

    @SessionAttribute(name = "countryCode", value = "US", javaClass = String::class)
    override fun sessionAttributes(): MutableMap<String, Serializable> {
        return super.sessionAttributes()
    }

}